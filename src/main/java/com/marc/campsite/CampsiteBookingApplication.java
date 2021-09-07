package com.marc.campsite;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
@Slf4j
public class CampsiteBookingApplication {

	public static void main(String[] args) {
		SpringApplication.run(CampsiteBookingApplication.class, args);
	}
}
