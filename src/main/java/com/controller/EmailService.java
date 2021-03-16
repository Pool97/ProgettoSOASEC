package com.controller;

import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * Servizio della business logic per l'invio dell'OTP mediante e-mail. Si assume il possesso di un indirizzo
   di posta elettronica Google.
 */

@Service
public class EmailService {
    private static final String USERNAME = "roberto.poletti01@gmail.com";
    private static final String PASSWORD = "vivailcss97";
    private final static String SUBJECT = "Two Factor Authentication code";
    private final static String CONTENT_DESCR = "Your two Factor Authentication code is: ";

    /**
     * Crea un oggetto Properties contenente le informazioni di configurazione della connessione
     * @return Proprietà della connessione SMTP
     */
    private Properties createConnectionProperties(){
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        return props;
    }

    /**
     * Crea una sessione mail con le proprietà SMTP
     * @return Sessione mail
     */

    private Session createMailSession(){
        return Session.getInstance(createConnectionProperties(), new Authenticator(){
            protected PasswordAuthentication getPasswordAuthentication(){
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        });
    }

    /**
     * Crea il messaggio e-mail contenente l'OTP
     * @param emailAddress Indirizzo e-mail dell'utente
     * @param twoFAcode Codice OTP generato
     * @return Messaggio e-mail
     */
    private MimeMessage createMessage(String emailAddress, String twoFAcode) throws MessagingException {
        MimeMessage message = new MimeMessage(createMailSession());
        message.setFrom(new InternetAddress(USERNAME));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(emailAddress));
        message.setSubject(SUBJECT);
        message.setText(CONTENT_DESCR + twoFAcode);
        return message;
    }

    /**
     * Invia il messaggio e-mail contenente l'OTP all'utente
     * @param emailAddress Indirizzo e-mail dell'utente
     * @param twoFAcode Codice OTP generato
     * @return Esito dell'invio del messaggio
     */
    public boolean sendEmail(String emailAddress, String twoFAcode){
        try {
            Transport.send(createMessage(emailAddress, twoFAcode));
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
