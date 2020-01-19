package com.google.spanner

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cache.annotation.EnableCaching

@SpringBootApplication
@EnableCaching
class GcpSpannerApplication {

    static void main(String[] args) {
        SpringApplication.run(GcpSpannerApplication, args)
    }

}
