#!/bin/bash

# ============================================
# www.fanyanchen.cn 专用部署脚本
# ============================================

set -e

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

DOMAIN="fanyanchen.cn"
WWW_DOMAIN="www.fanyanchen.cn"
APP_NAME="personal-website"
APP_DIR="/opt/${APP_NAME}"
PORT=8080

print_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

echo "=========================================="
echo "   部署到 ${WWW_DOMAIN}"
echo "=========================================="
echo ""

# 检查 root 权限
if [ "$EUID" -ne 0 ]; then 
    print_error "请使用 sudo 运行此脚本"
    exit 1
fi

# 1. 停止旧服务
print_info "[1/8] 停止旧服务..."
sudo systemctl stop ${APP_NAME} 2>/dev/null || true
print_success "旧服务已停止"

# 2. 创建目录
print_info "[2/8] 创建应用目录..."
sudo mkdir -p ${APP_DIR}/uploads/backgrounds
sudo mkdir -p ${APP_DIR}/logs
print_success "目录已创建"

# 3. 安装 jar 文件
print_info "[3/8] 安装应用..."
JAR_SOURCE=""
for file in ~/personal-website*.jar ./personal-website*.jar; do
    if [ -f "$file" ]; then
        JAR_SOURCE="$file"
        break
    fi
done

if [ -z "${JAR_SOURCE}" ]; then
    print_error "未找到 jar 文件！请确保 jar 文件在 ~ 或当前目录"
    exit 1
fi

print_info "找到 jar 文件：${JAR_SOURCE}"
sudo cp "${JAR_SOURCE}" ${APP_DIR}/app.jar
sudo chmod +x ${APP_DIR}/app.jar
print_success "应用已安装"

# 4. 创建 systemd 服务
print_info "[4/8] 创建 systemd 服务..."
sudo cat > /etc/systemd/system/${APP_NAME}.service << EOF
[Unit]
Description=Personal Website Application for ${WWW_DOMAIN}
After=syslog.target network.target

[Service]
User=root
WorkingDirectory=${APP_DIR}
ExecStart=/usr/bin/java -jar ${APP_DIR}/app.jar --server.port=${PORT}
SuccessExitStatus=143
Restart=on-failure
RestartSec=10
StandardOutput=append:${APP_DIR}/logs/app.log
StandardError=append:${APP_DIR}/logs/error.log

[Install]
WantedBy=multi-user.target
EOF

sudo systemctl daemon-reload
print_success "Systemd 服务已创建"

# 5. 配置 Nginx
print_info "[5/8] 配置 Nginx..."
sudo cat > /etc/nginx/sites-available/fanyanchen << 'NGINX_EOF'
server {
    listen 80;
    server_name fanyanchen.cn www.fanyanchen.cn;
    
    access_log /var/log/nginx/fanyanchen-access.log;
    error_log /var/log/nginx/fanyanchen-error.log;
    
    client_max_body_size 50M;
    
    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
    }
    
    # 静态资源缓存
    location ~* \.(jpg|jpeg|png|gif|ico|css|js)$ {
        proxy_pass http://localhost:8080;
        expires 30d;
        add_header Cache-Control "public, immutable";
    }
}
NGINX_EOF

sudo ln -sf /etc/nginx/sites-available/fanyanchen /etc/nginx/sites-enabled/
print_success "Nginx 配置已完成"

# 6. 启动服务
print_info "[6/8] 启动服务..."
sudo systemctl enable ${APP_NAME}
sudo systemctl start ${APP_NAME}
sleep 3

if systemctl is-active --quiet ${APP_NAME}; then
    print_success "应用服务启动成功！"
else
    print_error "应用服务启动失败！"
    print_error "查看日志：tail -f ${APP_DIR}/logs/app.log"
    exit 1
fi

# 7. 重启 Nginx
print_info "[7/8] 重启 Nginx..."
sudo nginx -t
if [ $? -eq 0 ]; then
    sudo systemctl restart nginx
    print_success "Nginx 重启成功！"
else
    print_error "Nginx 配置测试失败！"
    exit 1
fi

# 8. 显示状态和信息
echo ""
echo "=========================================="
print_success "部署完成！"
echo "=========================================="
echo ""
echo -e "${GREEN}访问地址：${NC}"
echo "  http://${DOMAIN}"
echo "  http://${WWW_DOMAIN}"
echo ""
echo -e "${GREEN}服务状态：${NC}"
sudo systemctl status ${APP_NAME} --no-pager -l
echo ""
echo -e "${GREEN}日志文件：${NC}"
echo "  应用日志：${APP_DIR}/logs/app.log"
echo "  错误日志：${APP_DIR}/logs/error.log"
echo "  Nginx 日志：/var/log/nginx/fanyanchen-error.log"
echo ""
echo -e "${YELLOW}下一步操作：${NC}"
echo "  1. 确保域名 ${WWW_DOMAIN} 已解析到服务器 IP"
echo "  2. 验证：ping ${WWW_DOMAIN}"
echo "  3. 配置 HTTPS（推荐）：sudo certbot --nginx -d ${DOMAIN} -d ${WWW_DOMAIN}"
echo ""
echo -e "${BLUE}常用管理命令：${NC}"
echo "  查看状态：sudo systemctl status ${APP_NAME}"
echo "  重启应用：sudo systemctl restart ${APP_NAME}"
echo "  查看日志：tail -f ${APP_DIR}/logs/app.log"
echo "  重启 Nginx：sudo systemctl restart nginx"
echo ""
