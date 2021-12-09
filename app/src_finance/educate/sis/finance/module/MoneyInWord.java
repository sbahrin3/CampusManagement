package educate.sis.finance.module;

public class MoneyInWord {

	private static final String[] majorNamesEN = { "", " thousand", " million", 
	" billion" };

	private static final String[] tensNamesEN = { "", " ten", " twenty", 
		" thirty", " fourty", " fifty", " sixty", " seventy", " eighty", 
	" ninety" };

	private static final String[] numNamesEN = { "", " one", " two", " three", 
		" four", " five", " six", " seven", " eight", " nine", " ten", 
		" eleven", " twelve", " thirteen", " fourteen", " fifteen", 
		" sixteen", " seventeen", " eighteen", " nineteen" };

	private static final String[] majorNamesBM = { "", " ribu", " juta", 
	" laksa" };

	private static final String[] tensNamesBM = { "", " sepuluh", " dua puluh", 
		" tiga puluh", " empat puluh", " lima puluh", " enam puluh", 
		" tujuh puluh", " lapan puluh", " sembilan puluh" };

	private static final String[] numNamesBM = { "", " satu", " dua", " tiga", 
		" empat", " lima", " enam", " tujuh", " lapan", " sembilan", 
		" sepuluh", " sebelas", " dua belas", " tiga belas", 
		" empat belas", " lima belas", " enam belas", " tujuh belas", 
		" lapan belas", " sembilan belas" };
	private static final int EN = 0;
	private static final int BM = 1;
	private int langType = 0;

	public MoneyInWord(int i) {
		this.langType = i;
	}

	public MoneyInWord() {
		this.langType = 0;
	}

	private String numNamesCase(String strWord, int number, int factor) {
		switch (this.langType) {
		case 0:
			strWord = numNamesEN[(number % factor)];
		case 1:
			strWord = numNamesBM[(number % factor)];
		}
		return strWord;
	}

	private String numNamesCase(String strWord, int number) {
		switch (this.langType) {
		case 0:
			strWord = numNamesEN[number] + " hundred";
		case 1:
			strWord = numNamesBM[number] + " ratus";
		}
		return strWord;
	}

	private String tensNamesCase(String strWord, int number, int factor) {
		switch (this.langType) {
		case 0:
			strWord = tensNamesEN[(number % factor)];
		case 1:
			strWord = tensNamesBM[(number % factor)];
		}
		return strWord;
	}

	private String majorNamesCase(String strWord, int p) {
		switch (this.langType) {
		case 0:
			strWord = majorNamesEN[p];
		case 1:
			strWord = majorNamesBM[p];
		}
		return strWord;
	}

	private String convertLessThanOneThousand(int number) {
		String strWord = "";
		if (number % 100 < 20) {
			strWord = numNamesCase(strWord, number, 100);
			number /= 100;
		} else {
			strWord = numNamesCase(strWord, number, 10);
			number /= 10;
			strWord = tensNamesCase(strWord, number, 10) + strWord;
			number /= 10;
		}


		if (number == 0)
			return strWord;

		return numNamesCase(strWord, number) + strWord;
	}

	public String convert(double d) {
		String s = Double.toString(d);
		String s1 = s.substring(0, s.indexOf("."));
		String s2 = s.substring(s.indexOf(".")+1);


		int n1 = Integer.parseInt(s1);
		int n2 = Integer.parseInt(s2);
		
		if ( s2.length() == 1 ) n2 = n2 * 10;
		
		

		if ( n2 > 0 ) {
			return convert(n1) + (langType == 0 ? " dan " : " and ") + convert(n2);
		}
		else
			return convert(n1);
	}

	public String convert(int number) {
		if (number == 0) {
			return "zero";
		}

		String prefix = "";

		if (number < 0) {
			number = -number;
			prefix = "negative";
		}

		String strWord = "";
		int place = 0;
		do
		{
			int n = number % 1000;
			if (n != 0) {
				String s = convertLessThanOneThousand(n);
				strWord = s + majorNamesCase(strWord, place) + strWord;
			}
			place++;
			number /= 1000;
		}while (number > 0);

		return (prefix + strWord).trim();
	}

	public String toWord(double d) {
		String word = convert(d);
		String s = Double.toString(d);
		String s2 = s.substring(s.indexOf(".")+1);
		int sen = Integer.parseInt(s2);
		if ( sen > 0 ) {
			return "RINGGIT MALAYSIA: " + word + " SEN SAHAJA";
		}
		else {
			return "RINGGIT MALAYSIA: " + word + " SAHAJA";
		}
	}

	public static void main(String[] args) {
		MoneyInWord f = new MoneyInWord();
		
		System.out.println(f.convert(100.04));

		/*
		System.out.println(f.convert(0));
		System.out.println(f.convert(1));
		System.out.println(f.convert(16));
		System.out.println(f.convert(100));
		System.out.println(f.convert(118));
		System.out.println(f.convert(200));
		System.out.println(f.convert(219));
		System.out.println(f.convert(800));
		System.out.println(f.convert(801));
		System.out.println(f.convert(1316));
		System.out.println(f.convert(1000000));
		System.out.println(f.convert(2000000));
		System.out.println(f.convert(3000200));
		System.out.println(f.convert(700000));
		System.out.println(f.convert(9000000));
		System.out.println(f.convert(123456789));
		System.out.println(f.convert(-45));

		System.out.println(f.convert(3463));
		
		*/
		//System.out.println(f.convert(34));
		//System.out.println(f.convert(563.34));
	}
}
