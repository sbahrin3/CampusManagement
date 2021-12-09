package u.sams;

import java.util.Random;

public class IC {
	
	public static void main(String[] args) throws Exception {
		
		String icno = "650629086011";
		String ic = getICNO(icno);
		System.out.println(ic);
	}

	private static String getICNO(String icno) {
		String y = icno.substring(0,2);
		String m = icno.substring(2,4);
		String d = icno.substring(4,6);
		String p = icno.substring(6,8);
		String g = icno.substring(11);
		
		int xy = Integer.parseInt(y);
		int xm = Integer.parseInt(m);
		int xd = Integer.parseInt(d);
		
		xy = xy - 4;
		xm = xm < 10 ? xm + 3 : xm > 4 ? xm - 3 : xm;
		xd = xd < 28 ? xd + 3 : xd > 4 ? xd - 3 : xd;
		
		String date = ""+ xy + ((xm < 10 ) ? "0" + xm : xm) + ((xd < 10 ) ? "0" + xd : xd);
		String ic = date + p;
		
		int[] t = createRandom(9,3);
		for ( int k : t) ic += k;
		ic = ic + g;
		return ic;
	}
	
	public static int[] createRandom(int high, int count) {
		//make sure count NEVER exceeds high
		if ( count > high ) count = high;
		int[] numbers = new int[count];
		Random random = new Random();
		for ( int k=0; k < count; k++ ) {
			boolean looping = true;
			int questionNo = 0;
			while ( isDuplicate(questionNo, numbers, k) ) questionNo = random.nextInt(high) + 1;
			numbers[k] = questionNo;
		}
		return numbers;
	}
	
	static boolean isDuplicate(int num, int[] numbers, int position) {
		boolean b = false;
		for (int i=position; i > -1; i-- ) {
			if ( num == numbers[i]) {
				b = true;
				break;
			}
		}
		return b;
	}

}
