package mainPack;


import assembly.*;
import buildAddr.InitAddrList;
import buildAddr.Verify_Addr;
import configPack.XMLData;
import fileWork.FileWork;
import genLabelsPack.GenCheckList;
import genLabelsPack.GenCuttingGuide;
import libPack.*;
import libPack.ClassesPack.Tray;
import libPack.InternalMsgCtrl.errKey;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/* instantiate -- create a instance of */
/**************************************************************************
 Class Addr is the data of the Master CSV file broken into usable fields.
 Class addrZipAry is an array of Addr objects sorted by zip
 (As coding progressed to other members of mailSet, addrZipAry became
 simply addrAry)
 ***************************************************************************/
/**************************************************************************
 * How it works:
 * GenLabels is very straight forward and liner
 * 1) Read options, resolve folders and read the CSV file
 * 2) Class initAddrList build addrZipAddr:  addressListAL converted to
 *    the class addr into an the array t
 *    addrZipAry is sorted in zip order
 *    The class addr holds all the necessary data of the CSV file
 * 3) Create an output folder:  a place to store stuff
 * 4) Sort addrZipAry in Bundle order (note, bundles remains sorted in zip order)
 *    The remaining processing is on addrBundleAry
 * 5) The Class SplitUtils is a "minor main" much the remainder of this "how it works"
 *    is controlled in SplitUtils
 * 6) Create TrayAL which splits zip bundles into USPS trays
 * 7) Count the zip codes: get a count of each zip code (stored in class Trays)
 * 8) Segment the bundles into packaging for labeling (sized for assembly label envelopes)
 *    The Class Segment takes over from here,
 *    Segment contains the trayTag, and indices into addrBundleAry for each segment
 *    Renewals for each segment are found
 *    Tray.zipCntAry is broken into segments
 *    The sequence label string is created and stored per segment
 * 9) Two CSV files are produced:
 *    Renewals are pulled out of addrbundleAry (flagged via -1) and a Renewals.csv is stored
 *    The remainder are stored in Master.csv
 *    As Master.csv is create sequence labels fill in any uneven segments.
 *
 ***************************************************************************/
/* These are some naming conventions I should (but do not) adhere to.
 * Thoughts:  filename is a simply a file (not the folders leading to it).
 *            path is a set of folders (leading to a file).
 *            pathname is path + filename
 *            path is considered an absolute path.
 *            absolute path normally includes the file; but on occasion
 *               just the folder path (when so, is specified)
 *            writepath is an output pathname
 ***************************************************************************/

// A reminder for cut-and-paste... under menu run choose edit configuration
// -f /work/Intellij/BulletinAssembly/GenLabels/src/main/resources/data (plus -nr other options)

/***************************************************************************/

// FIXIT -- I may not handle a quoted comma correctly.
    //      address "3333 SomeWind Lane, Apt 333" is properly double quoted
    //      yet the telephone number appears in in plus-4 column.
    //      Once the comma was removed in the DB, the error was corrected.

