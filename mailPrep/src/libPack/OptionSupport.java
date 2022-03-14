package libPack;

// OptionSupport is a private class used only by Shared
// handles pass-through calls and returns to OptionSupport
// OptionSupport handles folder and file names per CLIOptions needs.

import fileWork.FileWork;

import static libPack.InternalMsgCtrl.errKey;

/***********************************************************************
 // Options (CLIOptions (Args4j)  (and maybe some support stuff)
 /***********************************************************************
 * Side comment:  The filenames take a circuitous route to allow them to
 * be defined here in OptionSupport (under Shared).
 * The easy thing to do would be to define them in CLIOptions.java.
 * BUT I want to define them here in optionsSupport
 * Explained:
 *    1) define final default in OptionsSupport (available via pass-through
 *       in Shared)
 *    2) set CLIOptions filename String in Shared
 *    3) Then an instance of CLIOptions is created and shared.get...()
 *       is run which sets the CLIOptions var filename to the default.
 *    4) Then if a user provides an alternate filename it causes
 *       shared.set...() to call pass-though OptionSupport.set() to be
 *       called which sets filename in OptionsSupport
 *    5) Similarly, any use of the filename, calls a Shared.get, which
 *       is passed through to OptionSupport and returned.
 *
 *    Note that the instance of CLIOptions is created in Shared
 *    and optioned from Shared by Main.  This insures Shared defaults
 *    are set prior to CLIOptions asking for them.
 ***********************************************************************/

@SuppressWarnings("rawtypes")
public class OptionSupport {
    private Shared shared = null;
    private InternalMsgCtrl internalMsgCtrl = null;
    private FileWork fileWork = null;

    //###############################################
    // Defined Defaults
    private final String default_appFolderName = ".mailPrep";
    private final String default_configFileName = "config.xml";
    private final String default_documentsFolderName = "Documents";
    private final String default_bulletinFolderName = "Bulletin";
    private final String default_dataFolderName = "Assembly";
    private final String default_addressListFileName = "addressList.csv";
    private final String default_zipCodesFileName = "zipCodes.csv";
    private final String default_zipCountsFileName = "zipCounts.csv";

    // The data path is absolute path the data folder (where CSV files are found)
    // Side comment:  I normally use the name "path" as an absolute path to a FILE rather
    // than a FOLDER.  So I am breaking a rule here.  But, it seems right somehow.
    private String default_appFolderHome = "";
    private String default_dataFolderPathName = "";
    private String default_configFilePathName = "";
    private String default_addressListFilePathName = "";
    private String default_zipCodesFilePathName = "";
    private String default_zipCountsFilePathName = "";


    private String dataFolderPathName = "";


    //###############################################

    private String folderBreak = "";
    private String lineBreak = "";
    // ------------------------------------------------------
    // (See note above on how Shared and CLIOptions are blended.)

    // Option:
    // -af --appFolder --- is user provided appFolder
    //                     This is forced to live in the users home folder

    // The default folder hold an application required configuration file
    //     where "BulletinAssembly" is assumed the name of the application
    //     and .BulletinAssembly is the name of the folder in the users home folder
    //     (Linux) "$Home/.BulletinAssembly/config.xml"
    //     (Win)   "[user home]\MyDocuments\.BulletinAssembly\config.xml"
    private String os = "";
    private String appFolderName = "";
    private String appFolderPathName = "";
    private String configFileName = "";
    private String configFilePathName = "";

    // (See note above on how Shared and CLIOptions are blended.)
    // Option:
    // -cf -- configFile --- is the user provided

    // The default file name is config.xml.
    // If --appFolder is not provided the file lives in [see below] as config.xml
    //     (Linux) "$Home/.BulletinAssembly/config.xml"
    //     (Win)   "MyDocuments\BulletinAssembly\config.xml"

    // (Windows historically used and may still use "My Data Source" (or MyDataSource).
    // private final String default_configFileName = "config.xml";
    //-----------------------------------------------------------
    // A folder name (not a path) (see dataPathName)
    // data folder is where the CSV working files are found
    // private final String default_dataFolderName = "Assembly";
    private String dataFolderName = "";
    private String addressListFileName = "";
    private String addressListFilePathName = "";
    private String zipCodesFileName = "";
    private String zipCodesFilePathName = "";
    // (See note above on how Shared and CLIOptions are blended.)
    private String zipCountsFileName = "";
    private String zipCountsFilePathName = "";
    // ------------------------------------------------------
    // (See note above on how Shared and CLIOptions are blended.)

