$ws = New-Object -ComObject WScript.Shell
$startupFolder = [System.Environment]::GetFolderPath([System.Environment+SpecialFolder]::Startup)
$shortcutPath = Join-Path $startupFolder "CineVerse-AutoStart.lnk"
$projectDir = Split-Path -Parent $PSScriptRoot

$shortcut = $ws.CreateShortcut($shortcutPath)
$shortcut.TargetPath = "wscript.exe"
$shortcut.Arguments = "`"$projectDir\start-background.vbs`""
$shortcut.WorkingDirectory = $projectDir
$shortcut.Description = "CineVerse Auto Start (Spring Boot + Next.js)"
$shortcut.Save()

Write-Host "Auto-startup shortcut created at: $shortcutPath" -ForegroundColor Green
