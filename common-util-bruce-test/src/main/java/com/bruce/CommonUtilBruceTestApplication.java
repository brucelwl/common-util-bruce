package com.bruce;

import com.bruce.utils.TimeUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CommonUtilBruceTestApplication {

	public static void main(String[] args) {
		SpringApplication.run(CommonUtilBruceTestApplication.class, args);

		System.out.println(TimeUtils.now());
	}
}
