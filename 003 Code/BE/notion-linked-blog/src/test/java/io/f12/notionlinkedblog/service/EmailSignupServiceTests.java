package io.f12.notionlinkedblog.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.f12.notionlinkedblog.domain.dummy.DummyObject;
import io.f12.notionlinkedblog.domain.verification.EmailVerificationToken;
import io.f12.notionlinkedblog.repository.redis.EmailVerificationTokenRepository;

@ExtendWith(MockitoExtension.class)
class EmailSignupServiceTests {
	@InjectMocks
	EmailSignupService emailSignupService;
	@Mock
	EmailVerificationTokenRepository emailVerificationTokenRepository;

	@DisplayName("인증 코드")
	@Nested
	class VerificationCodeTests extends DummyObject {
		@DisplayName("정상 케이스")
		@Nested
		class SuccessCase {
			@DisplayName("검증 성공")
			@Test
			void verifyingCode() {
				//given
				EmailVerificationToken mockEmailVerificationToken = newMockEmailVerificationToken("1", "123456");

				// stub 1
				given(emailVerificationTokenRepository.findById(any())).willReturn(Optional.of(
					mockEmailVerificationToken));

				//when
				boolean isVerified = emailSignupService.verifyingCode("1", "123456");

				//then
				assertThat(isVerified).isTrue();
			}
		}

		@DisplayName("비정상 케이스")
		@Nested
		class FailureCase {
			@DisplayName("검증 실패")
			@Test
			void verifyingCode() {
				//given
				EmailVerificationToken mockEmailVerificationToken = newMockEmailVerificationToken("1", "123456");

				// stub 1
				given(emailVerificationTokenRepository.findById(any())).willReturn(Optional.of(
					mockEmailVerificationToken));

				//when
				boolean isVerified = emailSignupService.verifyingCode("1", "987654");

				//then
				assertThat(isVerified).isFalse();
			}
		}
	}
}
