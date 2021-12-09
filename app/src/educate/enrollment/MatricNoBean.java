package educate.enrollment;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import educate.enrollment.entity.MatricNo;
import lebah.db.PersistenceManager;

public class MatricNoBean {
	private PersistenceManager pm;
	private MatricNo matric;
	public static final String PREFIX_YY="{YY}";
	public static final String PREFIX_YYYY ="{YYYY}";
	public static final String PREFIX_MM ="{MM}";
	public static final String PREFIX_M ="{M}";
	
	public MatricNo getMatricDetail() throws Exception{
		pm = new PersistenceManager();
		matric = (MatricNo)pm.find(MatricNo.class,"1");
		if(matric == null){
			matric = new MatricNo();
			matric.setId("1");
			matric.setPrefix("{YY}");
			matric.setSize(5);
			matric.setCurrent(0);
			PersistenceManager.add(matric);
		}
		return matric;
	}
	
	public void update(MatricNo a)throws Exception{
		pm = new PersistenceManager();
		matric =(MatricNo)pm.find(MatricNo.class).whereId("1").forUpdate();
		matric.setPrefix(a.getPrefix());
		matric.setSize(a.getSize());
		pm.update();
	}
	
	public String genMatricNo() throws Exception{
		pm = new PersistenceManager();
		char[] cf = {'0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0',};
		
		matric = (MatricNo)pm.find(MatricNo.class,"1");
		int size = matric.getSize();
		String prefix = matric.getPrefix();
		long currentNo = matric.getCurrent();
		if(prefix.equals("{YY}")){
			String x = getPrefix(prefix);
			prefix = replace(prefix, "{YY}", x);
		}
		else if(prefix.equals("{YYYY}")){
			String x = getPrefix(prefix);
			prefix = replace(prefix, "{YYYY}", x);
		}
		else if(prefix.equals("{MM}")){
			
			 String x = getPrefix(prefix);
			 prefix = replace(prefix, "{M}", x);
		}
		else if ( prefix.equals("{M}")) {
			String x= getPrefix(prefix);
			prefix = replace(prefix, "{M}", x);
		}
		long newNo = ++currentNo;
		String strNo = prefix + new java.text.DecimalFormat(new String(cf, 0, size)).format(newNo);
		
		pm = new PersistenceManager();
		matric = (MatricNo)pm.find(MatricNo.class).whereId("1").forUpdate();
		matric.setCurrent(newNo);
		pm.update();
		
		return strNo;
	}
	private String getPrefix(String fix ){
		Format formatter;
		Date date = new Date();
		if(fix.equals(PREFIX_YY)){
			formatter = new SimpleDateFormat("yy");
			return formatter.format(date);
			
		}
		else if(fix.equals(PREFIX_YYYY)){
			 formatter = new SimpleDateFormat("yyyy");
			 return formatter.format(date);
			
		}
		else if(fix.equals(PREFIX_MM)){
			 formatter = new SimpleDateFormat("MM");
			 return formatter.format(date);	
		}
		else {
			formatter = new SimpleDateFormat("M");
			return formatter.format(date);	
			
		}
		
	}
	private  String replace(String str, String pattern, String replace) {
        int s = 0;
        int e = 0;
        StringBuffer result = new StringBuffer();
    
        while ((e = str.indexOf(pattern, s)) >= 0) {
            result.append(str.substring(s, e));
            result.append(replace);
            s = e+pattern.length();
        }
        result.append(str.substring(s));
        return result.toString();
    }
}
