package demo;

import java.math.BigDecimal;

public class MathRoundTest {
	
	public static void main(String[] args) throws Exception {
		
		BigDecimal initialValue = new BigDecimal(65.9);
		double rounded = initialValue.setScale(0,BigDecimal.ROUND_DOWN).doubleValue();
		System.out.println("rounded = " + rounded ); 
		
		initialValue = new BigDecimal(65.5);
		rounded = initialValue.setScale(0,BigDecimal.ROUND_HALF_UP).doubleValue();
		System.out.println("rounded = " + rounded ); 
		
	}

}
