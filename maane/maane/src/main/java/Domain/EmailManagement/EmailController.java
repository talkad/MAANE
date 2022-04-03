package Domain.EmailManagement;


import Domain.CommonClasses.Response;
import Domain.DataManagement.DataController;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.List;
import java.util.Properties;


public class EmailController {
    final private Properties prop = new Properties();
    private DataController dataController;

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

    public Response<Boolean> sendEmail(String workField, String surveyLink, String surveyToken) throws MessagingException {
        Response<List<String>> emailsToRes = dataController.getCoordinatorsEmails(workField);
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
            message.setFrom(new InternetAddress("no-reply@gmail.com"));
            message.setSubject("Sending Mail with pure Java Mail API ");

            /* Mail body with plain Text */
            message.setText("Hello User,"
                    + "\n\n If you read this, means mail sent with Java Mail API is successful");

            InternetAddress[] toEmailAddresses =
                    InternetAddress.parse(emailsTo);
/*            InternetAddress[] ccEmailAddresses =
                    InternetAddress.parse("user21@gmail.com, user22@gmail.com");
            InternetAddress[] bccEmailAddresses =
                    InternetAddress.parse("user31@gmail.com");*/

            message.setRecipients(Message.RecipientType.TO, toEmailAddresses);
            //message.setRecipients(Message.RecipientType.CC,ccEmailAddresses);
            //message.setRecipients(Message.RecipientType.BCC,bccEmailAddresses);

            /* Step 1: Create MimeBodyPart and set content and its Mime Type */
            BodyPart mimeBody = new MimeBodyPart();
            mimeBody.setContent("<h1> This is HTML content </h1><br> <b> User </b>" + " " + surveyLink + " survey token: " + surveyToken, "text/html");//todo make a better msg

            /* Step 2: Create MimeMultipart and  wrap the mimebody to it */
            Multipart multiPart = new MimeMultipart();
            multiPart.addBodyPart(mimeBody);

            /* Step 3: set the multipart content to Message in caller method*/
            message.setContent(multiPart);
            Transport.send(message);
            return new Response<>(true, false, "successfully sent emails");
        }
        return new Response<>(false, true, "failed to send emails");
    }
}
