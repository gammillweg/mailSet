package libPack;
// import System;
// import System.Collections;
// import System.Collections.Generic;
// import System.Linq;
// import System.Text;
// import System.Text.RegularExpressions;
// import System.Diagnostics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


// 110626 Improvements to IntToExcelColumn() and ExcelColumnToInt()
//          Previous code was very inefficient.  Now is fast
//          Does mostly calculations rather than extensive loops.
// 110626 Added addition varieties of columnToExcelColumnAL()
//          (String, int), (String, String) (int, int)

public class ExcelUtils
{
   final int TWOALPHAS = 26;
   final int THREEALPHAS = 676;
   final String stringEmpty = "";

   // Max vlaue for Excel is 16384 (XFD)
   // Max value for LibreOffice is 1024 (AMJ)
   final int MAXINT = 1024;

   //----------------------------------------------------------
   // Input must be greater than 0 and less than or equal to 16384
   // On error returns an empty String
   public String intToExcelColumn (int value)
   {
      // Excel is limited to the integer 16384, XFD
      // LibreOffice Cal seems to be limited to the integer 1024, AMJ
      
      if ((value < 1) || (value > MAXINT))
         return stringEmpty;

      // Discussion on converting from a column to an integer:348

      // Each pass through a set of one letter ("G") is count in the alphabet
      //      Thus (G = 7)
      // Each pass through a set of two letters ("GG") is 26 passes plus the set of 1
      //      Thus (GG = 26 x 7 = 182) + (G = 7) = 189
      // Each pass through a set of three letters ("GGG") is 676 passes plus the set of 2 plus the set of 1
      //      Thus (GGG = 676 x 7 = 4732) + (GG = 182) + (G = 7) = 4921
      // Discussion on converting from an integer to a column
      // The numbers above apply.  If one divides by 676 one gets the value of the third alpha
      // Then the remainder by 26 one gets the value of the second alpha; and what is
      // left over is the final alpha.

      int remainder = value;
      int[] aryInt = new int[3];
      String[] aryStr = {"", "", ""};
      

      if (remainder > THREEALPHAS)
      {
         aryInt[2] = remainder / THREEALPHAS;
         remainder = remainder % THREEALPHAS;
         aryStr[2] = countUpToValue (aryInt[2]);

         aryInt[1] = remainder / TWOALPHAS;
         remainder = remainder % TWOALPHAS;
         aryStr[1] = countUpToValue (aryInt[1]);
      }
      else if (value > TWOALPHAS)
      {
         aryInt[1] = remainder / TWOALPHAS;
         remainder = remainder % TWOALPHAS;
         aryStr[1] = countUpToValue (aryInt[1]);
      }
      aryInt[0] = remainder;
      aryStr[0] = countUpToValue (aryInt[0]);

      String rtn = aryStr[2] + aryStr[1] + aryStr[0];
      return rtn.toUpperCase ();
   }

   //--------------------------------------------------------------
   // Computes the excel column integer
   public int excelColumnToInt (String column)
   {
      // Discussion:
      // Each pass through a set of one letter ("G") is count in the alphabet
      //      Thus (G = 7)
      // Each pass through a set of two letters ("GG") is 26 passes plus the set of 1
      //      Thus (GG = 26 x 7 = 182) + (G = 7) = 189
      // Each pass through a set of three letters ("GGG") is 676 passes plus the set of 2 plus the set of 1
      //      Thus (GGG = 676 x 7 = 4732) + (GG = 182) + (G = 7) = 4921

      try
      {
      String col = column.toLowerCase ();

      // Verify a valid input
      if (col.length () > 3)
         return -1;

      int[] aryInt = new int[3];
      int[] aryCalc = new int[3];
      for (int inx = 0; inx < col.length (); inx++)
      {
         // in C# it is String.substring(startIndex, endIndex)
         // in Java it is String.subString(startIndex, len)
         String subStr = col.substring (inx, inx+1);
         
         aryInt[inx] = countToAlpha(subStr);
         if (aryInt[inx] == 0)
            return -1;

         if (col.length () == 1)
         {
            aryCalc[inx] = aryInt[inx];
            if (aryInt[inx] == 0)
               return -1;
         }
         else if (col.length () == 2)
         {
            switch (inx)
            {
               case 0:
                  aryCalc[inx] = aryInt[inx] * TWOALPHAS;
                  break;
               case 1:
                  aryCalc[inx] = aryInt[inx];
                  break;
            }
         }
         else
         {
            switch (inx)
            {
               case 0:
                  aryCalc[inx] = aryInt[inx] * THREEALPHAS;
                  break;
               case 1:
                  aryCalc[inx] = aryInt[inx] * TWOALPHAS;
                  break;
               case 2:
                  aryCalc[inx] = aryInt[inx];
                  break;
            }
         }
      }

      int rtn = aryCalc[0] + aryCalc[1] + aryCalc[2];
      if (rtn > MAXINT)
         return -1;
      return rtn;
      }
      catch (Exception excp)
      {
         //System.err.print("Error in excelColumnToInt():  ");
         //System.err.println (excp.toString());
         return -1;
      }
   }

