package configPack;

//190113
// Testing during creation of XMLParse.java and XMLData.java

// TO Run,
//   XMLTest xmlTest = new XMLTest();
//   xmlTest.runTest();

// The code is no longer complete.  All were there at one time... but not any more


import libPack.InternalFatalError;
import libPack.Shared;

public class XMLTest
{
   public void runTest() throws InternalFatalError
   {
      Shared shared = new Shared();
      XMLParse xmlParse = new XMLParse(shared);
      XMLData xmlData = xmlParse.xmlParse();

      // System.out.println("-------------- BulletinAssembly -------------");
      // System.out.println("DateReminder:  " + xmlData.get_DateReminder());
      // System.out.println("LastDataCorrection:  " + xmlData.get_LastDataCorrection());
      // System.out.println("ReminderUpdated:  " + xmlData.get_ReminderUpdated());
      // System.out.println("State:  " + xmlData.get_State());
      // System.out.println("DEN_SCF:  " + xmlData.get_DEN_SCF());
      // System.out.println("GJ_SCF:  " + xmlData.get_GJ_SCF());
      // System.out.println("DADC:  " + xmlData.get_DADC());

      String[] icAry = xmlData.get_ic();
      // for (String str : icAry) System.out.println("ic:  [" + str + "]");
      String[] omxAry = xmlData.get_omx();
      // for (String str : omxAry) System.out.println("omx:  [" + str + "]");
      String[] priorityAry = xmlData.get_priority();
      // for (String str : priorityAry) System.out.println("priority:  [" + str + "]");
      String[] orderAry = xmlData.get_order();
      // for (String str : orderAry) System.out.println("order:  [" + str + "]");
      String[] ZONE1Ary = xmlData.get_ZONE1();
      // for (String str : ZONE1Ary) System.out.println("ZONE1:  [" + str + "]");
      String[] ZONE2Ary = xmlData.get_ZONE2();
      // for (String str : ZONE2Ary) System.out.println("ZONE2:  [" + str + "]");
      String[] ZONE3Ary = xmlData.get_ZONE3();
      // for (String str : ZONE3Ary) System.out.println("ZONE3:  [" + str + "]");
      String[] ZONE4Ary = xmlData.get_ZONE4();
      // for (String str : ZONE4Ary) System.out.println("ZONE4:  [" + str + "]");
      String[] ZONE5Ary = xmlData.get_ZONE5();
      // for (String str : ZONE5Ary) System.out.println("ZONE5:  [" + str + "]");
      String[] ZONE6Ary = xmlData.get_ZONE6();
      // for (String str : ZONE6Ary) System.out.println("ZONE6:  [" + str + "]");
      String[] ZONE7Ary = xmlData.get_ZONE7();
      // for (String str : ZONE7Ary) System.out.println("ZONE7:  [" + str + "]");
      String[] ZONE8Ary = xmlData.get_ZONE8();
      // for (String str : ZONE8Ary) System.out.println("ZONE8:  [" + str + "]");


      // Zip Counts
      System.out.println("ZC_IC:  " + xmlData.get_ZC_IC());
      System.out.println("ZC_DEN_SCF:  " + xmlData.get_ZC_DEN_SCF());
      System.out.println("ZC_DADC:  " + xmlData.get_ZC_DADC());
      System.out.println("ZC_ZONE2:  " + xmlData.get_ZC_ZONE2());
      System.out.println("ZC_ZONE3:  " + xmlData.get_ZC_ZONE3());
      System.out.println("ZC_ZONE4:  " + xmlData.get_ZC_ZONE4());
      System.out.println("ZC_ZONE5:  " + xmlData.get_ZC_ZONE5());
      System.out.println("ZC_ZONE6:  " + xmlData.get_ZC_ZONE6());
      System.out.println("ZC_ZONE7:  " + xmlData.get_ZC_ZONE7());
      System.out.println("ZC_ZONE8:  " + xmlData.get_ZC_ZONE8());
      // AddressList
      System.out.println("Addr_LastName:  " + xmlData.get_Addr_LastName());
      System.out.println("Addr_FirstName:  " + xmlData.get_Addr_FirstName());
      System.out.println("Addr_OtherName:  " + xmlData.get_Addr_OtherName());
      System.out.println("Addr_Address:  " + xmlData.get_Addr_Address());
      System.out.println("Addr_City:  " + xmlData.get_Addr_City());
      System.out.println("Addr_State:  " + xmlData.get_Addr_State());
      System.out.println("Addr_Zip:  " + xmlData.get_Addr_Zip());
      System.out.println("Addr_Plus4:  " + xmlData.get_Addr_Plus4());
      System.out.println("Addr_Renewal:  " + xmlData.get_Addr_Renewal());
      // MMH -- MailMergeHeader
      System.out.println("MMH_LastName:  " + xmlData.get_MMH_LastName());
      System.out.println("MMH_FirstName:  " + xmlData.get_MMH_FirstName());
      System.out.println("MMH_OtherName:  " + xmlData.get_MMH_OtherName());
      System.out.println("MMH_Address:  " + xmlData.get_MMH_Address());
      System.out.println("MMH_City:  " + xmlData.get_MMH_City());
      System.out.println("MMH_State:  " + xmlData.get_MMH_State());
      System.out.println("MMH_Zip:  " + xmlData.get_MMH_Zip());
      System.out.println("MMH_Plus4:  " + xmlData.get_MMH_Plus4());
      System.out.println("MMH_Renewal:  " + xmlData.get_MMH_Renewal());
   }
}
