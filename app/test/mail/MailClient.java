package mail;

import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class MailClient
{


    public void sendMail(String mailServer, String from, String to,
                            String subject, String messageBody,
                            String[] attachments) throws MessagingException, AddressException
    {
        // Setup mail server
        Properties props = System.getProperties();
        props.put("mail.smtp.host", mailServer);
        
        // Get a mail session
        Session session = Session.getDefaultInstance(props, null);
        
        // Define a new mail message
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
        message.setSubject(subject);
        
        // Create a message part to represent the body text
        BodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setText(messageBody);
        
        //use a MimeMultipart as we need to handle the file attachments
        Multipart multipart = new MimeMultipart();
        
        //add the message body to the mime message
        multipart.addBodyPart(messageBodyPart);
        
        // add any file attachments to the message
        if ( attachments != null ) addAtachments(attachments, multipart);
        
        // Put all message parts in the message
        message.setContent(multipart);
        
        // Send the message
        Transport.send(message);


    }

    protected void addAtachments(String[] attachments, Multipart multipart)
                    throws MessagingException, AddressException
    {
        for(int i = 0; i<= attachments.length -1; i++)
        {
            String filename = attachments[i];
            MimeBodyPart attachmentBodyPart = new MimeBodyPart();
            
            //use a JAF FileDataSource as it does MIME type detection
            DataSource source = new FileDataSource(filename);
            attachmentBodyPart.setDataHandler(new DataHandler(source));
            
            //assume that the filename you want to send is the same as the
            //actual file name - could alter this to remove the file path
            attachmentBodyPart.setFileName(filename);
            
            //add the attachment
            multipart.addBodyPart(attachmentBodyPart);
        }
    }

    public static void main(String[] args)
    {
        try
        {
            MailClient client = new MailClient();
            String server="10.0.0.166";
            String from="myname@mydomain.com";
            String to = "someuser@somewhere.com";
            String subject="Test";
            String message="Testing";
            String[] filenames = null;// {"c:\\somefile.txt"};
        
            client.sendMail(server,from,to,subject,message,filenames);
        }
        catch(Exception e)
        {
            e.printStackTrace(System.out);
        }
        
    }
} 