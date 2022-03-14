package configPack;

// 190111
// This class is for storage and retrieval of data read from .0BulletinAssembly/config.xml (BulletinAssembly's config file)

import libPack.Shared;

import java.util.ArrayList;

// Input to all set_xxx() is String
// Out from most get_xxx() is String
// out from get_ic(), get_omx(), get_ZONE[1-8]() is String[]
// ZONE string ranges xxx-yyy are expanded
// ZONE range get_ZONE[1-8]() returns String array
public class XMLData
{
   public XMLData(Shared shared)
   {
      this.shared = shared;
   }
   Shared shared = null;

   // Names of Strings are Element names from the xml file

   // Date Reminder
   private String DateReminder;
   private String ReminderUpdated;
   private String LabelsAcross;
   private String MinTrayCnt;
   // Ranges
   private String State;
   private String DEN_SCF;
   private String GJ_SCF;
   private String DADC;
   private String[] icAry;
   private String[] omxAry;
   // USPS Priority order
   private String[] priorityAry;
   // SplitUtils Order
   private String[] orderAry;
   // Zones
   private String[] ZONE1Ary;
   private String[] ZONE2Ary;
   private String[] ZONE3Ary;
   private String[] ZONE4Ary;
   private String[] ZONE5Ary;
   private String[] ZONE6Ary;
   private String[] ZONE7Ary;
   private String[] ZONE8Ary;
   private String[] ZONE9Ary;

   // Zip -- ZipCodes
   private String Zs_ZIP;
   private String Zs_PIECES;
   private String Zs_CHK;
   private String Zs_TRAY;

   // ZC -- ZipCounts
   private String ZC_IC;
   private String ZC_DEN_SCF;
   private String ZC_DADC;
   private String ZC_ZONE2;
   private String ZC_ZONE3;
   private String ZC_ZONE4;
   private String ZC_ZONE5;
   private String ZC_ZONE6;
   private String ZC_ZONE7;
   private String ZC_ZONE8;
   private String ZC_ZONE9;


   // Addr -- AddressList
   private String Addr_LastName;
   private String Addr_FirstName;
   private String Addr_OtherName;
   private String Addr_Address;
   private String Addr_City;
   private String Addr_State;
   private String Addr_Zip;
   private String Addr_Plus4;
   private String Addr_Renewal;

   // MMH -- MailMergeHeader
   private String MMH_LastName;
   private String MMH_FirstName;
   private String MMH_OtherName;
   private String MMH_Address;
   private String MMH_City;
   private String MMH_State;
   private String MMH_Zip;
   private String MMH_Plus4;
   private String MMH_Renewal;

   // Label line by column
   private String Line1Column;
   private String Line2Column;
   private String Line3Column;
   private String Line4Column;
   private String Line5Column;


   // ------------- DateReminder ------------------------
   public void set_DateReminder(String value)
   {
      DateReminder = value;
   }
   public String get_DateReminder()
   {
      return DateReminder;
   }

   public void set_ReminderUpdated(String value)
   {
      ReminderUpdated = value;
   }
   public String get_ReminderUpdated()
   {
      return ReminderUpdated;
   }

   public void set_LabelsAcross(String value)
   {
      LabelsAcross = value;
   }
   public String get_LabelsAcross()
   {
      return LabelsAcross;
   }

   public void set_MinTrayCnt(String value)
   {
      MinTrayCnt = value;
   }
   public String get_MinTrayCnt()
   {
      return MinTrayCnt;
   }

   // ---------------- RANGES ---------------------------
   public void set_State(String value) { State = value; }
   public String get_State() { return State; }

   public void set_DEN_SCF(String value) { DEN_SCF = value; }
   public String get_DEN_SCF() { return DEN_SCF; }

   public void set_GJ_SCF(String value) { GJ_SCF = value; }
   public String get_GJ_SCF() { return GJ_SCF; }

   public void set_DADC(String value) { DADC = value; }
   public String get_DADC() { return DADC; }


   public void set_ic(String value)
   {
      String[] ary  = value.split("\n");
      icAry = cpArytoAry(ary);
   }
   public String[] get_ic() { return icAry; }

   public void set_omx(String value)
   {
      String[] ary  = value.split("\n");
      omxAry = cpArytoAry(ary);
   }
   public String[] get_omx() { return omxAry; }


   // ------------- UPSPRIORITY -------------------------
   public void set_priority(String value)
   {
      String[] ary  = value.split("\n");
      priorityAry = cpArytoAry(ary);
   }
   public String[] get_priority() { return priorityAry; }


