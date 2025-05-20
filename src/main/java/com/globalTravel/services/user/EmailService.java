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

            // Contenu HTML de l'email avec le logo
            String htmlContent = "<html>"
                    + "<body style='font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px;'>"
                    + "<div style='max-width: 600px; margin: 0 auto; background-color: #ffffff; padding: 20px; border-radius: 10px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); text-align: center;'>"
                    + "<img src=\"https://i.imgur.com/o6mJ0gJ.jpeg\" alt=\"Logo GlobalTravel\" style=\"max-width: 150px; margin: 20px auto; display: block;\"/>\n" // Remplace l'URL par celle de ton logo
                    + "<h1 style='color: #333333;'>Réinitialisation de votre mot de passe</h1>"
                    + "<p style='color: #555555; font-size: 16px;'>Vous avez demandé à réinitialiser votre mot de passe. Voici votre token de réinitialisation :</p>"
                    + "<div style='background-color: #f9f9f9; padding: 15px; border-radius: 5px; text-align: center; margin: 20px 0;'>"
                    + "<p style='font-size: 18px; font-weight: bold; color: #007BFF;'>" + resetToken + "</p>"
                    + "</div>"
                    + "<p style='color: #555555; font-size: 16px;'>Copiez ce token et collez-le dans la page de réinitialisation de mot de passe pour continuer.</p>"
                    + "<p style='color: #555555; font-size: 16px;'>Si vous n'avez pas demandé cette réinitialisation, veuillez ignorer cet email.</p>"
                    + "<p style='color: #555555; font-size: 16px;'>Cordialement,<br>L'équipe GlobalTravel</p>"
                    + "</div>"
                    + "</body>"
                    + "</html>";


            // Définir le contenu du message comme HTML
            message.setContent(htmlContent, "text/html");

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