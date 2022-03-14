package libPack;

import fileWork.FileWork;
import libPack.InternalMsgCtrl.errKey;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.IOException;

//import static java.lang.System.out;

// args4j is used to parse command line option (CLI)
// args4j jars are stored in /usr/local/args4j
// Arguments are parsed here and the results are stored in Share
public class CLIOptions
{
   public CLIOptions(Shared shared)
   {
      this.shared = shared;

      //-------------------------------------------------------------------------------
      // These must be set here.  Originally I called shared to set them  on their code
      // lines below.  BUT... run time java processes definitions PRIOR to the constructor.
      // Thus, share had not yet been properly defined yet.

      // Read comment at len of Shared on how default file names are defined in Shared.
      appFolderName = shared.getAppFolderName();
      configFileName = shared.getConfigFileName();
      dataFolderPathName = shared.getDataFolderPathName();
      addressListFileName = shared.getAddressListFileName();
      zipCodesFileName = shared.getZipCodesFileName();
      zipCountsFileName = shared.getZipCountsFileName();

      internalMsgCtrl = shared.getInternalMsgCtrl();
      //-------------------------------------------------------------------------------
   }
   Shared shared = null;
   InternalMsgCtrl internalMsgCtrl = null;

   private String appFolderName = "";
   private String appFolderPathName = "";
   private String configFileName = "";
   private String dataFolderPathName = "";
   private String addressListFileName = "";
   private String zipCodesFileName = "";
   private String zipCountsFileName = "";

   /*=================================================================================*/
   // Define argv4j valid options

   // argv4j's print option list call will print the following in alphbetical order from -?
   // It would be nice if it would print them in the order below; but is OK.

   //----------------------------------------------------------------------------
   //-- Help messages
   // An empty argument list will print a help message listing -?, -h, and -help
   // and state that one may use -d (--defaults) if accepting all option defaults

   @Option(name="-?", aliases="--optionList", usage="Print a list the valid options.", required=false)
   private  boolean validOptions = false;

   @Option(name="-lo", aliases="listOptions", usage="List the valid options. (lo List Options)", required=false)
   private  boolean listValidOptions = false; // added to avoid quoting the -? (if required by Linux shell)

   @Option(name="-h", aliases="--briefHelp", usage="Show a brief help message.", required=false)
   private  boolean briefHelp = false;

   @Option(name="-help", aliases="--help", usage="Show a verbose help message.", required=false)
   private  boolean verboseHelp = false;

   @Option(name="-vh", aliases="--verboseHelp", usage="Show a verbose help message. (vh Verbose Help)", required=false)
   private  boolean verboseHelp2 = false;  //Added just because -vh makes sense as an acronym


   //----------------------------------------------------------------------------
   // Folder options

   @Option(name="-dp", aliases="--dataPath", usage="Absolute folder path name. ", required=false)
   private String opDataFolderPathName = this.dataFolderPathName;

   //----------------------------------------------------------------------------
   // File options

   @Option(name="-af", aliases="--appFolder", usage="app folder name. Where config file lives. Forced into users home folder. (af:[App Folder])", required=false)
   private String opAppFolderName = this.appFolderName;

   @Option(name="-c", aliases="--config", usage="Configuration file name.", required=false)
   private String opConfigFileName = this.configFileName;

   @Option(name="-al", aliases="--addressList", usage="Alternate name for AddressList CSV file.(al:[Address List])", required=false)
   private String opAddressListFileName = this.addressListFileName;

   @Option(name="-zd", aliases="--zipCodes", usage="Alternate name for ZipCodes CSV file. (zd:[Zip coDes])", required=false)
   private String opZipCodesFileName = this.zipCodesFileName;

   @Option(name="-zt", aliases="--zipCounts", usage="Alternate name for ZipCounts CSV file. (zt:[Zip counTs])", required=false)
   private String opZipCountsFileName = this.zipCountsFileName;

   
   //----------------------------------------------------------------------------
   // Time options
   
   @Option(name="-nr", aliases="--noReminder", usage="Do NOT show a reminder as to when a config file update is due. (nr:[No Reminder])", required=false)
   private  boolean noReminder = false;