// TODO Rename to GenLabels_main... tried Refactor and the results were not acceptable
public class GenLabels_Main
{
    public static void main(String[] args) throws InternalFatalError {
        // There is ONE and ONLY ONE instance of Shared
        Shared shared = new Shared();
        //------------------------------------------

        shared.setAppName("genLabels");

        // Setup centralized user messages handler.
        // All System.out System.err messages should be passed through errorMegCtrl
        InternalMsgCtrl internalMsgCtrl = shared.getInternalMsgCtrl();
        String configFilePathName = shared.getConfigFilePathName();

        /*********************************************/
        //-- 1) Read user arguments

        try {
            // Argument processing is done with jar args4j and
            // the results are stored in Shared
            CLIOptions cliOptions = shared.getCLIOptions();
            if (cliOptions != null) {
                if ((cliOptions.cliOptionsMain(args)) == false)
                    System.exit(0);
            }
        } catch (IOException ioExcp) {
            //System.err.println(ioExcp.toString());
            internalMsgCtrl.err(errKey.ExceptionMsg, false, ioExcp.toString());
            System.exit(-1);
        }

        /*********************************************/
        // Keep as an example
        //Locale locale = new Locale("en", "EN");
        //ResourceBundle tstRB = ResourceBundle.getBundle("resourceBundles.ErrorMsgs");
        //String jkj = tstRB.getString("test1");
        //jkj = tstRB.getString("test2");

        /*********************************************/
        //-- 2) Read the configuration file

        // Read the BulletinAssembly configuration file "$Home/.BulletinAssembly/config.xml"
        try {
            shared.parseConfigData();
            XMLData xmlData = shared.getXMLData();
            if (xmlData == null) {
                internalMsgCtrl.err(errKey.FatalError, false,
                        "Unable to read the required configuration file.");
                internalMsgCtrl.err(errKey.FatalError, false, "Or, an error while reading " + shared.getConfigFileName());
                internalMsgCtrl.err(errKey.Message, false, "-------------------------------------------------------");
                internalMsgCtrl.err(errKey.Message, false, "genConfigFile may be used to generate a new " + shared.getConfigFileName());
                internalMsgCtrl.err(errKey.Message, false, "-------------------------------------------------------");

                String appName = shared.getAppName();
                internalMsgCtrl.out("Obtain the configuration file, edit as necessary, and rerun " + appName + ".");
                System.exit(-1);
            }
        } catch (Exception excp) {
            internalMsgCtrl.err(errKey.FatalError, false, "Failed to read the configuration file.");
            //System.err.println(excp.toString());
            //internalMsgCtrl.err(errKey.ExceptionMsg, false, excp.toString());
            System.exit(-1);
        }

        /*********************************************/
        //-- 3 Get and check for existence the data path

        FileWork fileWork = shared.getFileWork();

        String dataFolderPathName = shared.getDataFolderPathName();
        if (fileWork.isFolder(dataFolderPathName) == false) {
            String errMsg = "Is not a directory:  [" + dataFolderPathName + "]";
            internalMsgCtrl.err(errKey.FatalError, false, errMsg);
            System.exit(-1);
        }
        if (fileWork.fileExists(dataFolderPathName) == false) {
            String errMsg = "Unable to find data directory:  " + dataFolderPathName;
            internalMsgCtrl.err(errKey.FatalError, false, errMsg);
            System.exit(-1);
        }

        /*********************************************/
        //-- 4 Read Bulletin Assemblies Excel AddressList worksheet pre-saved as a CSV file

        String addressListFilename = shared.getAddressListFileName();
        String folderBreak = shared.getFolderBreak();
        String filePath = dataFolderPathName + folderBreak + addressListFilename;

        if (fileWork.fileExists(filePath) == false) {
            String errMsg = "Unable to find csv file:  " + filePath;
            internalMsgCtrl.err(errKey.FatalError, false, errMsg);
            System.exit(-1);
        }

        ArrayList<String> addressListAL = null;
        try {
            addressListAL = fileWork.readFile(filePath);

            Verify_Addr verifyAddrCSV = new Verify_Addr(shared);
            if (verifyAddrCSV.verifyAddrCSV_Main(addressListAL) == false) {
                String errMsg = "invalid address CSV file:  " + filePath;
                internalMsgCtrl.err(errKey.FatalError, false, errMsg);
            }
        } catch (InternalFatalError exc) {
            System.exit(-1);
        }

        /*********************************************/
        //-- 5) Process the AddressList CSV file and store in an array of class Addr
        //      sorted by zip code
        //      Three lower classes are used:  CheckRenewal, ZonePerZip and TrayTagPerZip
        //      These fill out all the fields in Class Addr (zip, zone, trayTag and bundle
        Addr[] addrZipAry = null;
        try {
            InitAddrList initAddrList = new InitAddrList(shared);
            addrZipAry = initAddrList.buildAddr(addressListAL);
            if (addrZipAry == null) {
                internalMsgCtrl.err(errKey.FatalError, false, "Unable to build the address list.");
                System.exit(-1);
            }
        } catch (InternalFatalError exc) {
            System.exit(-1);
        }

        /*********************************************/
        //-- 6)  Create a output folder named by the assembly data
        // Create a folder to store the output into
        MailPrepUtils genLabelsUtils = shared.getGenLabelsUtils();

        String fourthTueStr = genLabelsUtils.getForthTueStr();
        String datedOutputFoldername = String.format("%s_Bulletin", fourthTueStr);
        shared.setDatedOutputFolderName(datedOutputFoldername);


        String datedOutputPath = genLabelsUtils.getDatedOutputPath();
        try {
            fileWork.createPath(datedOutputPath, false);
        } catch (InternalFatalError err) {
            // The error was reported
            return;
        }
        // Store in shared
        shared.setDatedOutputFolderName(datedOutputFoldername);

        /*********************************************/
        // Numbers 7-xx Brake up the data (SplitUtils it) into ever decreasing
        // and specific SEGMENTS... eventually down to labeling envelopes
        // suitable for ease of applying labels in pre-sorted order
        // during Bulletin assembly.
        /*********************************************/
        //-- 7) addrZipAry (see 5 above) is broken into zip sorted bundles.
        //      of addrBundleAry (same data, different sort)
        //      Bundles are the UPS priority nomenclature
        //      Currently: MXD, OMX, SCF (my names:  DADC 3D and 5D)
        //      (see longer comment in libPack.ClassesPack.Tray)

        Addr[] addrBundleAry = null;
        SplitUtils splitUtils = new SplitUtils(shared);
        try {
            addrBundleAry = splitUtils.createAddrBundleAry(shared, addrZipAry);
            if (addrBundleAry == null) {
                internalMsgCtrl.err(errKey.FatalError, false, "addrBundleAry is null");
                System.exit(-1);
            }
        } catch (InternalFatalError excp) {
            System.exit(-1);
        }

        /*********************************************/
        //-- 8) Further break bundles into bounded zips  (upper to lower limits of
        //      individual zip code.  Groups 3D's and 5D's together.

        //-----------------------------------------------------------
        // Bundles are the main trayTag division as in 5D, 3D, SCF, ADC, OMX, MXD
        // Addr contains bundle classification.
        // The next division is trayTag.  Some bundles form a single trayTag, others,
        // notably 3D are sub divided (as in 800, 801...)
        // Find the bounds... startIndex and endIndex of each bundle
        // trayAL is sorted in configured processing order (bundle order)
        // Note that each bundles is in zip order.
        SplitByBundle splitByBundle = new SplitByBundle(shared);
        ArrayList<Tray> trayAL = splitByBundle.splitBundle(addrBundleAry);

        /*********************************************/
        //-- 9) Obtain the counts of each zip code

        // Count zip codes per Tray.trayTag tray String
        // Return is modified libPack.ClassesPack.tray.zipCntAry (trayAL)
        CntZipsPerTray cntZipPerTray = new CntZipsPerTray(shared);
        cntZipPerTray.cntZipEntry(trayAL, addrBundleAry);

        /*********************************************/
        //-- 10) Split the data into packaging sizes suitable to feed to generateLabelCSVs.
        //   Brace's of Start and End indices with tray.zipCntAry.
        // (libPack.ClassesPack.Brace is a packaging class of paired indices into Tray.zipCntAry)

        // The return is tray.bracedAL: an array list of segments consisting
        BraceZipCntAry braceZipCntAry = new BraceZipCntAry(shared, splitUtils);
        braceZipCntAry.segmentZipCntAry(trayAL, addrBundleAry);

        /*********************************************/
        //-- 11) Order the segments of Tray.bracedAL and sequence them.
        //       Establish start and len of each segment within addrBundleAry

        // A segment is a packaging class, an intermediate step in sorting trays and envelopes
        FillSegmentAry fillSegmentAry = new FillSegmentAry(shared, splitUtils);
        ClassesPack.Segment[] segmentAry = fillSegmentAry.fillSegments(trayAL, addrBundleAry);

        /*********************************************/
        //-- 12) Build a list of Segment labels (Legacy name:  sequence)
        //       Segments labels are used to label assembly envelopes with the
        //       segment (set of labels) packaged in the envelope.
        //       The data is a CSV list under AddressList headings, such that
        //       MS Words Label templet will print them out.  The data is blended
        //       into the current CSV data (see BlenCurrentAndSegmentLists (step 13))

        BuildSegmentLabels buildSegmentLabels = new BuildSegmentLabels(shared);
        ArrayList<String> segmentLabelAL = buildSegmentLabels.buildSegmentLabels_Main(
                segmentAry, addrBundleAry);

        // This must be save now for the end report, as the segmentLabelAL is destroyed as used
        int segmentLabelCnt = segmentLabelAL.size();
        /*********************************************/
        //-- 13) Build two ArrayList of data: 1) Renewals and 2) Current
        //
        //       The data is a CSV list under AddressList headings, such that
        //       MS Words Label template will print them out.

        //-- 13a) Build the Renewal list
        BuildRenewalLabesCSVList buildRenewalCSVList =
                new BuildRenewalLabesCSVList(segmentAry, shared, splitUtils);
        ArrayList<String> renewalListCSVAL =
                buildRenewalCSVList.buildRenewalCSVList_Main(addrBundleAry, segmentAry);

        //-- 13b) Build the Current List (also known as the Master list)
        BuildCurrentLabelsBundleAry buildCurrentBundleAry =
                new BuildCurrentLabelsBundleAry(segmentAry, shared, splitUtils);

        // An ArrayList is necessary for feeding write methods.  That will be
        // the next step in blendCurrentToAL()
        // But for now, segment indexing must be preformed from addrBundleAry

        Addr[] currentBundleAry = buildCurrentBundleAry.buildCurrentBundleAry_Main(
                addrBundleAry, segmentAry);


        /*********************************************/
        //-- 14) Add Segment Labels into currentListCSVAL
        //       This to make the list suitable to used by Microsoft Word Mail Merge
        //       to print Bulletin labels.
        //       (Note that renewalListCSVAL does not contain any Segment Labels.)
        BlendCurrentToAL blendCurrentToAL = new BlendCurrentToAL(shared, splitUtils);
        ArrayList<String> currentListCSVAL = null;
        try {
            currentListCSVAL = blendCurrentToAL.blendCurrentToAL_Main(
                    segmentAry, segmentLabelAL, currentBundleAry);
            if (currentListCSVAL == null) {
                internalMsgCtrl.err(errKey.FatalError, false, "blendCurrentCSVAL is null");
            }
        } catch (InternalFatalError exc) {
            System.exit(-1);
        }

        /*********************************************/
        //-- 15) Write out a CSV file suitable to be used by Microsoft Word Mail Merge
        //      to print Bulletin labels.
        //
        // TODO -- These two names should be command line options.

        //**************
        //-- 15a the current csv file
        boolean addrLabelsFlg = true;
        String currentLabelsFilename = shared.getCurrentLabelFilename();
        filePath = datedOutputPath + folderBreak + currentLabelsFilename;
        try {
            if (fileWork.writeFile(filePath, currentListCSVAL) == false) {
                internalMsgCtrl.err(errKey.FatalError, false, "Failed to create csv file:  " + currentLabelsFilename);
                addrLabelsFlg = false;
                throw new InternalFatalError();
            }
        } catch (InternalFatalError expt) {
            System.exit(-1);
        }
        //**************
        //-- 15b the renewal csv file
        boolean renwLabelsFlg = true;
        String renewalLabelsFilename = shared.getRenewalLabelFilename();
        filePath = datedOutputPath + folderBreak + renewalLabelsFilename;
        if (fileWork.writeFile(filePath, renewalListCSVAL) == false) {
            internalMsgCtrl.err(errKey.Error, false, "Failed to create csv file:  " + renewalLabelsFilename);
            renwLabelsFlg = false;
        }

        /*********************************************/
        //-- 16) Generate Two reports:
        //      1) CheckList.csv:  a list of the quantity of all zip codes
        //      2) CuttingGuide.txt:  the quantity of labels in each segment

        //**************
        //-- 16a) Build and write out CheckList.csv (TBD options to be named by user)
        String checkListFilename = shared.getCheckListFilename();

        filePath = datedOutputPath + folderBreak + checkListFilename;
        GenCheckList genCheckList = new GenCheckList(shared);
        ArrayList<String> checkListAL = genCheckList.genCheckList_Main(segmentAry, addrBundleAry);
        if ((checkListAL == null) || (checkListAL.size() == 0)) {
            internalMsgCtrl.err(errKey.Message, false, "Checklist failed.  No data.");
        }

        boolean checkListFlg = true;
        if (fileWork.writeFile(filePath, checkListAL) == false) {
            internalMsgCtrl.err(errKey.Error, false, "Failed to create csv file:  " + checkListFilename);
            checkListFlg = false;
        }

        //**************
        //-- 16b) Build and write out CuttingGuide.txt (TBD option to be named by user)
        String cuttingGuideFilename = shared.getCuttingGuideFilename();

        filePath = datedOutputPath + folderBreak + cuttingGuideFilename;
        GenCuttingGuide genCuttingGuide = new GenCuttingGuide(shared);
        ArrayList<String> cuttingGuildAL =
                genCuttingGuide.GenGuttingGuide_Main(segmentAry, addrBundleAry);
        if ((cuttingGuildAL == null) || (cuttingGuildAL.size() == 0)) {
            internalMsgCtrl.err(errKey.Message, false, "CuttingGuide failed.  No data.");
        }
        boolean cuttingGuideFlg = true;
        if (fileWork.writeFile(filePath, cuttingGuildAL) == false) {
            internalMsgCtrl.err(errKey.Error, false, "Failed to create csv file:  " + cuttingGuideFilename);
            cuttingGuideFlg = false;
        }

        /*********************************************/
        //-- 17 Reports to the user:

        internalMsgCtrl.out("---------------------------------------------------------");
        internalMsgCtrl.out("Configuration file path:         " + configFilePathName);
        internalMsgCtrl.out("Data directory:                  " + dataFolderPathName);
        internalMsgCtrl.out("   AddressList CSV file:         " + addressListFilename);
        internalMsgCtrl.out("   Created dated output folder:  " + datedOutputFoldername);

        if (addrLabelsFlg) internalMsgCtrl.out("      Created current labels CSV file:  " +
                currentLabelsFilename);
        if (renwLabelsFlg) internalMsgCtrl.out("      Created renewal labels CSV file:  " +
                renewalLabelsFilename);
        if (checkListFlg) internalMsgCtrl.out("      Created check list CSV file:      " +
                checkListFilename);
        if (cuttingGuideFlg) internalMsgCtrl.out("      Created cutting guide text file:  " +
                cuttingGuideFilename);
        internalMsgCtrl.out("---------------------------------------------------------");

        // -1 to account for the header line
        internalMsgCtrl.out("Renewals:              " + (renewalListCSVAL.size() - 1));
        internalMsgCtrl.out("Current:               " + (currentListCSVAL.size() - segmentLabelCnt - 1));
        int total = (renewalListCSVAL.size() - 1) + (currentListCSVAL.size() - segmentLabelCnt - 1);
        internalMsgCtrl.out("Total Issues:          " + total);

        internalMsgCtrl.out("Segments (Envelopes):  " + segmentLabelCnt);
        internalMsgCtrl.out("---------------------------------------------------------");

        internalMsgCtrl.out("For the convenience of cut and paste:");
        internalMsgCtrl.out("--------------");
        internalMsgCtrl.out(dataFolderPathName + folderBreak + addressListFilename);
        internalMsgCtrl.out("--------------");
        internalMsgCtrl.out(dataFolderPathName + folderBreak + datedOutputFoldername + folderBreak + currentLabelsFilename);
        internalMsgCtrl.out(dataFolderPathName + folderBreak + datedOutputFoldername + folderBreak + renewalLabelsFilename);
        internalMsgCtrl.out(dataFolderPathName + folderBreak + datedOutputFoldername + folderBreak + checkListFilename);
        internalMsgCtrl.out(dataFolderPathName + folderBreak + datedOutputFoldername + folderBreak + cuttingGuideFilename);
        internalMsgCtrl.out("---------------------------------------------------------");

        /*********************************************/
        if ((shared.getVerboseFlg())) {
            StringBuilder sb = internalMsgCtrl.getVerboseMessages();
            Scanner scan = new Scanner(sb.toString());
            while (scan.hasNextLine()) {
                String oneLine = scan.nextLine();
                System.out.println(oneLine);
            }
            // String[] lines = sb.toString().split("\\n");
            //for(String s: lines){
            //    System.out.println("Content = " + s);
            //    System.out.println("Length = " + s.length());
            //}
        }
        /*********************************************/
        // DONE) and getLabels is DONE!

        /*********************************************/

        //---------------------------------------------
        //--------- Debug Section ------------------
        //debug_addrZipAry(addrZipAry);
        //debug_addrBundleAry(addrBundleAry);
        //debugBracedAL(trayAL, addrBundleAry);

        // A unit zip is a set of zip codes that are all the same (as in 6 80303)
        // A segment is the packaging division of a tray into sets of address labels.

        //debugBracedAL(trayAL, addrBundleAry);
    }


