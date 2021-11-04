// Copyright 2021 by Bill Torpey (wallstprog@gmail.com). All Rights Reserved.
// This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 United States License.
// http://creativecommons.org/licenses/by-nc-nd/3.0/us/deed.en

import org.HdrHistogram.*;                   // http://hdrhistogram.org
import au.com.bytecode.opencsv.CSVReader;    // http://opencsv.sourceforge.net
import java.io.FileReader;


public class CSV2HdrHistogram {

	// defaults
	static int col = 2;	// 1-relative, assume timestamp is in col 1
	static int scaling = 1;
	static char separator = '\t';
	static int headers = 0;
	static String filename;
   // A Histogram covering the range from 1 usec to 1 minute with 3 decimal point resolution:
	static long max = 60000000L;
	static int precision = 3;
	
	// From https://udojava.com/2013/09/28/unescape-a-string-that-contains-standard-java-escape-sequences/
   /**
    * Unescapes a string that contains standard Java escape sequences.
    * <ul>
    * <li><strong>&#92;b &#92;f &#92;n &#92;r &#92;t &#92;" &#92;'</strong> :
    * BS, FF, NL, CR, TAB, double and single quote.</li>
    * <li><strong>&#92;X &#92;XX &#92;XXX</strong> : Octal character
    * specification (0 - 377, 0x00 - 0xFF).</li>
    * <li><strong>&#92;uXXXX</strong> : Hexadecimal based Unicode character.</li>
    * </ul>
    * 
    * @param st
    *            A string optionally containing standard java escape sequences.
    * @return The translated string.
    */
   static public String unescapeJavaString(String st) {

       StringBuilder sb = new StringBuilder(st.length());

       for (int i = 0; i < st.length(); i++) {
           char ch = st.charAt(i);
           if (ch == '\\') {
               char nextChar = (i == st.length() - 1) ? '\\' : st
                       .charAt(i + 1);
               // Octal escape?
               if (nextChar >= '0' && nextChar <= '7') {
                   String code = "" + nextChar;
                   i++;
                   if ((i < st.length() - 1) && st.charAt(i + 1) >= '0'
                           && st.charAt(i + 1) <= '7') {
                       code += st.charAt(i + 1);
                       i++;
                       if ((i < st.length() - 1) && st.charAt(i + 1) >= '0'
                               && st.charAt(i + 1) <= '7') {
                           code += st.charAt(i + 1);
                           i++;
                       }
                   }
                   sb.append((char) Integer.parseInt(code, 8));
                   continue;
               }
               switch (nextChar) {
               case '\\':
                   ch = '\\';
                   break;
               case 'b':
                   ch = '\b';
                   break;
               case 'f':
                   ch = '\f';
                   break;
               case 'n':
                   ch = '\n';
                   break;
               case 'r':
                   ch = '\r';
                   break;
               case 't':
                   ch = '\t';
                   break;
               case '\"':
                   ch = '\"';
                   break;
               case '\'':
                   ch = '\'';
                   break;
               // Hex Unicode: u????
               case 'u':
                   if (i >= st.length() - 5) {
                       ch = 'u';
                       break;
                   }
                   int code = Integer.parseInt(
                           "" + st.charAt(i + 2) + st.charAt(i + 3)
                                   + st.charAt(i + 4) + st.charAt(i + 5), 16);
                   sb.append(Character.toChars(code));
                   i += 5;
                   continue;
               }
               i++;
           }
           sb.append(ch);
       }
       return sb.toString();
   }
	

	public static void parseArgs(String[] args) {
	   for (int i = 0; i < args.length -1; ++i) {
	      if (args[i].equals("-h")) {
	         headers = Integer.parseInt(args[++i]);
	      }
	      else if (args[i].equals("-s")) {
            scaling = Integer.parseInt(args[++i]);
         }
         else if (args[i].equals("-c")) {
            col = Integer.parseInt(args[++i]);
         }
         else if (args[i].equals("-p")) {
            precision = Integer.parseInt(args[++i]);
         }
         else if (args[i].equals("-M")) {
            max = Long.parseLong(args[++i]);
         }
         else if (args[i].equals("-F")) {
            separator = unescapeJavaString(args[++i]).charAt(0);
         }
	   }
	   filename = args[args.length -1];
	}
	
	public static void main(String[] args) {
	   int i = 0;
		try {
		    parseArgs(args);

		    Histogram histogram = new Histogram(max, precision);
		    
		    CSVReader reader = new CSVReader(new FileReader(filename), separator);
		    String [] nextLine;
		    
		    // skip headers
		    for (i = 0; i < headers; ++i) { 
		    	nextLine = reader.readNext(); 
		    }
		    
		    while ((nextLine = reader.readNext()) != null) {
		      ++i; 
		    	double value = Double.parseDouble(nextLine[col-1]);		// CSVReader is 0-relative, we are 1-relative
		    	if (value > max) {
               System.err.printf("Value (%f) is greater than max (%d) at line %d\n", value, max, i);
		         System.exit(1);
		    	}
 		    	histogram.recordValue(Math.round(value * scaling));
		    }
		    reader.close();
	       histogram.outputPercentileDistribution(System.out, 1.0);
		}
		catch(Exception e) {
		    System.err.print("Got exception on input line ");
          System.err.println(i);
	       throw new RuntimeException(e);
		}
	}
}

