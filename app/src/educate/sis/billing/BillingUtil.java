/* ************************************************************************
LEBAH PORTAL FRAMEWORK, http://lebah.sf.net
Copyright (C) 2007  Shamsul Bahrin








MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.

* ************************************************************************ */
package educate.sis.billing;

/**
 * @author Shamsul Bahrin Abd Mutalib
 * @version 1.01
 */
public class BillingUtil
{
    private String ringgit;
    private String sen;
    
    public BillingUtil()
    {
    }
    
    public void toWordsBM(String s)
    {
        ringgit = "";
        sen = "";
        String strDollar = "";
        String strCent = "";
        int dotId = s.indexOf('.');
        if (dotId < 0)
        {
            strDollar = s;
            strCent = "0";
        } else        
        {
            strDollar = s.substring(0,(dotId));
            strCent = s.substring(dotId+1, s.length());
        }
        int lenOfDollar = strDollar.length();
        
        // TO DETERMINE IF VALUE IS TENS, HUNDREDS OR THOUSANDS
        if (lenOfDollar == 1)
        {
            ringgit = readDigit(strDollar);
            
        } else if (lenOfDollar == 2)
        {
            ringgit = readTens(strDollar);
            
        } else if (lenOfDollar == 3)
        {
            ringgit = readHundreds(strDollar);
            
        } else if ( (3 < lenOfDollar) && (lenOfDollar <= 6) )
        {
            ringgit = readThousands(strDollar);
            
        } else if ( (6 < lenOfDollar) && (lenOfDollar <= 9) )
        {
            ringgit = readMillions(strDollar);
        }
        if (Integer.parseInt(strCent) != 0 )
        {
            sen = readTens(strCent);
        }
    }
    
    public void toWordsEng(float f) {
    	String s = Float.toString(f);
    	toWordsEng(s);
    }
    
    public void toWordsEng(String s)
    {
        ringgit = "";
        sen = "";
        String strCent = "";
        String strDollar = "";
        int dotId = s.indexOf('.');
        if (dotId < 0)
        {
            strDollar = s;
            strCent = "0";
        } else        
        {
            strDollar = s.substring(0,(dotId));
            strCent = s.substring(dotId+1, s.length());
        }
        int lenOfDollar = strDollar.length();
        
        // TO DETERMINE IF VALUE IS TENS, HUNDREDS OR THOUSANDS
        if (lenOfDollar == 1)
        {
            ringgit = readDigitEng(strDollar);
            
        } else if (lenOfDollar == 2)
        {
            ringgit = readTensEng(strDollar);
            
        } else if (lenOfDollar == 3)
        {
            ringgit = readHundredsEng(strDollar);
            
        } else if ( (3 < lenOfDollar) && (lenOfDollar <= 6) )
        {
            ringgit = readThousandsEng(strDollar);
            
        } else if ( (6 < lenOfDollar) && (lenOfDollar <= 9) )
        {
            ringgit = readMillionsEng(strDollar);
        }
        if (Integer.parseInt(strCent) != 0 )
        {
	        if (strCent.length() == 1) strCent += "0";
            sen = readTensEng(strCent);
        }
    }
        
    public String getRinggit()
    {
        return ringgit;
    }
    
    public String getSen()
    {
        return sen;
    }
    
    private String readTens(String s)
    {
        String ringgit = "";
        int valueOfTens = Integer.parseInt(s);
        if  ( valueOfTens != 0 )
        {
            if ( valueOfTens == 10 )
            {
                ringgit = "sepuluh";
                
            } else
            {           
                // READ 1ST DIGIT
                String firstDigit = s.substring(0,1);
                int digit = Integer.parseInt(firstDigit);
                if (digit > 1)
                {
                    ringgit = readDigit(firstDigit);
                }
                
                // READ 2ND DIGIT        
                String secondDigit = s.substring(1,2);
                digit = Integer.parseInt(secondDigit);          
                if ( (valueOfTens > 10) && (valueOfTens < 20) )
                {
                    if (digit == 1)
                    {
                        ringgit = "sebelas";
                    } else
                    {
                        ringgit = readDigit(secondDigit) + " belas";
                    }
                } else
                {
                    if (Integer.parseInt(firstDigit) != 0)
                    {
                        ringgit = ringgit + " puluh " + readDigit(secondDigit);
                    } else {
                        ringgit = ringgit + readDigit(secondDigit);
                    }
                }
            }
        }
        return ringgit;
    }
    
