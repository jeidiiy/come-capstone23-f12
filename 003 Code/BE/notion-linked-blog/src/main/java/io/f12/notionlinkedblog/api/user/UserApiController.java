package io.f12.notionlinkedblog.api.user;

import static io.f12.notionlinkedblog.api.EmailApiController.*;

import javax.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.f12.notionlinkedblog.domain.user.dto.signup.UserSignupRequestDto;
import io.f12.notionlinkedblog.domain.user.dto.signup.UserSignupResponseDto;
import io.f12.notionlinkedblog.service.user.UserService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/users")
@RestController
public class UserApiController {

	private final UserService userService;

	@PostMapping("/email/signup")
	public ResponseEntity<UserSignupResponseDto> signupByEmail(
		@Validated UserSignupRequestDto requestDto,
		HttpSession httpSession) {
		boolean emailVerified = (boolean)httpSession.getAttribute(emailVerifiedAttr);

		if (emailVerified) {
			Long savedId = userService.signupByEmail(requestDto);
			UserSignupResponseDto responseDto = UserSignupResponseDto.builder().id(savedId).build();
			httpSession.removeAttribute(emailVerifiedAttr);
			return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
		}

		throw new IllegalStateException("이메일 검증이 되지 않았습니다.");
	}
}
