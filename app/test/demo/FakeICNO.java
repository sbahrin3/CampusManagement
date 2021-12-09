package demo;

import java.util.List;
import java.util.Random;

import educate.enrollment.entity.Student;
import lebah.template2.DbPersistence;

public class FakeICNO {
	
	
	public static void main(String[] args) throws Exception {
		
		DbPersistence db = new DbPersistence();
		
		List<Student> students = db.list("select a from Student a");
		System.out.println(students.size());
		db.begin();
		for ( Student s : students ) {
				String icno = "";
				int[] numbers = getRandomNumbers(9, 4);
				for ( int i=0; i<numbers.length;i++ ) {
					icno += numbers[i];
				}
				icno = icno + icno;
				s.getBiodata().setIcno(icno);
				System.out.println(icno);
				
			
		}
		db.commit();
		
	}
	
	private static int getRandom(int max) {
		int[] i = getRandomNumbers(max, 5);
		return i[0] - 1;
	}
	
	private static int[] getRandomNumbers(int high, int count) {
		//make sure count NEVER exceeds high
		if ( count > high ) count = high;
		int[] numbers = new int[count];
		Random random = new Random();
		for ( int k=0; k < count; k++ ) {
			boolean looping = true;
			int num = 0;
			while ( isDuplicate(num, numbers, k) ) num = random.nextInt(high) + 1;
			numbers[k] = num;
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
