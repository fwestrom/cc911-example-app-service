package com.msi.cc911.auth;

import java.net.URL;

public interface SecurityProperties {

    URL getAccessTokenUri();

    String getClientId();

    String getClientSecret();

    URL getUserInfoUri();
}
