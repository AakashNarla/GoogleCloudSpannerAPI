package com.google.spanner

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class GcpSpannerApplication {

	static void main(String[] args) {
		SpringApplication.run(GcpSpannerApplication, args)
	}

}
