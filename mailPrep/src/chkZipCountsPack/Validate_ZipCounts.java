package chkZipCountsPack;

import configPack.XMLData;
import libPack.*;

import java.util.*;

public class Validate_ZipCounts
{
   // Defined here and in Report_ZipCounts()
   final String delineate = "~~~";
   final String doneFlg = "###";
   Shared shared = null;
   XMLData xmlData = null;
   ExcelUtils excelUtils = null;
   InternalMsgCtrl internalMsgCtrl = null;
   MailPrepUtils mailPrepUtils = null;

   String zipCounts = "";
   String config = "";
   String zipCountFilePath = "";
   String configFilePath = "";
   //------------------------------------------------------------------------------
   // Fields initialized in initClassFields()
   private ArrayList<Integer> SCFRangeAL = null;
   private int[] addrSCFZipsAry = null;
   private int[] SCFColAry = null;
   //
   private ArrayList<Integer> ADCRangeAL = null;
   private int[] addrADCZipsAry = null;
   private int[] ADCColAry = null;
   //
   private ArrayList<Integer> ZONE1RangeAL = null;
   private int[] addrZone1ZipsAry = null;
   //
   private ArrayList<Integer> ZONE2RangeAL = null;
   private int[] addrZone2ZipsAry = null;
   private int[] ZONE2ColAry = null;
   //
   private ArrayList<Integer> ZONE3RangeAL = null;
   private int[] addrZone3ZipsAry = null;
   private int[] ZONE3ColAry = null;
   //
   private ArrayList<Integer> ZONE4RangeAL = null;
   private int[] addrZone4ZipsAry = null;
   private int[] ZONE4ColAry = null;
   //
   private ArrayList<Integer> ZONE5RangeAL = null;
   private int[] addrZone5ZipsAry = null;
   private int[] ZONE5ColAry = null;
   //
   private ArrayList<Integer> ZONE6RangeAL = null;
   private int[] addrZone6ZipsAry = null;
   private int[] ZONE6ColAry = null;
   //
   private ArrayList<Integer> ZONE7RangeAL = null;
   private int[] addrZone7ZipsAry = null;
   private int[] ZONE7ColAry = null;
   //
   private ArrayList<Integer> ZONE8RangeAL = null;
   private int[] addrZone8ZipsAry = null;
   private int[] ZONE8ColAry = null;
   //
   private ArrayList<Integer> ZONE9RangeAL = null;
   private int[] addrZone9ZipsAry = null;
   private int[] ZONE9ColAry = null;


   public Validate_ZipCounts(Shared shared)
   {
      this.shared = shared;
      xmlData = shared.getXMLData();
      excelUtils = shared.getExcelUtils();
      internalMsgCtrl = shared.getInternalMsgCtrl();
      mailPrepUtils = shared.getMailPrepUtils();

      zipCounts = shared.getZipCountsFileName();
      config = shared.getConfigFileName();
      zipCountFilePath = shared.getZipCountsFilePathName();
      configFilePath = shared.getConfigFilePathName();
   }

   //------------------------------------------------------------------------------

