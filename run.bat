@echo off

cd /d "%~dp0"

if "%~1"=="" (
    call gradlew.bat --console=plain run
) else (
    call gradlew.bat --console=plain run --args="%*"
)