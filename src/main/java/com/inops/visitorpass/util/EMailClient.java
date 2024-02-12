package com.inops.visitorpass.util;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.inops.visitorpass.entity.EMail;
import com.inops.visitorpass.entity.EmailTemplate;
import com.inops.visitorpass.entity.Employee;
import com.inops.visitorpass.service.job.ReportComputation;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.log4j.Log4j2;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
//@AllArgsConstructor
@Log4j2
@Service("emailClient")
public class EMailClient {

	private JavaMailSender javaMailSender(EMail email) {

		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

		mailSender.setHost(email.getSmtpHost());
		mailSender.setPort(email.getSmtpPort());

		// Set other properties
		mailSender.setUsername(email.getUsername());
		mailSender.setPassword(email.getPassword());

		Properties properties = new Properties();
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.starttls.enable", email.getUseTLS());

		mailSender.setJavaMailProperties(properties);

		return mailSender;

	}

	public void sendEmailWithAttachment(EMail email, EmailTemplate template, Map<String, byte[]> attachment,
			List<Employee> employeeEmails) throws MessagingException, IOException {
		JavaMailSender javaMailSender = javaMailSender(email);

		MimeMessage message = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true);

		helper.setFrom(email.getFromAddress());
		helper.setSubject(template.getSubject());
		helper.setText(template.getBody(), true);

		// Add attachment if provided
		attachment.forEach((k, v) -> {
			if (v != null) {
				try {
					helper.addAttachment(k, new ByteArrayResource(v));
				} catch (MessagingException e) {
					e.printStackTrace();
				}
			}
		});
		employeeEmails.forEach(emp -> {
			try {
				helper.setTo(emp.getEmail());
				javaMailSender.send(message);
			} catch (MessagingException e) {
				log.error("mail send exception {}", e.getMessage());
			}
		});

	}

}
