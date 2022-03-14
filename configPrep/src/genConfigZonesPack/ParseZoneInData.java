package genConfigZonesPack;

import libPack.InternalFatalError;
import libPack.InternalMsgCtrl;
import libPack.Shared;

import java.util.ArrayList;

public class ParseZoneInData
{
   public ParseZoneInData(Shared shared)
   {
      internalMsgCtrl = shared.getInternalMsgCtrl();
      lineBreak = shared.getLineBreak();
   }
   InternalMsgCtrl internalMsgCtrl = null;
   String lineBreak = "";
   final String headingZip = "ZIP Code";
   final String headingZone = "Zone";

   // Denver Zone ArrayList size is greater than 300 so the arbitrary 200 is a reasonable check
   final int MinALSize = 200;

   public String parseInDataAL(ArrayList<String> strAL) throws InternalFatalError
   {
      try
      {
         StringBuilder sb = new StringBuilder();
         // Break the data into two equal ArrayLists: Zips and Zones
         ArrayList<String> zipsAL = new ArrayList<>();
         ArrayList<String> zonesAL = new ArrayList<>();
         int alSize = strAL.size();
         boolean zipFlg = false;
         for (int inx = 0; inx < alSize; inx++)
         {
            String str = strAL.get(inx);
            if (str.equals(headingZip))
            {
               zipFlg = true;
               continue;
            }
            if (str.equals(headingZone))
            {
               zipFlg = false;
               continue;
            }
            if (zipFlg)
            {
               zipsAL.add(str);
            } else
            {
               zonesAL.add(str);
            }
         }
         // These two ArrayLists must be of equal size
         if (zonesAL.size()!=zipsAL.size())
         {
            internalMsgCtrl.err(InternalMsgCtrl.errKey.Error, true, "There is an unequal number of elements:");
            internalMsgCtrl.err(InternalMsgCtrl.errKey.Error, true, "number of zones: " + zonesAL.size());
            internalMsgCtrl.err(InternalMsgCtrl.errKey.Error, true, "number of zip codes: " + zipsAL.size());
            return "";
         }

         // Need to combine the zip and zone ArrayLists to an array of ArrayLists by zone
         // There is expected 9 zones, count from 1 so element 0 is not used
         final int maxZones = 10;
         ArrayList<String>[] ALary = new ArrayList[maxZones];
         for (int inx = 1; inx < maxZones; inx++)
         {
            // Initialize all elements of the array of ArrayLists
            ALary[inx] = new ArrayList<String>();
         }

         // Fill the elements of ALary with zips by zone
         String str1;
         String str2;
         for (int inx = 0; inx < zonesAL.size(); inx++)
         {
            String zoneStr = zonesAL.get(inx);
            // There are cases of an added "+" or "*" foot notes on the zone integer string
            if (zoneStr.length() > 1)
            {
               zoneStr = zoneStr.substring(0, 1);
            }
            int izone = Integer.parseInt(zoneStr);
            String zip = zipsAL.get(inx);
            // Zip strings are either 3 or 9 characters:  ddd or ddd---ddd
            if (zip.length()==3)
            {
               ALary[izone].add(zip + ", ");
            } else
            {
               str1 = zip.substring(0, 3);
               str2 = zip.substring(6, 9);
               zip = str1 + "-" + str2;
               ALary[izone].add(zip + ", ");
            }
         }

         String rtnStr = buildString(ALary);
         return rtnStr;
      }
        
      catch (Exception exc)
      {
         throw new InternalFatalError();
      }
   }

   private String buildString(ArrayList<String>[] ALary)
   {
      StringBuilder sb = new StringBuilder();
      sb.append("<ZONES>" + lineBreak);
      int cnt = 1;
      for (ArrayList<String> AL : ALary )
      {
         if (AL == null) continue;
         if (AL.size() > 0)
         {
            sb.append("  <ZONE" + cnt + ">");
            for (int inx = 0; inx < AL.size(); inx++)
            {
               sb.append(AL.get(inx));
            }
            sb.replace(sb.length()-2, sb.length(), "");
            sb.append("</ZONE" + cnt +">" + lineBreak);
         }
         cnt++;
      }
      sb.append("</ZONES>" + lineBreak);

      //String rtnStr = sb.toString();
      return (sb.toString());
   }

   public boolean verifyZoneInData(ArrayList<String> strAL)
   {
      // The input data is one long column with only one string per array list item
      int alSize = strAL.size();
      if (alSize < MinALSize)
      {
         internalMsgCtrl.err(InternalMsgCtrl.errKey.Error, true, "The input data may not be valid.");
         internalMsgCtrl.err(InternalMsgCtrl.errKey.Error, true, "The list is expected to contain " + MinALSize + " elements.");
         return false;
      }

      // There is expected to be 4 columns... we will check and ensure there is at least 2
      // Columns are head by the key work "ZIP Codes" or "Zone"
      // There must be at least 2 "Zip Codes" and 2 "Zones"
      // There must be the same number of "Zip Codes" as "Zones"
      int headingZipCnt = 0;
      int headingZoneCnt = 0;
      for (int inx = 0; inx < alSize; inx++)
      {
         String str = strAL.get(inx);
         if (str.equals(headingZip)) headingZipCnt++;
         if (str.equals(headingZone)) headingZoneCnt++;

         // TODO --- zips are either 3 or 9 characters... ensure that is true
      }
      if (headingZipCnt != headingZoneCnt)
      {
         internalMsgCtrl.err(InternalMsgCtrl.errKey.Error, true, "The count of the two column headings is not equal");
         return false;
      }
      if ((headingZipCnt < 3) && (headingZoneCnt < 3))
      {
         internalMsgCtrl.err(InternalMsgCtrl.errKey.Error, true, "There are less than expected Zone columns.");
         return false;
      }
      return true;
    }
}
