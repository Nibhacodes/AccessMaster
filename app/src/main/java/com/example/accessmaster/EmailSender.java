package com.example.accessmaster;

import android.content.Context;
import android.os.AsyncTask;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailSender {
    public static void sendEmail(Context context, String toEmail, String subject, String message) {
        new SendMailTask(toEmail, subject, message).execute();
    }

    private static class SendMailTask extends AsyncTask<Void, Void, Boolean> {
        private final String toEmail, subject, message;

        public SendMailTask(String toEmail, String subject, String message) {
            this.toEmail = toEmail;
            this.subject = subject;
            this.message = message;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            final String username = "ankitasingh84634@gmail.com";  // Sender Email
            final String password = "evws owcx dzrq wuvb";  // App-specific password (not your email password!)

            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });

            try {
                Message mimeMessage = new MimeMessage(session);
                mimeMessage.setFrom(new InternetAddress(username));
                mimeMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
                mimeMessage.setSubject(subject);
                mimeMessage.setText(message);
                Transport.send(mimeMessage);
                return true;
            } catch (MessagingException e) {
                e.printStackTrace();
                return false;
            }
        }
    }
}
