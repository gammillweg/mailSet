package libPack;

import configPack.XMLData;
import configPack.XMLParse;
import fileWork.FileWork;


// There is ONE and ONLY ONE instance of Shared
// Even so... Shared is not Singleton

public class Shared<dataFolderNameLiteral>
{
   public Shared() throws InternalFatalError
   {
      // Determine OS
      determineOS();
      internalMsgCtrl = new InternalMsgCtrl(this);

      // These are messages meant to bin determineOS(); but there is a conflict as to who is need first
      // determineOS() need internalMsgCtrl()  and internalMsgCtrl() need determineOS().
      // So I moved these error messages here (out of determineOS().

      if (os.contains("win"))
      {
         internalMsgCtrl.verbose("OS: " + os + "; lineBreak: \"\\r\\n\"; folderBreak:  \"" + folderBreak + "\"");
      } else
      {
         internalMsgCtrl.verbose("OS: " + os + "; lineBreak: \"\\n\"; folderBreak:  \"" + folderBreak + "\"");
      }

      xmlParse = new XMLParse(this);
      fileWork = new FileWork(this);
      optionSupport = new OptionSupport(this);

      cliOptions = new CLIOptions(this);

      genLabelsUtils = new MailPrepUtils(this);
      dateWork = new DateWork();
      classesPack = new ClassesPack();
      excelUtils = new ExcelUtils();
      mailPrepUtils = new MailPrepUtils(this);

      setMonthsAry();
      setDefaultRenewalMonthYear();

      // see readConfigFile() for setting of labelsAcross
      // labels across is a configured var; but it seem to
      // me it should be available from Shared.
      // see: default_labelsAcross = 3;
   }

   final int MaxCharAcrossLabels = 32;
   private final int default_LabelsAcross = 3;
   /***********************************************************************
    // Options (CLIOptions (Args4j)  (and maybe some support stuff)
    // From here down
    /***********************************************************************
    * Side comment:  The filenames take a circuitous route to allow them to
    * be defined here in Shared.  The easy thing to do would be to define
    * them in CLIOptions.java.  BUT I want to define them in Shared.
    * Explained:
    *    1) define final default in Shared,
    *    2) set CLIOptions filename String in Shared
    *    3) Then an instance of CLIOptions is created and shared.get...()
    *       is run which sets in CLIOptions filename to the default.
    *    4) Then if a user provides an alternate filename it causes
    *       shared.set...() to be called which sets filename in shared.
    *    5) Any user of filename gets it from shared.
    *
    *    Note that the instance of CLIOptions is created here in Shared
    *    and optioned from Shared by Main.  This insures Shared defaults
    *    are set prior to CLIOptions asking for them.
    ***********************************************************************/

   // Assumes UNIX (Linux) as the default
   // see determineOS() to set lineBreak and folderBreak
   private final String default_lineBreak = "\n";
   private final String default_folderBreak = "/";
   // TODO -- all of these should be command line options
   // TODO -- need supporting code/option for user provided name
   private final String default_CurrentLabelFilename = "CurrentListLabels.csv";
   private final String default_RenewalLabelFilename = "RenewalListLabels.csv";
   // TODO -- need supporting code/option for user provided name
   private final String default_CheckListFilename = "CheckList.csv";
   private final String default_CuttingGuideFilename = "CuttingGuide.txt";
   private XMLData xmlData = null;

   // ============================================================
   // ------------------------------------------------------
   // Classes instantiated in Shared
   private XMLParse xmlParse = null;
   private CLIOptions cliOptions = null;
   private FileWork fileWork = null;
   private OptionSupport optionSupport = null;
   private DateWork dateWork = null;
   private ExcelUtils excelUtils = null;
   private MailPrepUtils mailPrepUtils = null;

   // ============================================================
   // Default Definitions
   private MailPrepUtils genLabelsUtils = null;
   private ClassesPack classesPack = null;
   private InternalMsgCtrl internalMsgCtrl = null;
   // ============================================================
   private String folderBreak;
   private String lineBreak;
   // see readConfigFile() for setting of labelsAcross
   // labels across is a configured var; but it seem to
   // me it should be available from Shared.
   private int labelsAcross;
   private int default_labelsAcross = 3;
   // ------------------------------------------------------
   // Determine the OS
   // Only interested in Linux, Windows or Mac X OS (or newer)
   // Older versions of Mac line break is \r, and is not supported here
   private String os = "";
   // ------------------------------------------------------
   private String datedOutputFolderName = "";
   // (See note above on how Shared and CLIOptions are blended.)
   // private boolean noReminder;
   private boolean noReminder = false;
   // ------------------------------------------------------
   // (See note above on how Shared and CLIOptions are blended.)
   private String renewalMonthStr = "";
   private int renewalMonthInt = -1;

   //------------------------------------------------------
   // pass-through(s) to and from private Shared Class OptionSupport
   // Not all may be used.
   private String renewalYearStr = "";
   private int renewalYearInt = -1;
   private String renewalDateStr = "";
   private String[] monthsAry = null;