    private String readTensEng(String s)
    {
	    
        String ringgit = "";
        int valueOfTens = Integer.parseInt(s);
        if  ( valueOfTens != 0 )
        {
            if ( valueOfTens == 10 )
            {
                ringgit = "ten";
                
            } else
            {           
                // READ 1ST DIGIT
                String firstDigit = s.substring(0,1);
                int digit = Integer.parseInt(firstDigit);
                if (digit > 1)
                {
                    ringgit = readTenthDigitEng(firstDigit);
                }
                
                // READ 2ND DIGIT        
                String secondDigit = s.substring(1,2);
                digit = Integer.parseInt(secondDigit);          
                if ( (valueOfTens > 10) && (valueOfTens < 20) )
                {
                    switch(digit)
                    {
                        case 1: ringgit = "eleven"; break;
                        case 2: ringgit = "twelve"; break;
                        case 3: ringgit = "thirteen"; break;
                        case 4: ringgit = "fourteen"; break;
                        case 5: ringgit = "fifteen"; break;
                        case 6: ringgit = "sixteen"; break;
                        case 7: ringgit = "seventeen"; break;
                        case 8: ringgit = "eighteen"; break;
                        case 9: ringgit = "nineteen"; break;
                    }
                } else
                {
                    ringgit = ringgit + " " + readDigitEng(secondDigit);
                }
            }
        }
        return ringgit;
    }

    private String readHundreds(String s)
    {
        String ringgit = "";
        
        if (Integer.parseInt(s) != 0)
        {
            // READ 1ST DIGIT
            String firstDigit = s.substring(0,1);
            //System.out.println("hundreath = "+firstDigit);
            if (Integer.parseInt(firstDigit) != 0 )
            {
                ringgit = readDigit(firstDigit) + " ratus";
            }
            
            // READ NEXT 2 DIGITS
            String tens = s.substring(1,3);
            ringgit = ringgit + " " + readTens(tens);
        }
        return ringgit;
    }
        
    private String readHundredsEng(String s)
    {
        String ringgit = "";
        
        if (Integer.parseInt(s) != 0)
        {
            // READ 1ST DIGIT
            String firstDigit = s.substring(0,1);
            //System.out.println("hundreath = "+firstDigit);
            if (Integer.parseInt(firstDigit) != 0 )
            {
                ringgit = readDigitEng(firstDigit) + " Hundred";
            }
            
            // READ NEXT 2 DIGITS
            String tens = s.substring(1,3);
            if (Integer.parseInt(tens) != 0)
            {
                //ringgit = ringgit + " and " + readTensEng(tens);
                ringgit = ringgit + " " + readTensEng(tens);
            }
        }
        return ringgit;
    }
    
    private String readThousands(String s)
    {
        String ringgit = "";
        int lenOfString = s.length();
        
        // READ THE FIRST 3 DIGITS
        String digits = s.substring(0,(lenOfString - 3));
        if (digits.length() == 1)
        {           
            ringgit = readDigit(digits) + " ribu ";
            
        } else if (digits.length() == 2)
        {
                ringgit = readTens(digits) + " ribu ";
            
        } else if (digits.length() == 3)
        {
                ringgit = readHundreds(digits) + " ribu ";
        }
        
        // READ LAST 3 DIGITS
        digits = s.substring((lenOfString - 3),lenOfString);
        if (Integer.parseInt(digits) != 0)
        {
            ringgit = ringgit + readHundreds(digits);
        }
        
        return ringgit;
    }
    
    private String readThousandsEng(String s)
    {
        String ringgit = "";
        int lenOfString = s.length();
        
        // READ THE FIRST 3 DIGITS
        String digits = s.substring(0,(lenOfString - 3));
        if (digits.length() == 1)
        {           
            ringgit = readDigitEng(digits) + " Thousand ";
            
        } else if (digits.length() == 2)
        {
                ringgit = readTensEng(digits) + " Thousand ";
            
        } else if (digits.length() == 3)
        {
                ringgit = readHundredsEng(digits) + " Thousand ";
        }
        
        // READ LAST 3 DIGITS
        digits = s.substring((lenOfString - 3),lenOfString);
        if (Integer.parseInt(digits) != 0)
        {
            ringgit = ringgit + readHundredsEng(digits);
        }
        
        return ringgit;
    }
    
