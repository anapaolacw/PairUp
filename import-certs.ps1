# PowerShell script to export certificates and import them into JDK truststore
$javaHome = "C:\Program Files\Android\Android Studio\jbr"
$keystorePath = "$javaHome\lib\security\cacerts"
$urls = @(
    "https://dl.google.com",
    "https://repo.maven.apache.org",
    "https://plugins.gradle.org",
    "https://maven.pkg.jetbrains.space"
)

Write-Host "Exporting certificates from websites..."

foreach ($url in $urls) {
    try {
        $uri = [System.Uri]$url
        $request = [System.Net.HttpWebRequest]::Create($uri)
        $request.Timeout = 10000
        $request.AllowAutoRedirect = $false

        try {
            $response = $request.GetResponse()
            $response.Close()
        } catch {}

        $cert = $request.ServicePoint.Certificate

        if ($cert) {
            $cert2 = New-Object System.Security.Cryptography.X509Certificates.X509Certificate2($cert)
            $certName = $uri.Host -replace '\.', '-'
            $certFile = "$env:TEMP\$certName.cer"

            [System.IO.File]::WriteAllBytes($certFile, $cert2.Export([System.Security.Cryptography.X509Certificates.X509ContentType]::Cert))
            Write-Host "Exported certificate for $($uri.Host) to $certFile"

            # Import certificate into JDK truststore
            $keytoolPath = "$javaHome\bin\keytool.exe"
            Write-Host "Importing $certName into JDK truststore..."

            # First, try to delete if it exists
            & $keytoolPath -delete -alias $certName -keystore $keystorePath -storepass changeit -noprompt 2>$null

            # Import the certificate
            $importCmd = "& `"$keytoolPath`" -importcert -alias `"$certName`" -file `"$certFile`" -keystore `"$keystorePath`" -storepass changeit -noprompt"
            Invoke-Expression $importCmd

            if ($LASTEXITCODE -eq 0) {
                Write-Host "Successfully imported $certName" -ForegroundColor Green
            } else {
                Write-Host "Failed to import $certName (may need admin privileges)" -ForegroundColor Yellow
            }
        }
    } catch {
        Write-Host "Error processing $url : $_" -ForegroundColor Red
    }
}

Write-Host "`nDone! Please restart Gradle daemon with: .\gradlew --stop"

