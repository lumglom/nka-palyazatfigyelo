package hu.szatmarbanda.palyazat.service;

import hu.szatmarbanda.palyazat.model.Palyazat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${ertesites.cel.email:}")
    private String celEmail;

    public void sendAlert(Palyazat palyazat) {
        System.out.println("🔥 ÚJ PÁLYÁZAT TALÁLVA: " + palyazat.getTitle() + " -> " + palyazat.getLink());

        if (mailSender == null || celEmail == null || celEmail.isEmpty()) {
            System.out.println("E-mail küldés kihagyva: Nincs konfiturálva a mailSender vagy a cím.");
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(celEmail);
            message.setSubject("🔥 Új NKA Pályázat: " + palyazat.getTitle());
            message.setText("A figyelő új releváns pályázatot talált!\n\n" +
                    "Cím: " + palyazat.getTitle() + "\n" +
                    "Link: " + palyazat.getLink() + "\n\n" +
                    "Szatmár Banda Pályázatfigyelő");

            mailSender.send(message);
            System.out.println("E-mail sikeresen elküldve!");
        } catch (Exception e) {
            System.err.println("Hiba az e-mail küldésekor: " + e.getMessage());
        }
    }
}