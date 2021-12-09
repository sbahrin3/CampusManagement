package test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Parser {
	private File file;
	public void setFile(File f) {
		file = f;
	}
	public File getFile() {
		return file;
	}
	public String getContent() throws IOException {
		InputStream i = new FileInputStream(file);
		String output = "";
		int data; 
		while ((data = i.read()) > 0) {
			output += (char) data;
		}
		return output;
	}
	public String getContentWithoutUnicode() throws IOException {
		InputStream i = new FileInputStream(file);
		String output = "";
		int data;
		while ((data = i.read()) > 0) {
			if (data < 0x80) {
				output += (char) data;
			}
		}
		return output;
	}
	public void saveContent(String content) throws IOException {
		OutputStream o = new FileOutputStream(file);
		for (int i = 0; i < content.length(); i += 1) {
			o.write(content.charAt(i));
		}
	}
	
	public static void main(String[] args) {
		
		Parser p = new Parser();
		p.setFile(new File("c:/MyProjects/AeU/test.txt"));
		try {
			String content = p.getContentWithoutUnicode();
			System.out.println(content);
			p.saveContent(content + "ok");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}