   public ArrayList<String> valid_ZipCounts_Main(Addr[] addrAry, String[] ZC_zonesAry,
                                                 ArrayList<String> zipCountsContentAL)
   {
      ArrayList<String> errStrAL = new ArrayList<>();

      // Initialize necessary class fields used by multiple methods
      initClassFields(addrAry, zipCountsContentAL);

      // NOTE -- The Report (Report_ZipCounts.java is DEPENDENT on
      // the string flags:  delineate, doneFlg, and the headings
      // the head string is check to start with "---"

      // IC columns should never be in error, it is simply a copy of the configurations <IC> elements
      // One would expect there to always be a one-to-one correspondence.  But check it anyway.
      // May not be a total waste of cycles.
      //System.out.println("------------------IC--------------------------");
      String colStr = ZC_zonesAry[0];
      errStrAL.add("---    IC, column: " + colStr + "  ---");
      ArrayList<Integer> icErrAL = validate_IC(colStr, zipCountsContentAL);
      // ic was written first and is DIFFERENT, int -1 is the divider, addAllIC accounts for the -1
      addAllIC(errStrAL, icErrAL);

      //System.out.println("------------------SCF-------------------------");
      colStr = xmlData.get_ZC_DEN_SCF();
      errStrAL.add("---   SCF, column: " + colStr + " ---");
      ArrayList<Integer> SCFAL = findInvalidZips(SCFRangeAL, SCFColAry);
      addAll(errStrAL, SCFAL);
      errStrAL.add(delineate);
      boolean buildHandledFlg = true;
      boolean chkHandledFlg = false;
      ArrayList<Integer> missingSCFAL = findMissingZips(SCFColAry, addrSCFZipsAry, buildHandledFlg, chkHandledFlg);
      addAll(errStrAL, missingSCFAL);
      errStrAL.add(delineate);
      ArrayList<Integer> duplicatedSCFAL = findDuplicates(SCFColAry);
      addAll(errStrAL, duplicatedSCFAL);
      errStrAL.add(delineate);

      //System.out.println("------------------ADC-------------------------");
      colStr = xmlData.get_ZC_DADC();
      errStrAL.add("---   ADC, column: " + colStr + " ---");
      ArrayList<Integer> ADCAL = findInvalidZips(ADCRangeAL, ADCColAry);
      addAll(errStrAL, ADCAL);
      errStrAL.add(delineate);
      buildHandledFlg = false;
      chkHandledFlg = true;
      ArrayList<Integer> missingADCAL = findMissingZips(ADCColAry, addrADCZipsAry, buildHandledFlg, chkHandledFlg);
      addAll(errStrAL, missingADCAL);
      errStrAL.add(delineate);
      ArrayList<Integer> duplicatedADCAL = findDuplicates(SCFColAry);
      addAll(errStrAL, duplicatedADCAL);
      errStrAL.add(delineate);

      //System.out.println("------------------ZONE2-------------------------");
      colStr = xmlData.get_ZC_ZONE2();
      errStrAL.add("--- ZONE2, column: " + colStr + " ---");
      ArrayList<Integer> ZONE2AL = findInvalidZips(ZONE2RangeAL, ZONE2ColAry);
      addAll(errStrAL, ZONE2AL);
      errStrAL.add(delineate);
      ArrayList<Integer> missingZONE2AL = findMissingZips(ZONE2ColAry, addrZone2ZipsAry, buildHandledFlg, chkHandledFlg);
      addAll(errStrAL, missingZONE2AL);
      errStrAL.add(delineate);
      ArrayList<Integer> duplicatedZONE2AL = findDuplicates(ZONE2ColAry);
      addAll(errStrAL, duplicatedZONE2AL);
      errStrAL.add(delineate);

      //System.out.println("------------------ZONE3-------------------------");
      colStr = xmlData.get_ZC_ZONE3();
      errStrAL.add("--- ZONE3, column: " + colStr + " ---");
      ArrayList<Integer> ZONE3AL = findInvalidZips(ZONE3RangeAL, ZONE3ColAry);
      addAll(errStrAL, ZONE3AL);
      errStrAL.add(delineate);
      ArrayList<Integer> missingZONE3AL = findMissingZips(ZONE3ColAry, addrZone3ZipsAry, buildHandledFlg, chkHandledFlg);
      addAll(errStrAL, missingZONE3AL);
      errStrAL.add(delineate);
      ArrayList<Integer> duplicatedZONE3AL = findDuplicates(ZONE3ColAry);
      addAll(errStrAL, duplicatedZONE3AL);
      errStrAL.add(delineate);

      //System.out.println("------------------ZONE4-------------------------");
      colStr = xmlData.get_ZC_ZONE4();
      errStrAL.add("--- ZONE4, column: " + colStr + " ---");
      ArrayList<Integer> ZONE4AL = findInvalidZips(ZONE4RangeAL, ZONE4ColAry);
      addAll(errStrAL, ZONE4AL);
      errStrAL.add(delineate);
      chkHandledFlg = false;
      ArrayList<Integer> missingZONE4AL = findMissingZips(ZONE4ColAry, addrZone4ZipsAry, buildHandledFlg, chkHandledFlg);
      addAll(errStrAL, missingZONE4AL);
      errStrAL.add(delineate);
      ArrayList<Integer> duplicatedZONE4AL = findDuplicates(ZONE4ColAry);
      addAll(errStrAL, duplicatedZONE4AL);
      errStrAL.add(delineate);

      //System.out.println("------------------ZONE5-------------------------");
      colStr = xmlData.get_ZC_ZONE5();
      errStrAL.add("--- ZONE5, column: " + colStr + " ---");
      ArrayList<Integer> ZONE5AL = findInvalidZips(ZONE5RangeAL, ZONE5ColAry);
      addAll(errStrAL, ZONE5AL);
      errStrAL.add(delineate);
      ArrayList<Integer> missingZONE5AL = findMissingZips(ZONE5ColAry, addrZone5ZipsAry, buildHandledFlg, chkHandledFlg);
      addAll(errStrAL, missingZONE5AL);
      errStrAL.add(delineate);
      ArrayList<Integer> duplicatedZONE5AL = findDuplicates(ZONE5ColAry);
      addAll(errStrAL, duplicatedZONE5AL);
      errStrAL.add(delineate);

      //System.out.println("------------------ZONE6-------------------------");
      colStr = xmlData.get_ZC_ZONE6();
      errStrAL.add("--- ZONE6, column: " + colStr + " ---");
      ArrayList<Integer> ZONE6AL = findInvalidZips(ZONE6RangeAL, ZONE6ColAry);
      addAll(errStrAL, ZONE6AL);
      errStrAL.add(delineate);
      ArrayList<Integer> missingZONE6AL = findMissingZips(ZONE6ColAry, addrZone6ZipsAry, buildHandledFlg, chkHandledFlg);
      addAll(errStrAL, missingZONE6AL);
      ArrayList<Integer> duplicatedZONE6AL = findDuplicates(ZONE6ColAry);
      errStrAL.add(delineate);
      addAll(errStrAL, duplicatedZONE6AL);
      errStrAL.add(delineate);

      //System.out.println("------------------ZONE7-------------------------");
      colStr = xmlData.get_ZC_ZONE7();
      errStrAL.add("--- ZONE7, column: " + colStr + " ---");
      ArrayList<Integer> ZONE7AL = findInvalidZips(ZONE7RangeAL, ZONE7ColAry);
      addAll(errStrAL, ZONE7AL);
      errStrAL.add(delineate);
      ArrayList<Integer> missingZONE7AL = findMissingZips(ZONE7ColAry, addrZone7ZipsAry, buildHandledFlg, chkHandledFlg);
      addAll(errStrAL, missingZONE7AL);
      ArrayList<Integer> duplicatedZONE7AL = findDuplicates(ZONE7ColAry);
      errStrAL.add(delineate);
      addAll(errStrAL, duplicatedZONE7AL);
      errStrAL.add(delineate);

      //System.out.println("------------------ZONE8-------------------------");
      colStr = xmlData.get_ZC_ZONE8();
      errStrAL.add("--- ZONE8, column: " + colStr + " ---");
      ArrayList<Integer> ZONE8AL = findInvalidZips(ZONE8RangeAL, ZONE8ColAry);
      addAll(errStrAL, ZONE8AL);
      errStrAL.add(delineate);
      ArrayList<Integer> missingZONE8AL = findMissingZips(ZONE8ColAry, addrZone8ZipsAry, buildHandledFlg, chkHandledFlg);
      addAll(errStrAL, missingZONE8AL);
      errStrAL.add(delineate);
      ArrayList<Integer> duplicatedZONE8AL = findDuplicates(ZONE8ColAry);
      addAll(errStrAL, duplicatedZONE8AL);
      errStrAL.add(delineate);

      //System.out.println("------------------ZONE9-------------------------");
      colStr = xmlData.get_ZC_ZONE9();
      errStrAL.add("--- ZONE9, column: " + colStr + " ---");
      ArrayList<Integer> ZONE9AL = findInvalidZips(ZONE9RangeAL, ZONE9ColAry);
      addAll(errStrAL, ZONE9AL);
      errStrAL.add(delineate);
      ArrayList<Integer> missingZONE9AL = findMissingZips(ZONE9ColAry, addrZone9ZipsAry, buildHandledFlg, chkHandledFlg);
      addAll(errStrAL, missingZONE9AL);
      errStrAL.add(delineate);
      ArrayList<Integer> duplicatedZONE9AL = findDuplicates(ZONE9ColAry);
      addAll(errStrAL, duplicatedZONE9AL);
      errStrAL.add(delineate);

      // Terminate for report handling
      // See "private static final doneFlg" in Report_ZipCounts.java
      errStrAL.add(doneFlg);

      return errStrAL;
   }

