package io.f12.notionlinkedblog.repository.redis;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import io.f12.notionlinkedblog.domain.verification.EmailVerificationToken;
import io.f12.notionlinkedblog.security.service.SecureRandomService;

@Transactional
@SpringBootTest
class EmailVerificationTokenRepositoryTests {

	@Autowired
	SecureRandomService secureRandomService;

	@Autowired
	EmailVerificationTokenRepository tokenRepository;

	@DisplayName("이메일 인증을 위한 토큰 생성")
	@Test
	void save() {
		int randomCode = secureRandomService.generateRandomCode();
		String email = "test@gmail.com";
		String code = String.format("%06d", randomCode);

		EmailVerificationToken token = EmailVerificationToken.builder().email(email).code(code).build();
		EmailVerificationToken savedToken = tokenRepository.save(token);

		assertThat(tokenRepository.count()).isGreaterThan(0L);
		assertThat(savedToken.getEmail()).isEqualTo(email);
		assertThat(savedToken.getCode()).isEqualTo(code);
	}
}