   @Option(name="-rm", aliases="--renewalMonth", usage="Define a renewal month. (rm:[Renewal Month])", required=false)
   private  String renewalMonth = "";

   //----------------------------------------------------------------------------
   // Other options

   @Option(name="-d", aliases="--defaults", usage="Default options. Will be ignored if used with any other option.", required=false)
   private boolean defaultOptions = false;

   @Option(name="-v", aliases="--verbose", usage="Print debugging message and lots of stuff.", required=false)
   private  boolean verbose = false;

   @Option(name="-ld", aliases="--listDefaults", usage="list the coded default folder and file names. (ld:[List Defaults])", required=false)
   private  boolean listDefaults = false;



   /*=================================================================================*/

   /**
    * @param arguments Command-line arguments to be processed with Args4j.
    */
   public boolean cliOptionsMain(final String[] arguments) throws IOException, InternalFatalError
   {
      final CmdLineParser parser = new CmdLineParser(this);
      // if (arguments.endIndex < 1)
      // {
      //    parser.printUsage(out);
      //    System.exit(-1);
      // }
      try
      {
         // This is where Args4j fills vars defined above with options
         // provided by the user.
         // (Example:  configFilename is defined above
         //            configFilename is defined in shared as "config.xml"
         //            Above (constructor) local configFilename is received from shared
         //            User option -c "BulletinSlicerConfig.xml" is used
         //            parser.parseArgument() replaces local configFilenmame with user option name
         //            Below shared.setConfigFilename() is called to replace the default with the users name
         parser.parseArgument(arguments);

         // Return
         //    true:  will allow process to run
         //    false: will cause process to exit
         //

         if (arguments.length == 0)
         {
            emptyArgsList();
            return false;
         }

         if (defaultOptions)
         {
           if (arguments.length > 1)
           {
              internalMsgCtrl.err(errKey.Message, true, "More than one argument was used.");
              internalMsgCtrl.err(errKey.Message, true, "-d is ignored if used with any other option.");
           }
            // Nothing called
            // will return true below
         }

         // I check for the existence of folders and files
         FileWork fileWork = shared.getFileWork();
         String folderBreak = shared.getFolderBreak();

         // --------------------------------------------------
         if (validOptions) //-?  (Linux shell may require one to quote the -?)
         {
            internalMsgCtrl.out("-----------------------------------------------");
            parser.printUsage(System.out);
            internalMsgCtrl.out("-----------------------------------------------");
            return false;
         }

         if (listValidOptions) //-lo (added to avoid quoting the -? (if required by Linux shell)
         {
            internalMsgCtrl.out("-----------------------------------------------");
            parser.printUsage(System.out);
            internalMsgCtrl.out("-----------------------------------------------");
            return false;
         }
         // --------------------------------------------------

         if (briefHelp) //-h
         {
            briefHelpMessage();
            return false;
         }

         if (verboseHelp) //--help
         {
            verboseHelpMessage();
            return false;
         }
         if (verboseHelp2)
         {
            // Added because -vh just makes sense for someone trying to remember the options
            verboseHelpMessage();
            return false;
         }

         if (verbose)
         {
            internalMsgCtrl.setVerboseFlg(true);
            shared.setVerbosFlg(true);
         }

         if (listDefaults)
         {
            listDefaults();
            return false;
         }

         // Folders ------------------------------------------------------------------------------------
         // Check for the existence of folders FIRST, so they can be used to build a path for files
         if (opAppFolderName != "")
         {
            if (opAppFolderName.startsWith("~"))
            {
               opAppFolderName = shared.expandTilde(opAppFolderName);
            }
            // Check to see if exists first...
            if (fileWork.fileExists(opAppFolderName) == false)
            {
               internalMsgCtrl.err(errKey.FatalError, true, "Folder: [" + opAppFolderName + "] does not exist.");
               throw new InternalFatalError();
            }
            // Then check to see if is a folder
            if (fileWork.isFolder(opAppFolderName) == false)
            {
               internalMsgCtrl.err(errKey.FatalError, true, "Folder: [" + opAppFolderName + "] is not a folder.");
               throw new InternalFatalError();
            }
            shared.setAppFolderName(opAppFolderName);
         }

         if (opDataFolderPathName != "")
         {
            if (opDataFolderPathName.startsWith("~"))
            {
               opDataFolderPathName = shared.expandTilde(opDataFolderPathName);
            }
            // Check to see if exists first...
            if (fileWork.fileExists(opDataFolderPathName) == false)
            {
               internalMsgCtrl.err(errKey.FatalError, true, "Folder: [" + opDataFolderPathName + "] does not exist.");
               throw new InternalFatalError();
            }
            // Then check to see if is a folder.
            if (fileWork.isFolder(opDataFolderPathName) == false)
            {
               internalMsgCtrl.err(errKey.FatalError, true, "Folder: [" + opDataFolderPathName + "] is not a folder.");
               throw new InternalFatalError();
            }
            shared.setDataFolderPathName(opDataFolderPathName);
         }
         // Files ------------------------------------------------------------------------------
         // check for existence of files AFTER folders, so the folders can be used to build a path to the file

         if (opConfigFileName != "")
         {
            if (fileWork.isAbsolutPath(opConfigFileName))
            {
               internalMsgCtrl.err(errKey.Error, true, "[" + opConfigFileName + "] must be a file, is a path.");
               throw new InternalFatalError();
            }

            appFolderPathName = shared.getAppFolderPathName();
            String configFilePath = appFolderPathName + folderBreak + opConfigFileName;
            if (fileWork.fileExists(configFilePath) == false)
            {
               internalMsgCtrl.err(errKey.FatalError, true, "Folder: [" + configFilePath + "] does not exist.");
               throw new InternalFatalError();
            }

            shared.setConfigFileName(opConfigFileName);
         }

         if (opAddressListFileName != "")
         {
            if (fileWork.isAbsolutPath(opAddressListFileName))
            {
               internalMsgCtrl.err(errKey.Error, true, "[" + opAddressListFileName + "] must be a file.");
               internalMsgCtrl.err(errKey.Error, true, "[" + opAddressListFileName + "] is a path.");
               throw new InternalFatalError();
            }

            String dataFolderPathName = shared.getDataFolderPathName();
            String path = dataFolderPathName + folderBreak + opAddressListFileName;
            if (fileWork.fileExists(path) == false)
            {
               internalMsgCtrl.err(errKey.FatalError, true, "File: [" + path + "] does not exist.");
               throw new InternalFatalError();
            }

            shared.setAddressListFileName(opAddressListFileName);
         }

         if (opZipCodesFileName != "")
         {
            if (fileWork.isAbsolutPath(opZipCodesFileName))
            {
               internalMsgCtrl.err(errKey.Error, true, "[" + opZipCodesFileName + "] must be a file, is a path.");
               throw new InternalFatalError();
            }

            String dataFolderPathName = shared.getDataFolderPathName();
            String path = dataFolderPathName + folderBreak + opZipCodesFileName;
            if (fileWork.fileExists(path) == false)
            {
               internalMsgCtrl.err(errKey.FatalError, true, "File: [" + path + "] does not exist.");
               throw new InternalFatalError();
            }

            shared.setZipCodesFileName(opZipCodesFileName);
         }

         if (opZipCountsFileName != "")
         {
            if (fileWork.isAbsolutPath(opZipCountsFileName))
            {
               internalMsgCtrl.err(errKey.Error, true, "[" + opZipCountsFileName + "] must be a file, is a path.");
               throw new InternalFatalError();
            }

            String dataFolderPathName = shared.getDataFolderPathName();
            String path = dataFolderPathName + folderBreak + opZipCountsFileName;
            if (fileWork.fileExists(path) == false)
            {
               internalMsgCtrl.err(errKey.FatalError, true, "File: [" + path + "] does not exist.");
               throw new InternalFatalError();
            }

            shared.setZipCountsFileName(opZipCountsFileName);
         }
         //------------------------------------------------------------------------------------

         if (noReminder)
         {
            return true;
         }


         if (renewalMonth != "")
         {
            // If renewalMonth is valid the month int will be returned else -1
            int month = checkRenewalMonth(renewalMonth);
            if (month == -1) return false;

            shared.setRenewalMonth(month);
         }

         return true;
      }
      //catch (CmdLineException clEx)
      catch (InternalFatalError excp)
      {
         return false;
      }
      catch (Exception exc)
      {
         String errMsg = exc.toString();
         internalMsgCtrl.err(errKey.Message, false, "Invalid option.  Use \"-?\" or --help or --optionList to see valid options.");
         // TODO -- work on --verbose... if verbose were true, I really should show this message
         // the exception message will be from args4j, which I don't want the user to see
         // internalMsgCtrl.err(errKey.ExceptionMsg, false, errMsg);
         return false;
      }
   }