   // Runs through the ZipCounts IC column and compares against the config IC list.
   // There should not be any zips found in IC column not found in the config IC list.
   // Then the reverse, runs through the config IC list and compares against the ZipCounts IC Column.
   // There should not be any zips found in the config IC list not found in the IC column.
   // There should never be any errors in the differences, one should be a copy of the other.
   // But check... just to make sure... a waste of cycles... but we have cycles to spare.
   private ArrayList<Integer> validate_IC(String colStr, ArrayList<String> zipCountsContentAL)
   {
      ArrayList<Integer> icErrAL = new ArrayList<>();

      // The order of ZC_ZoneAry:  IC, Den_SCF, DADC, Zone2-Zone9
      ArrayList<String> colZipAL = excelUtils.CSVToColumn(colStr, zipCountsContentAL);

      int[] colZipAry = excelUtils.zip_StrColToIntCol(colZipAL);
      // It should be sorted... make sure. (it doesn't even need to be sorted; but I want it so.)
      Arrays.sort(colZipAry);

      String[] icStrAry = xmlData.get_ic();
      ArrayList<String> icStrAL = new ArrayList<String>(icStrAry.length);
      Collections.addAll(icStrAL, icStrAry);
      int[] icZipAry = excelUtils.zip_StrColToIntCol(icStrAL);
      // It should be sorted... make sure.
      Arrays.sort(icZipAry);

      //------------------------------------------------------------------------------
      // TODO -- I need to check this out!!!
      //
      // FIXIT -- I think -- the following will only find one error
      //       a second error may not be caught.  (not tested)
      //       I think I should revise this to use the code checkForInvalidZipCodes()
      // I ran one test, and it looks like it may catch multiple instances.
      // Main thing it:  IC is 5-digit column and config... others are 3 config and 5 column

      // Runs through the ZipCounts IC column and compares against the config IC list.
      for (int zip : colZipAry)
      {
         // Search the valid list of IC zip codes (icAry) with each zip code in the IC column
         int index = Arrays.binarySearch(icZipAry, zip);
         if (index < 0)
         {
            // If not found it is an error
            icErrAL.add(zip);
         }
      }
      icErrAL.add(-1);
      //------------------------------------------------------------------------------
      // runs through the config IC list and compares against the ZipCounts IC list.
      for (int zip : icZipAry)
      {
         // Search the valid list of IC zip codes (icAry) with each zip code in the IC column
         int index = Arrays.binarySearch(colZipAry, zip);
         if (index < 0)
         {
            // If not found it is an error
            icErrAL.add(zip);
         }
      }
      //------------------------------------------------------------------------------

      icErrAL.add(-1);
      ArrayList<Integer> duplicatedSCFAL = findDuplicates(SCFColAry);
      icErrAL.add(-1);

      return icErrAL;
   }
   //--------------------------------------------------------------------------------
   // Support for methods above
   private static int base =0;
   private static int inx = 0;    // steps through configAL (the zones configured zones (3-Digits))
   private static int index = 0;  // steps through colAry (the zip codes stored in the Excel zone column (5-Digits))
   private static int zip = 0;
   private static int limit = 0;

