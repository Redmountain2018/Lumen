@echo off
setlocal enabledelayedexpansion

REM ============================================
REM Path configuration (modify as needed)
REM ============================================
set "BASE_DIR=I:\Confidential\Lumen\Lumen-main"
set "MCVR_DIR=%BASE_DIR%\MCVR"
set "SHADERS_SRC=%MCVR_DIR%\build\src\shader\shaders"
set "RESOURCES_DIR=%BASE_DIR%\src\main\resources"
set "CORE_SRC=%MCVR_DIR%\build\src\core\Release"
set "GRADLE_DIR=%BASE_DIR%"
set "LIBS_SRC=%BASE_DIR%\build\libs"
set "MODS_DEST=P:\MCRadiance\versions\1.21.4-Fabric 0.18.4\mods"
set "START_BAT=P:\MCRadiance\start_minecraft.bat"

REM Java environment configuration
set "JAVA_HOME_PATH=P:\j21"

REM ============================================
REM Step 1: Switch to MCVR directory and run CMake build
REM ============================================
echo [1/7] Changing directory to: %MCVR_DIR%
cd /d "%MCVR_DIR%" || (
    echo ERROR: Cannot change to directory %MCVR_DIR%
    exit /b 1
)

echo [2/7] Running CMake build...
cmake --build build -j --config Release
if errorlevel 1 (
    echo ERROR: CMake build failed. Check output above.
    exit /b 1
)

REM ============================================
REM Step 2: Copy shaders folder (using robocopy)
REM ============================================
echo [3/7] Copying shaders folder to resources...

if not exist "%SHADERS_SRC%" (
    echo ERROR: Source shaders folder not found: %SHADERS_SRC%
    exit /b 1
)

set "TARGET_SHADERS=%RESOURCES_DIR%\shaders"

REM Delete existing target shaders folder to ensure clean copy
if exist "%TARGET_SHADERS%" (
    echo Removing old shaders folder: %TARGET_SHADERS%
    rmdir /s /q "%TARGET_SHADERS%" 2>nul
)

REM Ensure resources directory exists
if not exist "%RESOURCES_DIR%" mkdir "%RESOURCES_DIR%"

REM Use robocopy to mirror source to destination
echo Copying shaders using robocopy...
robocopy "%SHADERS_SRC%" "%TARGET_SHADERS%" /E /IS /IT /R:3 /W:5 /NP /NDL
set "ROBOEXIT=%errorlevel%"
if %ROBOEXIT% geq 8 (
    echo ERROR: robocopy failed with exit code %ROBOEXIT%
    exit /b 1
) else (
    echo Shaders folder copied successfully.
)

REM ============================================
REM Step 3: Copy core.dll and core.lib files
REM ============================================
echo [4/7] Copying core.dll and core.lib to resources...
copy /Y "%CORE_SRC%\core.dll" "%RESOURCES_DIR%\" >nul 2>&1
if errorlevel 1 (
    echo WARNING: core.dll not found or failed to copy.
)
copy /Y "%CORE_SRC%\core.lib" "%RESOURCES_DIR%\" >nul 2>&1
if errorlevel 1 (
    echo WARNING: core.lib not found or failed to copy.
)

REM ============================================
REM Step 4: Switch to Gradle directory
REM ============================================
echo [5/7] Changing directory to: %GRADLE_DIR%
cd /d "%GRADLE_DIR%" || (
    echo ERROR: Cannot change to directory %GRADLE_DIR%
    exit /b 1
)

REM ============================================
REM Step 5: Check and set JAVA_HOME if needed
REM ============================================
echo [6/7] Checking JAVA_HOME...
if "%JAVA_HOME%"=="" (
    echo WARNING: JAVA_HOME is not set. Setting it to %JAVA_HOME_PATH%
    set "JAVA_HOME=%JAVA_HOME_PATH%"
) else (
    echo JAVA_HOME is already set to %JAVA_HOME%
)

REM ============================================
REM Step 6: Run Gradle build
REM ============================================
echo Running Gradle build...
call gradlew.bat build
if errorlevel 1 (
    echo ERROR: Gradle build failed. Check output above.
    exit /b 1
)

REM ============================================
REM Step 7: Copy windows-specific jar files from libs to mods folder
REM ============================================
echo [7/7] Copying windows-specific jar files to mods folder...
if not exist "%LIBS_SRC%" (
    echo WARNING: Libs directory %LIBS_SRC% does not exist.
    goto :skip_copy
)

REM Create destination folder if it doesn't exist
if not exist "%MODS_DEST%" (
    echo Creating mods folder: %MODS_DEST%
    mkdir "%MODS_DEST%" 2>nul
    if errorlevel 1 (
        echo ERROR: Failed to create mods folder %MODS_DEST%
        exit /b 1
    )
)

REM Use xcopy to copy all matching jar files at once (avoids parsing issues)
xcopy "%LIBS_SRC%\*windows*.jar" "%MODS_DEST%\" /Y /Q >nul 2>&1
if errorlevel 1 (
    echo WARNING: Failed to copy windows jar files. No matching files found or copy error.
) else (
    echo Successfully copied windows jar files to mods folder.
)

:skip_copy

REM ============================================
REM Step 8: Launch Minecraft
REM ============================================
echo Launching Minecraft...
if not exist "%START_BAT%" (
    echo ERROR: Start script %START_BAT% not found.
    exit /b 1
)
call "%START_BAT%"
if errorlevel 1 (
    echo ERROR: Failed to start Minecraft.
    exit /b 1
)

echo All steps completed successfully!
endlocal