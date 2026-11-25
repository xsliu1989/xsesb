package com.xs.config;


import com.xs.dto.Dto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/config")
//public class TestConfig {
//
//    @Value("${test.url}")
//    private String useLocalCache;
//
//    @RequestMapping("/get")
//    public Dto get(@RequestBody Dto dto) {
//        dto.setCode("0099");
//        return dto;
//    }
//}