   protected enum State
   {
      START {
         @Override
         protected State execute(ArrayList<Integer> configAL, int[] colAry, ArrayList<Integer> errorAL) {
            // create the 5-digit of the config zone base
            base = (inx < configAL.size()) ? configAL.get(inx) : -1;
            if (base==-1) {
               return DONE;
            } // exit
            base *= 100;
            limit = base + 100;
            //System.out.print("(" + base + "-" + limit + ")");
            return NEXT_COLUMN_VALUE;
         }
      },
      NEXT_COLUMN_VALUE {
         @Override protected State execute(ArrayList<Integer> configAL, int[] colAry, ArrayList<Integer> errorAL) {

            // Make sure we have not exhausted the Zones column of zip codes.
            if (index==colAry.length) return DONE;

            // get the next member of colAry
            zip = colAry[index];
            //System.out.println(zip);
            return TST_GT_BASE;
         }
      },
      TST_GT_BASE  {
         @Override protected State execute(ArrayList<Integer> configAL, int[] colAry, ArrayList<Integer> errorAL) {
            // Compare for a valid zip
            // valid zip IS_GT_BASE, invalid zip state = IS_LT_BASE
            return (zip >= base) ? IS_GT_BASE : IS_LT_BASE;
         }
      },
      IS_GT_BASE {
         @Override protected State execute(ArrayList<Integer> configAL, int[] colAry, ArrayList<Integer> errorAL) {
            return (zip < limit) ? IS_LT_LIMIT : IS_GT_LIMIT;
         }
      },
      IS_LT_BASE {
         @Override protected State execute(ArrayList<Integer> configAL, int[] colAry, ArrayList<Integer> errorAL) {
            // this is invalid
            //System.out.println("\nBad:   " + zip);
            errorAL.add(zip);
            index++;

            // We may have stepped through the configured zones hunting for
            // an acceptable zone, not to have found it.  Thus, all the
            // configured zones are spent.  I hate to start over; but
            // I don't see a way to save the inx I need to save.
            inx = 0;
            return START;
         }
      },
      IS_LT_LIMIT {
         @Override protected State execute(ArrayList<Integer> configAL, int[] colAry, ArrayList<Integer> errorAL) {
            // is also smaller... is within range
            // move onto the next in the column and just keep going with
            // the configured array list.
            //System.out.println("\nGood:  "+ zip);
            index++;
            return START;
         }
      },
      IS_GT_LIMIT {
         @Override protected State execute(ArrayList<Integer> configAL, int[] colAry, ArrayList<Integer> errorAL) {
               // The column zip was not within the current range
               // step to the next range with the same zip
               inx++;
               return (inx < configAL.size()) ? START : IS_LT_BASE;
         }
      },
      DONE {
         @Override
         protected State execute(ArrayList<Integer> configAL, int[] colAry, ArrayList<Integer> errorAL)
         {
            // re-initialize the statics for the next pass through
            base = 0;
            inx = 0;    // steps through configAL (the zones configured zones (3-Digits))
            index = 0;  // steps through colAry (the zip codes stored in the Excel zone column (5-Digits))
            zip = 0;
            limit = 0;
            return HALT;
         }
      },
      HALT {
         @Override
         protected State execute(ArrayList<Integer> configAL, int[] colAry, ArrayList<Integer> errorAL)
         { return null; }
      };

