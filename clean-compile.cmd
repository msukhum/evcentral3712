@echo off
echo Cleaning and Compiling EVCentral SteVe Application...
echo.

REM Set JAVA_HOME to VS Code bundled JDK
set JAVA_HOME=C:\Users\Sukhum\.vscode\extensions\redhat.java-1.43.1-win32-x64\jre\21.0.7-win32-x86_64

REM Change to project directory
cd /d "%~dp0"

echo Running: mvnw.cmd clean compile
echo This will download all dependencies and recompile everything...
echo.

.\mvnw.cmd clean compile

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ✅ Clean compilation successful!
    echo All dependencies have been resolved.
    echo You can now run the application with F5 in VS Code or use run-dev.cmd
) else (
    echo.
    echo ❌ Compilation failed!
    echo Check the error messages above.
)

echo.
pause
