package mail;
// This example is from _Java Examples in a Nutshell_. (http://www.oreilly.com)
// Copyright (c) 1997 by David Flanagan
// This example is provided WITHOUT ANY WARRANTY either expressed or implied.
// You may study, use, modify, and distribute it for non-commercial purposes.
// For any commercial use, see http://www.davidflanagan.com/javaexamples

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;

/**
 * This program sends e-mail using a mailto: URL
 **/
public class SendMail {
  public static void main(String[] args) {
    try {
      // If the user specified a mailhost, tell the system about it.
      //if (args.length >= 1) System.getProperties().put("mail.host", args[0]);
    	
      System.getProperties().put("mail.host", "127.0.0.1");
      
      // A Reader stream to read from the console
      BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

      // Ask the user for the from, to, and subject lines
      System.out.print("From: ");
      String from = in.readLine();
      System.out.print("To: ");
      String to = in.readLine();
      System.out.print("Subject: ");
      String subject = in.readLine();

      // Establish a network connection for sending mail
      URL u = new URL("mailto:" + to);      // Create a mailto: URL 
      URLConnection c = u.openConnection(); // Create a URLConnection for it
      c.setDoInput(false);                  // Specify no input from this URL
      c.setDoOutput(true);                  // Specify we'll do output
      System.out.println("Connecting...");  // Tell the user what's happening
      System.out.flush();                   // Tell them right now
      c.connect();                          // Connect to mail host
      PrintWriter out =                     // Get output stream to mail host
        new PrintWriter(new OutputStreamWriter(c.getOutputStream()));

      // Write out mail headers.  Don't let users fake the From address
      out.println("From: \"" + from + "\" <" +
                  System.getProperty("user.name") + "@" + 
                  InetAddress.getLocalHost().getHostName() + ">");
      out.println("To: " + to);
      out.println("Subject: " + subject);
      out.println();  // blank line to end the list of headers

      // Now ask the user to enter the body of the message
      System.out.println("Enter the message. " + 
                         "End with a '.' on a line by itself.");
      // Read message line by line and send it out.
      String line;
      for(;;) {
        line = in.readLine();
        if ((line == null) || line.equals(".")) break;
        out.println(line);
      }

      // Close the stream to terminate the message 
      out.close();
      // Tell the user it was successfully sent.
      System.out.println("Message sent.");
      System.out.flush();
    }
    catch (Exception e) {  // Handle any exceptions, print error message.
      System.err.println(e);
      System.err.println("Usage: java SendMail [<mailhost>]");
    }
  }
}
