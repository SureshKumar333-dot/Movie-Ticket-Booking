$startupFolder = [System.Environment]::GetFolderPath([System.Environment+SpecialFolder]::Startup)
$shortcutPath = Join-Path $startupFolder "CineVerse-AutoStart.lnk"

if (Test-Path $shortcutPath) {
    Remove-Item -Path $shortcutPath -Force
    Write-Host "Removed auto-startup shortcut: $shortcutPath" -ForegroundColor Yellow
} else {
    Write-Host "Auto-startup shortcut was not found." -ForegroundColor Cyan
}
