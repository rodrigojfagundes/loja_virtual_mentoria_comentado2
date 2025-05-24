package jdev.mentoria.lojavirtual.service;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class ServiceSendEmail {

    private String userName = "rodrigojosefagundes8@gmail.com";
    //SENHA NOVA - tive q fazer isso pq o google disse q eu tava enviando
    //spam... dai eu coloquei na AWS para enviar alerta de senha com mais
    //de 140 dias... entao provavelmente daqui alguns dias (hj 25/03/2025)
    //ele vai enviar um monte de email.. e a conta pd ser bloqueada...
    //dai eu modifiquei agora para enviar alerta so com mais de 3000 dias
    //no LOJA_VIRTUAL_MENTORIA q ta no notebook o q ta no da AWS
    //ta para 140 dias ainda... entao para resolver isso eu alterei a senha
    //do email
    private String password = "Senha_E-Mail";
    //ESSA E A SENHA ANTIGA
    //private String password = "Senha_E-Mail";

    @Async
    public void enviarEmailHtml(String assunto, String textoMensagem, String emailDestino) throws MessagingException, UnsupportedEncodingException {
        
        Properties properties = new Properties();

        // Alex porque o envio dele parou de funcionar
        properties.put("mail.smtp.ssl.trust", "*");

        // Tadeu Sonar Lint por causa do "mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory"
        properties.put("mail.smtp.ssl.checkserveridentity", "true");

        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls", "true"); // Alex usou false
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.socketFactory.port", "465");
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(userName, password);
            }
        });

        session.setDebug(true);

        Address[] toUser = InternetAddress.parse(emailDestino);

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(userName, "Rodrigo - do Java Web", "UTF-8"));
        message.setRecipients(Message.RecipientType.TO, toUser);
        message.setSubject(assunto);
        message.setContent(textoMensagem, "text/html; charset=utf-8");

        Transport.send(message);
    }
}