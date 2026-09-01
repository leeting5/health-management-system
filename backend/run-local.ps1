$ErrorActionPreference = "Stop"

$envFile = Join-Path $PSScriptRoot ".env.local"

if (-not (Test-Path $envFile)) {
    throw "Missing local environment file: $envFile. Copy backend/.env.local.example to backend/.env.local and fill in your local configuration."
}

$lines = Get-Content $envFile -Encoding UTF8

foreach ($line in $lines) {
    $line = $line.Trim()
    if ($line -and -not $line.StartsWith("#")) {
        $separatorIndex = $line.IndexOf("=")
        if ($separatorIndex -gt 0) {
            $name = $line.Substring(0, $separatorIndex).Trim()
            $value = $line.Substring($separatorIndex + 1).Trim()
            $value = $value.Trim('"').Trim("'")
            [Environment]::SetEnvironmentVariable($name, $value, "Process")
        }
    }
}

$jwtSecret = [Environment]::GetEnvironmentVariable("JWT_SECRET", "Process")
if ($jwtSecret -and $jwtSecret.Length -lt 32) {
    throw "JWT_SECRET must be at least 32 characters."
}

Set-Location $PSScriptRoot
mvn spring-boot:run