   //--------------------------------------------------------------
   // For internal call to verify valid input
   // Expect lower case input
   // Feed values 1 through 26 (a-z) 
   private String countUpToValue (int value)
   {
      if ((value > 0) && (value < 27))
      {
         String str = "a";
         if (value == 1)
            return str;

         for (int inx = 1; inx < value; inx++)
         {
            str = getNext (str);
         }
         return str;
      }
      return "";
   }

   //--------------------------------------------------------------
   // For internal call to verify valid input
   // Expect lower case input
   // Feed only one alpha letter "a" through "z"
   private int countToAlpha (String column)
   {
      if (column.length() == 1)
      {
         String str;
         // MAXINT is NOT Integer.MAX_VALUE... rather maximum number of columns)
         // (I Had a bug when converted from C# to Java... I thought WAS MAX_VALUE
         // and crashed here with the (MAXINT + 1))
         for (int inx = 1; inx < (MAXINT + 1); inx++)
         {
            str = countUpToValue (inx);
            if (column.compareTo (str) == 0)
            {
               return inx;
            }
         }
      }
      return 0;
   }

   //--------------------------------------------------------------
   // Returns an upper case list of letters from String startIndex, int value number of columns
   // Start may be upper or lower and in Excel Column format
   // Value must be greater than 0 and less than or equal to 16384 for Excel (XFD)
   // Value must be greater than 0 and less than or equal to 1024 for LibreOffice Calc (AMJ)
   // On error returns an empty list
   public ArrayList < String > intToExcelColumnsAL (String start, int value)
   {
      // Excel is limited to the integer 1024, AMJ (XFD in Excel)
      if ((value < 1) || (value > MAXINT))
         return new ArrayList < String > ();
      ArrayList < String > tmpAL = new ArrayList < String > (value);

      // Verify the startIndex is a valid Excel column
      int verifyStart = excelColumnToInt (start);
      if (verifyStart == 0)
         return tmpAL;

      String str = start.toLowerCase ();
      tmpAL.add (str);
      //StringBuilder tmp = new StringBuilder();
      for (int inx = 0; inx < value; inx++)
      {
         str = getNext (str);
         tmpAL.add (str);
         int tst = countToAlpha (str);
         //tmp.AppendLine(str);
      }
      // Convert to upper case (can not do so in for loop above due to
      // getNext() working with lower case.
      ArrayList < String > columnAL = new ArrayList < String > (tmpAL.size ());
      for (String column:tmpAL)
      {
         columnAL.add (column.toUpperCase ());
      }

      // Verify that no greater column then the greatest allowed (XFD) was reached
      String last = columnAL.get (columnAL.size () - 1);
      int verifyEnd = excelColumnToInt (last);
      if (verifyEnd == 0)
         return (new ArrayList < String > (0));

      return columnAL;
   }

   // Returns an upper case list of letters from int first column to int count number of columns
   // Input must be greater than 0 and less than or equal to 16384
   // On error returns an empty list
   public ArrayList < String > intToExcelColumnsAL (int first, int count)
   {
      // Example:  first 6, count 9
      // returns F, G, H, I, J, K, L, M, N
      // 1  2  3  4  5  6  7  8  9 10 11 12 13 14 15
      //                1  2  3  4  5  6  7  8  9
      // A, B, C, D, E, F, G, H, I, J, K, L, M, N, O

      // Excel is limited to the integer 16384, XFD
      if ((first < 1) || (first > MAXINT))
         return new ArrayList < String > ();
      if ((count < 1) || (((first + count) - 1) > MAXINT))
         return new ArrayList < String > ();
      ArrayList < String > tmpAL = new ArrayList < String > (count);

      String str = intToExcelColumn (first).toLowerCase ();
      tmpAL.add (str);
      //StringBuilder tmp = new StringBuilder();
      // -1 in that the first column is counted
      for (int inx = first; inx < ((first + count) - 1); inx++)
      {
         str = getNext (str);
         tmpAL.add (str);
         //tmp.AppendLine(str);
      }

      // Convert to upper case (can not do so in for loop above due to
      // getNext() working with lower case.
      ArrayList < String > columnAL = new ArrayList < String > (tmpAL.size ());
      for (String column:tmpAL)
      {
         columnAL.add (column.toUpperCase ());
      }

      return columnAL;
   }