    // Option
    // -dp --dataFolderPath -- absolute path ending with folder name

    // dataFolderName or dataFolderPath:  where one finds the csv files to be processed

    // Defaults:
    //     (Linux) $HOME/Document/Bulletin/Assembly,
    //     (Win) C:\Users\[you]\MyDocuments\Bulletin\Assembly
    // (Windows historically used "My Data Source", historically may still (or MyDataSource).
    // (Windows historically used "Bulletin" (and may still use) rather than "Bulletin\Assembly".)
    //     Bulletin is where bulletin files and work is stored
    //     Assembly is where CSV files needed for work are stored


    public OptionSupport(Shared shared) {
        this.shared = shared;
        internalMsgCtrl = shared.getInternalMsgCtrl();
        fileWork = shared.getFileWork();

        os = shared.getOS();
        folderBreak = shared.getFolderBreak();
        lineBreak = shared.getLineBreak();

        //--------------------------------------------------------------------------------------
        // Define the default names of the required CSV files.
        // And ensure the usage names and paths are set from the defualts
        // App folder name is where application (config.xml) data is found
        // Data Folder is where CSV files to be processed are found
        //       AddressList.csv, ZipCodes.csv and ZipCounts.csv

        this.setDefaultAppFolderHome();
        this.appFolderName = default_appFolderName;
        this.appFolderPathName = default_appFolderHome + folderBreak + default_appFolderName;

        this.setDefaultDataFolderPathName();
        this.dataFolderName = default_dataFolderName;
        this.dataFolderPathName = default_dataFolderPathName;

        this.setDefaultConfigFilePathName();
        this.configFileName = default_configFileName;
        this.configFilePathName = default_configFilePathName;

        this.setDefaultAddressListFilePathName();
        this.addressListFileName = default_addressListFileName;
        this.addressListFilePathName = dataFolderPathName + folderBreak + addressListFileName;

        this.setDefaultZipCodesFilePathName();
        this.zipCodesFileName = default_zipCodesFileName;
        this.zipCodesFilePathName = dataFolderPathName + folderBreak + zipCodesFileName;

        this.setDefaultZipCountsFilePathName();
        this.zipCountsFileName = default_zipCountsFileName;
        this.zipCountsFilePathName = dataFolderPathName + folderBreak + zipCountsFileName;

    }

    // The application folder (appFolder) is forced to live in the uses home folder
    private void setDefaultAppFolderHome() {
        default_appFolderHome = shared.getHome();
    }

    private void setDefaultDataFolderPathName() {
        String dataHomeFolderPath = shared.getHome() + folderBreak + default_documentsFolderName + folderBreak + default_bulletinFolderName;
        this.default_dataFolderPathName = dataHomeFolderPath + folderBreak + this.default_dataFolderName;
    }

    private void setDefaultConfigFilePathName() {
        default_configFilePathName = default_appFolderHome + folderBreak + default_appFolderName +
                folderBreak + default_configFileName;
    }

    private void setDefaultAddressListFilePathName() {
        default_addressListFilePathName = default_dataFolderPathName + folderBreak + default_addressListFileName;
    }

    private void setDefaultZipCodesFilePathName() {
        default_zipCodesFilePathName = default_dataFolderPathName + folderBreak + default_zipCodesFileName;
    }

    private void setDefaultZipCountsFilePathName() {
        default_zipCountsFilePathName = default_dataFolderPathName + folderBreak + default_zipCountsFileName;
    }
    //--------------------------------------------------------------------------------------

    public String getAppFolderPath() {
        return appFolderPathName;
    }

    // A folder name (not a path)
    public String getAppFolderName() {
        return appFolderName;
    }

    // The application folder (appFolder) is forced to live in the uses home folder
    public void setAppFolderName(String appFolderName) {
        if (appFolderName == null || appFolderName == "") {
            internalMsgCtrl.err(errKey.FatalError, true, "Invalid app folder provided.");
        } else if (appFolderName.endsWith(folderBreak)) {
            // User may have terminated appFolderName with a "/"... if so remove it
            int len = appFolderName.length();
            this.appFolderName = appFolderName.substring(0, len - 1);
        } else {
            this.appFolderName = appFolderName;
        }

        // The application folder (appFolder) is forced to live in the uses home folder
        appFolderPathName = default_appFolderHome + folderBreak + appFolderName;

        // The app folder path change, must correct the config file path name
        setConfigFilePathName(appFolderPathName, configFileName);

        // messages for use if --verbose is set
        internalMsgCtrl.verbose("App folder is set to:  \"" + this.appFolderName + "\"");
        internalMsgCtrl.verbose("App folder path is set to:  \"" + this.appFolderPathName + "\"");
    }

