package com.globalTravel.services.user;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailService {

    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String EMAIL_FROM = "rayenneji1919@gmail.com";
    private static final String EMAIL_PASSWORD = "sbye zvvy hczg ahfw"; // Utilise un mot de passe d'application

    public static void sendResetEmail(String toEmail, String resetToken) {
        // Configuration des propriétés SMTP
        Properties props = new Properties();
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        // Création d'une session SMTP
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL_FROM, EMAIL_PASSWORD);
            }
        });

        try {
            // Création du message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_FROM));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Réinitialisation de votre mot de passe");

            // Contenu de l'email
            String emailContent = "Voici votre token de réinitialisation de mot de passe : \n\n"
                    + resetToken + "\n\n"
                    + "Copiez ce token et collez-le dans la page de réinitialisation de mot de passe pour continuer.";

            message.setText(emailContent);

            // Envoi du message
            Transport.send(message);
            System.out.println("Email envoyé avec succès à : " + toEmail);
        } catch (MessagingException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de l'envoi de l'email : " + e.getMessage());

            // Afficher des informations supplémentaires sur l'erreur
            if (e instanceof AuthenticationFailedException) {
                System.err.println("Échec de l'authentification SMTP. Vérifiez l'email et le mot de passe.");
            } else if (e instanceof SendFailedException) {
                System.err.println("Échec de l'envoi de l'email. Vérifiez l'adresse email du destinataire.");
            } else {
                System.err.println("Erreur inconnue lors de l'envoi de l'email.");
            }
        }
    }
}