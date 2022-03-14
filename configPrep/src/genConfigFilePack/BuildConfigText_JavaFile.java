package genConfigFilePack;

import fileWork.FileWork;
import libPack.InternalFatalError;
import libPack.InternalMsgCtrl;
import libPack.Shared;

import java.util.ArrayList;
import java.util.regex.Matcher;

public class BuildConfigText_JavaFile
{
   public BuildConfigText_JavaFile(Shared shared)
   {
      this.shared = shared;
      fileWork = shared.getFileWork();
      internalMsgCtrl = shared.getInternalMsgCtrl();
   }
   Shared shared = null;
   FileWork fileWork = null;
   InternalMsgCtrl internalMsgCtrl = null;

   public void buildJavaFile( String outPathName, String inPathName) throws InternalFatalError
   {
      if (inPathName=="")
      {
         // then the user did not supply a path to the config file.
         // Try the default
         inPathName = shared.getConfigFilePathName();
      }
      // Read in the contents of the existing configuration file
      ArrayList<String> contentsAL = null;
      try
      {
         contentsAL = fileWork.readFile(inPathName);
         if (contentsAL.size()==0)
         {
            internalMsgCtrl.out("Fatal error: The config xml file provided is empty or does not exist.");
            throw new InternalFatalError();
         }
      }
      catch (Exception excp)
      {
         internalMsgCtrl.err(InternalMsgCtrl.errKey.Error, true, "Unable to read: " + inPathName);
         throw new InternalFatalError();
      }

      try
      {
         // Create an ArrayList of ConfigText.java lines of text
         // To create a file ConfigText.java containing the code for Class ConfigText
         // used in the final output "done" string to create a cp command


         ArrayList<String> classAL = createConfigJavaFile(outPathName, contentsAL);
         if (classAL.size()==0)
         {
            internalMsgCtrl.out("createClass() failed.  No file was created.");
            throw new InternalFatalError();
         }

         // Write out the text of ConfigText.java
         if (fileWork.writeFile(outPathName, classAL)==false)
         {
            internalMsgCtrl.out("Failed to copy the data to disk.  No file was created.");
            throw new InternalFatalError();
         }
      }
      catch (InternalFatalError err)
      {

         throw new InternalFatalError();
      }

      String configPrepSource = shared.getConfigPrepSource();

      // Print out the paths used and results
      internalMsgCtrl.out("The file:  " + inPathName + ", was used as the source.");
      internalMsgCtrl.out("The file:  " + outPathName + ", was created.");
      internalMsgCtrl.out("Replace ConfigText.java in:");
      internalMsgCtrl.out(configPrepSource);
      internalMsgCtrl.out("Then rebuild the two Intellij project.");
      internalMsgCtrl.out("----------------------------------------------------------");

      internalMsgCtrl.out("For convenience (on Linux), copy and paste the following command....");
      internalMsgCtrl.out("   cp " + outPathName + " " + configPrepSource);
      internalMsgCtrl.out("----------------------------------------------------------");
    }

   // Create the lines of text that are Class configText in file ConfigText.java
   public static ArrayList<String> createConfigJavaFile(String outPathName, ArrayList<String> contentsAL)
   {
      ArrayList<String> classAL = new ArrayList<String>();

      classAL.add("package libPack;");
      classAL.add("// *****************************************************************.");
      classAL.add("// This file (ConfigText.java) can be recreated via the application genConfig.");
      classAL.add("// Edits to this file will be lost when genConfig is run and the resulting");
      classAL.add("// ConfigText.java is copied to genLables libPack.");
      classAL.add("// *****************************************************************.");
      classAL.add("");
      classAL.add("import libPack.Shared;");
      classAL.add("import fileWork.FileWork;");
      classAL.add("import java.util.ArrayList;");
      classAL.add("");
      classAL.add("// A default (DENVER) BulletinAssembly genLabels configuration file.");
      classAL.add("// This class will create a new necessary default (DENVER) config.xml.");
      classAL.add("public class ConfigText");
      classAL.add("{");
      classAL.add("   public ConfigText(Shared shared)");
      classAL.add("   {");
      classAL.add("      this.shared = shared;");
      classAL.add("   }");
      classAL.add("   private Shared shared = null;");
      classAL.add("");
      classAL.add("      ");
      classAL.add("   public String writeConfigText() throws InternalFatalError");
      classAL.add("   {");
      classAL.add("      FileWork fileWork = shared.getFileWork();");
      classAL.add("      String configFilePathName = shared.getConfigFilePathName();");
      classAL.add("");

      classAL.add("      ArrayList<String> configStrAL = createConfigStrAL();");
      classAL.add("      try");
      classAL.add("      {");
      classAL.add("         fileWork.writeFile(configFilePathName, configStrAL);");
      classAL.add("      }");
      classAL.add("      catch (InternalFatalError exct)");
      classAL.add("      {");
      classAL.add("         throw new InternalFatalError();");
      classAL.add("      }");
      classAL.add("");
      classAL.add("      return configFilePathName;");

      classAL.add("   }");

      classAL.add("");
      classAL.add("");

      classAL.add("   public ArrayList<String> createConfigStrAL()");
      classAL.add("   {");
      classAL.add("      ArrayList<String> configStrAL = new ArrayList<String>();");
      classAL.add("");

      //String patStr = "\"";
      //String repStr = "\\\"";
      //Pattern pattern = Pattern.compile(patStr);

      for (String str : contentsAL)
      {
         // Must escape the single and double quote marks.
         if (str.contains("'"))
         {
            //internalMsgCtrl.out("Original SQ: [" + str + "]");
            str = str.replaceAll("\'", Matcher.quoteReplacement("\\'"));
            //internalMsgCtrl.out("Replaced SQ: [" + str + "]");
            //internalMsgCtrl.out("--------------------------------------------------------------------------");
         }

         if (str.contains("\""))
         {
            //internalMsgCtrl.out("Original DQ: [" + str + "]");
            str = str.replaceAll("\"", Matcher.quoteReplacement("\\\""));
            //internalMsgCtrl.out("Replaced DQ: [" + str + "]");
            //internalMsgCtrl.out("--------------------------------------------------------------------------");
         }

         classAL.add("      configStrAL.add(" + "\"" + str + "\"" + ");");
      };
      classAL.add("");

      classAL.add("      return configStrAL;");
      classAL.add("   }");
      classAL.add("}");

      classAL.add("");

      return classAL;
   }
}
