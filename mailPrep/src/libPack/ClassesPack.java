package libPack;

import java.util.ArrayList;
import java.util.Comparator;

// Instantiate a number of small classes used to package data and as
// sort comparators
public class ClassesPack
{
   //---------------------------------------------------------------
   public Tray instantiateTray()
   {
      return new Tray();
   }

   public class Tray
   {
      // Bundle is the UPS priority nomenclature
      // Currently:  MXD, OMX, SCF and mine DADC, 3D and 5D
      // Tray is the pre-sort division:  MXD, OMX, DEN_SCF, DACD, and if
      // 3D:  the first three of the zip (currently: 800, 801. 802. 804)
      // 5D:  the entire zip (currently we do not have a 5D; but for
      //      testing 5D I created 80214, we use to have 80228)
      // A tray is defined is a minimum of 23 items of the same bundle.
      public String bundle = "";
      public String trayTag = "";

      // The startIndex within the sorted by bundle addrBundleAry
      // and the number of zips (len) of this bundle
      public int startIndex = -1;
      public int len = -1;

      // These three are used in conjunction for the final production of
      // labels ordered and slices for packaging and application during assembly.
      public void initZipCntAry(int val) { zipCntAry = new int[val]; }
      public void initBracedAL() { bracedAL = new ArrayList<Brace>(); }
   
      public int[] zipCntAry = null;
      public ArrayList<Brace> bracedAL = null;

      /**************************************************
       // development notes written during creation....
       // They may not be entirely correct... were not checked when code was finished.

       * renewalAL is a list of indices in addrBundleAry of addr's that will expire
       * with the current issue.  These are to receive a renewal notice and are
       * to be printed on colored labels.  As label text is created, renewalsAL is
       * referenced so as to separate them from Master.CSV into Renewal.CSV
       * zipCntAL is a list of counts of each zip.  Used to break the trayTag into
       * slices suitable to package for assembly
       * bracedAL is an array list of Brace, start and end indices within zipCntAry
       * of each segment.  Note that unit segments may have to be broken down further.
       *************************************************/
   }

   //---------------------------------------------------------------
   // Segments is a package class to hold the addrBundleAry start index and len
   // of each segment stored in Tray.BracedAL.  It is a packaging step between
   // establishing the trays and creating the CSV label file.
   public Segment instantiateSegment() { return new Segment(); }

   public class Segment
   {
      // The segment number (legacy: sequence number)
      public int segmentIndex = -1;

      public String trayTag = "";

      // start index within addrBundleAry
      public int startIndex = -1;
      
      // Number of members of this segment in addrBundleAry (the number of issues)
      public int len = -1;

      // Renewals of this segment
      // Index into addrBundleAry of each renewal issue
      public void initRenewalAry(int len) {this.renewalAry = new int[len]; }
      public int[] renewalAry = null;

      // Zip Counts of this segment
      // Count of all zip codes found in this segment (including renewals)
      // Counts from startIndex to compute the indices of all members of the segment
      //    within addrBundleAry
      public void initZipCntAry(int len) { this.zipCntAry = new int[len]; }
      public int[] zipCntAry = null;
   }

   //---------------------------------------------------------------
   public Brace instantiateBrace(int startIndex, int endIndex)
   {
      return new Brace(startIndex, endIndex);
   }

   // Originally was named Pair; but I thought that to generic.
   // I thought to name dyad as I used that in TapMaz for a column/row brace
   // But came to brace, as that is what the class does... the braces around
   // the zip counts, start and end.
   // Indices paired (start index and end index) within Tray.zipCntAry of a segment
   public class Brace
   {
      public Brace(int startIndex, int endIndex)
      {
         // indices into Tray.zipCntAry
         this.startIndex = startIndex;
         this.endIndex = endIndex;
      }
      public int startIndex;
      public int endIndex;
   }

   //---------------------------------------------------------------
   public AddrZipComparator instantiateAddrZipComparator()
   {
      return new AddrZipComparator();
   }
   public class AddrZipComparator implements Comparator<Addr>
   {
      @Override
      public int compare(Addr ob1, Addr ob2)
      {
         return ob1.zip.compareTo(ob2.zip);
      }
   }

   //---------------------------------------------------------------
   public AddrBundleComparator instantiateAddrBundleComparator()
   {
      return new AddrBundleComparator();
   }
   public class AddrBundleComparator implements Comparator<Addr>
   {
      @Override
      public int compare(Addr ob1, Addr ob2)
      {
         return ob1.bundle.compareTo(ob2.bundle);
      }
   }
}

