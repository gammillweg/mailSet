package libPack;

import configPack.XMLData;
import libPack.InternalMsgCtrl.errKey;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MailPrepUtils
{
   // Lots of little support function.
   // Not necessarily multi-use functions (many are only called once).
   // Just functions I chose to move to "un-clutter" some other class
   // (Just move around the clutter.)

   // Converted from C# to Java (started) 01/26/19

   //-------------------------------------------------------------------------------
   public MailPrepUtils(Shared shared)
   {
      this.shared = shared;
      lineBreak = shared.getLineBreak();
      folderBreak = shared.getFolderBreak();
      internalMsgCtrl = shared.getInternalMsgCtrl();
   }

   private Shared shared = null;
   private XMLData xmlData = null;
   InternalMsgCtrl internalMsgCtrl = null;
   private final String lineBreak;
   private final String folderBreak;

   //-------------------------------------------------------------------------------
   final String StringEmpty = "";

   //-------------------------------------------------------------------------------
   // Expand a range of values, input at xxx-yyy (strings) to
   // xxx [and inbetween] yyy (213-215 ==> 213, 214, 215)
   public int[] expandRange(String str)
   {
      if (str.isEmpty()) return new int[1];

      //Integer.parseInt(str); // returns int
      //Integer x = Integer.valueOf(str); // return Integer

      int[] rtnAry = null;
      String[] strAry = str.split("-");
      if (strAry.length > 1) {

         for (int inx = 0; inx < strAry.length; inx++) {
            strAry[0] = strAry[0].trim();
         }

         int start = Integer.parseInt(strAry[0]);
         int end = Integer.parseInt(strAry[1]);
         int len = (end - start) + 1;
         rtnAry = new int[len];

         int index = 0;
         for (int cnt = start; cnt <= end; cnt++) {
            rtnAry[index] = start + index;
            index++;
         }
      }
      else
      {
         rtnAry = new int[1];
         rtnAry[0] = Integer.valueOf(strAry[0]);
      }

      // internalMsgCtrl.out("--------------------------------\n");
      // for (int inx = 0; inx < rtnAry.endIndex; inx++)
      //    internalMsgCtrl.out("rtn: " + rtnAry[inx]);

      return rtnAry;
   }

   public String getDatedOutputPath()
   {
      String fourthTueStr = getForthTueStr();
      String datedName = String.format("%s_Bulletin", fourthTueStr);
      String dataFolderPathName = shared.getDataFolderPathName();
      String datedDataFolderPathName = dataFolderPathName + folderBreak + datedName;
      return datedDataFolderPathName;
   }

   public String getForthTueStr()
   {
      DateWork dateWork = new DateWork();
      int[] monthyear = dateWork.getCurrentMonthYear();
      int renewalMonth = monthyear[0] + 1;
      int renewalYear = shared.getRenewalYearInt();
      int fourthTue = dateWork.getTheFourthTueOfTheCurrentMonth();

      String forthTueStr = String.format("%2d%02d%2d", renewalYear, renewalMonth, fourthTue);
      return forthTueStr;
   }
   
   //-------------------------------------------------------------------------------
   // Convert configured keys to real trayTag names
   // From:  D5 to a list of 5-digit zip codes like 80228
   //        D3 to a list of 3-digit zip codes like 800, 801, 804
   //        SCF to DEN_SCF
   // Tray names are returned in configured SplitUtils Order
   public ArrayList < String > getTrayNames (ArrayList < Addr > addrList)
   {
      // The trays in the slice order list are generalized.
      // Rather than D3 we need the true trayTag names:  "800", "801" etc
      // Similarly for SCF and D5

      // orderAry
      //    [0] "D5"    String
      //    [1] "MXD"   String
      //    [2] "OMX"   String
      //    [3] "DADC"  String
      //    [4] "SCF"   String
      //    [5] "D3"    String
      if (xmlData == null)
      {
         xmlData = shared.getXMLData();
      }
      String[] orderAry = null;
      try
      {
         orderAry = xmlData.get_order();
      }
      catch (Exception excp)
      {
         internalMsgCtrl.err(errKey.Error, false, "Unable to obtain configuration data:  GenLabelsUtils");
         return (new ArrayList<String>(0));
      }

      // The contains only 2 members, and we are only interested in the [1]
      //-ArrayList<String> sliceOrderList = sliceOrderLists[0];

      ArrayList < String > trayList = new ArrayList < String > ();
      ArrayList < String > tmpList = new ArrayList < String > ();

      // The ones that need to be convered are:  D5, SCF and D3

      
      for (String str : orderAry)
      {
         tmpList.clear();
         switch (str)
         {
            case "D5":
               tmpList = getD5Trays(addrList);
               break;

            case "MXD":
               trayList.add("MXD");
               break;

            case "OMX":
               trayList.add("OMX");
               break;

            case "DADC":
               trayList.add("DADC");
               break;

            case "SCF":
               tmpList = getSCFTrays(addrList);
               break;

            case "D3":
               tmpList = getD3Trays(addrList);
               break;
         }
         for (String trayTag : tmpList)
         {
            trayList.add(trayTag);
         }
      }
      
      return trayList;
   }

   public ArrayList<String> getD3Trays(ArrayList<Addr> addrList)
   {
      ArrayList<String> trayList = new ArrayList<String>();

      String patStr = "^\\d\\d\\d$";
      Pattern pat = Pattern.compile(patStr);
      for (Addr addr : addrList)
      {
         String trayTag = addr.trayTag;
         // if (trayList.contains(trayTag)) continue;

         Matcher match = pat.matcher(trayTag);
         if (match.find ())
         {
            trayList.add(trayTag);
         }
      }
      return trayList;
   }

   public ArrayList<String> getD3Trays(Addr[] addrAry)
   {
      List<Addr> list = Arrays.asList(addrAry);
      ArrayList<Addr> al = new ArrayList<>(list);
      return getD3Trays(al);
   }


   
   private ArrayList<String> getSCFTrays(ArrayList<Addr> addrList)
   {
      ArrayList<String> trayList = new ArrayList<String>();

      for (Addr addr : addrList)
      {
         String trayTag = addr.trayTag;
         //if (trayList.contains(trayTag)) continue;
         if (trayTag.contains("SCF"))
         {
            trayList.add(trayTag);
         }
      }
      return trayList;

   }
   
   private ArrayList<String> getD5Trays(ArrayList<Addr> addrList)
   {
      ArrayList<String> trayList = new ArrayList<String>();

      String patStr = "^\\d\\d\\d\\d\\d$";
      Pattern pat = Pattern.compile(patStr);
      for (Addr addr : addrList)
      {
         String trayTag = addr.trayTag;
         //if (trayList.contains(trayTag)) continue;

         Matcher match = pat.matcher(trayTag);
         if (match.find ())
         {
            trayList.add(trayTag);
         }
      }
      return trayList;
   }
   /*
   //--------------------------------------------------------------
   public int CountD5Zip(String zip, ArrayList<Addr> addrList)
   {
      IEnumerable<Addr> query =
         from addr in addrList
         where addr.zip == zip
         select addr;

      return (query.size());
   }
   
     //-------------------------------------------------
     // Counts a single 5 digit zip in the address list
     public int CountD5toZipCnt(String zip, ArrayList<Addr> addrList)
     {
     IEnumerable<Addr> query =
     from addr in addrList
     where addr.zip == zip
     select addr;

     for (Addr address : query)
     {
     return (query.size());
     }

     return 0;
     }

     // Count all the 5 digit Zips in the Address List
     //public ArrayList<ZipCnt> CountD5toZipCnt(ArrayList<Addr> addrList)
     //{
     //    ArrayList<ZipCnt> zipCntList = new ArrayList<ZipCnt>();
     //    ArrayList<String> doneList = new ArrayList<String>();

     //    for (Addr addr : addrList)
     //    {
     //        String zip = addr.zip;

     //        // Has this zip has been previously counted?
     //        if (doneList.contains(zip)) continue;
     //        doneList.add(zip);

     //        int cnt = CountD5toZipCnt(zip, addrList);
     //        ZipCnt zipCnt = new ZipCnt();
     //        zipCnt.zip = zip;
     //        zipCnt.cnt = cnt;
     //        zipCntList.add(zipCnt);
     //    }
     //    return zipCntList;
     //}

     //-------------------------------------------------
     // Counts a single 3 Digit zip in the address list

     public int CountD3toZipCnt(String d3, ArrayList<Addr> addrList)
     {
     IEnumerable<Addr> query =
     from addr in addrList
     where addr.zip.substring(0,3) == d3
     select addr;

     for (Addr address : query)
     {
     return (query.size());
     }

     return 0;
     }

     //// Count all the 3 Digit Zips in the Address List
     //public ArrayList<ZipCnt> CountD3toZipCnt(ArrayList<Addr> addrList)
     //{
     //    ArrayList<ZipCnt> zipCntList = new ArrayList<ZipCnt>();
     //    ArrayList<String> doneList = new ArrayList<String>();

     //    for (Addr addr : addrList)
     //    {
     //        String d3 = addr.zip.substring(0, 3);

     //        // Has this zip has been previously counted?
     //        if (doneList.contains(d3)) continue;
     //        doneList.add(d3);

     //        int cnt = CountD3toZipCnt(d3, addrList);
     //        ZipCnt zipCnt = new ZipCnt();
     //        zipCnt.zip = d3;
     //        zipCnt.cnt = cnt;
     //        zipCntList.add(zipCnt);
     //    }
     //    return zipCntList;
     //}
     //-------------------------------------------

     // Counts all the 3 Digit zip codes in the address list per configurations RANGE
     // Return is List of String array[2]: [0] range name, [1] count (stored as String)

     //public ArrayList<String[]> CountRANGE(ArrayList<Addr> addrList, ConfigXML configXML)
     //{
     //    ArrayList<String[]> countList = new ArrayList<String[]>();

     //    ConfigUtils configUtils = new ConfigUtils();
     //    ArrayList<ArrayList<String>> rangeLists = configUtils.ParseConfigRange(configXML);

     //    for (ArrayList<String> rangeList : rangeLists)
     //    {
     //        String[] ary = new String[2];

     //        ArrayList<String> d3List = configUtils.ExpandRanges(rangeList[0], rangeLists);

     //        int total = 0;
     //        for (String d3 : d3List)
     //        {
     //            int cnt = CountD3toZipCnt(d3, addrList);
     //            total += cnt;
     //        }

     //        ary[0] = rangeList[0];
     //        ary[1] = total.ToString();
     //        countList.add(ary);
     //    }

     //    return countList;
     //}

     // Gets the first found yymmdd
     // Verifies mm is a number between 1 and 12 and dd is a number
     // no larger than the number of days in mm.
     public String GetyymmddFile(String defaultFile)
     {
     const String pattern = @"\d\d\d\d\d\d";
     Regex rgx = new Regex(pattern);
     Match match = rgx.Match(defaultFile);
     if (match.Success)
     {
     String str = StringEmpty;
     MatchCollection matches = Regex.Matches(defaultFile, pattern);
     for (int inx = 0; inx < matches.size; inx++)
     {
     str = matches[inx].Value;
     if (IsMMDD(str))
     {
     return str;
     }
     }
     }
     return StringEmpty;
     }

     // Expects format: yymmdd
     // check MM between 1-12, and DD is greater than 1
     // and no larger than the number of days in MM.
     public boolean IsMMDD(String str)
     {
     try
     {
     String yy = str.substring(0, 2);
     int iyy = Convert.ToInt32(yy);

     // if less than 70 is century 2000, if greater than 70 is century 1900
     iyy += (iyy < 70) ? 2000 : 1900;

     String mm = str.substring(2, 2);
     int imm = Convert.ToInt32(mm);
     String dd = str.substring(4, 2);
     int idd = Convert.ToInt32(dd);

     if (imm >= 1 && imm <= 12)
     {
     int days = DateTime.DaysInMonth(iyy, imm);
     return (idd >= 1 && idd <= days) ? true : false;
     }
     }
     catch (Exception excp)
     {
     }
     return false;
     }

     //// Returns an array of ArrayList<SplitUtils> where
     //// [0] is the balance.
     //// [1] is the Renewal and
     //public ArrayList<Addr>[] DivideAddrList(ArrayList<Addr> addrList, String renewalStr)
     //{
     //    // Break into two separate lists by renewal String
     //    ArrayList<Addr>[] addrListArray = new ArrayList<Addr>[2];
     //    addrListArray[0] = new ArrayList<Addr>();
     //    addrListArray[1] = new ArrayList<Addr>();
     //    for (Addr addr : addrList)
     //    {
     //        if (addr.renewal == renewalStr)
     //        {
     //            addrListArray[1].Add(addr);
     //        }
     //        else
     //        {
     //            addrListArray[0].Add(addr);
     //        }
     //    }
     //    return addrListArray;
     //}

     // Store Addr's in separate containers by trayTag
     // and count all the zips within this trayTag

     public ArrayList<Addr> SeparateAddrs(ArrayList<Addr> addrList, ArrayList<String> trayList)
     {
     ArrayList<Addr> addrList = new ArrayList<Addr>();

     for (String trayTag : trayList)
     {
     IEnumerable<Addr> query =
     from addr in addrList
     where addr.trayTag == trayTag
     select addr;

     if (query.size() > 0)
     {
     Addr addr = new Addr();
     addr.addrList = new ArrayList<Addr>();
     addr.zipCntList = new ArrayList<ZipCnt>();
     addr.trayTag = trayTag;

     // package up all the addr's that contain the active trayTag
     for (Addr addr : query)
     {
     addr.addrList.add(addr);
     }

     // Count all the zip codes within this trayTag
     ArrayList<String> doneList = new ArrayList<String>();
     for (Addr addr : addr.addrList)
     {
     String zip = addr.zip;
     if (doneList.contains(zip)) continue;
     doneList.add(zip);

     ZipCnt zipCnt = new ZipCnt();
     zipCnt.zip = zip;
     zipCnt.cnt = CountD5Zip(zip, addr.addrList);
     addr.zipCntList.add(zipCnt);
     }

     addrList.add(addr);
     }
     }
     return addrList;
     }

     public ArrayList<Addr> AddrByTray(ArrayList<Addr> addrList, String trayTag)
     {
     IEnumerable<Addr> query =
     from addr in addrList
     where addr.trayTag == trayTag
     select addr;

     ArrayList<Addr> AddrListByTray = new ArrayList<Addr>();
     if (query.size() > 0)
     {
     // package up all the addr's that contain the active trayTag
     for (Addr addr : query)
     {
     AddrListByTray.Add(addr);
     }
     }
     return AddrListByTray;
     }
   */
   // With Java Lambda... have been reduced to ONE LINE... Only called for
   // ONE location... the one line moved to that location
   //
   // In C# I used LINQ to sort a ArrayList<Addr> first of Addr.zip and
   // then on Addr.csv (which starts with the subscribers last name).
   // Now in Java I us a Lambda expression and only sort on zip
   // public ArrayList<Addr> sortAddrListByZip(ArrayList<Addr> addrAL)
   // {
   //    // Sort by Lambda (This was created by Intellj query, replace the Anonymous
   //    // (which I copied first) to Lambda.
   //    Collections.sort(addrAL, (addr1, addr2) -> addr1.zip.compareTo(addr2.zip));
   //    return addrAL;
   // }

   /********************************
    * following is all how to comments to keep as for reference
    * //Sorting using Anonymous Inner class. (Works)
    * //      Collections.sort(addrList, new Comparator<Addr>(){
    * //
    * //            public int compare(Addr addr1, Addr addr2){
    * //
    * //               return addr1.zip.compareTo(addr2.zip);
    * //            }
    * //
    * //         });
    * //      return addrList;
    *
    * // C# LINQ method if doing the sort (Lambda)
    * //ArrayList<Addr> sortedAddrList = new ArrayList<Addr>(addrList.size());
    *
    * // IEnumerable<Addr> query =
    * //    from addr in addrList
    * //    orderby addr.csv
    * //    orderby addr.zip
    * //    select addr;
    *
    * // for (Addr addr : query)
    * // {
    * //    sortedAddrList.add(addr);
    * // }
    *
    * //return sortedAddrList;
    *
    * //---------------------------------------------------------------
    * // How I sorted in the past -- used an array list sort as
    * // I could not get a List sort to work.  But the LINQ code works well.
    *
    * //ArrayList tmpAl = new ArrayList(addrList.size());
    * //for (Addr addr : addrList)
    * //{
    * //    Addr clone = addr.Clone();
    * //    tmpAl.Add(clone);
    * //}
    * //addrList.clear();
    *
    * //AddrSortByZip addrSortByZip = new AddrSortByZip();
    * //tmpAl.Sort(addrSortByZip);
    *
    * //for (Addr addr : tmpAl)
    * //{
    * //    addrList.add(addr);
    * //}
    * ********************************/

   /*
    * // Input A List of Addr
    * // Return a List of String of unique zips found in the AddrList
    * public ArrayList<String> UniqueZipAddrList(ArrayList<Addr> addrList)
    * {
    * ArrayList<String> uniqueZipList = new ArrayList<String>();
    * 
    * IEnumerable<String> query =
    * (from addrzip in addrList
    * orderby addrzip.zip
    * select addrzip.zip).Distinct();
    * 
    * for (String addrzip : query)
    * {
    * String zip = addrzip.trim();
    * uniqueZipList.add(zip);
    * }
    * return uniqueZipList;
    * }
    * 
    * // Input A List of String
    * // Return a sorted List of unique Strings
    * public ArrayList<String> UniqueStrList(ArrayList<String> strList)
    * {
    * ArrayList<String> uniqueStrList = new ArrayList<String>();
    * 
    * IEnumerable<String> query =
    * (from zips in strList
    * orderby zips
    * select zips).Distinct();
    * 
    * for (String zip : query)
    * {
    * uniqueStrList.add(zip);
    * }
    * return uniqueStrList;
    * }
    * 
    * // Convert a list of 5 digit zip codes Strings to a list of 3 digit zip code Strings
    * public ArrayList<String> D5toD3(ArrayList<String> d5List)
    * {
    * ArrayList<String> d3List = new ArrayList<String>(d5List.size());
    * 
    * Regex rgx = new Regex(@"^\d\d\d\d\d$");
    * for (String str : d5List)
    * {
    * Match match = rgx.Match(str);
    * if (match.Success)
    * {
    * d3List.add(str.substring(0, 3));
    * }
    * // If NOT a 5 digit Zip code then the data is TOSSED
    * }
    * 
    * return d3List;
    * }
    * 
    * // Find zip codes in one single column (integer)
    * // Excel counts from 1, C arrays from 0 -- Column is reduced by one
    * // On error -- return an empty list
    * public ArrayList<String> FindZipsByColumn(ArrayList<String> contentList, int column)
    * {
    * // I thought to force column to be an Excel apha column name;
    * // but then I would need to use library ExcelUtils, and I don't
    * // want to do that.
    * column--;
    * 
    * try
    * {
    * ArrayList<String> zipList = new ArrayList<String>();
    * for (String str : contentList)
    * {
    * String[] pieces;
    * if (str.contains("\""))
    * {
    * pieces = CSVStringParse(str).ToArray();
    * }
    * else
    * {
    * pieces = str.split(",");
    * }
    * 
    * if (column < pieces.endIndex)
    * {
    * String zip = pieces[column].trim();
    * 
    * if (IsAnchored5Digits(zip))
    * {
    * zipList.add(zip);
    * }
    * }
    * }
    * return zipList;
    * }
    * catch (Exception excp)
    * {
    * return (new ArrayList<String>(0));
    * }
    * }
    */


   
   // return all Strings in one single column (integer)
   // Excel counts from 1, C arrays from 0 -- Column is reduced by one
   // if parameter line == -1, does the entire file, else only the line specified
   // On error -- return an empty list
   public ArrayList < String > returnByColumn (ArrayList < String >
                                               contentList, int column,
                                               int row)
   {
      // I thought to force column to be an Excel apha column name;
      // but then I would need to use library ExcelUtils, and I don't
      // want to do that.
      column--;
      row--;

      String[]pieces = {""};

      // Side comment about chkRow and the extra conditionals it causes in the code:
      // Originally (in C# version) the row number was not a parameter.
      // Hence, to get the content of row 1, one went through ALL rows.
      // The calling routine went through many columns and this through ALL rows for each column.
      // It was to wasteful for me to accept.  Hence... chkRow.

      int chkRow = -1;
      try
      {
         ArrayList < String > strList = new ArrayList < String > ();
         for (int inx = 0; inx < contentList.size (); inx++)
         {
            // if line == -1, the chkLine will always == line and will process
            // thus the entire file will be processed, else, process just line number.
            chkRow = (row == -1) ? inx : row;
            
            String str = contentList.get(inx);
            if (inx == chkRow)
            {
               // If content contains a "," then Excel quotes content
               // Check to see if content quoted.
               if (str.contains ("\""))
               {
                  // Content is quoted, there exists an extra comma to process
                  ArrayList < String > al = cvsStringParse(str);
                  pieces = cpArrayListToArray (al);
               }
               else
               {
                  // No extra comma's to be concerned with
                  pieces = str.split(",");
               }

               if ((column < pieces.length) && (column > -1))
               {
                  strList.add (pieces[column].trim());
               }

               // if not checking the entire file, we have processed
               // our line of interest and we are done.
               if (row != -1) break;
            }
         }
         return strList;
      }
      catch (Exception excp)
      {
         return (new ArrayList < String > (0));
      }
   }

   // Is the String a 5 digit zip code
   public boolean IsAnchored5Digits (String str)
   {
      String patternStr = "^\\d\\d\\d\\d\\d$";
      Pattern pattern = Pattern.compile (patternStr);
      Matcher matcher = pattern.matcher (str);
      return matcher.find();

      //Regex rgx = new Regex(@); // looking for 5 digits
      //Match match = rgx.Match(str);
      //return (match.Success) ? true : false;
   }
   /*
    * // Is the String a 4 digit Plus 4, with a leading "-"
    * public boolean IsPlus4(String str)
    * {
    * Regex rgx = new Regex(@"^-\d\d\d\d$"); // looking for 4 digits with a leading dash
    * Match match = rgx.Match(str);
    * return (match.Success) ? true : false;
    * }
    * 
    * // Is String 4 ditis without a leading dash
    * public boolean IsAnchored4Digits(String str)
    * {
    * System.Text.RegularExpressions.Regex rgx = new System.Text.RegularExpressions.Regex(@"^\d\d\d\d$");
    * System.Text.RegularExpressions.Match match = rgx.Match(str);
    * return (match.Success) ? true : false;
    * }
    * 
    * // Returns a list of Strings found in zipList1 not in the zipList2
    * public ArrayList<String> Except(ArrayList<String> zipList1, ArrayList<String> zipList2)
    * {
    * #region commentOutDEBUG
    * ////--------------------------------------------------
    * //Trace.WriteLine("--------------- enter: Except ----------------------");
    * //Trace.WriteLine("--------------- zipList1 ----------------------");
    * //for (int inx = 0; inx < zipList1.size(); inx++)
    * //{
    * //    Trace.Write(zipList1[inx] + ", ");
    * //    if (inx == 0) continue;
    * //    if ((inx % 8) == 0) Trace.WriteLine("");
    * //}
    * //Trace.WriteLine("");
    * 
    * ////--------------------------------------------------
    * ////Trace.WriteLine("--------------- zipList2 ----------------------");
    * //for (int inx = 0; inx < zipList2.size(); inx++)
    * //{
    * //    Trace.Write(zipList2[inx] + ", ");
    * //    if (inx == 0) continue;
    * //    if ((inx % 8) == 0) Trace.WriteLine("");
    * //}
    * //Trace.WriteLine("");
    * ////--------------------------------------------------
    * #endregion
    * 
    * // "Introducing Microsoft LINQ", Paolo Pialorsi and Marco Rosso
    * // LINQ Except operator yields all the elements in the FIRST assembly
    * // that are not present in the SECOND assembly.
    * 
    * // I want all the elements in zipList2 (the zip codes column A list) which are not in
    * // the address list.
    * 
    * IEnumerable<String> query = null;
    * if (zipList2.size() >= 1)
    * {
    * query =
    * (from zone in zipList1
    * select zone
    * ).Except(
    * from addrs in zipList2
    * select addrs);
    * }
    * else
    * {
    * // the list is empty, return an empty list
    * // the zone name, or an empty list.
    * return (new ArrayList<String>());
    * }
    * 
    * ArrayList<String> exceptList = query.ToList();
    * 
    * #region commentOutDEBUG
    * ////--------------------------------------------------
    * //Trace.WriteLine("--------------- exit Except ----------------------");
    * //Trace.WriteLine("--------------- exceptList ----------------------");
    * //for (int inx = 0; inx < exceptList.size(); inx++)
    * //{
    * //    Trace.Write(exceptList[inx] + ", ");
    * //    if (inx == 0) continue;
    * //    //if ((inx % 8) == 0) Trace.WriteLine("");
    * //}
    * //Trace.WriteLine("");
    * //Trace.WriteLine("==============================================================");
    * ////--------------------------------------------------
    * #endregion
    * 
    * return exceptList;
    * }
    * 
    * 
    * //-----------------------------------------------------------
    * // Might someday be usefull
    * // On error or empty list, returns a empty ArrayList<String>
    * private ArrayList<String> ArrayListToList(ArrayList StringArrayList)
    * {
    * if (StringArrayList != null)
    * {
    * ArrayList<String> StringList = new ArrayList<String>(StringArrayList.size);
    * return ArrayListToList(StringArrayList, StringList);
    * }
    * ArrayList<String> errList = new ArrayList<String>(0);
    * return errList;
    * }
    * 
    * // Convert an ArrayList containing String to ArrayList<String)
    * // On error or empty list, returns a empty ArrayList<String>
    * private ArrayList<String> ArrayListToList(ArrayList StringArrayList, ArrayList<String> StringList)
    * {
    * ArrayList<String> errList = new ArrayList<String>(0);
    * try
    * {
    * if (StringList != null)
    * {
    * if (StringArrayList != null)
    * {
    * StringList.clear();
    * for (String str : StringArrayList)
    * {
    * StringList.add(str);
    * }
    * // Good return
    * return StringList;
    * }
    * else
    * {
    * // Error return
    * // return other than null -- an empty list
    * return errList;
    * }
    * }
    * else
    * {
    * // Error return
    * // return other than null -- an empty list
    * return errList;
    * }
    * }
    * catch (Exception excp)
    * {
    * // Error return
    * // return other than null -- an empty list
    * return errList;
    * }
    * }
    * 
    * //----------------------------------------------------
    * // Convert a ArrayList<String> to an ArrayList
    * // On error or empty list, returns a empty ArrayList
    * private ArrayList ListToArrayList(ArrayList<String> StringList)
    * {
    * try
    * {
    * if (StringList != null)
    * {
    * ArrayList StringArrayList = new ArrayList(StringList.size);
    * for (String str : StringList)
    * {
    * StringArrayList.add(str);
    * }
    * return StringArrayList;
    * }
    * else
    * {
    * // Error return
    * // return other than null -- an empty list
    * ArrayList errArrayList = new ArrayList(0);
    * return errArrayList;
    * }
    * }
    * catch (Exception excp)
    * {
    * // Error return
    * // return other than null -- an empty list
    * ArrayList errArrayList = new ArrayList(0);
    * return errArrayList;
    * }
    * }
    * 
    */
   //----------------------------------------------------
   // This method lives in ExcelUtils... it is duplicated here
   // in that I don't want to refer ExcelUtils from this library
   //----------------------------------------------------------------------------
   // Parse an Excels Comma Separated Vector (CSV) String
   // Breaks each cell into a member of a String List
   // Accounts for quoted Strings.  Excel puts double quotes around
   // cells that contain a comma.  Excel doubles double quotes.
   // On error returns an allocated but empty String List
   public ArrayList < String > cvsStringParse (String csvStr)
   {
      ArrayList < String > collectionList = new ArrayList < String > ();
      String collectionStr;
      boolean quoteState = false;
      boolean pairedQuoteState = false;

      try
      {
         // Break the String into a character Array to be parsed by a state engin
         char[] charAry = csvStr.toCharArray ();

         // Initialize for collections
         collectionList.clear ();
         collectionStr = StringEmpty;

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
                  collectionList.add (collectionStr);

                  // Open a new collection
                  collectionStr = StringEmpty;
               }
               // never collect a comma; unless with quotes as above
               continue;
            }

            collectionStr += chr;
         }
         return collectionList;
      }
      catch (Exception excp)
      {
         return (new ArrayList < String > (1));
      }
   }

   //================================
   // Here is code that may allow me to learn how to get ArrayList.toArray()
   // to work.  BUT... this is not a good time to try and make it work.
   // Besides... at the moment... I can't seem to get the method to be called.
   // (I'm missing some piece of knowledge... or maybe... it was never called
   // to begin with.  But, I think it requires a comma in a addr excel field.
   // which I have added in the test data; but still not called.
   // I give up for now... not worth the time I am spending.
   
   // private Integer[] mapValuesToAry(BundleMark bundleMark)
   // {
   //    // To ease access... convert indexCntMap int values to an array
   //    Collection<Integer> values = bundleMark.indexCntMap.values();
   //    Integer[] cntAry = new Integer[values.size()];
   //    return (values.toArray(cntAry));
   // }
   
   // Could not use toArray() from Object to String and could not cast
   // So the easy way out...
   public String[] cpArrayListToArray(ArrayList <String> al)
   {
      // NOTE -- (May not be used) Leave the break here... I want to know if I EVER use this method
      String[]rtnStrAry = new String[al.size ()];
      for (int inx = 0; inx < al.size (); inx++)
      {
         rtnStrAry[inx] = al.get (inx);
      }
      return rtnStrAry;
   }

   //---------------------------------------------------------------
   // Separate from a 5D list the 3D values
   // Any string in D5AL != 5 will be ignored
   public ArrayList<String> D5ToD3(ArrayList<String> D5AL)
   {
      ArrayList<String> D3AL = new ArrayList<>();
      for (String str : D5AL)
      {
         if (str.length() != 5) continue;
         {
            String strD3 = str.substring(0, 3);
            if (D3AL.contains(strD3)) continue;
            D3AL.add(strD3);
         }
      }
      return D3AL;
   }

   public ArrayList<String> D5ToD3(String[] D5Ary)
   {
      List<String> list = Arrays.asList(D5Ary);
      ArrayList<String> al = new ArrayList<>(list);
      return D5ToD3(al);
   }

   public ArrayList<String> uniqueStr(ArrayList<String> strAL) {
      ArrayList<String> al = new ArrayList<>();
      final Set<String> set = new HashSet<>();
      for (String str : strAL) {
         if (set.add(str) == true) {
            al.add(str);
         }
      }
      return al;
   }

   // Convert String members of  String Array to an Integer Array
   public int[] StrAryToIntAry(String[] strAry) {
      int[] intAry = new int[strAry.length];
      for (int inx = 0; inx < strAry.length; inx++) {
         try {
            intAry[inx] = Integer.parseInt(strAry[inx]);
         }
         catch (Exception exc)
         {
            internalMsgCtrl.err(errKey.Error, false, "String does not convert to Integer: " + strAry[inx]);
            return null;
         }
      }
      return intAry;
   }

