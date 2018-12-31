package com.msi.cc911;

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
    @Secured("Call Taker")
    public Greeting greeting1() {
        return new Greeting("Hello, call taker!");
    }

    @RequestMapping("/greeting2")
    @Secured({ "Call Taker Supervisor" })
    public Greeting greeting2() {
        return new Greeting("Hello, supervisor!");
    }
}
