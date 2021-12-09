package educate.koha;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class UrlRead {
	
	public static String get(String url) throws Exception {
	    try {
	        String line;
	        URL u = new URL(url);
	        BufferedReader d = new BufferedReader(new InputStreamReader(u.openStream()));
	        line = d.readLine();
//	        while ((line =  d.readLine()) != null) {
//	          System.out.println(line);
//	        }
	        return line;
	      }
	      catch (MalformedURLException e) {
	        System.err.println(e);
	      }
	      catch (IOException e) {
	        System.err.println(e);
	      }
	      return "";
	}
	
	
	public static void main(String[] args) throws Exception {
		String result = get("http://opac/cgi-bin/test/get-md5.pl?value=lipaskudung");
		System.out.println("pwd = " + result);
	}

}
