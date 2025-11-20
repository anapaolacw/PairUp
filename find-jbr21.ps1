# Script to find jbr-21 installation on your system
Write-Host "Searching for jbr-21 (JetBrains Runtime 21)..." -ForegroundColor Cyan
Write-Host ""

$foundJDKs = @()

# Search locations
$searchLocations = @(
    "$env:ProgramFiles\JetBrains",
    "$env:ProgramFiles\Android\Android Studio",
    "${env:ProgramFiles(x86)}\JetBrains",
    "${env:ProgramFiles(x86)}\Android\Android Studio",
    "$env:LOCALAPPDATA\JetBrains\Toolbox\apps",
    "$env:LOCALAPPDATA\Programs",
    "$env:USERPROFILE\.jdks",
    "$env:ProgramFiles\Java",
    "$env:ProgramFiles\Eclipse Adoptium"
)

foreach ($location in $searchLocations) {
    if (Test-Path $location) {
        Write-Host "Searching $location..." -ForegroundColor Gray
        $jdks = Get-ChildItem $location -Recurse -Directory -Filter "jbr*" -ErrorAction SilentlyContinue |
                Where-Object { Test-Path "$($_.FullName)\bin\java.exe" }

        foreach ($jdk in $jdks) {
            try {
                $version = & "$($jdk.FullName)\bin\java.exe" -version 2>&1 | Select-String "version"
                $foundJDKs += [PSCustomObject]@{
                    Path = $jdk.FullName
                    Version = $version
                }
            } catch {
                # Skip if can't get version
            }
        }

        # Also check for any Java installations
        $javas = Get-ChildItem $location -Recurse -Directory -Filter "jdk*" -ErrorAction SilentlyContinue |
                 Where-Object { Test-Path "$($_.FullName)\bin\java.exe" } |
                 Select-Object -First 5

        foreach ($java in $javas) {
            try {
                $version = & "$($java.FullName)\bin\java.exe" -version 2>&1 | Select-String "version"
                if ($version -match "17|21") {
                    $foundJDKs += [PSCustomObject]@{
                        Path = $java.FullName
                        Version = $version
                    }
                }
            } catch {
                # Skip if can't get version
            }
        }
    }
}

Write-Host ""
if ($foundJDKs.Count -eq 0) {
    Write-Host "❌ No JDK 17 or 21 found!" -ForegroundColor Red
    Write-Host ""
    Write-Host "You need to either:" -ForegroundColor Yellow
    Write-Host "1. Use the IDE's Gradle sync (it has jbr-21 configured)"
    Write-Host "2. Download Java 17 or 21 from: https://adoptium.net/temurin/releases/" -ForegroundColor Cyan
    Write-Host ""
} else {
    Write-Host "✅ Found JDK installations:" -ForegroundColor Green
    Write-Host ""

    $counter = 1
    foreach ($jdk in $foundJDKs) {
        Write-Host "[$counter] $($jdk.Version)" -ForegroundColor Yellow
        Write-Host "    Path: $($jdk.Path)" -ForegroundColor White
        Write-Host ""
        $counter++
    }

    Write-Host "To use one of these with Gradle, add this to gradle.properties:" -ForegroundColor Cyan
    Write-Host ""
    $escapedPath = $foundJDKs[0].Path -replace '\\', '\\\\'
    Write-Host "org.gradle.java.home=$escapedPath" -ForegroundColor Green
    Write-Host ""
    Write-Host "(Use double backslashes!)" -ForegroundColor Gray
}

Write-Host ""
Write-Host "Current Gradle is using:" -ForegroundColor Cyan
& ".\gradlew" --version | Select-String "Daemon JVM"