   //=======================================================================
   // Reurns a List of count columns inclusive of the startIndex
   // From beinging column a number of columns
   // On error returns an empty list
   public ArrayList < String > columnToExcelColumnAL (String start, int count)
   {
      int first = excelColumnToInt (start);
      return intToExcelColumnsAL (first, count);
   }

   // Returns a List of columns inclusive of the 2 given column Strings
   // From begining column to ending column
   // On error returns an empty list
   public ArrayList < String > columnToExcelColumnAL (String start, String end)
   {
      int first = excelColumnToInt (start);
      int last = excelColumnToInt (end);
      return columnToExcelColumnAL (first, last);
   }

   // Returns a List of columns inclusive of the 2 given column intergers
   // On error returns an empty list
   public ArrayList < String > columnToExcelColumnAL (int start, int end)
   {
      ArrayList < String > al = new ArrayList < String > ();

      String first = intToExcelColumn (start);
      String last = intToExcelColumn (end);

      if ((start == 0) || (end == 0))
         return al;

      if (start == end)
      {
         al.add (first.toUpperCase ());
         return al;
      }

      int count = Math.abs (start - end);

      if (end > start)
      {
         al = intToExcelColumnsAL (first, count);
      }
      else
      {
         // TODO -- (May not be used) this case of left to right order will not give a correctly
         // ordered return till I write an al.reverse()
         
         // The startIndex and len are right to left so return a reversed list
         al = intToExcelColumnsAL (last, count);

         // TODO -- (May not be used) must write an ArrayList Reverse, as is not provided
         //al.reverse();           
      }
      return al;
   }

   //=======================================================================

   //----------------------------------------------------------
   // From Code Project: "Count with letters" by Zeltera 17 Jun 2007 
   // Private:  getNextChar()
   // Private:  reverseString()
   // Private:  getNext()
   private char getNextChar (char c)
   {
      if (c < 'z')
         return (char) ((int) c + 1);
      else
         return 'a';
   }

   private String reverseString (String str)
   {
      StringBuilder sb = new StringBuilder ();
      for (int i = str.length () - 1; i >= 0; i--)
         sb.append (str.charAt (i));
      return sb.toString ();
   }

   private String getNext (String currentString)
   {
      currentString = currentString.toLowerCase ();
      if (currentString.length () == 0)
         return "a";

      char lastCharacter = currentString.charAt (currentString.length () - 1);
      String subString =
         currentString.substring (0, currentString.length () - 1);

      if (lastCharacter == 'z')
         return getNext (subString) + 'a';
      else
         return subString + (char) ((int) lastCharacter + 1);
   }

   //--------------------------------------------------------------
   // Expects a String of the form $A$4 or $A$3:$B$3 
   //  -- Expects the dollar signs.
   //  -- Expects the colon between two cells if more than one cell
   // Returns the String without "$" (A4 or A3:B3)
   // On error (no regular expression match): returns the unchanged input String.
   public String removeRangeDollars (String rangeStr)
   {
      // if one cell was selected rangeStr resembles:  $A$4
      // if multiple cells was selected rangeStr resembles:  $A$3:$B$3

      try
      {
         String str = rangeStr;
         String rangePatStr = "\\$\\w{1,2}\\$\\d{1,2}:\\$\\w{1,2}\\$\\d{1,2}";
         Pattern rangePat = Pattern.compile (rangePatStr);

         Matcher match = rangePat.matcher (rangeStr);
         if (match.find ())
         {
            str = rangeStr.replaceAll("(\\$)(\\w{1,2})(\\$)(\\d{1,2}):(\\$)(\\w{1,2})(\\$)(\\d{1,2})",
                                      "$2$4:$6$8");
         }
         else
         {
            // Remove the dollar signs:  $A$4 --> A4
            rangePatStr = "\\$\\w{1,2}\\$\\w{1,2}";
            rangePat = Pattern.compile (rangePatStr);
            Matcher match1 = rangePat.matcher (rangeStr);
            if (match1.find ())
            {
               str = rangeStr.replaceAll ("(\\$)(\\w{1,2})", "$2");
            }
         }
         //internalMsgCtrl.out("RemoveRangeDollars: [" + str + "] (after replace)");

         // returns rangeStr without "$"'s or rangeStr as received no match
         return str;
      }
      catch (Exception excp)
      {
         //System.err.println (excp.toString());
         return (excp.toString());
      }
      finally
      {
         // return rangeStr as received on error
         return rangeStr;
      }
   }

