package com.msi.cc911.config;

import com.msi.cc911.JWTAuthenticationFilter;
import com.msi.cc911.JWTAuthorizationFilter;
import com.msi.cc911.auth.AccessTokenVerifier;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@Configuration
@EnableOAuth2Sso
public class ApplicationSecurity extends WebSecurityConfigurerAdapter {
    private final AccessTokenVerifier accessTokenVerifier;
    private final UserDetailsService userDetailsService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public ApplicationSecurity(AccessTokenVerifier accessTokenVerifier, BCryptPasswordEncoder bCryptPasswordEncoder, UserDetailsService userDetailsService) {
        this.accessTokenVerifier = accessTokenVerifier;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .antMatcher("/**")
            .authorizeRequests()
                .antMatchers("/")
                .permitAll()
            .anyRequest()
                .authenticated().and()
                    .addFilter(new JWTAuthenticationFilter(authenticationManager()))
                    .addFilter(new JWTAuthorizationFilter(accessTokenVerifier, authenticationManager()));
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }

    @Bean
    public CommonsRequestLoggingFilter requestLoggingFilter() {
        CommonsRequestLoggingFilter loggingFilter = new CommonsRequestLoggingFilter();
        loggingFilter.setIncludeClientInfo(true);
        loggingFilter.setIncludeQueryString(true);
        loggingFilter.setIncludePayload(true);
        loggingFilter.setIncludeHeaders(false);
        return loggingFilter;
    }
}