    // An absolute path to the app folder
    public String getAppFolderPathName() {
        return appFolderPathName;
    }

    //-----------------------------------------------------------------------------
    public String getConfigFileName() {
        return configFileName;
    }

    public void setConfigFileName(String configFileName) {

        if (configFileName == null || configFileName == "") {
            // I can not abide an empty configFileName
            this.configFileName = default_configFileName;
        } else if (configFileName.endsWith(folderBreak)) {
            // User may have terminated configFileName with a "/"... if so remove it
            int len = configFileName.length();
            this.configFileName = configFileName.substring(0, len - 1);
        } else {
            this.configFileName = configFileName;
        }

        setConfigFilePathName(appFolderPathName, configFileName);

        if (fileWork.fileExists(configFilePathName)) {
            this.configFileName = configFileName;
        } else {
            this.configFileName = default_configFileName;
            this.configFilePathName = default_appFolderHome + folderBreak + default_appFolderName + default_configFileName;
        }

        internalMsgCtrl.verbose("Config file is set to:  \"" + configFileName + "\"");
        internalMsgCtrl.verbose("Config file path is set to:  \"" + configFilePathName + "\"");
    }

    private void setConfigFilePathName(String appFolderPathName, String configFileName) {
        this.configFilePathName = default_appFolderHome + folderBreak + appFolderName +
                folderBreak + configFileName;
    }

    public String getConfigFilePathName() {
        return configFilePathName;
    }

    // ------------------------------------------------------
    // (See note above on how Shared and CLIOptions are blended.)

    // Option

    // A folder name (not a path) (see dataPathName)
    public String getDataFolderName() {
        return dataFolderName;
    }

    public String getDataFolderPathName() {
        return dataFolderPathName;
    }
    // ------------------------------------------------------
    // (See note above on how Shared and CLIOptions are blended.)

    // Option
    // -zc --zipCodes  -- the CSV file containing zip code data

    public void setDataFolderPathName(String dataFolderPathName) {
        if (dataFolderPathName == null || dataFolderPathName == "") {
            this.dataFolderPathName = default_dataFolderPathName;
            return;
        }

        // replace leading "~" with home (home does not have a trailing slash)
        if (dataFolderPathName.startsWith("~")) {
            this.dataFolderPathName = shared.expandTilde(dataFolderPathName);
        } else {
            this.dataFolderPathName = dataFolderPathName;
        }

        char ch = this.dataFolderPathName.charAt(0);
        if (os.equals("linux")) {
            if (!(ch == '/')) {
                internalMsgCtrl.err(errKey.Error, true, "Data path must be an absolute path.");
                internalMsgCtrl.err(errKey.Error, true, "Data path must begin with a " + folderBreak);
                return;
            }
            if (!this.dataFolderPathName.contains(folderBreak)) {
                internalMsgCtrl.err(errKey.Error, true, "Data path must be an absolute path.");
                internalMsgCtrl.err(errKey.Error, true, "Data path contain a " + folderBreak);
                return;
            }
        } else {
            // is Windows
            if (!this.dataFolderPathName.contains(folderBreak)) {
                internalMsgCtrl.err(errKey.Message, true, "Data path must be an absolute path.");
                internalMsgCtrl.err(errKey.Message, true, "Data path must begin with a drive letter.");
                return;
            }
            // Check to see if starts with a letter
            if (!(ch >= 'C' && ch <= 'Z')) {
                internalMsgCtrl.err(errKey.Error, true, "Data path must be an absolute path.");
                internalMsgCtrl.err(errKey.Error, true, "Data path must begin with a drive letter.");
                return;
            }
        }

        if (this.dataFolderPathName.endsWith(folderBreak)) {
            internalMsgCtrl.err(errKey.Error, true, "Data aps path must not end with a " + folderBreak);
            return;
        }
        // There isn't any need to check for existence or throw an exception.
        // The calls will ultimately end up in GenLabelsMain "//-- 3 Get and check for existence the data path"
        // and there, if the file is not found, will then cause a InternalFatalError exception to be thrown.
        // "Unable to find data directory: xxxx"

        // As the data path has been changed via options, files containing data path must be corrected.
        setAddressListFilePathName(addressListFileName);
        setZipCodesFilePathName(zipCodesFileName);
        setZipCountsFilePathName(zipCountsFileName);
    }

    //----------------------------------------------------------
    // Address List


