# Create a custom truststore with all necessary certificates
$customTruststore = "C:\Users\AChazaroWatty\Documents\Hymans Projects\Tutorials\PairUp\gradle-truststore.jks"
$javaHome = "C:\Program Files\Android\Android Studio\jbr"
$keytool = "$javaHome\bin\keytool.exe"

# Remove old truststore if exists
if (Test-Path $customTruststore) {
    Remove-Item $customTruststore -Force
}

Write-Host "Creating custom truststore..."

# First, copy the default cacerts as a starting point
$defaultCacerts = "$javaHome\lib\security\cacerts"
Copy-Item $defaultCacerts $customTruststore -Force

Write-Host "Truststore created at: $customTruststore"
Write-Host "Default password: changeit"
Write-Host ""
Write-Host "Now exporting certificates from websites..."

$urls = @(
    "https://dl.google.com",
    "https://repo.maven.apache.org",
    "https://plugins.gradle.org",
    "https://maven.pkg.jetbrains.space"
)

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
            Write-Host "Exported certificate for $($uri.Host)"

            # Delete existing alias if present
            & $keytool -delete -alias $certName -keystore $customTruststore -storepass changeit -noprompt 2>$null

            # Import the certificate
            $result = & $keytool -importcert -alias $certName -file $certFile -keystore $customTruststore -storepass changeit -noprompt 2>&1

            if ($LASTEXITCODE -eq 0) {
                Write-Host "  [OK] Imported $certName" -ForegroundColor Green
            } else {
                Write-Host "  [FAIL] Failed to import $certName" -ForegroundColor Red
                Write-Host "    $result"
            }
        }
    } catch {
        Write-Host "Error processing $url : $_" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "Done! Custom truststore created at:"
Write-Host $customTruststore -ForegroundColor Cyan

