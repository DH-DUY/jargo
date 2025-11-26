# Script to remove old WebP icons
Write-Host "Removing old WebP icons..." -ForegroundColor Cyan

$resPath = "f:\ANDROID\jargo\app\src\main\res"
$mipmapFolders = @("mipmap-mdpi", "mipmap-hdpi", "mipmap-xhdpi", "mipmap-xxhdpi", "mipmap-xxxhdpi")

$removed = 0
foreach ($folder in $mipmapFolders) {
    $path = Join-Path $resPath $folder
    $webpFiles = Get-ChildItem -Path $path -Filter "*.webp" -ErrorAction SilentlyContinue
    
    foreach ($file in $webpFiles) {
        Remove-Item $file.FullName -Force
        Write-Host "Removed: $($file.Name)" -ForegroundColor Yellow
        $removed++
    }
}

Write-Host "`nRemoved $removed WebP files" -ForegroundColor Green
Write-Host "Ready to build!" -ForegroundColor Magenta