    public String getAddressListFilePathName() {
        return addressListFilePathName;
    }

    public String getAddressListFileName() {
        return addressListFileName;
    }

    public void setAddressListFileName(String addressListFileName) {
        if (addressListFileName == null || addressListFileName == "") {
            // I can not abide an empty addressListFileName
            this.addressListFileName = default_addressListFileName;
        } else if (addressListFileName.endsWith(folderBreak)) {
            // User may have terminated addressListFileName with a "/"... if so remove it
            int len = addressListFileName.length();
            this.addressListFileName = addressListFileName.substring(0, len - 1);

        } else {
            this.addressListFileName = addressListFileName;
        }

        setAddressListFilePathName(this.addressListFileName);
        if (fileWork.fileExists(this.addressListFilePathName)) {
            this.addressListFileName = addressListFileName;
        } else {
            this.addressListFileName = default_zipCodesFileName;
            this.addressListFilePathName = dataFolderPathName + folderBreak + default_addressListFileName;
        }

        internalMsgCtrl.verbose("Address file is set to:  \"" + this.addressListFileName + "\"");
        internalMsgCtrl.verbose("Address file path is set to:  \"" + this.addressListFilePathName + "\"");
    }

    private void setAddressListFilePathName(String addressListFileName) {
        this.addressListFilePathName = dataFolderPathName + folderBreak + addressListFileName;
    }

    public String getZipCodesFileName() {
        return zipCodesFileName;
    }
    // ------------------------------------------------------
    // Zip Codes


    public String getZipCodesFilePathName() {
        return zipCodesFilePathName;
    }

    public String getZipCountsFileName() {
        return zipCountsFileName;
    }

    public void setZipCodesFileName(String zipCodesFileName) {
        if (zipCodesFileName == null || zipCodesFileName == "") {
            // I can not abide an empty zipCodesFileName
            this.zipCodesFileName = default_zipCodesFileName;
        } else if (zipCodesFileName.endsWith(folderBreak)) {
            // User may have terminated zipCodesFileName with a "/"... if so remove it
            int len = zipCodesFileName.length();
            this.zipCodesFileName = zipCodesFileName.substring(0, len - 1);
        } else {
            this.zipCodesFileName = zipCodesFileName;
        }


        setZipCodesFilePathName(zipCodesFileName);
        if (fileWork.fileExists(zipCodesFilePathName)) {
            this.zipCodesFileName = zipCodesFileName;
        } else {
            this.zipCodesFileName = default_zipCodesFileName;
            this.zipCodesFilePathName = dataFolderPathName + folderBreak + default_zipCodesFileName;
        }

        internalMsgCtrl.verbose("Zip Code file is set to:  \"" + zipCodesFileName + "\"");
        internalMsgCtrl.verbose("Zip Code file path is set to:  \"" + zipCodesFilePathName + "\"");
    }

    private void setZipCodesFilePathName(String zipCodesFileName) {
        this.zipCodesFilePathName = dataFolderPathName + folderBreak + zipCodesFileName;
    }

    //----------------------------------------------------------
    // Zip Counts


    public String getZipCountsFilePathName() {
        return zipCountsFilePathName;
    }

    public void setZipCountsFileName(String zipCountsFileName) {
        if (zipCountsFileName == null || zipCountsFileName == "") {
            // I can not abide an empty zipCountsFileName
            this.zipCountsFileName = default_zipCountsFileName;
        } else if (zipCountsFileName.endsWith(folderBreak)) {
            // User may have terminated zipCountsFileName with a "/"... if so remove it
            int len = zipCountsFileName.length();
            this.zipCountsFileName = zipCountsFileName.substring(0, len - 1);
        } else {
            this.zipCountsFileName = zipCountsFileName;
        }

        setZipCountsFilePathName(zipCountsFileName);
        if (fileWork.fileExists(zipCountsFilePathName)) {
            this.zipCountsFileName = zipCountsFileName;
        } else {
            this.zipCountsFileName = default_zipCountsFileName;
            this.zipCountsFilePathName = dataFolderPathName + folderBreak + default_zipCountsFileName;
        }
        internalMsgCtrl.verbose("Zip Counts file is set to:  \"" + zipCountsFileName + "\"");
        internalMsgCtrl.verbose("Zip Counts file path is set to:  \"" + zipCountsFilePathName + "\"");
    }

    private void setZipCountsFilePathName(String zipCountsFileName) {
        this.zipCountsFilePathName = dataFolderPathName + folderBreak + zipCountsFileName;
    }

    // ------------------------------------------------------

}
