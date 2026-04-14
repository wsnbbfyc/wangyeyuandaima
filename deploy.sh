#!/bin/bash

# ============================================
# 个人网站一键部署脚本（Linux 服务器）
# ============================================

set -e

echo "=========================================="
echo "   个人网站自动部署脚本"
echo "=========================================="
echo ""

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 检查是否以 root 运行
if [ "$EUID" -ne 0 ]; then 
    echo -e "${RED}错误：请使用 sudo 运行此脚本${NC}"
    exit 1
fi

# 配置变量
APP_NAME="personal-website"
APP_DIR="/opt/${APP_NAME}"
JAR_FILE="${APP_DIR}/${APP_NAME}.jar"
SERVICE_NAME="${APP_NAME}"
PORT=80

# 函数：打印信息
print_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 函数：检查 Java
check_java() {
    print_info "检查 Java 环境..."
    if ! command -v java &> /dev/null; then
        print_warning "Java 未安装，正在安装 OpenJDK 8..."
        apt update
        apt install -y openjdk-8-jdk
    else
        java_version=$(java -version 2>&1 | head -n 1)
        print_info "已安装：$java_version"
    fi
}

# 函数：创建目录
create_directories() {
    print_info "创建应用目录..."
    mkdir -p ${APP_DIR}
    mkdir -p ${APP_DIR}/uploads
    mkdir -p ${APP_DIR}/uploads/backgrounds
    mkdir -p ${APP_DIR}/logs
}

# 函数：停止旧服务
stop_service() {
    print_info "停止旧服务..."
    if systemctl is-active --quiet ${SERVICE_NAME}; then
        systemctl stop ${SERVICE_NAME}
        print_info "服务已停止"
    else
        print_info "服务未运行"
    fi
}

# 函数：备份旧版本
backup_old_version() {
    if [ -f "${JAR_FILE}" ]; then
        print_info "备份旧版本..."
        cp ${JAR_FILE} ${JAR_FILE}.bak.$(date +%Y%m%d%H%M%S)
    fi
}

# 函数：安装 jar 文件
install_jar() {
    print_info "请上传 jar 文件到当前目录..."
    
    # 检查当前目录是否有 jar 文件
    JAR_SOURCE=""
    for file in ~/personal-website*.jar ./personal-website*.jar; do
        if [ -f "$file" ]; then
            JAR_SOURCE="$file"
            break
        fi
    done
    
    if [ -z "${JAR_SOURCE}" ]; then
        print_error "未找到 jar 文件！请先将 jar 文件上传到 ~ 或当前目录"
        exit 1
    fi
    
    print_info "找到 jar 文件：${JAR_SOURCE}"
    cp "${JAR_SOURCE}" "${JAR_FILE}"
    chmod +x "${JAR_FILE}"
    print_info "Jar 文件已安装"
}

# 函数：创建 systemd 服务
create_systemd_service() {
    print_info "创建 systemd 服务..."
    
    cat > /etc/systemd/system/${SERVICE_NAME}.service << EOF
[Unit]
Description=Personal Website Application
After=syslog.target network.target

[Service]
User=root
WorkingDirectory=${APP_DIR}
ExecStart=/usr/bin/java -jar ${JAR_FILE} --server.port=${PORT}
SuccessExitStatus=143
Restart=on-failure
RestartSec=10
StandardOutput=append:${APP_DIR}/logs/app.log
StandardError=append:${APP_DIR}/logs/error.log

[Install]
WantedBy=multi-user.target
EOF
    
    systemctl daemon-reload
    print_info "Systemd 服务已创建"
}

# 函数：配置防火墙
configure_firewall() {
    print_info "配置防火墙..."
    
    if command -v ufw &> /dev/null; then
        ufw allow ${PORT}/tcp
        print_info "已开放端口 ${PORT}"
    else
        print_warning "未检测到 UFW，请手动配置防火墙"
    fi
}

# 函数：启动服务
start_service() {
    print_info "启动服务..."
    systemctl enable ${SERVICE_NAME}
    systemctl start ${SERVICE_NAME}
    
    sleep 3
    
    if systemctl is-active --quiet ${SERVICE_NAME}; then
        print_info "✅ 服务启动成功！"
    else
        print_error "❌ 服务启动失败！"
        print_error "查看日志：tail -f ${APP_DIR}/logs/app.log"
        exit 1
    fi
}

# 函数：显示状态
show_status() {
    echo ""
    echo "=========================================="
    echo "   部署完成！"
    echo "=========================================="
    echo ""
    systemctl status ${SERVICE_NAME} --no-pager -l
    echo ""
    print_info "访问地址：http://你的服务器 IP"
    print_info "日志文件：${APP_DIR}/logs/app.log"
    print_info "错误日志：${APP_DIR}/logs/error.log"
    echo ""
}

# 主流程
main() {
    check_java
    create_directories
    stop_service
    backup_old_version
    install_jar
    create_systemd_service
    configure_firewall
    start_service
    show_status
}

# 执行
main