      //------------------------------------------------------------
      // All cases above must implement this abstract
      // Input:  configAL and colAry are the data process, passed in
      // OutPut: errorAL is the data built during processing, passed out by reference
      protected abstract State execute(ArrayList<Integer> configAL, int[] colAry, ArrayList<Integer> errorAL);
   }

   private ArrayList<Integer> findInvalidZips(ArrayList<Integer> configAL, int[] colAry)
   {
      ArrayList<Integer> errorAL = new ArrayList<>();
      State currentState = State.START;
      while(currentState != State.HALT) {
         currentState = currentState.execute(configAL, colAry, errorAL);
         //System.out.println(currentState);
      }
      return errorAL;
   }

   //-------------------------------------------------------------------
   private int[] HandledAry = null;
   private ArrayList<Integer> findMissingZips(int[] zipColAry, int[] zipsAry, boolean buildHandledFlg, boolean chkHandledFlg)
   {
      // The little hack, which I am not proud of, takes care of an overlap problem between SCF and near zones.
      // certainly zone2 in Denver, and perhaps in zone3 else where.  SCF is defined 800-812, ADC is defined 800-812
      // and Zone1 is defined 800-807, 811-812.  Thus (at this moment) 80911, 80915 and 81007 are reported missing
      // from Zone2; but were previously accounted for in SCF.
      if (buildHandledFlg)
      {
         HandledAry = Arrays.copyOf(zipsAry, zipsAry.length);
      }

      ArrayList<Integer> missingAL = new ArrayList<>();
      for (int zip : zipsAry)
      {
         if (chkHandledFlg)
         {
            if (Arrays.binarySearch(HandledAry, zip) > 0) continue;
         }

         if (Arrays.binarySearch(zipColAry, zip) < 0) missingAL.add(zip);
      }
      return missingAL;
   }

