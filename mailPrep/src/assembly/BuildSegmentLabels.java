package assembly;

// Segments labels are used to label assembly envelopes with the segment (set of
// labels) packaged in the envelope.
// Example of a segment label (lines 1, 2, and 3 (array members 0, 1, 2))
//     segmentLabelAry
//     array member: 0 = "(02)  Tray(SCF) (80301-80601)"
//     array member: 1 = "Total:19  Renewals:0  Singles:8"
//     array member: 2 = "80303(4)  80516(2)  80601(5)"
//
// These labels are interspersed between Bulletin address label such
// that each set of labels across a sheet is filled.  Thus, if there
// are 6 labels in a segment then there would be no need to fill out
// the labels across the sheet.  (This assumes 3 across.)  However,
// if there where only 5 labels in a segment, 1 segment label would
// be added to that line.  For a resulting even total of 6.

import configPack.XMLData;
import libPack.Addr;
import libPack.ClassesPack;
import libPack.ExcelUtils;
import libPack.Shared;

import java.util.ArrayList;

public class BuildSegmentLabels
{
   public BuildSegmentLabels(Shared shared)
   {
      this.shared = shared;
      xmlData = shared.getXMLData();
      excelUtils = shared.getExcelUtils();

      columnStrAry = xmlData.getLineColumns();
      columnIntAry = new int[columnStrAry.length];
      for (int inx = 0; inx < columnIntAry.length; inx++)
      {
         columnIntAry[inx] = excelUtils.excelColumnToInt(columnStrAry[inx]);
      }

      maxChars = shared.getMaxCharAcrossLabels();
   }

   private Shared shared = null;
   private XMLData xmlData = null;
   private ExcelUtils excelUtils = null;

   private String[] columnStrAry = null;
   private int[] columnIntAry = null;

   private final int maxChars;   // max characters per label line

   public ArrayList<String> buildSegmentLabels_Main(
      ClassesPack.Segment[] segmentAry,
      Addr[] addrBundleAry)
   {
      ArrayList<String> segmentLabelAL = new ArrayList<String>();

      for (ClassesPack.Segment segment : segmentAry)
      {
         String line = buildSegmentLabelLine(segment, addrBundleAry);
         segmentLabelAL.add(line);
      }

      return segmentLabelAL;
   }

   // The return is a a excel CSV formatted segment label
   private String  buildSegmentLabelLine(ClassesPack.Segment segment, Addr[] addrBundleAry)
   {
      // Example of a segment label (lines 1, 2, and 3 (array members 0, 1, 2))
      //     array member: labelLine1Column = "02)  Tray{SCF} {80301-80601}"
      //     array member: labelLine2Column = "Total:19  Renewals:0  Singles:8"
      //     array member: labelLine3Column = "80303(4)  80516(2)  80601(5)"

      // ,,02)  Tray{SCF} {80301-80601},Total:19  Renewals:0  Singles:,80303(4)  80516(2)  80601(5),

      // line1 is the column of the renewal date which is on the far right of
      // the excel sheet as currently designed.
      // This code is a potential problem IF the xcel format is changed

      String line = ",";

      line += buildSegmentLine1(segment, addrBundleAry) + ",";
      line += buildSegmentLine2(segment) + ",";
      ArrayList<String> zipsAL = buildMultipleZips(segment, addrBundleAry);
      for (String str : zipsAL)
      {
         line += str + ",";
      }

      // The return is a modified labelAry (2, 3, or 4 filled)
      //ArrayList<String>lineAL =  buildAdditionalLines(zipsAL);
      //for (String str : lineAL)
      //{
         //line += str + ",";
      //}
      return line;
    }

   // Segments labels are used to label assembly envelopes with the segment (set of
   // labels) packaged in the envelope.
   // Line 1 titles the label (the sequence number, the Tray, and the zip code range
   //        Example String "00) Tray{MXD} {30548-95828}"
   private String buildSegmentLine1(ClassesPack.Segment segment, Addr[] addrBundleAry)
   {
      String firstZip = addrBundleAry[segment.startIndex].zip;
      String lastZip = addrBundleAry[segment.startIndex + (segment.len-1)].zip;

      String line = String.format("%02d)  Tray %s   %s-%s",
            segment.segmentIndex, segment.trayTag,
            firstZip, lastZip);
      return line;
   }

   // Segments labels are used to label assembly envelopes with the segment (set of
   // labels) packaged in the envelope.
   // Line 2 titles the label
   //        Example String "Total:3 Renewals:0 Singles:3"
   private String buildSegmentLine2(ClassesPack.Segment segment)
   {
      int renewals = (segment.renewalAry == null) ? 0 : segment.renewalAry.length;

      // Count the number of single zip codes in zipCntAry
      int singles = countSingles(segment);

      // DO NOT USE comma's -- this ends up in a CSV file
      String line = String.format("Total %d  Renewals %d  Singles %d",
            (segment.len + renewals), renewals, singles);
      return line;
   }

   // Count the number of single zip codes
   private int countSingles(ClassesPack.Segment segment)
   {
      int cnt = 0;
      for (int val : segment.zipCntAry) { if (val == 1) cnt++; }
      return cnt;
   }
   
   // Find those zips and there zip cnts
   private ArrayList<String> buildMultipleZips(
      ClassesPack.Segment segment, Addr[] addrBundleAry)
   {
      String line = "";
      boolean flg = false;

      ArrayList<String> zipsAL = new ArrayList<String>();
      int index = segment.startIndex;
      for (int val : segment.zipCntAry)
      {
         if (val == 1) { index++; continue; }
         String zipStr = addrBundleAry[index].zip;
         zipStr += "(" + val + ")";

         if ((line.length() + zipStr.length()) > maxChars)
         {
            zipsAL.add(line);
            line = zipStr + " ";
            flg = true;
         }
         else
         {
            line += zipStr + " ";
            flg = true;
         }

         index += val;
      }

      // save the last line collected
      if (flg)
      {
         zipsAL.add(line);
      }

      return zipsAL;
   }

   // Break zipsAL up into multiple lines approxamately MaxChars characters long
   // Return is filled linesAL
   private ArrayList<String> buildAdditionalLines(ArrayList<String> zipsAL)
   {
      ArrayList<String> lineAL = new ArrayList<String>(3);
      if (zipsAL.size() == 0) return (new ArrayList<String>(0));

      int len = 0;
      String line = "";
      for (String str : zipsAL)
      {
         // DO NOT USE commas -- this ends up in a CSV file
         line += str + "  ";
         // check to see if the next set of 8 chars will exceed MaxChars
         // Magic 9:  zip code == 5 + (cnt) == 4 ==> 9
         // (count likely == 3, as cnt likely < 10) but I play it safe
         if (line.length() >= (maxChars - 9) )
         {
            // remove the trailing space + comma
            line = line.substring(0, line.length()-2);
            lineAL.add(line);
            line = "";
         }
      }

      ArrayList<String> rtnAL = new ArrayList<String>(lineAL.size());
      for (String str : lineAL)
      {
         // remove the trailing space + comma
         str = str.substring(0, str.length() - 2);
         rtnAL.add(str);
      }
      return rtnAL;
   }


}
