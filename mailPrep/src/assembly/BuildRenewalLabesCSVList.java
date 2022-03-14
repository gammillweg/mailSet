package assembly;

import libPack.Addr;
import libPack.ClassesPack;
import libPack.Shared;
import libPack.SplitUtils;

import java.util.ArrayList;

// Create String  ArrayList that may be used to create a CSV
// file in address label format suitable to be read by Microsoft Word
// to create Bulletin labels in packaging order with envelope sequence
// labels intermixed.
public class BuildRenewalLabesCSVList
{
   public BuildRenewalLabesCSVList(ClassesPack.Segment[] segementAry,
                                   Shared shared, SplitUtils splitUtils)
   {
      this.shared = shared;
      this.segmentAry = segmentAry;
      this.splitUtils = splitUtils;
   }
   
   private Shared shared = null;
   private SplitUtils splitUtils = null;
   private ClassesPack.Segment[] segmentAry = null;

   public ArrayList<String> buildRenewalCSVList_Main(
      Addr[] addrBundleAry,
      ClassesPack.Segment[] segmentAry)
   {
      //   build the segment labels string
      //   and create the renewaAL -- the data for the renewal.csv file
      ArrayList<String> renewalListCSVAL = new ArrayList<String>();

      String headerCSVStr = splitUtils.getHeaderCSVStr();
      renewalListCSVAL.add(headerCSVStr);
      
      ArrayList<String[]> segmentLabelsAL = new ArrayList<String[]>(segmentAry.length);
      
      for (ClassesPack.Segment segment : segmentAry)
      {
         // The return is a modified segmentLabelAL (List of String[],
         // (each is a segment label),

         // Collect the renewal CSV lines from addrBundleAry
         ArrayList<String> renewalListAL = renewalListCSV(segment, addrBundleAry);
         if (renewalListAL != null)
         {
            renewalListCSVAL.addAll(renewalListAL);
         }
      }
      return renewalListCSVAL;
   }

   // The return is the CSV String member of addrBundleAry indexed by segment.renewalsAL
   // Note that once the string is stored in the return renewalListAL, the CSV String
   // of addrBundleAry is set to an empty string ("") so that it can not be used
   // again in the AddressList (Master) list.
   public ArrayList<String> renewalListCSV(ClassesPack.Segment segment, Addr[] addrBundleAry)
   {
      if (segment.renewalAry == null) return null;

      ArrayList<String> renewalListAL = new ArrayList<String>(segment.renewalAry.length);
      for (int inx : segment.renewalAry)
      {
         Addr addr = addrBundleAry[inx];

         String bulletinLabelCSVStr = splitUtils.buildBulletinLableCSVStr(addr);
         renewalListAL.add(bulletinLabelCSVStr);
      }
      return renewalListAL;
   }

}

