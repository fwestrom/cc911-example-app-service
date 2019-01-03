package com.msi.cc911.auth.me;

public class UserInfo {
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private ServicesInfo services;

    public UserInfo() {
    }

    public UserInfo(String id, String email, String firstName, String lastName) {
        this();
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public UserInfo(String id, String email, String firstName, String lastName, ServicesInfo services) {
        this(id, email, firstName, lastName);
        this.services = services;
    }

    public String getId() {
        return id;
    }

    public void setId(String value) {
        id = value;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String value) {
        email = value;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String value) {
        firstName = value;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String value) {
        lastName = value;
    }

    public ServicesInfo getServices() {
        return services;
    }

    public void setServices(ServicesInfo value) {
        services = value;
    }
}

