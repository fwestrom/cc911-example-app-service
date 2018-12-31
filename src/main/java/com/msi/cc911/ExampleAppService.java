package com.msi.cc911;

import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
@EnableOAuth2Sso
public class ExampleAppService { //implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(ExampleAppService.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);
    }

    // @Override
    // public void run(String... args) throws Exception {
    //     System.out.println("Hello, world!");
    // }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
