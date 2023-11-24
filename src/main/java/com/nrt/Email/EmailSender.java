package com.nrt.Email;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.log4j.Log4j2;

/**
 * @author Ramu singh
 * 
 */

@Log4j2
@Component
public class EmailSender {

	@Autowired
	private JavaMailSender javaMailSender;

	@Autowired
	private TemplateEngine templateEngine;

	// to send the email to provided mail address with the passed information
	// (using[map] )
	public void sendEmail(String emailTo, String subject, String filePath, Map<String, Object> sourceMap)
			throws MessagingException {
		MimeMessage message = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true);

		Context context = new Context();
		for (Map.Entry<String, Object> entry : sourceMap.entrySet()) {
			context.setVariable(entry.getKey(), entry.getValue());
		}

		String emailContent = templateEngine.process(filePath, context);
		helper.setTo(emailTo);
		helper.setSubject(subject);
		helper.setText(emailContent, true);
		ClassPathResource imageResource = new ClassPathResource("templates/html/email/nrt.png");
		helper.addInline("nrtLogo", imageResource);
		javaMailSender.send(message);
		log.info("Email sender get  called successfully");
	}

	public void sendEmail(String emailTo, String subject, String emailTemplatefilePath, String attachmentFileName,
			byte[] pdfBytes) throws MessagingException {
		MimeMessage message = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true);
		Context context = new Context();
		String emailContent = templateEngine.process(emailTemplatefilePath, context);
		helper.setTo(emailTo);
		helper.setSubject(subject);
		helper.setText(emailContent, true);

		ClassPathResource imageResource = new ClassPathResource("templates/html/email/nrt.png");
		helper.addAttachment(attachmentFileName, new ByteArrayResource(pdfBytes));
		helper.addInline("nrtLogo", imageResource);

		javaMailSender.send(message);
		log.info(" sendEmail with attachment called successfully");
	}

}