   //-------------------------------------------------------------------
   // taken from https://stackoverflow.com/questions/7414667/identify-duplicates-in-a-list
   // with my changes  just to use set to find duplicates
   public ArrayList<Integer> findDuplicates(int[] colAry)
   {
      ArrayList<Integer> errAL = new ArrayList<>();
      final Set<Integer> set1 = new HashSet<>();

      ArrayList<Integer> doneAL = new ArrayList<>();

      for (Integer val : colAry)
      {
         if (set1.add(val) == false)
         {
            // if a zip is duplicated multiple times I don't want to report it but once.
            if (doneAL.contains(val)) continue;

            // This is a duplicate
            errAL.add(val);
            doneAL.add(val);
         }
      }
      return errAL;
   }

   //-------------------------------------------------------------------
   // Initialize class fields used by multiple members of this class
   private void initClassFields(Addr[] addrAry, ArrayList<String> zipCountsContentAL)
   {
      //----------------------------------------------------------------------------
      // IC -- IC is different from the others.  Both configured and zipCount columns are 5-digits
      //       in the others the configured is 3-digits and the columns are 5-digits
      //       I initialize for IC within
      //----------------------------------------------------------------------------
      // SCF
      // The string is currently a single range (800-812) (and has been for a long time)
      // BUT... it could be a split range (800-810, 812).  The possibility is accounted for.
      String SCFStr = xmlData.get_DEN_SCF();
      String SCFcol = xmlData.get_ZC_DEN_SCF();
      SCFRangeAL = supportInit1RangeAL(SCFStr);
      addrSCFZipsAry = supportInitAddrStrZip(addrAry, "SCF");
      SCFColAry = supportInitColumnAry(SCFcol, zipCountsContentAL);

      //----------------------------------------------------------------------------
      // ADC
      // (There is a one to one correspondence between DEN_SCF and DEN_ADC. SCF is less expensive,
      // thus, ADC is currently empty.  BUT, the code is here, if ACD comes become useful again.)
      // The string is currently a single range (800-812) (and has been for a long time)
      // BUT... it could be a split range (800-810, 812).  The possibility is accounted for.
      String ADCStr = xmlData.get_DADC();
      String ADCcol = xmlData.get_ZC_DADC();
      ADCRangeAL = supportInit1RangeAL(ADCStr);
      addrADCZipsAry = supportInitAddrStrZip(addrAry, "ADC");
      ADCColAry = supportInitColumnAry(ADCcol, zipCountsContentAL);

      //----------------------------------------------------------------------------
      // Zone1
      // There isn't a zone1 zip Counts column
      // Zone1 is part of IC, SCF and ADC
      String[] ZONE1Ary = xmlData.get_ZONE1();
      ZONE1RangeAL = supportInit2RangeAL(ZONE1Ary);
      addrZone1ZipsAry = supportInitAddrZoneZip(addrAry, "ZONE1");

      //----------------------------------------------------------------------------
      // Zone2
      String[] ZONE2Ary = xmlData.get_ZONE2();
      String ZONE2col = xmlData.get_ZC_ZONE2();
      ZONE2RangeAL = supportInit2RangeAL(ZONE2Ary);
      ZONE2ColAry = supportInitColumnAry(ZONE2col, zipCountsContentAL);
      addrZone2ZipsAry = supportInitAddrZoneZip(addrAry, "ZONE2");

      //----------------------------------------------------------------------------
      // Zone3
      String[] ZONE3Ary = xmlData.get_ZONE3();
      String ZONE3col = xmlData.get_ZC_ZONE3();
      ZONE3RangeAL = supportInit2RangeAL(ZONE3Ary);
      ZONE3ColAry = supportInitColumnAry(ZONE3col, zipCountsContentAL);
      addrZone3ZipsAry = supportInitAddrZoneZip(addrAry, "ZONE3");

      //----------------------------------------------------------------------------
      // Zone4
      String[] ZONE4Ary = xmlData.get_ZONE4();
      String ZONE4col = xmlData.get_ZC_ZONE4();
      ZONE4RangeAL = supportInit2RangeAL(ZONE4Ary);
      ZONE4ColAry = supportInitColumnAry(ZONE4col, zipCountsContentAL);
      addrZone4ZipsAry = supportInitAddrZoneZip(addrAry, "ZONE4");

      //----------------------------------------------------------------------------
      // Zone5
      String[] ZONE5Ary = xmlData.get_ZONE5();
      String ZONE5col = xmlData.get_ZC_ZONE5();
      ZONE5RangeAL = supportInit2RangeAL(ZONE5Ary);
      ZONE5ColAry = supportInitColumnAry(ZONE5col, zipCountsContentAL);
      addrZone5ZipsAry = supportInitAddrZoneZip(addrAry, "ZONE5");

      //----------------------------------------------------------------------------
      // Zone6
      String[] ZONE6Ary = xmlData.get_ZONE6();
      String ZONE6col = xmlData.get_ZC_ZONE6();
      ZONE6RangeAL = supportInit2RangeAL(ZONE6Ary);
      ZONE6ColAry = supportInitColumnAry(ZONE6col, zipCountsContentAL);
      addrZone6ZipsAry = supportInitAddrZoneZip(addrAry, "ZONE6");

      //----------------------------------------------------------------------------
      // Zone7
      String[] ZONE7Ary = xmlData.get_ZONE7();
      String ZONE7col = xmlData.get_ZC_ZONE7();
      ZONE7RangeAL = supportInit2RangeAL(ZONE7Ary);
      ZONE7ColAry = supportInitColumnAry(ZONE7col, zipCountsContentAL);
      addrZone7ZipsAry = supportInitAddrZoneZip(addrAry, "ZONE7");

      //----------------------------------------------------------------------------
      // Zone8
      String[] ZONE8Ary = xmlData.get_ZONE8();
      String ZONE8col = xmlData.get_ZC_ZONE8();
      ZONE8RangeAL = supportInit2RangeAL(ZONE8Ary);
      ZONE8ColAry = supportInitColumnAry(ZONE8col, zipCountsContentAL);
      addrZone8ZipsAry = supportInitAddrZoneZip(addrAry, "ZONE8");

      //----------------------------------------------------------------------------
      // Zone9
      String[] ZONE9Ary = xmlData.get_ZONE9();
      String ZONE9col = xmlData.get_ZC_ZONE9();
      ZONE9RangeAL = supportInit2RangeAL(ZONE9Ary);
      ZONE9ColAry = supportInitColumnAry(ZONE9col, zipCountsContentAL);
      addrZone9ZipsAry = supportInitAddrZoneZip(addrAry, "ZONE9");
   }