   private int checkRenewalMonth(String renewalMonth)
   {
      // Check for a valid renewalMonth string
      String[] abbreMonths = shared.getAbbreMonths();
      String errStr = "";
      for (int inx = 0; inx < abbreMonths.length; inx++)
      {
         String month = abbreMonths[inx];
         if (renewalMonth.equals(month))
         {
            return inx;
         }
         errStr += month + ", ";
      }

      //If got to here... did not find a valid renewal month
      internalMsgCtrl.err(errKey.Message, false, "Invalid value supplied to option renewalMonth.");
      errStr = errStr.substring(0, errStr.length() - 2);
      String errMsg = "Valid values are:  " + errStr;
      internalMsgCtrl.err(errKey.Message, false, "Valid values are:  " + errStr);
      return -1;
   }

   // if the user does not provide any options
   private void emptyArgsList()
   {
      // At least one argument is required
      // An empty argument list will show the following
      internalMsgCtrl.out("---------------------------------------------------------");
      internalMsgCtrl.out("genLabels -arg1 -arg2 etc");
      internalMsgCtrl.out("Use one of the following to get help:");
      internalMsgCtrl.out("   -h    (--briefHelp)");
      internalMsgCtrl.out("   -help (--help) is verbose help");
      internalMsgCtrl.out("   -vh   (--verboseHelp");
      internalMsgCtrl.out("   -?    (--optionList) (Linux shell may require \"-?\")");
      internalMsgCtrl.out("   -lo   (--listOptions (avoids \"-?\" if required)");
      internalMsgCtrl.out("   -ld   (--listDefaults)");
      internalMsgCtrl.out("   -d    (--defaults)");
      internalMsgCtrl.out("At lease one option must be used.");
      internalMsgCtrl.out("");
      internalMsgCtrl.out("---------------------------------------------------------");
      internalMsgCtrl.out("    You must supply at least one option!");
      internalMsgCtrl.out("    Try -d (--defaults).");
      internalMsgCtrl.out("    -d accepts all coded defaults");
      internalMsgCtrl.out("    -d will be ignored if used with any other option.");
      internalMsgCtrl.out("    -ld will list the defaults that will be used.");
      internalMsgCtrl.out("---------------------------------------------------------");
    }
   // -h (--briefHelp)
   private void briefHelpMessage()
   {
      internalMsgCtrl.out("--------------------------------------");
      internalMsgCtrl.out("[BulletinAssembly] genLabels ...  Usage:  genLabels -arg1 -arg2 etc");
      internalMsgCtrl.out("See a list a valid options via \"-?\" or --help or --optionList.");
      internalMsgCtrl.out("See a list of default folders and csv file names via -help or --verboseHelp.");
      internalMsgCtrl.out("");
      internalMsgCtrl.out("The configuration file config.xml is stored in [Home]/.BulletinAssembly.");
      internalMsgCtrl.out("   Unless arguments -af (App Folder) and/or -c (Config filename) are specified.");
      internalMsgCtrl.out("Necessary Excel csv files are stored in [Home]/Documents/Bulletin/Assembly");
      internalMsgCtrl.out("   Unless argument -dp (Data absolute Path) is specified");
      internalMsgCtrl.out( "  -dp are absolute paths and must start with (linus: \"/\")");
      internalMsgCtrl.out("   or (windows: Drive Letter)");
      internalMsgCtrl.out("CSV files addressList.csv, zipCodes.csv and zipCounts.csv are expected.");
      internalMsgCtrl.out("   Unless arguments -al (Address List) or -zd (Zip coDes) or -zt (Zip CounTs) are specified.");
      internalMsgCtrl.out("The renewal month is next month.");
      internalMsgCtrl.out("   Unless argument -rm is used.");
      internalMsgCtrl.out("The generated balance and renewal csv files will be stored");
      internalMsgCtrl.out("   in a folder under the csv files.  No option is available");
      internalMsgCtrl.out("   to change the name or location of the folder.");
      internalMsgCtrl.out("--------------------------------------");
   }

