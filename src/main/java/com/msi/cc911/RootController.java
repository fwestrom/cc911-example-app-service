package com.msi.cc911;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RootController {
    @RequestMapping("")
    public Root root() {
        return new Root();
    }
}
