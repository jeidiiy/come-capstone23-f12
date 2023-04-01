package io.f12.notionlinkedblog.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.f12.notionlinkedblog.domain.verification.EmailVerificationToken;
import io.f12.notionlinkedblog.repository.redis.EmailVerificationTokenRepository;
import io.f12.notionlinkedblog.security.service.SecureRandomService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class EmailSignupService {
	private final JavaMailSender javaMailSender;
	private final SecureRandomService secureRandomService;
	private final EmailVerificationTokenRepository emailVerificationTokenRepository;

	public boolean verifyingCode(final String id, final String code) {
		EmailVerificationToken verificationToken = emailVerificationTokenRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("유효하지 않은 ID입니다."));

		return verificationToken.getCode().equals(code);
	}

	public String sendMail(final String email) {
		int randomCode = secureRandomService.generateRandomCode();
		String code = String.format("%06d", randomCode);
		SimpleMailMessage mail = createVerificationMail(email, code);

		EmailVerificationToken verificationToken = EmailVerificationToken.builder().email(email).code(code).build();
		emailVerificationTokenRepository.save(verificationToken);

		javaMailSender.send(mail);

		return verificationToken.getId();
	}

	private SimpleMailMessage createVerificationMail(final String email, String code) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(email);
		message.setSubject("[노션 연동 블로그 서비스] 인증 코드입니다.");

		message.setText("[" + code + "] 메일 확인 인증 코드입니다.");

		return message;
	}
}