   // -vh (--verboseHelp)
   private void verboseHelpMessage()
   {
      internalMsgCtrl.out("--------------------------------------");
      internalMsgCtrl.out("Use arg -? (--optionList) for a simple list of valid arguments");
      internalMsgCtrl.out("   zsh may try to process and fail -?, quote as in:  \"-?\"");
      internalMsgCtrl.out("   or use -lo (list option) to avoid need to quote if required.");
      internalMsgCtrl.out("Use arg -h (--briefHelp) for a brief help message");
      internalMsgCtrl.out("This message is the result of -help (--verboseHelp), a more verbose help message");
      internalMsgCtrl.out("--verbose will show information that may prove helpful.");
      internalMsgCtrl.out("-ld (--listDefaults) to see coded file and path names.");
      internalMsgCtrl.out("-d  (--defaults) to accept all coded file and path defaults.");
      internalMsgCtrl.out( "   -d is the simplest way to bypass the requirement of at least one argument.");
      internalMsgCtrl.out( "   -d will be ignored if used with any other option.");
      internalMsgCtrl.out("");
      internalMsgCtrl.out("List of defaults: (see -ld above)");
      internalMsgCtrl.out("   [home]/.BulletinAssembly  : Where config file lives");
      internalMsgCtrl.out("   config.xml                : the required configuration file");
      internalMsgCtrl.out("   [home]/Documents/Bulletin : Where bulletin stuff lives");
      internalMsgCtrl.out("   [home]/Documents/Bulletin/Assembly : Where active csv files live");
      internalMsgCtrl.out("   Active csv files names are:");
      internalMsgCtrl.out("      addressList.csv        : Excel csv of the address sheet");
      internalMsgCtrl.out("      zipCodes.csv           : Excel csv of the ZipCodes sheet");
      internalMsgCtrl.out("      zipCounts.csv          : Excel csv of the ZipCounts sheet");
      internalMsgCtrl.out("   The renewal month is next month (if run in May, renewal month == Jun)");
      internalMsgCtrl.out("   Reminders is true, and will remind you if the config file is out of data");
      internalMsgCtrl.out("   Reminders are muted if set false via -nr.");
      internalMsgCtrl.out("Defaults are listed above per Unix; but Windows names are similar.");
      internalMsgCtrl.out("All of the above may be changed by options.");
      internalMsgCtrl.out("You are not forced to use the default folders or default csv file name.");
      internalMsgCtrl.out("");
      internalMsgCtrl.out("A data out folder will be created in the Assembly folder where processed files");
      internalMsgCtrl.out("will be written.  No option exists to change the name or redirect the location.");
      internalMsgCtrl.out("The folder will be named by the date of the 4th Tuesday, underscore, Bulletin.");
      internalMsgCtrl.out("Example: [home]/Documents/Bulletin/Assembly/210323_Bulletin");
      internalMsgCtrl.out("");
      internalMsgCtrl.out("An absolute path is the entire string (including the filename) from top to bottom.");
      internalMsgCtrl.out("Absolute paths and must start with:  linus: \"/\" or windows: Drive Letter.");
      internalMsgCtrl.out("Absolute paths must end with a filename, and may not end with a \"/\"");
      internalMsgCtrl.out("");
      internalMsgCtrl.out("You can set any renewal month.");
      internalMsgCtrl.out("    Valid renewal month arguments (any one of the following):");
      internalMsgCtrl.out("        Jan, Feb, Mar, Apr, May, Jun, Jul, Aug, Sep, Oct, Nov, Dec");
      internalMsgCtrl.out("        Months may be spelled in full.");
      internalMsgCtrl.out("        Lower case, Upper case, or mixed case is acceptable");
      internalMsgCtrl.out("");
      internalMsgCtrl.out("Example to change the name and location of where Bulletin stuff lives.");
      internalMsgCtrl.out("   java -jar genLabels --dataPath ~/Documents/Bulletin/Assembly");
      internalMsgCtrl.out("   java -jar genLabels -dp C:\\\\Users\\weg\\Documents\\\"My Data Sources\"");
      internalMsgCtrl.out("Example to change the renewal date and ignore configuration date reminders.");
      internalMsgCtrl.out("   java -jar genLabels -rm Sep -nr");
      internalMsgCtrl.out("--------------------------------------");
   }

