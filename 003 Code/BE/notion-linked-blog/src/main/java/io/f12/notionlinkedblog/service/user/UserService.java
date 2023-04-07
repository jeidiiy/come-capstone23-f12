package io.f12.notionlinkedblog.service.user;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.f12.notionlinkedblog.domain.user.User;
import io.f12.notionlinkedblog.domain.user.dto.signup.UserSignupRequestDto;
import io.f12.notionlinkedblog.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class UserService {

	private final UserRepository userRepository;

	public Long signupByEmail(UserSignupRequestDto requestDto) {
		User newUser = requestDto.toEntity();
		Optional<User> foundUser = userRepository.findByEmail(newUser.getEmail());
		if (foundUser.isEmpty()) {
			User savedUser = userRepository.save(newUser);
			return savedUser.getId();
		}

		throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
	}
}