   //--------------------------------------------------------------
   // Helper enum for getRangeCells()
   public enum RangeSpread
   {
      unknown, horz, vert, rect
   }

   // Expects a String of the form $A$4 or $A$3:$B$3
   //  -- Expects the dollar signs.
   //  -- Expects the colon between two cells if more than one cell
   // Returns an array list of all cells in the range in the form $col$row
   // If a rectangular range:  the columns grouped together 
   //                           ($A$3, $A$4, $A$5, $B$3, $B$4, $B$5)
   // Conversion note:  it returns LOWER CASE... above my old comment shows upper case.
   //                   for the moment... leave lower... may be a good reason???
   // On error returns an empty array list
   public ArrayList getRangeCells (String rangeStr)
   {
      ArrayList<String> alRtn = new ArrayList ();

      // One will len up with empty Strings in parts
      String[] parts = rangeStr.split("[\\$:]");

      String str = rangeStr;
      String rangePatStr = "\\$\\w{1,2}\\$\\d{1,2}:\\$\\w{1,2}\\$\\d{1,2}";
      Pattern rangePat = Pattern.compile (rangePatStr);

      Matcher match = rangePat.matcher (rangeStr);
      if (match.find ())
      {
         RangeSpread espread = helperRangeSpread(rangeStr);

         // The return is:  [0] empty, [1] first  column, [2] first  row
         //                 [3] empty, [4] second column, [5] second row
           
         if (parts.length == 6)
         {
            //parts[0] == "";
            int colLeft = excelColumnToInt(parts[1]);
            int rowLeft = Integer.valueOf(parts[2]);
            //parts[3] == "";
            int colRight = excelColumnToInt(parts[4]);
            int rowRight = Integer.valueOf(parts[5]);

            // Form the individual cell names
            ArrayList<Integer> rows = new ArrayList();
            ArrayList<Integer> cols = new ArrayList();
            String tmpStr = stringEmpty;
            String cell = stringEmpty;
            int inx;
            switch (espread)
            {
               case horz:
                  for (inx = colLeft; inx < colRight; inx++)
                  {
                     cols.add(inx);
                  }
                  cols.add(inx);  // need to add the last column

                  for (int col : cols)
                  {
                     tmpStr = intToExcelColumn(col).toLowerCase();
                     cell = "$" + tmpStr + "$" + rowLeft;  // rowLeft == rowRight
                     alRtn.add(cell);
                  }
                  break;

               case vert:
                  for (inx = rowLeft; inx < rowRight; inx++)
                  {
                     rows.add(inx);
                  }
                  rows.add(inx); // need to add the last row

                  for (int row : rows)
                  {
                     tmpStr = intToExcelColumn(colRight).toLowerCase();  // colRight == colLeft
                     cell = "$" + tmpStr + "$" + row;
                     alRtn.add(cell);
                  }
                  break;

               case rect:
                  for (inx = colLeft; inx < colRight; inx++)
                  {
                     cols.add(inx);
                  }
                  cols.add(inx);  // need to add the last column

                  for (inx = rowLeft; inx < rowRight; inx++)
                  {
                     rows.add(inx);
                  }
                  rows.add(inx); // need to add the last row

                  for (int col : cols)
                  {
                     tmpStr = intToExcelColumn(col).toLowerCase();  // colRight == colLeft
                     for (int row : rows)
                     {
                        cell = "$" + tmpStr + "$" + row;
                        alRtn.add(cell);
                     }
                  }
                  break;

               default:
                  // will return an empty alRtn
                  break;
            }
            // will return an empty alRtn
         }
         // will return an empty alRtn 
      }
      else
      {
         // This a single cell range: $A$4
         alRtn.add(rangeStr);
      }
      return alRtn;		// an empty alRtn
   }

   //--------------------------------------------------------------
   // Expects a String of the form $A$3:$B$3
   //  -- Expects the dollar signs.
   //  -- Expects the colon between two cells if more than one cell
   // Figures out if the range is a single row horizontal,
   // a single column vertical or a rectangle or multiple rows and columns.
   private RangeSpread helperRangeSpread(String rangeStr)
   {
      String colon = ":";
      String[] pairs = rangeStr.split(colon);

      // Must break to an array of endIndex 2
      if (pairs.length == 2)
      {
         String dollar = "\\$";
         String[] leftPair = pairs[0].split(dollar);
         String[] rightPair = pairs[1].split(dollar);

         // Must break to an array of endIndex 3
         if ((leftPair.length == 3) && (rightPair.length == 3))
         {
            // Compare the first letter, Columns... if same... is a vertical 
            if ((leftPair[1].compareTo(rightPair[1])) == 0)
            {
               return RangeSpread.vert;
            }
            // Compare the second value, Rows... if same... is horiz
            //if (leftPair[2] == rightPair[2]) (C# can do == on strings ... not so Java
            if ((leftPair[2].compareTo(rightPair[2])) == 0)
            {
               return RangeSpread.horz;
            }
            // Else must be rectangular
            return RangeSpread.rect;
         }

         // error return
         return RangeSpread.unknown;
      }

      // error return
      return RangeSpread.unknown;
   }