   //-ld (--listDefaults)
   private void listDefaults()
   {
      String appName = shared.getAppName();
      String renewalDate = shared.getRenewalDateStr();
      String default_currentLabelFilename = shared.getCurrentLabelFilename();
      String default_renewalLabelFilename = shared.getRenewalLabelFilename();
      String default_CuttingGuideFilename = shared.getCuttingGuideFilename();
      String default_CheckListFilename = shared.getCheckListFilename();
      int MaxCharAcrossLabels = shared.getMaxCharAcrossLabels();
      String default_MaxCharAcrossLabels = String.valueOf(MaxCharAcrossLabels);
      int labelsAcross = shared.getLabelsAcross();
      //String default_labelsAcross = String.valueOf(labelsAcross);
      String default_AppFolderName = shared.getAppFolderName();
      String default_AppFolderPathName = shared.getApFolderPathName();
      String default_ConfigFileName = shared.getConfigFileName();
      String default_DataFolderPathName = shared.getDataFolderPathName();
      String default_AddressListFileName = shared.getAddressListFileName();
      String default_AddressListFilePathName = shared.getAddressListFilePathName();
      String default_ZipCodesFileName = shared.getZipCodesFileName();
      String default_ZipCodesFilePathName = shared.getZipCodesFilePathName();
      String default_ZipCountsFileName = shared.getZipCountsFileName();
      String default_ZipCountsFilePathName = shared.getZipCountsFilePathName();

      internalMsgCtrl.out("--------------------------------------------------");
      internalMsgCtrl.out("Default assignments");
      internalMsgCtrl.out("");
      internalMsgCtrl.out("        appName = " + appName);
      internalMsgCtrl.out("        labelsAcross = " + labelsAcross);
      internalMsgCtrl.out("        renewalDate = " + renewalDate);
      internalMsgCtrl.out("default_currentLabelFilename = " + default_currentLabelFilename);
      internalMsgCtrl.out("default_renewalLabelFilename = " + default_renewalLabelFilename);
      internalMsgCtrl.out("default_CuttingGuideFilename = " + default_CuttingGuideFilename);
      internalMsgCtrl.out("default_CheckListFilename = " + default_CheckListFilename);
      internalMsgCtrl.out("default_MaxCharAcrossLabels = " + default_MaxCharAcrossLabels);
      internalMsgCtrl.out( "default_AppFolderName = " + default_AppFolderName);
      internalMsgCtrl.out("default_AppFolderPathName = " + default_AppFolderPathName);
      internalMsgCtrl.out("default_ConfigFileName = " + default_ConfigFileName);
      internalMsgCtrl.out("default_DataFolderPathName = " + default_DataFolderPathName);
      internalMsgCtrl.out("default_AddressListFileName = " + default_AddressListFileName);
      internalMsgCtrl.out("default_AddressListFilePathName = " + default_AddressListFilePathName);
      internalMsgCtrl.out("default_ZipCodesFileName = " + default_ZipCodesFileName);
      internalMsgCtrl.out("default_ZipCodesFilePathName = " + default_ZipCodesFilePathName);
      internalMsgCtrl.out("default_ZipCountsFileName = " + default_ZipCountsFileName);
      internalMsgCtrl.out("default_ZipCountsFilePathName = " + default_ZipCountsFilePathName);
      internalMsgCtrl.out("--------------------------------------------------");
   }

}
