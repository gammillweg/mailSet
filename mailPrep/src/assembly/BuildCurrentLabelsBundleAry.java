package assembly;

import libPack.Addr;
import libPack.ClassesPack;
import libPack.Shared;
import libPack.SplitUtils;

// Create Addr Bundle for the Current list of subscribers from addrBundleAry.
// This will be blended with segment labels and copied into a necessary
// ArrayList in BlendCurrentToAL; but here, as using Segments (which are
// indexed into addrBundleAry, we must use a BundleAry.
public class BuildCurrentLabelsBundleAry
{
   public BuildCurrentLabelsBundleAry(ClassesPack.Segment[] segementAry,
                                      Shared shared, SplitUtils splitUtils)
   {
      this.shared = shared;
      this.segmentAry = segmentAry;
      this.splitUtils = splitUtils;
   }

   private Shared shared = null;
   private SplitUtils splitUtils = null;
   private ClassesPack.Segment[] segmentAry = null;

   public Addr[] buildCurrentBundleAry_Main(Addr[] addrBundleAry, ClassesPack.Segment[] segmentAry)
   {
      Addr[] currentBundleAry = new Addr[addrBundleAry.length];
      
      for (ClassesPack.Segment segment : segmentAry)
      {
         // Collect the currenList subscription (Master) Addr from addrBundleAry
         // The return is modified currentBundleAry.
         currentBundle(segment, addrBundleAry, currentBundleAry);
      }
      return currentBundleAry;
   }

   private void currentBundle(ClassesPack.Segment segment,
                              Addr[] addrBundleAry,
                              Addr[] currentBundleAry)
   {
      for (int inx = segment.startIndex; inx < (segment.startIndex + segment.len); inx++)
      {
         // Is this a renewal?
         if (segment.renewalAry != null)
         {
            if (containsIndex(segment.renewalAry, inx)) continue;
         }
         // else is current
         currentBundleAry[inx] = addrBundleAry[inx];
      }         
   }

   private boolean containsIndex(int[] ary, int index)
   {
      for (int inx = 0; inx < ary.length; inx++)
      {
         if (index == ary[inx]) return true;
      }
      return false;
   }
}

