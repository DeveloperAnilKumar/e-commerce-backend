package Online.EmailUtlity;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@Component
public class EmailConfig {

    @Autowired
    private JavaMailSender mailSender;

    public boolean sendEmail(String to, String subject, String text, String[] cc, String[] bcc, MultipartFile file) {

        boolean flag = false;

        try {

            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(message, file != null);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text,true);

            if (cc != null) {
                helper.setCc(cc);
            }
            if (bcc != null) {
                helper.setBcc(bcc);
            }
            if (file != null) {
                helper.addAttachment(Objects.requireNonNull(file.getOriginalFilename()), file);
            }

            mailSender.send(message);
            flag = true;
        } catch (Exception e) {

            e.printStackTrace();

            flag = false;

        }

        return flag;

    }

    public boolean sendEmail(String to, String subject, String text) {
        return sendEmail(to, subject, text, null, null, null);
    }
}

