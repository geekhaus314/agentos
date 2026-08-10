$dirs = @(
    '/AgentOS/core',
    '/AgentOS/modules/commercial-roofing',
    '/AgentOS/modules/commercial-insurance',
    '/AgentOS/modules/hvac',
    '/AgentOS/modules/restoration',
    '/AgentOS/modules/industrial',
    '/AgentOS/integration/adapters/crm',
    '/AgentOS/integration/adapters/erp',
    '/AgentOS/integration/adapters/sms',
    '/AgentOS/integration/adapters/email',
    '/AgentOS/integration/adapters/calendar',
    '/AgentOS/integration/adapters/storage',
    '/AgentOS/integration/adapters/webhook',
    '/AgentOS/infra/auth',
    '/AgentOS/infra/tenancy',
    '/AgentOS/infra/authz',
    '/AgentOS/infra/audit',
    '/AgentOS/infra/billing',
    '/AgentOS/ui/widgets',
    '/AgentOS/ui/portal',
    '/AgentOS/ui/worker',
    '/AgentOS/docs/api',
    '/AgentOS/docs/flows',
    '/AgentOS/docs/patterns'
)
foreach ($d in $dirs) {
    New-Item -ItemType Directory -Path $d -Force -ErrorAction SilentlyContinue
}
Write-Host 'Done creating directories'