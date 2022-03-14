package fileWork;

//190114

import libPack.InternalFatalError;
import libPack.InternalMsgCtrl;
import libPack.InternalMsgCtrl.errKey;
import libPack.Shared;

import java.io.*;
import java.util.ArrayList;


public class FileWork
{
   private String lineBreak;
   private String folderBreak;
   private String os;
   private boolean osFlg;  // true==linux, false==windows
   private Shared shared = null;
   private InternalMsgCtrl internalMsgCtrl = null;

   public FileWork(Shared shared)
   {
      this.shared = shared;
      os = shared.getOS();  // either linux or win
      osFlg = (os.equals("linux")) ? true:false;
      lineBreak = shared.getLineBreak();
      folderBreak = shared.getFolderBreak();
      internalMsgCtrl = shared.getInternalMsgCtrl();
   }

   // Read a text file into an ArrayList of String
   public ArrayList<String> readFile(String filePath) throws InternalFatalError
   {
      ArrayList<String> contentAL = new ArrayList<String>();
      File file = new File(filePath);
      if (file.exists())
      {
         try
         {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            while ((line = bufferedReader.readLine())!=null)
            {
               contentAL.add(line);
            }
            bufferedReader.close();
            fileReader.close();
         }
         catch (IOException e)
         {
            e.printStackTrace();
         }
         finally
         {
            // Learning comment:
            // I only had the catch (no finally) and tested an invalid filename.
            // I could not get a return of an empty array list.  Adding the finally
            // solved he problem.  The catch happens. Then the finally returns the
            // empty array list for the calling method to printout an error.
            return contentAL;
         }
      } else
      {
         internalMsgCtrl.err(errKey.FatalError, false, "Could not find:  " + filePath);
         //throw new InternalFatalError("Folder does not exist");
         throw new InternalFatalError();
      }
   }

   // Write a text file from an ArrayList of String
   public boolean writeFile(String filePath, ArrayList<String> strAL) throws InternalFatalError
   {
      try
      {
         File file = new File(filePath);
         FileWriter fileWriter = new FileWriter(file);
         BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
         for (String str : strAL)
         {
            bufferedWriter.write(str + lineBreak);
         }
         bufferedWriter.close();
         fileWriter.close();
      }
      catch (IOException expt)
      {
         String errMsg = expt.getMessage();
         internalMsgCtrl.err(errKey.FatalError, false, errMsg);
         //expt.printStackTrace();
         //return false;
         throw new InternalFatalError();
      }
      return true;
   }

   // If isFolder() returns true, then know it is a Directory, therefor it must also EXIST
   public boolean isFolder(String path)
   {
      // The default for folder is an empty string
      if (path=="")
      {
         return false;
      } else
      {
         String tmpPath = path;
         if (path.startsWith("~"))
         {
            tmpPath = shared.expandTilde(path);
         }
         if (path.startsWith("."))
         {
            try
            {
               tmpPath = new File(".").getCanonicalPath();
            }
            catch (Exception excp)
            {
               internalMsgCtrl.err(errKey.Error, true, "Unable to expande \".\" to CWD.");
               return false;
            }
         }
         // Then folder has been set as an option... ensure that it exists
         File file = new File(tmpPath);
         // Then shared folder is OK... nothing more to do
         // I know return (file.ex....) works... I wanted to see results in debug
         boolean flg = file.isDirectory();
         return flg;
      }
   }

   public boolean isFile(String path)
   {
      System.out.println("DB isFile() path:  " + path);
      // The default for folder is an empty string
      if (path=="")
      {
         return false;
      } else
      {
         String tmpPath = path;
         if (path.startsWith("~"))
         {
            tmpPath = shared.expandTilde(path);
         }
         // Then folder has been set as an option... ensure that it exists
         File file = new File(tmpPath);
         // Then shared folder is OK... nothing more to do
         // I know return (file.ex....) works... I wanted to see results in debug
         boolean flg = file.isFile();
         return flg;
      }
   }

   // filePath: absolute file path
   public boolean fileExists(String filePath)
   {
      File file = new File(filePath);
      return file.exists();
   }

   // return true if is an absolutePath
   public boolean isAbsolutPath(String path)
   {
      String os = shared.getOS();
      // true if linux, false if Windows
      boolean osFlg = (os.equals("linux")) ? true:false;

      if (path=="")
      {
         return false;
      }

      char ch = path.charAt(0);
      switch (ch)
      {
         case '/':
            return (os.equals("linux")) ? true:false;
         case '~':
            return (os.equals("linux")) ? true:false;
         default:
            // Windows Top level is DriveLetter:\ (C:\"
            if ((ch >= 'C' && ch <= 'Z') && (osFlg==false) /* Windows */)
            {
               if (path.length() < 3) return false;
               char ch1 = path.charAt(1);
               char ch2 = path.charAt(2);
               return (ch1==':' & ch2=='\\') ? true:false;
            }
            return false;
      }
   }

   // create a path to a file or folder
   // fileFlg:  true path ends in a filename
   //           false path ends with a folder name
   // error throws InternalFatalError
   public void createPath(String pathName, boolean fileFlg) throws InternalFatalError
   {
      try
      {
         File file = new File(pathName);
         if (file.exists()) return;
         if (fileFlg)
         {
            String fileName = file.getName();
            // strip the fileName from the path
            String tmpPath = file.getParent();
            File ff = new File(tmpPath);
            if (ff.exists())
            {
               // Then the path to the file exists
               file.createNewFile();
               return;
            }
            // the path must be created
            ff.mkdirs();
            // now the file
            file.createNewFile();
            return;
         } else
         {
            file.mkdirs();
            return;
         }
      }
      catch (Exception excp)
      {
         internalMsgCtrl.err(errKey.Error, true, "Unable to create a path.");
         throw new InternalFatalError();
      }
   }
}


// Keep for reference -- fina the CWD... must be wrapped in try/catch
//     String currentPath = "";
//     try
//     {
//        currentPath = new java.io.File(".").getCanonicalPath();
//     }
//     catch (Exception excp)
//    {
//       throw new InternalFatalError();
//    }