   // ------------- SLICEORDER --------------------------
   public void set_order(String value)
   {
      String[] ary  = value.split("\n");
      orderAry = cpArytoAry(ary);
   }
   public String[] get_order() { return orderAry; }

   // ---------------- ZONES ----------------------------
   public void set_ZONE1(String value)
   {
      String[] ary = value.split(",");
      ZONE1Ary = cpArytoAry(ary);
      ZONE1Ary = expandZones(ZONE1Ary);
   }
   public String[] get_ZONE1() { return ZONE1Ary; }

   public void set_ZONE2(String value)
   {
      String[] ary = value.split(",");
      ZONE2Ary = cpArytoAry(ary);
      ZONE2Ary = expandZones(ZONE2Ary);
   }
   public String[] get_ZONE2() { return ZONE2Ary; }

   public void set_ZONE3(String value)
   {
      String[] ary = value.split(",");
      ZONE3Ary = cpArytoAry(ary);
      ZONE3Ary = expandZones(ZONE3Ary);
   }
   public String[] get_ZONE3() { return ZONE3Ary; }

   public void set_ZONE4(String value)
   {
      String[] ary = value.split(",");
      ZONE4Ary = cpArytoAry(ary);
      ZONE4Ary = expandZones(ZONE4Ary);
   }
   public String[] get_ZONE4() { return ZONE4Ary; }

   public void set_ZONE5(String value)
   {
      String[] ary = value.split(",");
      ZONE5Ary = cpArytoAry(ary);
      ZONE5Ary = expandZones(ZONE5Ary);
   }
   public String[] get_ZONE5() { return ZONE5Ary; }

   public void set_ZONE6(String value)
   {
      String[] ary = value.split(",");
      ZONE6Ary = cpArytoAry(ary);
      ZONE6Ary = expandZones(ZONE6Ary);
   }
   public String[] get_ZONE6() { return ZONE6Ary; }

   public void set_ZONE7(String value)
   {
      String[] ary = value.split(",");
      ZONE7Ary = cpArytoAry(ary);
      ZONE7Ary = expandZones(ZONE7Ary);
   }
   public String[] get_ZONE7() { return ZONE7Ary; }

   public void set_ZONE8(String value)
   {
      String[] ary = value.split(",");
      ZONE8Ary = cpArytoAry(ary);
      ZONE8Ary = expandZones(ZONE8Ary);
   }
   public String[] get_ZONE8() { return ZONE8Ary; }

   public void set_ZONE9(String value)
   {
      String[] ary = value.split(",");
      ZONE9Ary = cpArytoAry(ary);
      ZONE9Ary = expandZones(ZONE9Ary);
   }
   public String[] get_ZONE9() { return ZONE9Ary; }

   public ArrayList<String[]> get_zones()
   {
      ArrayList<String[]> zonesAL = new ArrayList<String[]>(12);
      zonesAL.add(ZONE1Ary);    
      zonesAL.add(ZONE2Ary);
      zonesAL.add(ZONE3Ary);
      zonesAL.add(ZONE4Ary);
      zonesAL.add(ZONE5Ary);
      zonesAL.add(ZONE6Ary);
      zonesAL.add(ZONE7Ary);
      zonesAL.add(ZONE8Ary);
      zonesAL.add(ZONE9Ary);
      return zonesAL;
   }

   // ------------- ZIPCODES -------------------------
   // Column Alpa Values
   public void set_Zs_ZIP(String value) { Zs_ZIP = value; }
   public String get_Zs_ZIP() { return Zs_ZIP; }
   public void set_Zs_PIECES(String value) { Zs_PIECES = value; }
   public String get_Zs_PIECES() { return Zs_PIECES; }
   public void set_Zs_CHK(String value) { Zs_CHK = value; }
   public String get_Zs_CHK() { return Zs_CHK;  }
   public void set_Zs_TRAY(String value) { Zs_TRAY = value; }
   public String get_Zs_TRAY() { return Zs_TRAY; }

   // --------------- ZIPCOUNTS -------------------------
   public void set_ZC_IC(String value) { ZC_IC = value; }
   public String get_ZC_IC() { return ZC_IC; }

   public void set_ZC_DEN_SCF(String value) { ZC_DEN_SCF = value; }
   public String get_ZC_DEN_SCF() { return ZC_DEN_SCF; }

   public void set_ZC_DADC(String value) { ZC_DADC = value; }
   public String get_ZC_DADC() { return ZC_DADC; }

   public void set_ZC_ZONE2(String value) { ZC_ZONE2 = value; }
   public String get_ZC_ZONE2() { return ZC_ZONE2; }