   // ------------------------------------------------------
   // These four are a set under mailPrep
   // with calls to shared.setXXX() below
   private String appName = "";
   public void setAppName(String appName) { this.appName = appName; }
   public String getAppName() {return appName;}
   // ------------------------------------------------------


   public CLIOptions getCLIOptions()
   {
      return cliOptions;
   }
   public ExcelUtils getExcelUtils() { return excelUtils; }
   public MailPrepUtils getMailPrepUtils() { return mailPrepUtils; }

   public FileWork getFileWork()
   {
      return fileWork;
   }

   public MailPrepUtils getGenLabelsUtils()
   {
      return genLabelsUtils;
   }

   public ClassesPack getClassesPack()
   {
      return classesPack;
   }

   public InternalMsgCtrl getInternalMsgCtrl()
   {
      return internalMsgCtrl;
   }

   // ------------------------------------------------------
   public int getDefault_LabelsAcross()
   {
      return default_LabelsAcross;
   }

   // TODO -- need supporting code/option for user provided name
   public String getCurrentLabelFilename()
   {
      return default_CurrentLabelFilename;
   }

   public String getRenewalLabelFilename()
   {
      return default_RenewalLabelFilename;
   }

   // TODO -- need supporting code/option for user provided name
   public String getCuttingGuideFilename()
   {
      return default_CuttingGuideFilename;
   }

   public String getCheckListFilename()
   {
      return default_CheckListFilename;
   }

   public int getMaxCharAcrossLabels()
   {
      return MaxCharAcrossLabels;
   }

   public String getAppFolderPathName()
   {
      return optionSupport.getAppFolderPathName();
   }

   public String getAppFolderName()
   {
      return optionSupport.getAppFolderName();
   }

   public void setAppFolderName(String appFolderName) throws InternalFatalError
   {
      optionSupport.setAppFolderName(appFolderName);
   }

   public String getApFolderPathName()
   {
      return optionSupport.getAppFolderPathName();
   }

   // ------------------------------------------------------
   // labelsAcross is a configured value

   public String getConfigFileName()
   {
      return optionSupport.getConfigFileName();
   }

   public void setConfigFileName(String configFileName)
   {
      optionSupport.setConfigFileName(configFileName);
   }

   public String getConfigFilePathName() { return optionSupport.getConfigFilePathName(); }

   public void setDataFolderPathName(String dataFolderPathName) throws InternalFatalError {
      optionSupport.setDataFolderPathName(dataFolderPathName);
   }

   public String getDataFolderPathName() { return optionSupport.getDataFolderPathName(); }

   public String getAddressListFileName()
   {
      return optionSupport.getAddressListFileName();
   }

   public void setAddressListFileName(String addressListFileName) {
      optionSupport.setAddressListFileName(addressListFileName);
   }

   public String getAddressListFilePathName()
   {
      return optionSupport.getAddressListFilePathName();
   }

   public String getZipCodesFileName()
   {
      return optionSupport.getZipCodesFileName();
   }

   public void setZipCodesFileName(String zipCodesFileName)
   {
      optionSupport.setZipCodesFileName(zipCodesFileName);
   }

   public String getZipCodesFilePathName() { return optionSupport.getZipCodesFilePathName(); }

   public String getZipCountsFileName()
   {
      return optionSupport.getZipCountsFileName();
   }

   public void setZipCountsFileName(String zipCountsFileName)
   {
      optionSupport.setZipCountsFileName(zipCountsFileName);
   }

   public String getZipCountsFilePathName()
   {
      return optionSupport.getZipCountsFilePathName();
   }

   // TODO no reminder and renewal month
   //TODO  all the other sets....
   // ------------------------------------------------------
   // Cause the assembly configuration file ($Home/.mailPrep/config.xml)
   public void parseConfigData()
   {
      xmlData = xmlParse.xmlParse();

      // labels across is a configured value
      setLabelsAcross();
   }

   public XMLData getXMLData()
   {
      return xmlData;
   }

   public int getLabelsAcross()
   {
      if (labelsAcross==0)
         return default_LabelsAcross;
      return labelsAcross;
   }

   public void setLabelsAcross()
   {
      try
      {
         String labelsAcrossStr = xmlData.get_LabelsAcross();
         labelsAcross = Integer.valueOf(labelsAcrossStr);
      }
      catch (Exception excp)
      {
         labelsAcross = default_LabelsAcross;
      }
   }
   //----------------------------------------------------
   //=========================================================================

   // ------------------------------------------------------
   // String cwd = System.getProperty ("user.dir");
   // String dataFile = "data/tst_MasterList.csv";
   // String filePath = cwd + folderBreak + dataFile;
   // ------------------------------------------------------

   public String getOS()
   {
      return this.os;
   }

   public String determineOS()
   {
      this.os = System.getProperty("os.name").toLowerCase();
      if (os.contains("win"))
      {
         lineBreak = "\r\n";
         folderBreak = "\\";
         // This message was moved to the constructor (see comment there)
         //internalMsgCtrl.err(ErrorMsgCtrl.errKey.Message, true, "OS: " + os + "; lineBreak: \"\\r\\n\"; folderBreak:  \"" + folderBreak + "\"");
      } else
      {
         // if Linux, Unix or Mac X use defaults "\n" and "/"
         folderBreak = default_folderBreak;
         lineBreak = default_lineBreak;
         // This message was moved to the constructor (see comment there)
         //internalMsgCtrl.err(errKey.Message, true, "OS: " + os + "; lineBreak: \"\\n\"; folderBreak:  \"" + folderBreak + "\"");
      }
      return os;
   }

