package com.sffy;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@MapperScan({"com.sffy.**.mapper"})
@SpringBootApplication
@RestController
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    /**
     * 访问首页提示
     *
     * @return /
     */
    @GetMapping("/")
    public String index() {
        return "Backend service started successfully";
    }

}
