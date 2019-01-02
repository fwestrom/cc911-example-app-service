package com.msi.cc911.example;

import com.msi.cc911.example.Root;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RootController {
    @RequestMapping("")
    public Root root() {
        return new Root();
    }
}
