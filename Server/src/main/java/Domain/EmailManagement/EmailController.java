package Domain.EmailManagement;


import Domain.CommonClasses.Response;
import Domain.UsersManagment.UserController;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;


public class EmailController {
    final private Properties prop = new Properties();
    UserController userController;

    private EmailController(){
        prop.put("mail.smtp.username", "invalidinvalid9@gmail.com");
        prop.put("mail.smtp.password", "");
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true"); // TLS
        userController = UserController.getInstance();
    }

    private static class CreateSafeThreadSingleton {
        private static final EmailController INSTANCE = new EmailController();
    }

    public static EmailController getInstance() {
        return EmailController.CreateSafeThreadSingleton.INSTANCE;
    }

    public void sendEmail(String currUser, String surveyLink) throws MessagingException {//todo surveyLink?
        Response<List<String>> emailsTo = userController.getCoordinatorEmails(currUser);//todo implement
        Session mailSession = Session.getInstance(prop, new javax.mail.Authenticator() {
            @Override
            protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(prop.getProperty("mail.smtp.username"),
                        prop.getProperty("mail.smtp.password"));
            }
        });

        javax.mail.Message message = new MimeMessage(mailSession);
        message.setFrom(new InternetAddress("no-reply@gmail.com"));
        message.setSubject("Sending Mail with pure Java Mail API ");

        /* Mail body with plain Text */
        message.setText("Hello User,"
                + "\n\n If you read this, means mail sent with Java Mail API is successful");

        InternetAddress[] toEmailAddresses =
                InternetAddress.parse("shakedcmh@gmail.com");//todo switch to pull addresses according to coordinators and the requested supervisors field
        InternetAddress[] ccEmailAddresses =
                InternetAddress.parse("user21@gmail.com, user22@gmail.com");
        InternetAddress[] bccEmailAddresses =
                InternetAddress.parse("user31@gmail.com");

        message.setRecipients(Message.RecipientType.TO,toEmailAddresses);
        //message.setRecipients(Message.RecipientType.CC,ccEmailAddresses);
        //message.setRecipients(Message.RecipientType.BCC,bccEmailAddresses);

        /* Step 1: Create MimeBodyPart and set content and its Mime Type */
        BodyPart mimeBody = new MimeBodyPart();
        mimeBody.setContent("<h1> This is HTML content </h1><br> <b> User </b>" + " " +  surveyLink, "text/html");//todo add surveyId as well?

        /* Step 2: Create MimeMultipart and  wrap the mimebody to it */
        Multipart multiPart = new MimeMultipart();
        multiPart.addBodyPart(mimeBody);

        /* Step 3: set the multipart content to Message in caller method*/
        message.setContent(multiPart);
        Transport.send(message);
    }
    //public static void main(String [] args) throws MessagingException, IOException {
}
