@echo off
chcp 65001 >nul
cd /d %~dp0

echo ====== 编译中... ======
if not exist out mkdir out
javac -encoding UTF-8 -d out src\server\ChatServer.java src\client\ChatClient.java 2>nul

echo ===== 聊天室客户端 =====
echo.
java -cp out client.ChatClient
pause
