<#
PowerShell helper: push_to_github.ps1
Automates: init git (if needed), commit, create remote via gh or set remote manually, and push to GitHub.
Usage: open PowerShell, cd to project root and run:
    Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass; .\push_to_github.ps1
This script intentionally does NOT store or print Personal Access Tokens.
#>

# Helper to run commands and show output
function Run-Command($cmd) {
    Write-Host "-> $cmd" -ForegroundColor Cyan
    $proc = Start-Process -FilePath powershell -ArgumentList "-NoProfile -Command $cmd" -NoNewWindow -Wait -PassThru -WindowStyle Hidden
    return $proc.ExitCode
}

Write-Host "Script: push_to_github.ps1 - automatiza git init/commit/remote/push" -ForegroundColor Green

# Ensure we're in the project root (user should run from repo root)
$root = Get-Location
Write-Host "Pasta atual: $root"

# Check git installed
if (-not (Get-Command git -ErrorAction SilentlyContinue)) {
    Write-Error "Git não encontrado no PATH. Instale Git (https://git-scm.com/) e reexecute este script."; exit 1
}

# Is this a git repo?
$inRepo = $false
try { git rev-parse --is-inside-work-tree > $null 2>&1; if ($LASTEXITCODE -eq 0) { $inRepo = $true } } catch { }

if (-not $inRepo) {
    Write-Host "Inicializando repositório git..."
    git init
} else { Write-Host "Repositório git já inicializado." }

# Create minimal .gitignore if missing (won't overwrite existing)
$gitignorePath = Join-Path $root '.gitignore'
if (-not (Test-Path $gitignorePath)) {
    @".gradle/
local.properties
/.idea
*.iml
/build/
**/build/
captures/
.externalNativeBuild/
.cxx/
.DS_Store
"@ | Out-File -Encoding utf8 $gitignorePath
    Write-Host ".gitignore criado." -ForegroundColor Yellow
} else { Write-Host ".gitignore já existe." }

# Stage & commit if there are changes
$status = git status --porcelain
if ($status -ne "") {
    Write-Host "Existem alterações locais. Fazendo add + commit..."
    git add .
    git commit -m "Initial commit"
} else {
    Write-Host "Sem alterações para commitar." -ForegroundColor Yellow
}

# Ask whether to use gh
$useGh = Read-Host "Deseja usar o GitHub CLI (gh) para criar o repositório e dar push automaticamente? (s/n) [s]"
if ([string]::IsNullOrWhiteSpace($useGh)) { $useGh = 's' }

$defaultOwner = 'GabrielAfonso22'
$defaultRepo = 'AP2des_web'
$owner = Read-Host "GitHub username (pressione Enter para usar '$defaultOwner')"
if ([string]::IsNullOrWhiteSpace($owner)) { $owner = $defaultOwner }
$repo = Read-Host "Nome do repositório (pressione Enter para usar '$defaultRepo')"
if ([string]::IsNullOrWhiteSpace($repo)) { $repo = $defaultRepo }

if ($useGh -match '^[sS]') {
    if (-not (Get-Command gh -ErrorAction SilentlyContinue)) {
        Write-Warning "gh CLI não encontrada. Instale-a (https://cli.github.com/) ou escolha não usar gh na próxima execução.";
        $useGh = 'n'
    }
}

if ($useGh -match '^[sS]') {
    Write-Host "Usando gh CLI para criar e enviar o repositório $owner/$repo..."
    # Ensure gh auth
    gh auth status 2>$null
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Você precisa autenticar o gh. Será aberto um assistente."
        gh auth login
    }
    # Try to create; if exists, set remote and push
    $createCmd = "gh repo create $owner/$repo --source=. --remote=origin --push --confirm"
    Write-Host "Executando: $createCmd"
    $ec = Run-Command $createCmd
    if ($ec -ne 0) {
        Write-Warning "gh repos creation/push retornou código $ec. Tentando apenas configurar remote e push..."
        git remote remove origin 2>$null
        git remote add origin https://github.com/$owner/$repo.git
        git branch -M main
        Write-Host "Fazendo push para origin/main..."
        git push -u origin main
    } else {
        Write-Host "Repositório criado e push efetuado via gh (se tudo correu bem)." -ForegroundColor Green
    }
} else {
    # Manual remote flow
    $protocol = Read-Host "Escolha protocolo para o remoto: (1) HTTPS (padrão) (2) SSH"
    if ($protocol -ne '2') { $protocol = 'https' } else { $protocol = 'ssh' }

    if ($protocol -eq 'https') {
        $remoteUrl = "https://github.com/$owner/$repo.git"
    } else {
        $remoteUrl = "git@github.com:$owner/$repo.git"
    }

    # Add or set origin
    if (git remote | Select-String -Pattern '^origin$' > $null) {
        Write-Host "Remoto origin já existe — atualizando URL para $remoteUrl"
        git remote set-url origin $remoteUrl
    } else {
        git remote add origin $remoteUrl
    }

    git branch -M main

    Write-Host "Tentando push para $remoteUrl ..."
    git push -u origin main
    if ($LASTEXITCODE -ne 0) {
        Write-Error "Push falhou. Se usar HTTPS, forneça seu usuário e como senha um Personal Access Token (PAT) com escopo 'repo'. Se usar SSH, verifique se adicionou a chave pública ao GitHub e que o ssh-agent está rodando.";
        Write-Host "Dicas rápidas: para HTTPS, gere um PAT em https://github.com/settings/tokens; para SSH, copie sua chave com: Get-Content $env:USERPROFILE\.ssh\id_ed25519.pub | Set-Clipboard" -ForegroundColor Yellow
        exit 1
    } else { Write-Host "Push realizado com sucesso." -ForegroundColor Green }
}

Write-Host "Fim do script. Verifique o repositório no GitHub (https://github.com/$owner/$repo)" -ForegroundColor Green
