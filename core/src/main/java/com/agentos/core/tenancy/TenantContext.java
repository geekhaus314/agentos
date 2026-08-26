package com.agentos.core.tenancy;

import java.util.UUID;

/**
 * Simple ThreadLocal tenant context for non-controller code to obtain the current tenant.
 */
public final class TenantContext {
    private static final ThreadLocal<UUID> CURRENT = new ThreadLocal<>();

    private TenantContext() {}

    public static void setTenantId(UUID tenantId) {
        CURRENT.set(tenantId);
    }

    public static UUID getTenantId() {
        return CURRENT.get();
    }

    public static void clear() {
        CURRENT.remove();
    }
}