    //--------------------  Debug routines follow -------------------------------

    //-----------------------------------------------
    // static void debug_addrZipAry(Addr[] addrZipAry)
    // {
    //    // DEBUG KEEP
    //    // Put in file: sorted_addrZipAry,txt
    //    for (int inx = 0; inx < addrZipAry.length; inx++)
    //    {
    //       Addr addr = addrZipAry[inx];
    //       System.out.println("Inx: " + inx + ") zip:  " + addr.zip +
    //                                ", trayTag:  " + addr.trayTag + ", zone:  " + addr.zone +
    //                                ", renewal:  " + addr.renewal);
    //    }
    //    System.out.println("-----------------------------------------------------------------");
    // }

    //-----------------------------------------------
    // static void debug_addrBundleAry(Addr[] addrBundleAry)
    // {
    //    // DEBUG KEEP
    //    // Put in file: sorted_addrBundleAry.txt
    //    System.out.println("------------------------sorted_addrBundleAry------------------------");
    //    System.out.println("--------------------- sorted in bundle order -----------------------");
    //    for (int inx = 0; inx < addrBundleAry.length; inx++)
    //    {
    //       Addr addr = addrBundleAry[inx];
    //       System.out.println("Inx: " + inx + ") zip:  " + addr.zip +
    //                                ", trayTag:  " + addr.trayTag + ", zone:  " + addr.zone +
    //                                ", renewal:  " + addr.renewal);
    //    }
    //    System.out.println("-----------------------------------------------------------------");
    // }

