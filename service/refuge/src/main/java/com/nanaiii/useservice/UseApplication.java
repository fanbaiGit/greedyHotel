package com.nanaiii.useservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
//@EnableDiscoveryClient
@ComponentScan("com.nanaiii")
@MapperScan("com.nanaiii.useservice.mapper")
public class UseApplication {

    public static void main(String[] args) {
        SpringApplication.run(UseApplication.class, args);
    }

}
