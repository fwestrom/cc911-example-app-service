package com.msi.cc911.auth;

public class Permission {
    private final String id;
    private final String name;

    public Permission(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
