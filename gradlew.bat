@echo off
setlocal

rem Check if portable JDK exists in tools/
if exist "%~dp0tools\jdk-17.0.20.1+1" (
    set "JAVA_HOME=%~dp0tools\jdk-17.0.20.1+1"
    set "PATH=%JAVA_HOME%\bin;%PATH%"
)

rem Check if portable Gradle exists in tools/
if exist "%~dp0tools\gradle-8.5\bin\gradle.bat" (
    call "%~dp0tools\gradle-8.5\bin\gradle.bat" %*
) else if exist "%~dp0tools\gradle-8.5\bin\gradle" (
    call "%~dp0tools\gradle-8.5\bin\gradle" %*
) else (
    call gradle %*
)
