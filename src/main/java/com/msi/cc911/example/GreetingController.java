package com.msi.cc911.example;

import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingController {

    @RequestMapping("/greeting")
    public Greeting greeting() {
        return new Greeting("Hello, world!");
    }

    @RequestMapping("/greeting1")
    @Secured("ROLE_call-taker")
    public Greeting greeting1() {
        return new Greeting("Hello, call taker!");
    }

    @RequestMapping("/greeting2")
    @Secured("ROLE_supervisor")
    public Greeting greeting2() {
        return new Greeting("Hello, supervisor!");
    }
}
