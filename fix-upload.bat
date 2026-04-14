@echo off
chcp 65001 >nul
echo ==========================================
echo    修复上传功能
echo ==========================================
echo.

REM 创建上传目录
echo [1/3] 创建上传目录...
if not exist "uploads\backgrounds" (
    mkdir uploads\backgrounds
    echo [成功] 创建 backgrounds 目录
) else (
    echo [信息] backgrounds 目录已存在
)

if not exist "uploads\files" (
    mkdir uploads\files
    echo [成功] 创建 files 目录
) else (
    echo [信息] files 目录已存在
)

REM 设置目录权限（Windows）
echo.
echo [2/3] 设置目录权限...
icacls uploads /grant Everyone:(OI)(CI)F /T >nul 2>&1
echo [完成] 目录权限已设置

REM 清理旧的数据库文件（可选）
echo.
echo [3/3] 检查数据库配置...
if exist "personal_website.mv.db" (
    echo [信息] 数据库文件存在
) else (
    echo [提示] 数据库将在首次运行时自动创建
)

echo.
echo ==========================================
echo    ✅ 修复完成！
echo ==========================================
echo.
echo 现在可以运行 run.bat 启动网站
echo.
pause
