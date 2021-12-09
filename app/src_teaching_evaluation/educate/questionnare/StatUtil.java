package educate.questionnare;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class StatUtil {
	
	
    public static double sum(List<Double> a){
        if (a.size() > 0) {
            double sum = 0;

            for (Double i : a) {
                sum += i;
            }
            return sum;
        }
        return 0;
    }
	
    public static double mean(List<Double> a){
    	if ( a.size() == 0 ) return 0;
        double sum = sum(a);
        double mean = 0;
        mean = sum / (a.size() * 1.0);
        return round2(mean);
    }
    
    public static double median(List<Double> a){
    	if ( a.size() == 0 ) return 0;
        int middle = a.size()/2;
        if (a.size() % 2 == 1) {
            return round2(a.get(middle));
        } else {
           return round2((a.get(middle-1) + a.get(middle)) / 2.0);
        }
    }  
    
    
    public static double round2(double value) {
    	int places = 2;
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
    
    
    public static double sd(List<Double> a){
    	return getSD(a);
    }
    
    
    public static double sdx(List<Integer> x){
    	
    	List<Double> a = new ArrayList<Double>();
    	for ( Integer d : x ) {
    		a.add(Double.parseDouble(Integer.toString(d)));
    	}
    	
    	if ( a.size() - 1 == 0 ) return 0;
        int sum = 0;
        double mean = mean(a);
 
        for (Double i : a)
            sum += Math.pow((i - mean), 2);
        return round2(Math.sqrt( sum / ( a.size() - 1 ) ));
    }   
    
    public static double getSDI(List<Integer> x) {
    	List<Double> a = new ArrayList<Double>();
    	for ( Integer d : x ) {
    		a.add(Double.parseDouble(Integer.toString(d)));
    	}
    	return getSD(a);
    }
    
    public static double getSD(List<Double> ns) {
    	double numbers[] = new double[ns.size()];
    	int i = 0;
    	for ( double n : ns ) {
    		numbers[i] = n;
    		i++;
    	}
    	return getSDX(numbers);
    }
    
    
    public static double getSDX(double numbers[]) {
    	
    	double average = 0;
    	double total = 0;
    	for (int i=0; i<numbers.length;i++) {
    		total += numbers[i];
    	}
    	average = total/numbers.length;
    	
        double sd = 0;
        for (int i=0; i<numbers.length;i++) {
        	sd += ((numbers[i] - average)*(numbers[i] - average)) / (numbers.length - 1);
        }
        
        return Math.sqrt(sd);
        
        
    }
 
    public static void main(String[] args) {
    	
    	List<Integer> n = new ArrayList<Integer>();
    	n.add(3);
    	n.add(4);
    	n.add(5);
    	n.add(4);
    	n.add(3);
    	n.add(3);
    	n.add(3);
    	n.add(4);
    	n.add(5);
    	n.add(4);
    	n.add(4);
    	n.add(4);
    	n.add(3);
    	n.add(4);
    	n.add(5);
    	n.add(4);
    	n.add(4);
    	n.add(5);
    	n.add(3);
    	n.add(4);
    	n.add(4);
    	n.add(4);
    	n.add(5);
    	n.add(3);
    	n.add(4);
    	n.add(4);
    	n.add(5);
    	n.add(5);
    	n.add(5);
    	n.add(4);
    	n.add(4);
    	n.add(4);
    	n.add(4);
    	n.add(5);
    	n.add(5);
    	n.add(4);
    	n.add(4);
    	n.add(3);
    	n.add(4);
    	
    	double s = getSDI(n);
    	
    	System.out.println("sd = " + s);
    }
   

}
