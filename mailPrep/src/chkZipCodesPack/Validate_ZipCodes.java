package chkZipCodesPack;

import libPack.Addr;
import libPack.InternalMsgCtrl;
import libPack.Shared;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Validate_ZipCodes
{
   public Validate_ZipCodes(Shared shared)
   {
      this.shared = shared;
      internalMsgCtrl = shared.getInternalMsgCtrl();
   }

   Shared shared = null;
   InternalMsgCtrl internalMsgCtrl = null;

   public ArrayList<Integer> findMissingCodeZips_Main(Addr[] addrAry, ArrayList<String> zipColAL)
   {
      ArrayList<String> missingAL = new ArrayList<>();
      // Convert the String ArrayList to a sorted array of int
      int[] zipCodesAry = cleanZipCol(zipColAL);
      int[] zipAddrAry = cleanAddrZipCol(addrAry);

      // zipAddrAry is the master.  I don't care if zipCodesAry and zips not found in
      // zipAddrAry. In fact that will happen often.  I only care about zips code in
      // zipAddrAry not in zipCodesAry.
      ArrayList<Integer> missingIntAL = findMissing(zipAddrAry, zipCodesAry);

      return missingIntAL;
   }


   // Return an ArrayList<Integer> address zip not found in zipCodes
   private ArrayList<Integer> findMissing(int[] zipAddrAry, int[] zipCodesAry)
   {
      // zipAddrAry is the master.
      // I don't care if zipCodesAry zips are not found in zipAddrAry.
      // I only care about zips code in zipAddrAry not found in zipCodesAry.

      ArrayList<Integer> missingIntAL = new ArrayList<>();
      boolean found = false;
      for (int index = 0; index < zipAddrAry.length; index++)
      {
         int inx = Arrays.binarySearch(zipCodesAry, zipAddrAry[index]);
         if (inx < 0)
         {
            missingIntAL.add(zipAddrAry[index]);
         }
      }
      return missingIntAL;
   }

   // Copy the zip member of addrAry to one array, convert to an integer and sort
   private int[] cleanAddrZipCol(Addr[] addrAry)
   {
      // copy zip members of addrAry into an ArrayList of zip codes
      ArrayList zipAddrAL = new ArrayList(addrAry.length);
      for (int inx = 0; inx < addrAry.length; inx++)
      {
         zipAddrAL.add(addrAry[inx].zip);
      }
      // Convert the String ArrayList to a sorted array of int
      int[] zipAddrAry = cleanZipCol(zipAddrAL);
      return zipAddrAry;
   }

   // There is a heading line and very likely lots of empty data in zipColAL
   // Get rid of anything other than a 5-digit zip and
   // convert the strings to integers.  Put them into an int array, and sort it.
   // Return a sorted int array of zip codes
   private int[] cleanZipCol(ArrayList<String> zipColAL)
   {
      ArrayList<Integer> intAL = new ArrayList<>(zipColAL.size());
      for (int inx = 0; inx < zipColAL.size(); inx++)
      {
         String str = zipColAL.get(inx);
         if (str.isEmpty()) break;

         // Must contain 5 characters if a 5-digit zip code
         // This does not go in... this condition should never be hit
         // Verify check for this problem and the column should have been cleaned up to get by Verify

         // I cut this... Excel might drop the leading zero... making a zip 03842 only 4-digits
         //if (str.length() != 5) { continue;  }

         // Should be zip 5-digit Integers
         // Again, this should never fail, as Verify should have found this problem
         // and the user should have cleaned the failures up.
         try
         {
            intAL.add(Integer.parseInt(str));
         }
         catch (Exception excp)
         {
            // Something other than an Integer Zip code
            continue;
         }
      }

      Collections.sort(intAL);
      int[] intAry = new int[intAL.size()];
      for(int inx = 0; inx < intAL.size(); inx++) { intAry[inx] = intAL.get(inx); }
      return intAry;
   }
}
