package educate.sis.finance.module;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import educate.db.DbPersistence;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class SponsorInvoiceContentModule extends LebahModule {
	
	private DbPersistence db = new DbPersistence();
	private String path = "apps/sponsor_invoice_content/";
	
	public void preProcess() {
		System.out.println(command);
	}

	@Override
	public String start() {
		readFile();
		return path + "start.vm";
	}
	
	
	private void readFile() {
		try {
			String realPath = getServletContext().getRealPath("").replace("\\","/");
			BufferedReader in = new BufferedReader(new FileReader(realPath + "/apps/sponsor_invoice_content/letter_content/content.html"));
			String content = "";
			String line;
			while((line = in.readLine()) != null) 			{
				content += line;
			}
			context.put("content", content);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Command("saveContent")
	public String saveContent() throws Exception {
		String content = getParam("message");
		
		String realPath = getServletContext().getRealPath("").replace("\\","/");
		File file = new File(realPath + "/apps/sponsor_invoice_content/letter_content/content.html");
		if (!file.exists()) {
			file.createNewFile();
		}
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(content);
		bw.close();
		
		return path + "/saveContent.vm";
	}

}



