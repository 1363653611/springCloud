package com.zbcn.adminserver.mail;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Map;

/**
 *  @title SendEmailUtils
 *  @Description 发邮件工具类
 *  @author zbcn8
 *  @Date 2020/1/10 17:06
 */
@Component
public class SendEmailUtils {


	@Autowired
	private JavaMailSender mailSender;

	@Resource
	private JavaMailSender javaMailSender;


	@Resource
	private FreeMarkerConfigurer configurer;

	@Value("${spring.mail.username}")
	private String sender;


	public void sendMail(Map<String, Object> model, String title, String email_template, String toMail, String[] ccMail) throws MessagingException, IOException, TemplateException {
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		//防止成为垃圾邮件，披上outlook的马甲
		mimeMessage.addHeader("X-Mailer","Microsoft Outlook Express 6.00.2900.2869");
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,true);
		//发送方
		helper.setFrom(sender);
		//接受方
		helper.setTo(InternetAddress.parse(toMail));
		//抄送
		if(ccMail != null && ccMail.length > 0){
			helper.setCc(ccMail);
		}
		//读取邮件模板且填充内容
		Template template = configurer.getConfiguration().getTemplate(email_template);
		String text = FreeMarkerTemplateUtils.processTemplateIntoString(template,model);
		helper.setText(text,true);
		//设置标题
		helper.setSubject(title);
		mailSender.send(mimeMessage);
	}


}
