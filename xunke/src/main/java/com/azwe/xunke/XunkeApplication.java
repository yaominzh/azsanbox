package com.azwe.xunke;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication(scanBasePackages = {"com.azwe.xunke"})
@MapperScan("com.azwe.xunke.dal")
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class XunkeApplication {

    public static void main(String[] args) {
        SpringApplication.run(XunkeApplication.class, args);
    }

}
