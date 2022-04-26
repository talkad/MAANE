package Domain.EmailManagement;


import Domain.CommonClasses.Response;
import Domain.DataManagement.DataController;
import Domain.UsersManagment.UserController;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.List;
import java.util.Properties;


public class EmailController {
    final private Properties prop = new Properties();
    private final DataController dataController;

    private EmailController(){
        prop.put("mail.smtp.username", "invalidinvalid9@gmail.com");//todo create proper email and password
        prop.put("mail.smtp.password", "");
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true"); // TLS
        dataController = DataController.getInstance();
    }

    private static class CreateSafeThreadSingleton {
        private static final EmailController INSTANCE = new EmailController();
    }

    public static EmailController getInstance() {
        return CreateSafeThreadSingleton.INSTANCE;
    }

    public Response<Boolean> sendEmail(String workField, String surveyLink, String surveyToken)  {
        Response<List<String>> emailsToRes = UserController.getInstance().getCoordinatorsEmails(workField);
        if (!emailsToRes.isFailure()) {
            String emailsTo = String.join(", ", emailsToRes.getResult());
            Session mailSession = Session.getInstance(prop, new javax.mail.Authenticator() {
                @Override
                protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(prop.getProperty("mail.smtp.username"),
                            prop.getProperty("mail.smtp.password"));
                }
            });

            javax.mail.Message message = new MimeMessage(mailSession);
            try {
                message.setFrom(new InternetAddress("no-reply@gmail.com"));
            } catch (MessagingException e) {
                e.printStackTrace();
            }
            try {
                message.setSubject("Sending Mail with pure Java Mail API ");
            } catch (MessagingException e) {
                e.printStackTrace();
            }

            /* Mail body with plain Text */
            try {
                message.setText("Hello User,"
                        + "\n\n If you read this, means mail sent with Java Mail API is successful");
            } catch (MessagingException e) {
                e.printStackTrace();
            }

            InternetAddress[] toEmailAddresses =
                    new InternetAddress[0];
            try {
                toEmailAddresses = InternetAddress.parse(emailsTo);
            } catch (AddressException e) {
                e.printStackTrace();
            }
/*            InternetAddress[] ccEmailAddresses =
                    InternetAddress.parse("user21@gmail.com, user22@gmail.com");
            InternetAddress[] bccEmailAddresses =
                    InternetAddress.parse("user31@gmail.com");*/

            try {
                message.setRecipients(Message.RecipientType.TO, toEmailAddresses);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
            //message.setRecipients(Message.RecipientType.CC,ccEmailAddresses);
            //message.setRecipients(Message.RecipientType.BCC,bccEmailAddresses);

            /* Step 1: Create MimeBodyPart and set content and its Mime Type */
            BodyPart mimeBody = new MimeBodyPart();
            try {
                mimeBody.setContent("<h1> This is HTML content </h1><br> <b> User </b>" + " " + surveyLink + " survey token: " + surveyToken, "text/html");//todo make a better msg
            } catch (MessagingException e) {
                e.printStackTrace();
            }

            /* Step 2: Create MimeMultipart and  wrap the mimebody to it */
            Multipart multiPart = new MimeMultipart();
            try {
                multiPart.addBodyPart(mimeBody);
            } catch (MessagingException e) {
                e.printStackTrace();
            }

            /* Step 3: set the multipart content to Message in caller method*/
            try {
                message.setContent(multiPart);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
            try {
                Transport.send(message);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
            return new Response<>(true, false, "successfully sent emails");
        }
        return new Response<>(false, true, "failed to send emails");
    }
}