   //------------------------------------
   // This code is duplicated in LibUtils and ConfigUtils to avoid
   // referencing ExcelUtils from those Libraries
   //----------------------------------------------------------------------------
   // Parse an Excels Comma Separated Vector (CSV) String
   // Breaks each cell into a member of a String List.
   // Accounts for quoted Strings.  Excel puts double quotes around
   // cells that contain a comma.  Excel doubles double quotes.
   // On error returns an allocated but empty String List

   public String[] CSVStringParseAry(String csvStr)
   {
      ArrayList<String> al = CSVStringParse(csvStr);
      String[] ary = new String[al.size()];
      Arrays.setAll(ary, al::get);
      return ary;
   }

   public ArrayList < String > CSVStringParse (String csvStr)
   {
      ArrayList < String > collectionAL = new ArrayList < String > ();
      String collectionStr;
      boolean quoteState = false;
      boolean pairedQuoteState = false;

      try
      {
         // Break the String into a character Array to be parsed by a state engine
         char[] charAry = csvStr.toCharArray ();

         // Initialize for collections
         collectionAL.clear ();
         collectionStr = stringEmpty;

         for (char chr:charAry)
         {
            if (chr == '"')
            {
               if (pairedQuoteState)
               {
                  // Normally, never collect a double quote.
                  // But we want to collect this one.
                  collectionStr += chr;

                  // close the paired state
                  pairedQuoteState = false;
               }
               if (quoteState)
               {
                  // This quote may startIndex a pair of double quotes
                  pairedQuoteState = true;
               }

               // simply trigger off and on
               quoteState = !quoteState;

               // never collect a double quote; unless paired as above
               continue;
            }
            // Anytime is not a double quote then obviously not pair
            pairedQuoteState = false;

            if (chr == ',')
            {
               if (quoteState)
               {
                  // normally, never collect a comma (continue below)
                  // but we want to collect this one
                  collectionStr += chr;
               }
               else
               {
                  // Close collection
                  collectionAL.add (collectionStr);

                  // Open a new collection
                  collectionStr = stringEmpty;
               }
               // never collect a comma; unless with quotes as above
               continue;
            }

            collectionStr += chr;
         }
         return collectionAL;
      }
      catch (Exception excp)
      {
         return (new ArrayList < String > (1));
      }
   }

   //--------------------------------------------------------------------------------
   // Returns the column requested by Excel letter (Capitalized) from a csv list
   public ArrayList<String> CSVToColumn(String column, ArrayList<String> csvContent)
   {
      ArrayList<String>columnContent = new ArrayList<>();
      // Excel columns count from 1, Java arrays count from 0
      // The column A == 1 and the first array member is 0
      // Must subtract 1
      int colInt = this.excelColumnToInt(column) - 1;
      for(int inx = 0; inx < csvContent.size(); inx++)
      {
         String csvStr = csvContent.get(inx);
         String[] csvAry = CSVStringParseAry(csvStr);
         columnContent.add(csvAry[colInt]);
      }
      return columnContent;
   }

   // Convert all strings in a column to integers
   // Expects to be sent an Excel column, one string per ArrayList<String> member.
   // Expects to be a column of zip codes.
   // Member of the ArrayList (Excel column) that are empty (empty cell) or
   // text that can not be converted to an Integer (text which is not a zip code),
   // will be ignored and thrown away.
   // In other words:  The column does not have to contain all clean digit text.
   // But no errors will be generated if none clean digit text is found.
   public int[] zip_StrColToIntCol(ArrayList<String> strColAL)
   {
      ArrayList<Integer> intAL = new ArrayList<>();
      for (int inx = 0; inx < strColAL.size(); inx++)
      {
         try
         {
            String str = strColAL.get(inx);
            int val = Integer.parseInt(str);
            intAL.add(val);
         }
         catch (Exception excp)
         {
            continue;
         }
      }
      // convert the ArrayList to an int[] (courtesy StackOverflow)
      int[] rtn = new int[intAL.size()];
      Arrays.setAll(rtn, intAL::get);
      return rtn;
   }
 }
