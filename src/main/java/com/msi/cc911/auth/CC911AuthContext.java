package com.msi.cc911.auth;

@SuppressWarnings({ "WeakerAccess", "unused" })
public class CC911AuthContext {
    private final String userId;
    private final String userEmail;
    private final Iterable<Permission> permissions;

    public CC911AuthContext(String userId, String userEmail, Iterable<Permission> permissions) {
        this.userId= userId;
        this.userEmail = userEmail;
        this.permissions = permissions;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public Iterable<Permission> getPermissions() {
        return permissions;
    }
}