   private ArrayList<Integer> supportInit1RangeAL(String range)
   {
      String[] rangeAry = range.split(",");
      ArrayList<Integer> rangeAL = new ArrayList<>();
      for (String str : rangeAry)
      {
         int[] expandedAry = mailPrepUtils.expandRange(str);
         for (int val : expandedAry) rangeAL.add(val);
      }
      // Fields should be sorted in Excel; but maybe not... ensure sorted
      Collections.sort(rangeAL);
      return rangeAL;
   }

   private ArrayList<Integer> supportInit2RangeAL(String[] range)
   {
      ArrayList<Integer> rangeAL = new ArrayList<>();
      for (String rangeStr : range)
      {
         String[] rangeAry = rangeStr.split(",");
         for (String str : rangeAry)
         {
            int[] expandedAry = mailPrepUtils.expandRange(str);
            for (int val : expandedAry) rangeAL.add(val);
         }
      }
      // Fields should be sorted in Excel; but maybe not... ensure sorted
      Collections.sort(rangeAL);
      return rangeAL;
   }

   private int[] supportInitColumnAry(String countsCol, ArrayList<String> zipCountsContentAL)
   {
      ArrayList<String> columnAL = excelUtils.CSVToColumn(countsCol, zipCountsContentAL);
      int[] columnAry = excelUtils.zip_StrColToIntCol(columnAL);
      // Fields should be sorted in Excel; but maybe not... ensure sorted
      Arrays.sort(columnAry);
      return columnAry;
   }

