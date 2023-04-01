package io.f12.notionlinkedblog.service;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;

import io.f12.notionlinkedblog.domain.verification.EmailVerificationToken;
import io.f12.notionlinkedblog.repository.redis.EmailVerificationTokenRepository;
import io.f12.notionlinkedblog.security.service.SecureRandomService;

@SpringBootTest
class EmailSignupServiceTests {
	@Autowired
	EmailSignupService emailSignupService;
	@MockBean
	JavaMailSender javaMailSender;
	@Autowired
	SecureRandomService secureRandomService;
	@Autowired
	EmailVerificationTokenRepository emailVerificationTokenRepository;

	@Nested
	@DisplayName("인증 코드")
	class VerificationCodeTests {
		@Nested
		@DisplayName("정상 케이스")
		class SuccessCase {
			@DisplayName("검증 성공")
			@Test
			void verifyingCode() {
				//given
				final String email = "test3@gmail.com";
				int randomCode = secureRandomService.generateRandomCode();
				String code = String.format("%06d", randomCode);

				EmailVerificationToken verificationToken = EmailVerificationToken.builder()
					.email(email)
					.code(code)
					.build();
				emailVerificationTokenRepository.save(verificationToken);

				//when
				EmailVerificationToken foundedVerificationToken = emailVerificationTokenRepository.findById(
						verificationToken.getId())
					.orElseThrow(() -> new IllegalArgumentException("유효하지 않은 ID입니다."));

				//then
				assertThat(verificationToken.getCode()).isEqualTo(foundedVerificationToken.getCode());
			}
		}

		@Nested
		@DisplayName("비정상 케이스")
		class FailureCase {
			@DisplayName("검증 실패")
			@Test
			void verifyingCode() {
				//given
				final String email = "test5@gmail.com";
				int randomCode = secureRandomService.generateRandomCode();
				String code = String.format("%06d", randomCode);

				EmailVerificationToken verificationToken = EmailVerificationToken.builder()
					.email(email)
					.code(code)
					.build();
				emailVerificationTokenRepository.save(verificationToken);
				EmailVerificationToken foundedVerificationToken = emailVerificationTokenRepository.findById(
						verificationToken.getId())
					.orElseThrow(() -> new IllegalArgumentException("유효하지 않은 ID입니다."));

				//when
				boolean isVerified = emailSignupService.verifyingCode(foundedVerificationToken.getId(), "invalidCode");

				//then
				assertThat(isVerified).isFalse();
			}
		}
	}
}
