package educate.util;

public class DatePartNotValidException extends Exception {
	
	public DatePartNotValidException() {
		super("MyKad number has date part which is not a valid Date.");
	}

}
