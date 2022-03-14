package buildAddr;

import configPack.XMLData;
import libPack.*;

import java.util.ArrayList;
import java.util.Arrays;

// Separate the zip into separate zip5 classes (with and internal sort on plus4)
public class InitAddr
{
   public InitAddr(Shared shared)
   {
      this.shared = shared;
      this.xmlData = shared.getXMLData();
      buildAssemblyUtils = new MailPrepUtils(shared);
      excelUtils = shared.getExcelUtils();
      internalMsgCtrl = shared.getInternalMsgCtrl();
   }

   Shared shared = null;
   MailPrepUtils buildAssemblyUtils = null;
   ExcelUtils excelUtils = null;
   XMLData xmlData = null;
   InternalMsgCtrl internalMsgCtrl = null;

   final String stringEmpty = "";

   // Return is a sorted by zip array of class Addr
   // Return an empty list on error
   public Addr[] initAddr_Main(ArrayList<String> contentAL) throws InternalFatalError
   {
      try {
         String[] exceColumnHeaderStrAry = xmlData.getAddressListHeadings();
         String[] excelColumnStrAry = xmlData.getAddressListColumns();

         // The return is the excel columns of zip and renewal within headerColumnsLists
         ExcelUtils excelUtils = shared.getExcelUtils();
         int zipColumn = -1;
         int renewalColumn = -1;
         zipColumn = (excelUtils.excelColumnToInt(excelColumnStrAry[6])) - 1;
         renewalColumn = (excelUtils.excelColumnToInt(excelColumnStrAry[8])) - 1;

         // Valid data contains a 5 digit zip code in zipColumn
         // There may be (will be) invalid addr data in the content:  Such as the headers
         // and COMP breaks.  And perhaps others.
         int invalidCnt = countInvalid(contentAL, zipColumn);

         // Creates Addr classes, Sorts the list and gets rid of empty content lines
         Addr[] addrZipAry = fillAddrList(contentAL, zipColumn, renewalColumn, invalidCnt);

         return addrZipAry;
      }
      catch (Exception excp)
      {
         internalMsgCtrl.err(InternalMsgCtrl.errKey.FatalError, false, "Failed in initAddr_Main()");
         String errMsg = excp.toString();
         internalMsgCtrl.err(InternalMsgCtrl.errKey.ExceptionMsg, false, errMsg);
         //throw new InternalFatalError("Folder does not exist");
         throw new InternalFatalError();
         //return null;
      }
   }

   private int countInvalid(ArrayList<String> contentAL, int zipColumn)
   {
      String str = stringEmpty;
      int invalidCnt = 0;
      for (int inx = 0; inx < contentAL.size(); inx++)
      {
         str = contentAL.get(inx);
         String[] fieldsAry;

         // contains double quote
         if (str.contains("\""))
         {
            fieldsAry = excelUtils.CSVStringParseAry(str);
         }
         else
         {
            fieldsAry = contentAL.get(inx).split(",");
            if (fieldsAry.length == 0)
            {
               invalidCnt++;
               continue;
            }
         }

         if (buildAssemblyUtils.IsAnchored5Digits(fieldsAry[zipColumn]) == false)
         {
            invalidCnt++;
         }
      }
      return invalidCnt;
   }

   private Addr[] fillAddrList(ArrayList<String> contentAL,
                               int zipColumn, int renewalColumn,
                               int invalidCnt)
   {
      // There is invalid data in contentAL, which has been counted.
      // account for it when allocating addrZipAry.
      Addr[] addrZipAry = new Addr[contentAL.size() - invalidCnt];

      String str = stringEmpty;
      int index = 0;
      try
      {
         for (int inx = 0; inx < contentAL.size(); inx++)
         {
            str = contentAL.get(inx);
            String[] fieldsAry;

            // contains double quote
            if (str.contains("\""))
            {
               fieldsAry = excelUtils.CSVStringParseAry(str);
            }
            else
            {
               fieldsAry = contentAL.get(inx).split(",");
               if (fieldsAry.length == 0)
               {
                  continue;
               }
            }

            if (buildAssemblyUtils.IsAnchored5Digits(fieldsAry[zipColumn]))
            {
               Addr addr = new Addr();
               addr.zip = fieldsAry[zipColumn];
               addr.renewal = fieldsAry[renewalColumn];
               addr.csv = contentAL.get(inx);

               addrZipAry[index++] = addr;
            }
         }

         // (Below find two other sort examples, in notes find sort.org.
         // I CHOSE to use a defined sort clase that impliments Comparator
         // because it is more clear to me.)

         // Sort addrZipAry by zip
         ClassesPack classesPack = shared.getClassesPack();
         ClassesPack.AddrZipComparator addrZipComparator = classesPack.instantiateAddrZipComparator();
         Arrays.sort(addrZipAry, addrZipComparator);

         // A further example of the Comparator implimented as an anonymous declaration
         // Arrays.sort(addrZipAry, new Comparator<Addr>()
         // {
         //    @Override
         //    public int compare(Addr ob1, Addr ob2)
         //    {
         //       return ob1.getZip().compareTo(ob2.getZip());
         //    }
         // });
         //
         // And further yet... as a Lambda expression
         // Arrays.sort(addrZipAry, (addr1, addr2) -> addr1.zip.compareTo(addr2.zip));
         //
         //for (Addr addr : addrZipAry) addr.debugPrintlnBrief();
      }
      catch (Exception excp)
      {
         String errMsg = "Bad Data at or near row:  " + index + "\n\n" +
            str + "\n\n";
         //System.SystemErrPln(errMsg, "buildaddr", "InitAddr", "fillAddrList");
         //System.err.println(errMsg);

         return null;
      }
      return addrZipAry;
   }
}
