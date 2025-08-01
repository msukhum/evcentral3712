@echo off
echo Compiling EVCentral SteVe Application...
echo.

REM Set JAVA_HOME to VS Code bundled JDK
set JAVA_HOME=C:\Users\Sukhum\.vscode\extensions\redhat.java-1.43.1-win32-x64\jre\21.0.7-win32-x86_64

REM Change to project directory
cd /d "%~dp0"

REM Run Maven compile
echo Running: mvnw.cmd compile
.\mvnw.cmd compile

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ✅ Compilation successful!
    echo You can now run the application with F5 in VS Code or use run.cmd
) else (
    echo.
    echo ❌ Compilation failed!
    echo Check the error messages above.
)

echo.
pause
