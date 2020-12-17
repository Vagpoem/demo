package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class DemoApplicationTests {

	@Value("#{'${request.srctype}'.split(',')}")
	List<String> list1;

	@Value("#{'${captcha.type.desc}'.split(',')}")
	List<String> list2;

	@Value("${minute}")
	int minute;

	@Test
	void contextLoads() {

		System.out.println(list1);
		System.out.println(list2);

	}

}
