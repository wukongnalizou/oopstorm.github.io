package com.example.gmt;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "local", url = "http://localhost:9093/local")
public interface LocalService {

    @GetMapping
    String local();

}