    private String readMillions(String s)
    {
        String ringgit = "";
        int lenOfString = s.length();
        // READ THE FIRST 3 DIGITS
        String digits = s.substring(0,(lenOfString - 6));
        if (digits.length() == 1)
        {           
            ringgit = readDigit(digits) + " juta ";
            
        } else if (digits.length() == 2)
        {
                ringgit = readTens(digits) + " juta ";
            
        } else if (digits.length() == 3)
        {
                ringgit = readHundreds(digits) + " juta ";
        }
        
        // READ THE MIDDLE 3 DIGITS
        digits = s.substring((lenOfString - 6),(lenOfString - 3));
        if (Integer.parseInt(digits) != 0)
        {        
            if (digits.length() == 1)
            {           
                ringgit = ringgit + readDigit(digits) + " ribu ";
                
            } else if (digits.length() == 2)
            {
                    ringgit = ringgit + readTens(digits) + " ribu ";
                
            } else if (digits.length() == 3)
            {
                    ringgit = ringgit + readHundreds(digits) + " ribu ";
            }
        }
        
        // READ LAST 3 DIGITS
        digits = s.substring((lenOfString - 3),lenOfString);
        if (Integer.parseInt(digits) != 0)
        {
            ringgit = ringgit + readHundreds(digits);
        }
        
        return ringgit;
    }
    
    private String readMillionsEng(String s)
    {
        String ringgit = "";
        int lenOfString = s.length();
        // READ THE FIRST 3 DIGITS
        String digits = s.substring(0,(lenOfString - 6));
        if (digits.length() == 1)
        {           
            ringgit = readDigitEng(digits) + " million ";
            
        } else if (digits.length() == 2)
        {
                ringgit = readTensEng(digits) + " million ";
            
        } else if (digits.length() == 3)
        {
                ringgit = readHundredsEng(digits) + " million ";
        }
        
        // READ THE MIDDLE 3 DIGITS
        digits = s.substring((lenOfString - 6),(lenOfString - 3));
        if (Integer.parseInt(digits) != 0)
        {        
            if (digits.length() == 1)
            {           
                ringgit = ringgit + readDigitEng(digits) + " thousand ";
                
            } else if (digits.length() == 2)
            {
                    ringgit = ringgit + readTensEng(digits) + " thousand ";
                
            } else if (digits.length() == 3)
            {
                    ringgit = ringgit + readHundredsEng(digits) + " thousand ";
            }
        }
        
        // READ LAST 3 DIGITS
        digits = s.substring((lenOfString - 3),lenOfString);
        if (Integer.parseInt(digits) != 0)
        {
            ringgit = ringgit + readHundredsEng(digits);
        }
        
        return ringgit;
    }
    
    private String readDigit(String s)
    {
        String strDigit = "";
        switch(Integer.parseInt(s))
        {
            case 0: strDigit = ""; break;
            case 1: strDigit = "satu"; break;
            case 2: strDigit = "dua"; break;
            case 3: strDigit = "tiga"; break;
            case 4: strDigit = "empat"; break;
            case 5: strDigit = "lima"; break;
            case 6: strDigit = "enam"; break;
            case 7: strDigit = "tujuh"; break;
            case 8: strDigit = "lapan"; break;
            case 9: strDigit = "sembilan"; break;
        }
        return strDigit;
    }
    
    private String readDigitEng(String s)
    {
        String strDigit = "";
        switch(Integer.parseInt(s))
        {
            case 0: strDigit = ""; break;
            case 1: strDigit = "One"; break;
            case 2: strDigit = "Two"; break;
            case 3: strDigit = "Three"; break;
            case 4: strDigit = "Four"; break;
            case 5: strDigit = "Five"; break;
            case 6: strDigit = "Six"; break;
            case 7: strDigit = "Seven"; break;
            case 8: strDigit = "Eight"; break;
            case 9: strDigit = "Nine"; break;
        }
        return strDigit;
    }
    
    private String readTenthDigitEng(String s)
    {
        String strDigit = "";
        switch(Integer.parseInt(s))
        {
            case 0: strDigit = ""; break;
            case 1: strDigit = "Ten"; break;
            case 2: strDigit = "Twenty"; break;
            case 3: strDigit = "Thirty"; break;
            case 4: strDigit = "Forty"; break;
            case 5: strDigit = "Fifty"; break;
            case 6: strDigit = "Sixty"; break;
            case 7: strDigit = "Seventy"; break;
            case 8: strDigit = "Eighty"; break;
            case 9: strDigit = "Ninety"; break;
        }
        return strDigit;
    }
}
