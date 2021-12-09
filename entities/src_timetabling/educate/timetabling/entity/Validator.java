package educate.timetabling.entity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Validator {
    String message=" ";
    int count =1;
    boolean result = true;
    private String pattern = "yyyy-MM-dd";
    private SimpleDateFormat format = new SimpleDateFormat(pattern);
    
    public void date(String date,String fieldname){
       if(date.equals("")){
           message =message+count+". "+fieldname+ " cannot be empty <br/>";
           result = false;
           increment();
       }
       else{
           try {
               Date temp = format.parse(date);
               Calendar cal = Calendar.getInstance();
               cal.setLenient(false);
               cal.setTime(temp);
           }
           catch (Exception e) {
               message =message+count+". "+fieldname+ " is not a date format <br/>";
               result = false;
               increment();
           }
       }
     
    }
    
    public void string(String str,String fieldname){
         if(str.length() == 0 && str.equalsIgnoreCase("")){
             message =message+count+". "+fieldname+ " is empty <br/>";
             result = false;
             increment();
         }
    }
    private void increment(){
        count++;
    }
    public void integer(String str,String fieldname){
        if(str.length() == 0 && str.equalsIgnoreCase("")){
            message = message+count+". "+fieldname+ " is empty <br/>";
            result = false;
            increment();
        }
        else{
            try{
                int data= Integer.parseInt(str);
             }
             catch(Exception a){
                 message =message+count+". "+fieldname+ " is not an integer <br/>";
                 result = false;
                 increment();
             }
        }  
    }
    public void setResult(boolean result){
        this.result=result;
    }
    public boolean getResult(){
        return this.result;
    }
    public void setMessage(String message,String fieldname,String errorType){
        if(fieldname != null){
        this.message = this.message+count+". "+fieldname+ " is not an "+errorType+" <br/>";
        increment();
        }
        else{
            this.message = this.message+count+". "+message+" <br/>";
        }
    }
    public String getMessage(){
        return this.message;
    }
}
