package com.zhu.gradleproject;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author zwy
 */
@MapperScan("com.zhu.gradleproject.mapper")
@SpringBootApplication
public class GradleProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(GradleProjectApplication.class, args);
    }

}
