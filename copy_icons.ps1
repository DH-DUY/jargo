# Script to copy app icons from android folder to main res folder
# Author: Cascade AI Assistant

Write-Host "Starting icon copy process..." -ForegroundColor Cyan

# Define paths
$sourcePath = "f:\ANDROID\jargo\app\src\main\res\drawable\android\res"
$destPath = "f:\ANDROID\jargo\app\src\main\res"

# List of mipmap folders to copy
$mipmapFolders = @(
    "mipmap-mdpi",
    "mipmap-hdpi", 
    "mipmap-xhdpi",
    "mipmap-xxhdpi",
    "mipmap-xxxhdpi",
    "mipmap-anydpi-v26"
)

# Check if source exists
if (-not (Test-Path $sourcePath)) {
    Write-Host "❌ Source path not found: $sourcePath" -ForegroundColor Red
    exit 1
}

# Counter for copied files
$totalCopied = 0

# Copy each mipmap folder
foreach ($folder in $mipmapFolders) {
    $source = Join-Path $sourcePath $folder
    $dest = Join-Path $destPath $folder
    
    if (Test-Path $source) {
        Write-Host "`nProcessing $folder..." -ForegroundColor Yellow
        
        # Create destination folder if not exists
        if (-not (Test-Path $dest)) {
            New-Item -ItemType Directory -Path $dest -Force | Out-Null
            Write-Host "   Created folder: $folder" -ForegroundColor Green
        }
        
        # Get all PNG files in source
        $files = Get-ChildItem -Path $source -Filter "ic_launcher*" -File
        
        foreach ($file in $files) {
            $destFile = Join-Path $dest $file.Name
            
            # Copy with overwrite
            Copy-Item -Path $file.FullName -Destination $destFile -Force
            $sizeKB = [math]::Round($file.Length/1KB, 1)
            Write-Host "   Copied: $($file.Name) ($sizeKB KB)" -ForegroundColor Green
            $totalCopied++
        }
    } else {
        Write-Host "   Folder not found: $folder" -ForegroundColor DarkYellow
    }
}

Write-Host "`nCopy completed!" -ForegroundColor Green
Write-Host "Total files copied: $totalCopied" -ForegroundColor Cyan
Write-Host "`nNext steps:" -ForegroundColor Yellow
Write-Host "   1. Build the project: .\gradlew.bat assembleDebug" -ForegroundColor White
Write-Host "   2. Install APK on device" -ForegroundColor White
Write-Host "   3. Clear launcher cache to see new icon" -ForegroundColor White
Write-Host "`nDone!" -ForegroundColor Magenta
