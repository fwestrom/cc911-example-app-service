package com.msi.cc911.spring;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.msi.cc911.auth.AccessTokenVerifier;
import com.msi.cc911.auth.CC911AuthContext;
import com.msi.cc911.auth.Permission;
import com.msi.cc911.auth.TokenType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {
    private static final Logger log = LoggerFactory.getLogger(JWTAuthorizationFilter.class);
    private final AccessTokenVerifier accessTokenVerifier;

    public JWTAuthorizationFilter(AccessTokenVerifier accessTokenVerifier, AuthenticationManager authManager) {
        super(authManager);
        this.accessTokenVerifier = accessTokenVerifier;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        log.warn("JWTAuthorizationFilter.doFilterInternal");

        String header = req.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(req, res);
            return;
        }
        UsernamePasswordAuthenticationToken authentication = getAuthentication(req);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(req, res);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        TokenType tokenType = null;
        String token = request.getHeader("Authorization");
        if (token != null && token.toLowerCase().startsWith("bearer")) {
            tokenType = TokenType.Bearer;
            token = token.split(" ", 2)[1];
        }

        try {
            CC911AuthContext authContext = accessTokenVerifier.verify(tokenType, token);

            ArrayList<GrantedAuthority> authorities = new ArrayList<>();
            for (Permission p: authContext.getPermissions()) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + p.getId()));
            }

            return new UsernamePasswordAuthenticationToken(authContext.getUserEmail(), authContext, authorities);
        }
        catch (Exception ex) {
            log.warn("Error while verifying bearer token.", ex);
            return null;
        }
    }
}