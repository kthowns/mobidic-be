package com.kimtaeyang.mobidic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class MobidicApplication {

    public static void main(String[] args) {
        SpringApplication.run(MobidicApplication.class, args);
    }

}