    //-----------------------------------------------
    // static void debugBracedAL(ArrayList<ClassesPack.Tray> trayAL, Addr[] addrBundleAry)
    // {
    //    System.out.println("------------------ sorted in bundle order -----------------------");
    //    for (Tray tray : trayAL)
    //    {
    //       if (tray.trayTag.isEmpty()) continue;

    //       int[] zipCntAry = tray.zipCntAry;
    //       System.out.println("----------------------" + tray.trayTag + "----------------------");

    //       int dbCnt = 0;
    //       int dbInx = 0;
    //       System.out.println("00,01,02,03,04,05,06,07,08,09,10,11,12,13,14,15,16,17," +
    //                                "18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,32");
    //       for (int inx = 0; inx < zipCntAry.length; inx++, dbInx++)
    //       {
    //          //System.out.print("(" + inx + "," + zipCntAry[inx] + ")");
    //          String dbStr = String.format("%02d,", zipCntAry[inx]);
    //          System.out.print(dbStr);
    //          //System.out.print(zipCntAry[inx] + ",");
    //          dbCnt += zipCntAry[inx];
    //       }

    //       System.out.println("");
    //       for (Brace brace : tray.bracedAL)
    //       {
    //          if (brace.startIndex == brace.endIndex)
    //          {
    //             System.out.print(" * ");
    //          }
    //          else
    //          {
    //             for (int inx = brace.startIndex; inx < brace.endIndex; inx++) {
    //                if (inx==brace.startIndex) {
    //                   System.out.print(" s ");
    //                   continue;
    //                }
    //                System.out.print(".. ");
    //             }
    //             System.out.print(" e ");
    //          }
    //       }
    //       System.out.println("");
    //       for (Brace brace : tray.bracedAL)
    //       {
    //          System.out.print("(" + brace.startIndex + "," + brace.endIndex + ")");
    //       }
    //       System.out.print("\n\n");
    //    }
    //}
}
