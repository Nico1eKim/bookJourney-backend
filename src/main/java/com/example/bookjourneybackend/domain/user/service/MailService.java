package com.example.bookjourneybackend.domain.user.service;

import com.example.bookjourneybackend.global.exception.GlobalException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.bookjourneybackend.global.response.status.BaseExceptionResponseStatus.CANNOT_CREAT_EMAIL;
import static com.example.bookjourneybackend.global.response.status.BaseExceptionResponseStatus.UNABLE_TO_SEND_EMAIL;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender emailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;


    public void sendEmail(String toEmail, String title, String text) {
        MimeMessage emailForm = createEmailForm(toEmail, title, text);
        try {
            emailSender.send(emailForm);
        } catch (RuntimeException e) {
            log.info("MailService.sendEmail exception occur toEmail: {}, " +
                    "title: {}, text: {}", toEmail, title, text);
            throw new GlobalException(UNABLE_TO_SEND_EMAIL);
        }
    }


    // 발신할 이메일 데이터 세팅
    private MimeMessage createEmailForm(String toEmail, String title, String text) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            // 발신자 이름과 이메일 설정
            helper.setFrom(fromEmail,"책산책");
            helper.setTo(toEmail);
            helper.setSubject(title);
            helper.setText(text);

            return message;
        } catch (Exception e) {
            throw new GlobalException(CANNOT_CREAT_EMAIL);
        }
    }
}
