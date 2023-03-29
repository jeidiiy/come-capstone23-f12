package io.f12.notionlinkedblog.security.service;

import static io.f12.notionlinkedblog.security.service.SecureRandomService.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.token.SecureRandomFactoryBean;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest(classes = {SecureRandomFactoryBean.class, SecureRandomService.class})
class SecureRandomServiceTests {

	@Autowired
	SecureRandomService secureRandomService;

	@DisplayName("인증 코드 생성")
	@Test
	void generateVerifyingCode() {
		int randomCode = secureRandomService.generateRandomCode();
		Assertions.assertThat(randomCode).isLessThan(DEFAULT_SECURE_BOUND);
	}
}