   // ------------------------------------------------------
   public String getFolderBreak()
   {
      return folderBreak;
   }

   public String getLineBreak()
   {
      return lineBreak;
   }

   public String getDatedOutputFolderName()
   {
      return datedOutputFolderName;
   }

   // ============================================================
   // TODO -- This sets the renewal month a month GREATER than the current month...
   //         Which assumes the apt is run the month prior to the renewal month.
   //         PERHAPS NOT SO
   //         Need an option to force renewal month to the users desired month
   //         THIS is a NECESSARY to do... During COVID I ran into the problem
   //         and using the .NET version had change JUL to SEP

   public void setDatedOutputFolderName(String datedOutputFolderName)
   {
      this.datedOutputFolderName = datedOutputFolderName;
   }

   public String getDatedOutputPath()
   {
      return optionSupport.getDataFolderName() + folderBreak + getDatedOutputFolderName();
   }

   public String getCWD()
   {
      return System.getProperty("user.dir");
   }

   public String getHome()
   {
      return System.getProperty("user.home");
   }

   public boolean getNoReminder()
   {
      return noReminder;
   }

   public void setNoReminder(boolean noReminder)
   {
      this.noReminder = noReminder;
   }

   // (See note above on how Shared and CLIOptions are blended.)
   // RenewalMonth is one greater than the current month but still Java 0 based
   public void setRenewalMonth(int month)
   {
      // The returned value from Calendar is zero based.
      // The value of the int is one less than what may be expected.
      // Integer Jan == 0  (NOT 1)  Integer Dec == 11 (NOT 12)

      // The Renewal month is a month GREATER than the current month
      // This is still 0 based.  (Example: current month = Jan, so
      // Java 0 based Jan == 0 & Feb == 1,
      // adding 1 to Jan==0  ends up with a zero based Fed == 1
      // (NOT the common Feb is month 2)
      month += 1;

      // May have stepped to the next year
      //(note that 12 is correct (in effect == 13 as we are zero based (dec is 11)))
      if (month==12) month = 0;

      String[] monthsAry = getAbbreMonths();
      if ((month >= 0) && (month <= 11))
      {
         renewalMonthInt = month;
         renewalMonthStr = monthsAry[month];
      }
   }

   // ------------------------------------------------------
   // (See note above on how Shared and CLIOptions are blended.)
   public String getRenewalMonthStr()
   {
      return renewalMonthStr;
   }

   public int getRenewalMonthInt()
   {
      return renewalMonthInt;
   }

   public String getRenewalYearStr()
   {
      return renewalYearStr;
   }

   public int getRenewalYearInt()
   {
      return renewalYearInt;
   }

   // ------------------------------------------------------
   // (See note above on how Shared and CLIOptions are blended.)
   // Sets the default renewal month to be "next" month
   private void setDefaultRenewalMonthYear()
   {
      // Add a month to the current month
      // Adding a month because:

      // The returned value from Calendar is zero based.
      // The value of the int is one less than what may be expected.
      // Integer Jan == 0  (NOT 1)  Integer Dec == 11 (NOT 12)
      int[] monthYear = dateWork.getCurrentMonthYear();

      setRenewalMonth(monthYear[0]);
      setRenewalYear(monthYear[1]);
   }

   // ------------------------------------------------------
   // (See note above on how Shared and CLIOptions are blended.)
   private void setMonthsAry()
   {
      monthsAry = dateWork.getMonthAbbreviations();
   }

   // ------------------------------------------------------
   // (See note above on how Shared and CLIOptions are blended.)
   public String[] getAbbreMonths()
   {
      return monthsAry;
   }

   // ------------------------------------------------------
   // (See note above on how Shared and CLIOptions are blended.)
   // Of the form "mmm/yy"
   public String getRenewalDateStr()
   {
      if (renewalDateStr.isEmpty())
      {
         this.renewalDateStr = renewalMonthStr + "-" + renewalYearStr;
      }
      return this.renewalDateStr;
   }

   // ------------------------------------------------------
   // (See note above on how Shared and CLIOptions are blended.)
   // Returns the year of the century ... with the century removed
   // (input integer 2019 -- returns integer 19)
   public void setRenewalYear(int year)
   {
      // MAGIC NUMBER ALERT... this program is locked into the 20th century
      if ((year > 2000) && (year < 2100))
      {
         year -= 2000;

         renewalYearInt = year;
         renewalYearStr = String.format("%2d", year);
      }
   }

   boolean verboseFlg = false;
   public void setVerbosFlg(boolean flg)
   {
      verboseFlg = flg;
   }

   public boolean getVerboseFlg() { return verboseFlg; }

   public String expandTilde(String path)
   {
      int len = path.length();
      String head = path.substring(1,len);
      String expansion = this.getHome() + head;
      return expansion;
   }
}
