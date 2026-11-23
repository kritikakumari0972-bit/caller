@echo off
setlocal
set GRADLE_VERSION=9.6.0
if "%GRADLE_USER_HOME%"=="" set GRADLE_USER_HOME=%USERPROFILE%\.gradle
set DIST_ROOT=%GRADLE_USER_HOME%\wrapper\dists\gradle-%GRADLE_VERSION%-bin\caller-bootstrap
set GRADLE_HOME=%DIST_ROOT%\gradle-%GRADLE_VERSION%
set GRADLE_BIN=%GRADLE_HOME%\bin\gradle.bat
set ZIP_PATH=%DIST_ROOT%\gradle-%GRADLE_VERSION%-bin.zip
if not exist "%GRADLE_BIN%" (
    if not exist "%DIST_ROOT%" mkdir "%DIST_ROOT%"
    if not exist "%ZIP_PATH%" (
        powershell -NoProfile -ExecutionPolicy Bypass -Command "Invoke-WebRequest -Uri 'https://services.gradle.org/distributions/gradle-%GRADLE_VERSION%-bin.zip' -OutFile '%ZIP_PATH%'"
        if errorlevel 1 exit /b 1
    )
    powershell -NoProfile -ExecutionPolicy Bypass -Command "Expand-Archive -LiteralPath '%ZIP_PATH%' -DestinationPath '%DIST_ROOT%' -Force"
    if errorlevel 1 exit /b 1
    del "%ZIP_PATH%"
)
call "%GRADLE_BIN%" %*
exit /b %ERRORLEVEL%
