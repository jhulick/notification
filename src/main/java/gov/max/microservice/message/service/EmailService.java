package gov.max.microservice.message.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.util.Date;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender javaMailSender;
    private final String from;

    /**
     * Constructor
     * @param javaMailSender - a JavaMailSender instance
     */
    @Autowired
    public EmailService(@Value("${mail.from}") String from, JavaMailSender javaMailSender) {
        this.from = from;
        this.javaMailSender = javaMailSender;
    }

    public void send(String to, String subject, String body) {
        javaMailSender.send(getMimeMessagePreparator(to, subject, body));
        log.info("Sent an email to {}", to);
    }

    private MimeMessagePreparator getMimeMessagePreparator(String to, String subject, String body) {
        return new MimeMessagePreparator() {
            @Override
            public void prepare(MimeMessage mimeMessage) throws Exception {
                MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
                message.setTo(to);
                message.setFrom(from);
                message.setSubject(subject);
                message.setSentDate(new Date());
                message.setText(body, true);
            }
        };
    }
}
