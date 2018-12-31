package com.msi.cc911;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.Serializable;

@Service("userDetailsService")
public class CCUserDetailsService implements UserDetailsService, PermissionEvaluator {
    private static final Logger log = LoggerFactory.getLogger(CCUserDetailsService.class);

    public CCUserDetailsService() {
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        SecurityContext sc = SecurityContextHolder.getContext();
        Authentication authentication = sc.getAuthentication();
        log.warn("Authentication.getName(): " + authentication.getName());
        return null;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Object o, Object o1) {
        log.warn("CCUserDetailsService.hasPermission() authentication: {}, o: {}, o1: {}", authentication, o, o1);
        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable serializable, String s, Object o) {
        log.warn("CCUserDetailsService.hasPermission() authentication: {}, serializable: {}, s: {}, o: {}", authentication, serializable, s, o);
        return false;
    }
}
