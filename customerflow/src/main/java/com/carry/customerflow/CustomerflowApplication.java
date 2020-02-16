package com.carry.customerflow;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan
@EnableCaching
@MapperScan(basePackages = {"com.carry.customerflow.mapper"})
@SpringBootApplication
public class CustomerflowApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomerflowApplication.class, args);
    }

}
