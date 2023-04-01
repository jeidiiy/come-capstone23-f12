package io.f12.notionlinkedblog.api;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.http.HttpStatus.*;

import java.net.HttpCookie;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import io.f12.notionlinkedblog.domain.verification.EmailVerificationToken;
import io.f12.notionlinkedblog.repository.redis.EmailVerificationTokenRepository;
import io.f12.notionlinkedblog.security.service.SecureRandomService;
import io.f12.notionlinkedblog.service.EmailSignupService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EmailApiControllerTests {
	private static final String redisCookieName = "x-redis-id";
	@Autowired
	private TestRestTemplate testRestTemplate;
	@Autowired
	private EmailVerificationTokenRepository emailVerificationTokenRepository;
	@Autowired
	private SecureRandomService secureRandomService;
	@MockBean
	private EmailSignupService mockEmailSignupService;

	@Nested
	@DisplayName("인증 코드 검증")
	class VerificationCodeVerifyingTests {
		@Nested
		@DisplayName("정상 케이스")
		class SuccessCase {
			@DisplayName("검증 성공")
			@Test
			void success() {
				//given
				final String url = "/api/email/code";
				final String email = "test@gmail.com";

				String code = String.format("%06d", secureRandomService.generateRandomCode());
				EmailVerificationToken token = EmailVerificationToken.builder().email(email).code(code).build();

				EmailVerificationToken verificationToken = emailVerificationTokenRepository.save(token);
				String redisId = verificationToken.getId();

				HttpHeaders headers = new HttpHeaders();
				headers.add(COOKIE, redisCookieName + "=" + redisId);
				RequestEntity<String> requestEntity = new RequestEntity<>(code, headers, POST, URI.create(url));

				//when
				when(mockEmailSignupService.verifyingCode(redisId, code)).thenReturn(true);
				ResponseEntity<String> responseEntity = testRestTemplate.postForEntity(url, requestEntity,
					String.class);

				//then
				assertThat(responseEntity.getStatusCode()).isEqualTo(NO_CONTENT);
			}
		}

		@Nested
		@DisplayName("비정상 케이스")
		class FailureCase {
			@DisplayName("잘못된 ID로 인한 인증 실패")
			@Test
			void notProvideValidID() {
				//given
				final String url = "/api/email/code";
				final String redisId = "invalidID";

				HttpHeaders headers = new HttpHeaders();
				headers.add("Cookie", redisCookieName + "=" + redisId);
				RequestEntity<String> requestEntity = new RequestEntity<>(headers, POST, URI.create(url));

				//when
				ResponseEntity<String> responseEntity = testRestTemplate.postForEntity(url, requestEntity,
					String.class);

				//then
				assertThat(responseEntity.getStatusCode()).isEqualTo(BAD_REQUEST);
			}

			@DisplayName("잘못된 인증 코드로 인한 인증 실패")
			@Test
			void notProvideValidCode() {
				//given
				final String url = "/api/email/code";
				final String email = "test@gmail.com";

				String code = String.format("%06d", secureRandomService.generateRandomCode());
				EmailVerificationToken token = EmailVerificationToken.builder().email(email).code(code).build();
				emailVerificationTokenRepository.save(token);
				final String redisId = token.getId();

				HttpHeaders headers = new HttpHeaders();
				headers.add("Cookie", redisCookieName + "=" + redisId);
				RequestEntity<String> requestEntity = new RequestEntity<>(headers, POST, URI.create(url));

				//when
				ResponseEntity<String> responseEntity = testRestTemplate.postForEntity(url, requestEntity,
					String.class);

				//then
				assertThat(responseEntity.getStatusCode()).isEqualTo(BAD_REQUEST);
			}
		}
	}

	@Nested
	@DisplayName("인증 코드 전송")
	class VerificationCodeSendingTests {
		@Nested
		@DisplayName("정상 케이스")
		class SuccessCase {
			@DisplayName("전송 성공")
			@Test
			void success() {
				//given
				final String email = "test@gmail.com";
				final String tmpRedisId = "redisId";

				//when
				when(mockEmailSignupService.sendMail(email)).thenReturn(tmpRedisId);
				ResponseEntity<String> responseEntity = testRestTemplate.postForEntity("/api/email", email,
					String.class);

				List<HttpCookie> httpCookies = HttpCookie.parse(
					Objects.requireNonNull(responseEntity.getHeaders().getFirst(SET_COOKIE)));
				Optional<HttpCookie> redisCookieOptional = httpCookies.stream()
					.filter(cookie -> cookie.getName().equals(redisCookieName))
					.findFirst();

				//then
				assertThat(responseEntity.getStatusCode()).isEqualTo(NO_CONTENT);
				assertThat(redisCookieOptional.isPresent()).isTrue();
				assertThat(redisCookieOptional.get().getValue()).isEqualTo(tmpRedisId);
			}
		}

		@Nested
		@DisplayName("비정상 케이스")
		class FailureCase {
			@DisplayName("이메일이 입력되지 않아 실패")
			@Test
			void isEmpty() {
				//given
				final String email = "";

				//when
				ResponseEntity<String> responseEntity = testRestTemplate.postForEntity("/api/email", email,
					String.class);

				//then
				assertThat(responseEntity.getStatusCode()).isEqualTo(BAD_REQUEST);
			}

			@DisplayName("이메일 형식에 맞지 않아 실패")
			@Test
			void isInvalidFormat() {
				//given
				final String email = "invali\"d@domain.com";

				//when
				ResponseEntity<String> responseEntity = testRestTemplate.postForEntity("/api/email", email,
					String.class);

				//then
				assertThat(responseEntity.getStatusCode()).isEqualTo(BAD_REQUEST);
			}
		}
	}
}
