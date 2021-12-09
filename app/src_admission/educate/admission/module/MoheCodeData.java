package educate.admission.module;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.ServletContext;

public class MoheCodeData {
	
	private ServletContext servletContext;
	
	public MoheCodeData(ServletContext sc) {
		this.servletContext = sc;
	}
	
	public void saveMoheCodes(String filename, String content) throws Exception {
		String realPath = servletContext.getRealPath("").replace("\\","/");
		File file = new File(realPath + "/kodMOHE/" + filename);
		if (!file.exists()) {
			file.createNewFile();
		}
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(content);
		bw.close();

	}
	
	public List<MoheCode> getMoheCodes(String filename) throws Exception {
		String realPath = servletContext.getRealPath("").replace("\\","/");
		BufferedReader in = new BufferedReader(new FileReader(realPath + "/kodMOHE/" + filename));
		List<MoheCode> moheCodes = new ArrayList<MoheCode>();
		String line = "";
		StringTokenizer st;
		while((line = in.readLine()) != null) 			{
			st = new StringTokenizer(line, "=");
			int cnt = 0;
			MoheCode code = new MoheCode();
			while (st.hasMoreElements()) {
				cnt++;
				if ( cnt == 1 ) code.setCode(st.nextToken());
				else code.setName(st.nextToken());
			}		
			moheCodes.add(code);
		}
		return moheCodes;
	}

}