   public void set_ZC_ZONE3(String value) { ZC_ZONE3 = value; }
   public String get_ZC_ZONE3() { return ZC_ZONE3; }

   public void set_ZC_ZONE4(String value) { ZC_ZONE4 = value; }
   public String get_ZC_ZONE4() { return ZC_ZONE4; }

   public void set_ZC_ZONE5(String value) { ZC_ZONE5 = value; }
   public String get_ZC_ZONE5() { return ZC_ZONE5; }

   public void set_ZC_ZONE6(String value) { ZC_ZONE6 = value; }
   public String get_ZC_ZONE6() { return ZC_ZONE6; }

   public void set_ZC_ZONE7(String value) { ZC_ZONE7 = value; }
   public String get_ZC_ZONE7() { return ZC_ZONE7; }

   public void set_ZC_ZONE8(String value) { ZC_ZONE8 = value; }
   public String get_ZC_ZONE8() { return ZC_ZONE8; }

   public void set_ZC_ZONE9(String value) { ZC_ZONE9 = value; }
   public String get_ZC_ZONE9() { return ZC_ZONE9; }

   public String[] get_ZC_zones()
   {
      String[] ZC_zonesAry = new String[11];
      ZC_zonesAry[0] = ZC_IC;
      ZC_zonesAry[1] = ZC_DEN_SCF;
      ZC_zonesAry[2] = ZC_DADC;
      ZC_zonesAry[3] = ZC_ZONE2;
      ZC_zonesAry[4] = ZC_ZONE3;
      ZC_zonesAry[5] = ZC_ZONE4;
      ZC_zonesAry[6] = ZC_ZONE5;
      ZC_zonesAry[7] = ZC_ZONE6;
      ZC_zonesAry[8] = ZC_ZONE7;
      ZC_zonesAry[9] = ZC_ZONE8;
      ZC_zonesAry[10] = ZC_ZONE9;
      return ZC_zonesAry;
   }

   // ------------- ADDRESSLIST -------------------------
   // Column Alpa Values
   public void set_Addr_LastName(String value) { Addr_LastName = value; }
   public String get_Addr_LastName() { return Addr_LastName; }

   public void set_Addr_FirstName(String value) { Addr_FirstName = value; }
   public String get_Addr_FirstName() { return Addr_FirstName; }

   public void set_Addr_OtherName(String value) { Addr_OtherName = value; }
   public String get_Addr_OtherName() { return Addr_OtherName; }

   public void set_Addr_Address(String value) { Addr_Address = value; }
   public String get_Addr_Address() { return Addr_Address; }

   public void set_Addr_City(String value) { Addr_City = value; }
   public String get_Addr_City() { return Addr_City; }

   public void set_Addr_State(String value) { Addr_State = value; }
   public String get_Addr_State() { return Addr_State; }

   public void set_Addr_Zip(String value) { Addr_Zip = value; }
   public String get_Addr_Zip() { return Addr_Zip; }

   public void set_Addr_Plus4(String value) { Addr_Plus4 = value; }
   public String get_Addr_Plus4() { return Addr_Plus4; }

   public void set_Addr_Renewal(String value) { Addr_Renewal = value; }
   public String get_Addr_Renewal() { return Addr_Renewal; }

   public String[] getAddressListColumns()
   {
      String[] strAry = new String[9];
      strAry[0] = Addr_LastName;
      strAry[1] = Addr_FirstName;
      strAry[2] = Addr_OtherName;
      strAry[3] = Addr_Address;
      strAry[4] = Addr_City;
      strAry[5] = Addr_State;
      strAry[6] = Addr_Zip;
      strAry[7] = Addr_Plus4;
      strAry[8] = Addr_Renewal;
      return strAry;
   }

   // ------------- MAILMERGEHEADER ---------------------
   // Column Header strings
   public void set_MMH_LastName(String value) { MMH_LastName = value; }
   public String get_MMH_LastName() { return MMH_LastName; }

   public void set_MMH_FirstName(String value) { MMH_FirstName = value; }
   public String get_MMH_FirstName() { return MMH_FirstName; }

   public void set_MMH_OtherName(String value) { MMH_OtherName = value; }
   public String get_MMH_OtherName() { return MMH_OtherName; }

   public void set_MMH_Address(String value) { MMH_Address = value; }
   public String get_MMH_Address() { return MMH_Address; }

   public void set_MMH_City(String value) { MMH_City = value; }
   public String get_MMH_City() { return MMH_City; }

