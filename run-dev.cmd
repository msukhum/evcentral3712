@echo off
echo Starting EVCentral SteVe Application...
echo.

REM Set JAVA_HOME to VS Code bundled JDK
set JAVA_HOME=C:\Users\Sukhum\.vscode\extensions\redhat.java-1.43.1-win32-x64\jre\21.0.7-win32-x86_64

REM Change to project directory
cd /d "%~dp0"

echo Running: mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev
echo.
echo The application will start on: http://127.0.0.1:8180/steve/manager/dashboard
echo Login: admin / 1234567
echo.
echo Press Ctrl+C to stop the application
echo.

.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev
