package com.msi.cc911.auth.me;

public class ServicesInfo {
    private ServiceInfo admin;
    private ServiceInfo enterprise_admin;
    private Cc911ServiceInfo cc911;

    public ServiceInfo getAdmin() {
        return admin;
    }

    public ServiceInfo getEnterpriseAdmin() {
        return enterprise_admin;
    }

    public Cc911ServiceInfo getCc911() {
        return cc911;
    }
}