//   //---------------------------------------------------------------
//   // Expand all 3D members of the encoded ary and
//   // return all 3D zips defined by the received ranges as a String[]
//   public String[] expandZones(String[] ary)
//   {
//      // Data is ary of 3D zip codes ranges like this: 465-468.
//      // Or simply a single 3D zip:  456
//      // Where I expand such to Strings 465, 466, 467, 468 one each per member
//      // of the return ary
//
//      ArrayList<Integer> al = new ArrayList<>();
//      int range = -1;
//      int[] rangeAry;
//      for (int index = 0; index < ary.length; index++)
//      {
//         rangeAry = null;
//         String str = ary[index];
//         if (str.contains("-"))
//         {
//            rangeAry = this.expandRange(ary[index]);
//         }
//         else
//         {
//            range = Integer.parseInt(str);
//         }
//
//         if (rangeAry != null)
//         {
//            for (int inx = 0; inx < rangeAry.length; inx++)
//            {
//               al.add(rangeAry[inx]);
//            }
//         }
//         else
//         {
//            al.add(range);
//         }
//      }
//
//      String[] rtnAry = convertALTo3DStrAry(al);
//      return rtnAry;
//   }
//
//   // Convert Integer to zero padded 3D String
//   public String[] convertALTo3DStrAry(ArrayList<Integer> al)
//   {
//      String[] strAry = new String[al.size()];
//      for (int inx = 0; inx < al.size(); inx++)
//      {
//         // Convert Integer to zero padded 3D String
//         Integer val = al.get(inx);
//         strAry[inx] =  String.format("%03d", val);
//         //internalMsgCtrl.out("StrAry[" + inx + "] = " + strAry[inx]);
//      }
//      return strAry;
//   }
//
//   // Duplicate of method found in GenLabelsUtils.  Duplication necessary
//   // to resolve problems with creating GenLabelsUtils in Shared.
//   public int[] expandRange(String str)
//   {
//      if (str.isEmpty()) return new int[1];
//
//      //Integer.parseInt(str); // returns int
//      //Integer x = Integer.valueOf(str); // return Integer
//
//      int[] rtnAry = null;
//      String[] strAry = str.split("-");
//      if (strAry.length > 1) {
//
//         for (int inx = 0; inx < strAry.length; inx++) {
//            strAry[0] = strAry[0].trim();
//         }
//
//         int start = Integer.parseInt(strAry[0]);
//         int end = Integer.parseInt(strAry[1]);
//         int len = (end - start) + 1;
//         rtnAry = new int[len];
//
//         int index = 0;
//         for (int cnt = start; cnt <= end; cnt++) {
//            rtnAry[index] = start + index;
//            index++;
//         }
//      }
//      else
//      {
//         rtnAry = new int[1];
//         rtnAry[0] = Integer.valueOf(strAry[0]);
//      }
//
//      // internalMsgCtrl.out("--------------------------------\n");
//      // for (int inx = 0; inx < rtnAry.endIndex; inx++)
//      //    internalMsgCtrl.out("rtn: " + rtnAry[inx]);
//
//      return rtnAry;
//   }
//   //---------------------------------------------------------------
}