   private int[] supportInitAddrZoneZip(Addr[] addrAry, String zoneStr)
   {
      ArrayList<String> zoneZipAL = new ArrayList<>();
      for (Addr addr : addrAry)
      {
         if (addr.zone.equals(zoneStr))
         {
            if (zoneZipAL.contains(addr.zip)) continue;
            zoneZipAL.add(addr.zip);
         }
      }
      int[] zoneZipAry = excelUtils.zip_StrColToIntCol(zoneZipAL);
      return zoneZipAry;
   }

   // String may be whatever; but the code is written to catch "SCF" and "ADC"
   // compares str against addr.trayTag
   private int[] supportInitAddrStrZip(Addr[] addrAry, String str)
   {
      ArrayList<String> StrZipAL = new ArrayList<>();
      for (Addr addr : addrAry)
      {
         if (addr.trayTag.equals(str))
         {
            if (StrZipAL.contains(addr.zip)) continue;
            StrZipAL.add(addr.zip);
         }
      }
      int[] StrZipAry = excelUtils.zip_StrColToIntCol(StrZipAL);
      return StrZipAry;
   }

   //------------------------------------------------------------------------------
   // IC is different from the others, The others have two returns (invalid and missing)
   // IC only has one return with the two separated by a flag (-1).
   private void addAllIC(ArrayList<String> errStrAL, ArrayList<Integer> errIntAL)
   {
      // I had two choices:
      //    1) I could put the test for -1 in addALL() and waste the conditional check
      //       on all but IC
      //    2) Separate to a separated method
      // I chose 2

      for (int val : errIntAL)
      {
         if (val==-1)
         {
            errStrAL.add(delineate);
            continue;
         }
         String str = Integer.toString(val);
         errStrAL.add(str);
      }
   }

   public void addAll(ArrayList<String> errStrAL, ArrayList<Integer> errIntAL)
   {
      for (int val : errIntAL)
      {
         String str = Integer.toString(val);
         errStrAL.add(str);
      }
   }
}
