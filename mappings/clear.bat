@echo off
setlocal enabledelayedexpansion

:: 切换到脚本所在目录（若想对当前命令行所在目录操作，可删掉这一行）
cd /d "%~dp0"

set /a count=0

echo ========================================
echo   批量删除文件名包含 "$" 的文件
echo   目录: %cd%
echo ========================================
echo.

:: ---------- 第一步：先列出将被删除的文件 ----------
echo [预览] 以下文件将被删除：
for /r %%f in (*$*) do (
    if exist "%%f" echo    %%f
)

echo.
set /p confirm=确认删除以上文件吗？(Y/N): 
if /i not "%confirm%"=="Y" (
    echo 已取消操作。
    pause
    exit /b
)

:: ---------- 第二步：执行删除 ----------
echo.
for /r %%f in (*$*) do (
    if exist "%%f" (
        del /f /q "%%f"
        set /a count+=1
    )
)

echo ========================================
echo   完成！共删除 !count! 个文件。
echo ========================================
pause