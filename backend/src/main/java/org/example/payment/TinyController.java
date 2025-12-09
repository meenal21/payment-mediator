package org.example.payment;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TinyController {

    @GetMapping("/ping")
    public String ping() {
        return "OK";
    }
}
