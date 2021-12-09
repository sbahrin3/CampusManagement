package educate.sis.module;

public class Pct {
	
	public double calc(double x, double t) {
		return (x/t)*100;
	}
	
	public double calc(int x, int t) {
		return ((double)x/(double)t)*100;
	}
	
	public static void main(String[] args) throws Exception {
		System.out.println(new Pct().calc(3,9));
	}

}
