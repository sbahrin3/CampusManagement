package educate.timetabling.entity;

import java.util.Map;

public class Pagination {
    private int paginationNumber = 10;
    public String Param(Map<String, String>  data){
        String param="";
        for (Map.Entry<String, String> item : data.entrySet()) {
            param=param+"&="+item.getKey()+"="+item.getValue();
        }
        return param;
    }
    public int getPaginationNumber(){
        return paginationNumber;
    }
    public void setPaginationNumber(int paginationNumber){
        this.paginationNumber = paginationNumber;
    }
    public int paginationsize(int size){
        if((size % paginationNumber) == 0){
             size = size-1;
        }
        return size;
    }
}
