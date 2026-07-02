@echo off
chcp 65001 >nul
cd /d %~dp0

echo ====== 编译中... ======
if not exist out mkdir out
javac -encoding UTF-8 -d out src\server\ChatServer.java

echo ===== 聊天室服务端 =====
echo.
echo 启动端口: 8889
echo 按 Ctrl+C 可停止服务端
echo ======================
echo.
java -cp out ChatServer 8889
pause
