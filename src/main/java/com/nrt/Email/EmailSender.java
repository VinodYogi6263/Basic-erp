package com.nrt.Email;

import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

@Component
public class EmailSender {

	@Autowired
	private JavaMailSender javaMailSender;
	@Autowired
	private  TemplateEngine templateEngine;

	private Logger log = LoggerFactory.getLogger(EmailSender.class);
	
	

	@Value("${username}")
	private String username;

	public void send(String toEmail, String body) {
		String path = "D://SS/test.jpg";
		try {

			MimeMessage mimeMessage = javaMailSender.createMimeMessage();

			MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

			mimeMessageHelper.setFrom(username);

			mimeMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));

			mimeMessageHelper.setText(body);

			mimeMessageHelper.setSubject("FORGOT YOUR PASSWORD");

			FileSystemResource fileSystemResource = new FileSystemResource(new File(path));

			mimeMessageHelper.addAttachment(fileSystemResource.getFilename(), fileSystemResource);

			javaMailSender.send(mimeMessage);

			log.info("Email send class called successfully");

		} catch (MessagingException e) {
			log.error("error inside the email send call ");
			log.error(e.getMessage());
		}
	}
	
	
	 
	 public void sendWelcomeEmail(String to, String username, String role, String password,String subject) throws MessagingException {
	        MimeMessage message = javaMailSender.createMimeMessage();
	        MimeMessageHelper helper = new MimeMessageHelper(message, true);

	        Context context = new Context();
	        context.setVariable("username", username);
	        context.setVariable("role", role);
	        context.setVariable("password", password);

	        String emailContent = templateEngine.process("/html/email/welcome", context);

	        helper.setTo(to);
	        helper.setSubject(subject);
	        helper.setText(emailContent, true);
	        ClassPathResource imageResource = new ClassPathResource("templates/html/email/nrt.png");
	        helper.addInline("nrtLogo", imageResource);
	        javaMailSender.send(message);
	    }
	


}