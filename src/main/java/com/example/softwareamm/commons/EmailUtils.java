package com.example.softwareamm.commons;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import java.time.LocalDate;
import java.util.Properties;

public class EmailUtils {
    public static void inviaEmail(String destinatario, int OTP){
        //Email Azienda
        String username = "mario.rossi.test52@gmail.com";
        //Password autenticazione di google
        String password = "pimtfxrfgniydyva";

        Properties props = new Properties();

        //Protocolli di rete
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.from", "mario.rossi.test52@gmail.com");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.port", "587");
        props.setProperty("mail.debug", "true");

        Session session = Session.getInstance(props, null);
        MimeMessage msg = new MimeMessage(session);

        //Destinatario
        try {
            msg.setRecipients(Message.RecipientType.TO, destinatario);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

        //Oggetto e contenuto
        try {
            msg.setSubject("Recupero Password");
            //msg.setText(messaggio);
            msg.setContent(
                    "<!DOCTYPE html>\n" +
                            "<html>\n" +
                            "<head>\n" +
                            "<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\">\n" +
                            "<title>Recupero password</title>\n" +
                            "</head>\n" +
                            "<body>\n" +
                            "<div style=\"text-align: center;\">\n" +
                            "<table style=\"width: 595px; height: 544px;\" border=\"1\">\n" +
                            "<tbody>\n" +
                            "<tr>\n" +
                            "<td>\n" +
                            "<h2 style=\"text-align: left; margin-left: 40px;\"><span style=\"color: #0059ff;\">Procedura di recupero password</span></h2>\n" +
                            "<div style=\"margin-left: 40px;\">\n" +
                            "</div><p style=\"text-align: left; margin-left: 40px;\">Gentile dipendente,</p>\n" +
                            "<div style=\"margin-left: 40px;\">\n" +
                            "</div><p style=\"text-align: left; margin-left: 40px;\">da Sua richiesta è stato generato il codice di autenticazione per reimpostare la password.</p>\n" +
                            "<div style=\"margin-left: 40px;\">\n" +
                            "</div><p style=\"text-align: left; margin-left: 40px;\">Ecco il codice OTP generato: </p>\n" +
                            "<div style=\"margin-left: 40px;\">\n" +
                            "</div><p style=\"text-align: left; margin-left: 40px;\"><br></p>\n" +
                            "<div style=\"margin-left: 40px;\">\n" +
                            "</div><h2 style=\"text-align: left; margin-left: 40px;\"><b>" + OTP + "</b></h2>\n" +
                            "<div style=\"margin-left: 40px;\">\n" +
                            "</div><p style=\"text-align: left; margin-left: 40px;\"><br></p>\n" +
                            "<p style=\"text-align: left; margin-left: 40px;\">Per continuare la procedura di recupero password inserire l'OTP nel software aziendale.</p>\n" +
                            "<div style=\"margin-left: 40px;\">\n" +
                            "</div><p style=\"text-align: left; margin-left: 40px;\">Se non si fosse a conoscenza di tale procedura, ignorare l'email.</p>\n" +
                            "<div style=\"margin-left: 40px;\">\n" +
                            "</div><p style=\"text-align: left; margin-left: 40px;\">Qualora si dovessero verificare altre procedure non richieste, contattare l'amministratore.</p>\n" +
                            "<p style=\"text-align: left; margin-left: 40px;\"><br></p>\n" +
                            "<p style=\"text-align: left; margin-left: 40px;\"><i>Agency Team <br>(" + Utils.formattaData(LocalDate.now()) + ")</i></p>\n" +
                            "</td></tr></tbody></table></div>\n" +
                            "</body></html>", "text/html");
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

        Transport transport = null;
        try {
            transport = session.getTransport("smtp");
        } catch (NoSuchProviderException e) {
            throw new RuntimeException(e);
        }

        try {
            transport.connect(username, password);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

        try {
            transport.sendMessage(msg, msg.getAllRecipients());
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

        try {
            transport.close();
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
    public static void inviaOTP(String destinatario, int OTP) {
        new Thread(() -> inviaEmail(destinatario, OTP)).start();
    }
}