   public void set_MMH_State(String value) { MMH_State = value; }
   public String get_MMH_State() { return MMH_State; }

   public void set_MMH_Zip(String value) { MMH_Zip = value; }
   public String get_MMH_Zip() { return MMH_Zip; }

   public void set_MMH_Plus4(String value) { MMH_Plus4 = value; }
   public String get_MMH_Plus4() { return MMH_Plus4; }

   public void set_MMH_Renewal(String value) { MMH_Renewal = value; }
   public String get_MMH_Renewal() { return MMH_Renewal; }

   public String[] getAddressListHeadings()
   {
      // Note that these are Bulletin Label specific.
      // Headings: Phone, Subscribed and Comps are not included )
      String[] strAry = new String[9];
      strAry[0] = MMH_LastName;
      strAry[1] = MMH_FirstName;
      strAry[2] = MMH_OtherName;
      strAry[3] = MMH_Address;
      strAry[4] = MMH_City;
      strAry[5] = MMH_State;
      strAry[6] = MMH_Zip;
      strAry[7] = MMH_Plus4;
      strAry[8] = MMH_Renewal;
      return strAry;
   }

   // ------------- LABELLINES ---------------------
   // Labels Lines by column value (headers)
   public void set_Line1Column(String value) { Line1Column = value; }
   public String get_Line1Column() { return Line1Column; }

   public void set_Line2Column(String value) { Line2Column = value; }
   public String get_Line2Column() { return Line2Column; }

   public void set_Line3Column(String value) { Line3Column = value; }
   public String get_Line3Column() { return Line3Column; }

   public void set_Line4Column(String value) { Line4Column = value; }
   public String get_Line4Column() { return Line4Column; }

   public void set_Line5Column(String value) { Line5Column = value; }
   public String get_Line5Column() { return Line5Column; }

   public String[] getLineColumns()
   {
      String[] strAry = new String[5];
      strAry[0] = Line1Column;
      strAry[1] = Line2Column;
      strAry[2] = Line3Column;
      strAry[3] = Line4Column;
      strAry[4] = Line5Column;
      return strAry;
   }

   //===========================================================================


   // ------------- Support Utils ------------------------
   private String[] cpArytoAry(String[] ary)
   {
      // keep as a ref: ArrayList<String> al =  new ArrayList<>(Arrays.asList(ary));

      // I choose not to use ary.endIndex, as there are some empty stings.
      // Likely, one at each len; but I choose to count them to be sure.

      // Count
      int val = 0;
      for (String str : ary)
      {
         str = str.trim();
         if (str.length() == 0) continue;
         val++;
      }

      // Allocate and fill
      String[] rtnAry = new String[val];
      val = 0;
      for (String str : ary)
      {
         str = str.trim();
         if (str.length() == 0) continue;
         rtnAry[val++] = str;
      }
      return rtnAry;
   }

   // Expand all 3D members of the encoded ary and
   // return all 3D zips defined by the received ranges as a String[]
   private String[] expandZones(String[] ary)
   {
      // Data is ary of 3D zip codes ranges like this: 465-468.
      // Or simply a single 3D zip:  456
      // Where I expand such to Strings 465, 466, 467, 468 one each per member
      // of the return ary

      ArrayList<Integer> al = new ArrayList<>();
      int range = -1;
      int[] rangeAry;
      for (int index = 0; index < ary.length; index++)
      {
         rangeAry = null;
         String str = ary[index];
         if (str.contains("-"))
         {
            rangeAry = this.expandRange(ary[index]);
         }
         else
         {
            range = Integer.parseInt(str);
         }

         if (rangeAry != null)
         {
            for (int inx = 0; inx < rangeAry.length; inx++)
            {
               al.add(rangeAry[inx]);
            }
         }
         else
         {
            al.add(range);
         }
      }

      String[] rtnAry = convertALTo3DStrAry(al);
      return rtnAry;
   }

   // Convert Integer to zero padded 3D String
   private String[] convertALTo3DStrAry(ArrayList<Integer> al)
   {
      String[] strAry = new String[al.size()];
      for (int inx = 0; inx < al.size(); inx++)
      {
         // Convert Integer to zero padded 3D String
         Integer val = al.get(inx);
         strAry[inx] =  String.format("%03d", val);
         //internalMsgCtrl.out("StrAry[" + inx + "] = " + strAry[inx]);
      }
      return strAry;
   }

   // Duplicate of method found in GenLabelsUtils.  Duplcation necessay
   // to resolve problems with creating GenLabelsUtils in Shared.
   private int[] expandRange(String str)
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
}
