package configPack;

import fileWork.FileWork;
import libPack.InternalMsgCtrl;
import libPack.InternalMsgCtrl.errKey;
import libPack.Shared;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

// 190111 Parse the config file
//

/*******************************************
 190111

 Class parses "$Home/.BulletinAssembly/config.xml"... the Bulletin pre assembly processor
 The parser uses Java DOM XML Parsing and stores all data read in class XMLData

 The programs name was changed from Windows C# name:  BulletinSlicer to
 Java BulletinAssembly.

 The config file was renamed from BulletinSlicerConfig.xml to "$Home/.BulletinAssembly/config.xml"
 The config file was moved from /user/weg/Documents/Data Sources to "$Home/.BulletinAssembly/config.xml"
 The parse of the Config file is performed via Java's DOM library
 https://www.tutorialspoint.com/java_xml/java_dom_parse_document.htm


 Side note:  I'm sure I could generalize and call the parser and get data as needed
 rather that duplicate the data in XMLData.  But the xml config file is not large
 and this is easier and more straight forward to get the job done.

 Bill Gammill
 ******************************************/

public class XMLParse
{
   public XMLParse(Shared shared)
   {
      this.shared = shared;
      internalMsgCtrl = shared.getInternalMsgCtrl();
   }
   private Shared shared = null;
   private InternalMsgCtrl internalMsgCtrl = null;

   private String errStr = "";

   public XMLData xmlParse()
   {
      // Set the path to BulletinAssembly's config file: "$Home/.BulletinAssembly/config.xml"
      String configFilePathName = shared.getConfigFilePathName();
      FileWork fileWork = shared.getFileWork();
      if (fileWork.fileExists(configFilePathName) == false)
      {
         String msg = "file: [" + configFilePathName + "] does not exist.";
         internalMsgCtrl.err(errKey.FatalError, false, msg);
         return null;
      }
      internalMsgCtrl.verbose("Using Configuration file:  " + configFilePathName);

       // The data is stored in XMLData
      XMLData xmlData = new XMLData(shared);

      try
      {
         File inputFile = new File(configFilePathName);
         DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
         DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
         Document doc = dBuilder.parse(inputFile);
         doc.getDocumentElement().normalize();
         //internalMsgCtrl.out("\nRoot element :" + doc.getDocumentElement().getNodeName()+ "\n");

         //--------------------------------------------------------------------------------
         //---------------------------DATEREMINDER-----------------------------------------
         NodeList nList = doc.getElementsByTagName("DATEREMINDER");
         //internalMsgCtrl.out("DATEREMINDER: ----------------------------: " + nList.getLength());
         if (nList == null)
         {
            internalMsgCtrl.err(errKey.Error, false,"Error reading configuration file, element:  DATEREMINDER");
            return xmlData;
         }
         if (nList.getLength() == 0)
         {
            internalMsgCtrl.err(errKey.Error, false,"Configuration file, element:  DATEREMINDER, is empty");
         }
         
         for (int temp = 0; temp < nList.getLength(); temp++)
         {
            Node nNode = nList.item(temp);
            //internalMsgCtrl.out("Current Element :" + nNode.getNodeName());

            if (nNode.getNodeType() == Node.ELEMENT_NODE)
            {
               Element eElement = (Element) nNode;

               errStr = "DateReminder";
               String DateReminder =
                     eElement.getElementsByTagName("DateReminder").item(0).getTextContent();
               //internalMsgCtrl.out("Date Reminder : " + DateReminder);
               xmlData.set_DateReminder(DateReminder);

	       errStr = "ReminderUpdated";
               String ReminderUpdated =
                     eElement.getElementsByTagName("ReminderUpdated").item(0).getTextContent();
               //internalMsgCtrl.out("Reminder Updated : " + ReminderUpdated);
               xmlData.set_ReminderUpdated(ReminderUpdated);

            }
         }

         //--------------------------------------------------------------------------------
         //----------------------------LABELSACROSS----------------------------------------
         nList = doc.getElementsByTagName("LABELSACROSS");
         //internalMsgCtrl.out("LABELSACROSS: ----------------------------: " + nList.getLength());
         if (nList == null)
         {
            internalMsgCtrl.err(errKey.Error, false,"Error reading configuration file, element:  LABELSACROSS");
            return xmlData;
         }
         if (nList.getLength() == 0)
         {
            internalMsgCtrl.err(errKey.Error, false,"Configuration file, element:  LABELSACROSS, is empty");
         }

         for (int temp = 0; temp < nList.getLength(); temp++)
         {
            Node nNode = nList.item(temp);
            //internalMsgCtrl.out("Current Element :" + nNode.getNodeName());

            if (nNode.getNodeType() == Node.ELEMENT_NODE)
            {
               Element eElement = (Element) nNode;

	       errStr = "LabelsAcross";
               String LabelsAcross =
                     eElement.getElementsByTagName("LabelsAcross").item(0).getTextContent();
               //internalMsgCtrl.out("Labels Across : " + LabelsAcross);
               xmlData.set_LabelsAcross(LabelsAcross);
            }
         }

         //--------------------------------------------------------------------------------
         //---------------------------- MINTRAYCNT ----------------------------------------
         nList = doc.getElementsByTagName("MINTRAYCNT");
         //internalMsgCtrl.out("MINTRAYCNT: ----------------------------: " + nList.getLength());
         for (int temp = 0; temp < nList.getLength(); temp++)
         {
            Node nNode = nList.item(temp);
            //internalMsgCtrl.out("Current Element :" + nNode.getNodeName());

            if (nNode.getNodeType() == Node.ELEMENT_NODE)
            {
               Element eElement = (Element) nNode;

	       errStr = "MinTrayCnt";
               String MinTrayCnt =
                     eElement.getElementsByTagName("MinTrayCnt").item(0).getTextContent();
               //internalMsgCtrl.out("Minimum Tray Count: " + MinTrayCnt);
               xmlData.set_MinTrayCnt(MinTrayCnt);
            }
         }

         //--------------------------------------------------------------------------------
         //-------------------------------RANGES-------------------------------------------
         nList = doc.getElementsByTagName("RANGES");
         //internalMsgCtrl.out("\nRANGES: ----------------------------: " + nList.getLength());
         if (nList == null)
         {
            internalMsgCtrl.err(errKey.Error, false,"Error reading configuration file, element:  RANGES");
            return xmlData;
         }
         if (nList.getLength() == 0)
         {
            internalMsgCtrl.err(errKey.Error, false,"Configuration file, element:  RANGES, is empty");
         }
         
         for (int temp = 0; temp < nList.getLength(); temp++)
         {
            //internalMsgCtrl.out("temp:  " + temp);

            Node nNode = nList.item(temp);
            //internalMsgCtrl.out("Current Element :" + nNode.getNodeName());

            if (nNode.getNodeType() == Node.ELEMENT_NODE)
            {
               Element eElement = (Element) nNode;
	       errStr = "STATE";
               String STATE = eElement.getElementsByTagName("STATE").item(0).getTextContent();
               //internalMsgCtrl.out("State : " + STATE);
               xmlData.set_State(STATE);

	       errStr = "DEN_SCF";
               String DEN_SCF = eElement.getElementsByTagName("DEN_SCF").item(0).getTextContent();
               //internalMsgCtrl.out("DEN SCF : " + DEN_SCF);
               xmlData.set_DEN_SCF(DEN_SCF);

	       errStr = "GJ_SCF";
               String GJ_SCF = eElement.getElementsByTagName("GJ_SCF").item(0).getTextContent();
               //internalMsgCtrl.out("GJ SCF : " + GJ_SCF);
               xmlData.set_GJ_SCF(GJ_SCF);

	       errStr = "DADC";
               String DADC = eElement.getElementsByTagName("DADC").item(0).getTextContent();
               //internalMsgCtrl.out("DADC : " + DADC);
               xmlData.set_DADC(DADC);

	       errStr = "IC";
               String ic = eElement.getElementsByTagName("IC").item(0).getTextContent();
               //internalMsgCtrl.out("IC   # " + ic);
               xmlData.set_ic(ic);

	       errStr = "OMX";
               String omx = eElement.getElementsByTagName("OMX").item(0).getTextContent();
               //internalMsgCtrl.out("OMX  : " + omx);
               xmlData.set_omx(omx);
            }
         }

         //--------------------------------------------------------------------------------
         //------------------------------USPSPRIORITY--------------------------------------
         nList = doc.getElementsByTagName("USPSPRIORITY");
         //internalMsgCtrl.out("USPSPRIORITY: ----------------------------: " + nList.getLength());
         if (nList == null)
         {
            internalMsgCtrl.err(errKey.Error, false,"Error reading configuration file, element:  USPSPRIORITY");
            return xmlData;
         }
         if (nList.getLength() == 0)
         {
            internalMsgCtrl.err(errKey.Error, false,"Configuration file, element:  USPSPRIORITY, is empty");
         }

         for (int temp = 0; temp < nList.getLength(); temp++)
         {
            Node nNode = nList.item(temp);
            //internalMsgCtrl.out("Current Element :" + nNode.getNodeName());

            if (nNode.getNodeType() == Node.ELEMENT_NODE)
            {
               Element eElement = (Element) nNode;

	       errStr = "PRIORITY";
	       String priority = eElement.getElementsByTagName("PRIORITIES").item(0).getTextContent();
               xmlData.set_priority(priority);
               //internalMsgCtrl.out("Priorities : " + priority);
            }
         }

         //--------------------------------------------------------------------------------
         //-----------------------------------SLICEORDER-----------------------------------
         nList = doc.getElementsByTagName("SLICEORDER");
         //internalMsgCtrl.out("SLICEORDER: ----------------------------: " + nList.getLength());
         for (int temp = 0; temp < nList.getLength(); temp++)
         {
            Node nNode = nList.item(temp);
            //internalMsgCtrl.out("Current Element :" + nNode.getNodeName());

            if (nNode.getNodeType() == Node.ELEMENT_NODE)
            {
               Element eElement = (Element) nNode;

	       errStr = "ORDER";
               String order = eElement.getElementsByTagName("ORDERS").item(0).getTextContent();
               xmlData.set_order(order);
               //internalMsgCtrl.out("Orders: " + order);
            }
         }

         //--------------------------------------------------------------------------------
         //--------------------------------------ZONES-------------------------------------
         nList = doc.getElementsByTagName("ZONES");
         //internalMsgCtrl.out("ZONES: ----------------------------: " + nList.getLength());
         if (nList == null)
         {
            internalMsgCtrl.err(errKey.Error, false,"Error reading configuration file, element:  ZONES");
            return xmlData;
         }
         if (nList.getLength() == 0)
         {
            internalMsgCtrl.err(errKey.Error, false,"Configuration file, element:  ZONES, is empty");
         }
         
         for (int temp = 0; temp < nList.getLength(); temp++)
         {
            Node nNode = nList.item(temp);
            //internalMsgCtrl.out("Current Element :" + nNode.getNodeName());

            if (nNode.getNodeType() == Node.ELEMENT_NODE)
            {
               Element eElement = (Element) nNode;
               errStr = "ZONE1";
               String ZONE1 = eElement.getElementsByTagName("ZONE1").item(0).getTextContent();
               xmlData.set_ZONE1(ZONE1);
               //internalMsgCtrl.out("Zone 1: " + zone1);

               errStr = "ZONE2";
               String ZONE2 = eElement.getElementsByTagName("ZONE2").item(0).getTextContent();
               xmlData.set_ZONE2(ZONE2);
               //internalMsgCtrl.out("Zone 2: " + ZONE2);

               errStr = "ZONE3";
               String ZONE3 = eElement.getElementsByTagName("ZONE3").item(0).getTextContent();
               xmlData.set_ZONE3(ZONE3);
               //internalMsgCtrl.out("Zone 3: " + ZONE3);

               errStr = "ZONE4";
               String ZONE4 = eElement.getElementsByTagName("ZONE4").item(0).getTextContent();
               xmlData.set_ZONE4(ZONE4);
               //internalMsgCtrl.out("Zone 4: " + ZONE4);

               errStr = "ZONE5";
               String ZONE5 = eElement.getElementsByTagName("ZONE5").item(0).getTextContent();
               xmlData.set_ZONE5(ZONE5);
               //internalMsgCtrl.out("Zone 5: " + ZONE5);

               errStr = "ZONE6";
               String ZONE6 = eElement.getElementsByTagName("ZONE6").item(0).getTextContent();
               xmlData.set_ZONE6(ZONE6);
               //internalMsgCtrl.out("Zone 6: " + ZONE6);

               errStr = "ZONE7";
               errStr = "ZONE7";
               String ZONE7 = eElement.getElementsByTagName("ZONE7").item(0).getTextContent();
               xmlData.set_ZONE7(ZONE7);
               //internalMsgCtrl.out("Zone 7: " + ZONE7);

               errStr = "ZONE8";
               String ZONE8 = eElement.getElementsByTagName("ZONE8").item(0).getTextContent();
               xmlData.set_ZONE8(ZONE8);
               //internalMsgCtrl.out("Zone 8: " + ZONE8);

               errStr = "ZONE9";
               String ZONE9 = eElement.getElementsByTagName("ZONE9").item(0).getTextContent();
               xmlData.set_ZONE9(ZONE9);
               //internalMsgCtrl.out("Zone 9: " + ZONE9);
            }
         }

         //--------------------------------------------------------------------------------
         //--------------------------------------ZIPCOUNTS---------------------------------
         nList = doc.getElementsByTagName("ZIPCOUNTS");
         //internalMsgCtrl.out("ZIPCOUNTS: ----------------------------: " + nList.getLength());
         if (nList == null)
         {
            internalMsgCtrl.err(errKey.Error, false,"Error reading configuration file, element:  ZIPCOUNTS");
            return xmlData;
         }
         if (nList.getLength() == 0)
         {
            internalMsgCtrl.err(errKey.Error, false,"Configuration file, element:  ZIPCOUNTS, is empty");
         }

         for (int temp = 0; temp < nList.getLength(); temp++)
         {
            Node nNode = nList.item(temp);
            //internalMsgCtrl.out("Current Element :" + nNode.getNodeName());

            if (nNode.getNodeType() == Node.ELEMENT_NODE)
            {
               Element eElement = (Element) nNode;

	       errStr = "ZC_IC";
               String IC = eElement.getElementsByTagName("IC").item(0).getTextContent();
               xmlData.set_ZC_IC(IC);
               //internalMsgCtrl.out("ZC_IC: " + IC);
               //eElement.getElementsByTagName("IC").item(0).getTextContent());

	       errStr = "ZC_DEN_SCF";
               String DEN_SCF = eElement.getElementsByTagName("DEN_SCF").item(0).getTextContent();
               xmlData.set_ZC_DEN_SCF(DEN_SCF);
               //internalMsgCtrl.out("ZC_DEN_SCF: " + DEN_SCF);
               //eElement.getElementsByTagName("DEN_SCF").item(0).getTextContent());

	       errStr = "ZC_DADC";
               String DADC = eElement.getElementsByTagName("DADC").item(0).getTextContent();
               xmlData.set_ZC_DADC(DADC);
               //internalMsgCtrl.out("ZC_DADC: " + DADC);
               //eElement.getElementsByTagName("DADC").item(0).getTextContent());

	       errStr = "ZC_ZONE2";
               String ZONE2 = eElement.getElementsByTagName("ZONE2").item(0).getTextContent();
               xmlData.set_ZC_ZONE2(ZONE2);
               //internalMsgCtrl.out("ZC_ZONE2: " + ZONE2);
               //eElement.getElementsByTagName("ZONE2").item(0).getTextContent());

	       errStr = "ZC_ZONE3";
               String ZONE3 = eElement.getElementsByTagName("ZONE3").item(0).getTextContent();
               xmlData.set_ZC_ZONE3(ZONE3);
               //internalMsgCtrl.out("ZC_ZONE3: " + ZONE3);
               //eElement.getElementsByTagName("ZONE3").item(0).getTextContent());

	       errStr = "ZC_ZONE4";
	       String ZONE4 = eElement.getElementsByTagName("ZONE4").item(0).getTextContent();
               xmlData.set_ZC_ZONE4(ZONE4);
               //internalMsgCtrl.out("ZC_ZONE4: " + ZONE4);
               //eElement.getElementsByTagName("ZONE4").item(0).getTextContent());

	       errStr = "ZC_ZONE5";
	       String ZONE5 = eElement.getElementsByTagName("ZONE5").item(0).getTextContent();
               xmlData.set_ZC_ZONE5(ZONE5);
               //internalMsgCtrl.out("ZC_ZONE5: " + ZONE5);
               //eElement.getElementsByTagName("ZONE5").item(0).getTextContent());

	       errStr = "ZC_ZONE6";
	       String ZONE6 = eElement.getElementsByTagName("ZONE6").item(0).getTextContent();
               xmlData.set_ZC_ZONE6(ZONE6);
               //internalMsgCtrl.out("ZC_ZONE6: " + ZONE6);
               //eElement.getElementsByTagName("ZONE6").item(0).getTextContent());

	       errStr = "ZC_ZONE7";
	       String ZONE7 = eElement.getElementsByTagName("ZONE7").item(0).getTextContent();
               xmlData.set_ZC_ZONE7(ZONE7);
               //internalMsgCtrl.out("ZC_ZONE7: " + ZONE7);
               //eElement.getElementsByTagName("ZONE7").item(0).getTextContent());

	       errStr = "ZC_ZONE8";
	       String ZONE8 = eElement.getElementsByTagName("ZONE8").item(0).getTextContent();
               xmlData.set_ZC_ZONE8(ZONE8);
               //internalMsgCtrl.out("ZC_ZONE8: " + ZONE8);
               //eElement.getElementsByTagName("ZONE8").item(0).getTextContent());
               // TODO -- ZONE9 has appeared in the USPS Zones table; but my CSV files
               // are not up to date with ZONE9... here is the code, a bit early

	       errStr = "ZC_ZONE9";
	       String ZONE9 = eElement.getElementsByTagName("ZONE9").item(0).getTextContent();
               xmlData.set_ZC_ZONE9(ZONE9);
               //internalMsgCtrl.out("ZC_ZONE9: " + ZONE9);
               //eElement.getElementsByTagName("ZONE9").item(0).getTextContent());
             }
         }

         //--------------------------------------ZIPCODES---------------------------------
         nList = doc.getElementsByTagName("ZIPCODES");
         //internalMsgCtrl.out("ZIPCODES: ----------------------------: " + nList.getLength());
         if (nList == null)
         {
            internalMsgCtrl.err(errKey.Error, false,"Error reading configuration file, element:  ZIPCODES");
            return xmlData;
         }
         if (nList.getLength() == 0)
         {
            internalMsgCtrl.err(errKey.Error, false,"Configuration file, element:  ZIPCONES, is empty");
         }

         for (int temp = 0; temp < nList.getLength(); temp++)
         {
            Node nNode = nList.item(temp);
            //internalMsgCtrl.out("Current Element :" + nNode.getNodeName());

            if (nNode.getNodeType() == Node.ELEMENT_NODE)
            {
               Element eElement = (Element) nNode;

	       errStr = "ZIP";
               String ZIP = eElement.getElementsByTagName("ZIP").item(0).getTextContent();
               xmlData.set_Zs_ZIP(ZIP);
               //internalMsgCtrl.out("Zs_ZIP: " + ZIP);
               //eElement.getElementsByTagName("ZIP").item(0).getTextContent());

	       errStr = "PIECES";
               String PIECES = eElement.getElementsByTagName("PIECES").item(0).getTextContent();
               xmlData.set_Zs_PIECES(PIECES);
               //internalMsgCtrl.out("ZC_PIECES: " + PIECES);
               //eElement.getElementsByTagName("PIECES").item(0).getTextContent());

	       errStr = "CHK";
	       String CHK = eElement.getElementsByTagName("CHK").item(0).getTextContent();
               xmlData.set_Zs_CHK(CHK);
               //internalMsgCtrl.out("ZC_CHK: " + CHK);
               //eElement.getElementsByTagName("CHK").item(0).getTextContent());

	       errStr = "TRAY";
	       String TRAY = eElement.getElementsByTagName("TRAY").item(0).getTextContent();
               xmlData.set_Zs_TRAY(TRAY);
               //internalMsgCtrl.out("ZC_TRAY: " + TRAY);
               //eElement.getElementsByTagName("TRAY").item(0).getTextContent());
            }
         }

         //--------------------------------------------------------------------------------
         //--------------------------------------ADDRESSLIST-------------------------------
         nList = doc.getElementsByTagName("ADDRESSLIST");
         //internalMsgCtrl.out("ADDRESSLIST: ----------------------------: " + nList.getLength());
         if (nList == null)
         {
            internalMsgCtrl.err(errKey.Error, false,"Error reading configuration file, element:  ADDRESSLIST");
            return xmlData;
         }
         if (nList.getLength() == 0)
         {
            internalMsgCtrl.err(errKey.Error, false,"Configuration file, element:  ADDRESSLIST, is empty");
         }
         
         for (int temp = 0; temp < nList.getLength(); temp++)
         {
            Node nNode = nList.item(temp);
            //internalMsgCtrl.out("Current Element :" + nNode.getNodeName());

            if (nNode.getNodeType() == Node.ELEMENT_NODE)
            {
               Element eElement = (Element) nNode;

	       errStr = "LastName";
               String Addr_LastName = eElement.getElementsByTagName("LastName").item(0).getTextContent();
               xmlData.set_Addr_LastName(Addr_LastName);
               //internalMsgCtrl.out("lastname: " + Addr_LastName);

	       errStr = "FirstName";
               String Addr_FirstName = eElement.getElementsByTagName("FirstName").item(0).getTextContent();
               xmlData.set_Addr_FirstName(Addr_FirstName);
               //internalMsgCtrl.out("firstname: " + Addr_FirstName);

	       errStr = "OtherName";
               String Addr_OtherName = eElement.getElementsByTagName("OtherName").item(0).getTextContent();
               xmlData.set_Addr_OtherName(Addr_OtherName);
               //internalMsgCtrl.out("othername: " + Addr_OtherName);

	       errStr = "Address";
               String Addr_Address = eElement.getElementsByTagName("Address").item(0).getTextContent();
               xmlData.set_Addr_Address(Addr_Address);
               //internalMsgCtrl.out("address: " + Addr_Address);

	       errStr = "City";
               String Addr_City = eElement.getElementsByTagName("City").item(0).getTextContent();
               xmlData.set_Addr_City(Addr_City);
               //internalMsgCtrl.out("city: " + Addr_City);

	       errStr = "State";
               String Addr_State = eElement.getElementsByTagName("State").item(0).getTextContent();
               xmlData.set_Addr_State(Addr_State);
               //internalMsgCtrl.out("state: " + Addr_State);

	       errStr = "Zip";
               String Addr_Zip = eElement.getElementsByTagName("Zip").item(0).getTextContent();
               xmlData.set_Addr_Zip(Addr_Zip);
               //internalMsgCtrl.out("zip: " + Addr_Zip);

	       errStr = "Plus4";
               String Addr_Plus4 = eElement.getElementsByTagName("Plus4").item(0).getTextContent();
               xmlData.set_Addr_Plus4(Addr_Plus4);
               //internalMsgCtrl.out("plus4: " + Addr_Plus4);

	       errStr = "Renewal";
               String Addr_Renewal = eElement.getElementsByTagName("Renewal").item(0).getTextContent();
               xmlData.set_Addr_Renewal(Addr_Renewal);
               //internalMsgCtrl.out("renewal: " + Addr_Renewal);

            }
         }

         //--------------------------------------------------------------------------------
         //--------------------------------------MAILMERGEHEADER---------------------------
         nList = doc.getElementsByTagName("MAILMERGEHEADER");
         //internalMsgCtrl.out("MAILMERGEHEADER: ----------------------------: " + nList.getLength());
         if (nList == null)
         {
            internalMsgCtrl.err(errKey.Error, false,"Error reading configuration file, element:  MAILMERGEHEADER");
            return xmlData;
         }
         if (nList.getLength() == 0)
         {
            internalMsgCtrl.err(errKey.Error, false,"Configuration file, element:  MAILMERGEHEADER, is empty");
         }

         for (int temp = 0; temp < nList.getLength(); temp++)
         {
            Node nNode = nList.item(temp);
            //internalMsgCtrl.out("Current Element :" + nNode.getNodeName());

            if (nNode.getNodeType() == Node.ELEMENT_NODE)
            {
               Element eElement = (Element) nNode;

	       errStr = "MMH_LastName";
               String MMH_LastName = eElement.getElementsByTagName("LastName").item(0).getTextContent();
               xmlData.set_MMH_LastName(MMH_LastName);
               //internalMsgCtrl.out("lastname: " + MMH_LastName);

	       errStr = "MMH_FirstName";
               String MMH_FirstName = eElement.getElementsByTagName("FirstName").item(0).getTextContent();
               xmlData.set_MMH_FirstName(MMH_FirstName);
               //internalMsgCtrl.out("firstname: " + MMH_FirstName);

	       errStr = "MMH_OtherName";
               String MMH_OtherName = eElement.getElementsByTagName("OtherName").item(0).getTextContent();
               xmlData.set_MMH_OtherName(MMH_OtherName);
               //internalMsgCtrl.out("othername: " + MMH_OtherName);

	       errStr = "MMH_Address";
               String MMH_Address = eElement.getElementsByTagName("Address").item(0).getTextContent();
               xmlData.set_MMH_Address(MMH_Address);
               //internalMsgCtrl.out("address: " + MMH_Address);

	       errStr = "MMH_City";
               String MMH_City = eElement.getElementsByTagName("City").item(0).getTextContent();
               xmlData.set_MMH_City(MMH_City);
               //internalMsgCtrl.out("city: " + MMH_City);

	       errStr = "MMH_State";
               String MMH_State = eElement.getElementsByTagName("State").item(0).getTextContent();
               xmlData.set_MMH_State(MMH_State);
               //internalMsgCtrl.out("state: " + MMH_State);

	       errStr = "MMH_Zip";
               String MMH_Zip = eElement.getElementsByTagName("Zip").item(0).getTextContent();
               xmlData.set_MMH_Zip(MMH_Zip);
               //internalMsgCtrl.out("zip: " + MMH_Zip);

	       errStr = "MMH_Plus4";
               String MMH_Plus4 = eElement.getElementsByTagName("Plus4").item(0).getTextContent();
               xmlData.set_MMH_Plus4(MMH_Plus4);
               //internalMsgCtrl.out("plus4: " + MMH_Plus4);

	       errStr = "MMH_Renewal";
               String MMH_Renewal = eElement.getElementsByTagName("Renewal").item(0).getTextContent();
               xmlData.set_MMH_Renewal(MMH_Renewal);
               //internalMsgCtrl.out("renewal: " + MMH_Renewal);

            }
         }

         //--------------------------------------------------------------------------------
         //---------------------------------- LABELLINES ----------------------------------
         nList = doc.getElementsByTagName("LABELLINES");
         //internalMsgCtrl.out("LABELLINES: ----------------------------: " + nList.getLength());
         if (nList == null)
         {
            internalMsgCtrl.err(errKey.Error, false,"Error reading configuration file, element:  LABELLINES");
            return xmlData;
         }
         if (nList.getLength() == 0)
         {
            internalMsgCtrl.err(errKey.Error, false,"Configuration file, element:  LABELLINES, is empty");
         }
         
         for (int temp = 0; temp < nList.getLength(); temp++)
         {
            Node nNode = nList.item(temp);
            //internalMsgCtrl.out("Current Element :" + nNode.getNodeName());

            if (nNode.getNodeType() == Node.ELEMENT_NODE)
            {
               Element eElement = (Element) nNode;

	       errStr = "Line1Column";
               String Line1Column = eElement.getElementsByTagName("Line1Column").item(0).getTextContent();
               xmlData.set_Line1Column(Line1Column);
               //internalMsgCtrl.out("line1Column: " + Line1Column);

	       errStr = "Line2Column";
               String Line2Column = eElement.getElementsByTagName("Line2Column").item(0).getTextContent();
               xmlData.set_Line2Column(Line2Column);
               //internalMsgCtrl.out("line2Column: " + Line2Column);

	       errStr = "Line3Column";
               String Line3Column = eElement.getElementsByTagName("Line3Column").item(0).getTextContent();
               xmlData.set_Line3Column(Line3Column);
               //internalMsgCtrl.out("line3Column: " + Line3Column);

	       errStr = "Line4Column";
               String Line4Column = eElement.getElementsByTagName("Line4Column").item(0).getTextContent();
               xmlData.set_Line4Column(Line4Column);
               //internalMsgCtrl.out("line4Column: " + Line4Column);

	       errStr = "Line5Column";
               String Line5Column = eElement.getElementsByTagName("Line5Column").item(0).getTextContent();
               xmlData.set_Line5Column(Line5Column);
               //internalMsgCtrl.out("line5Column: " + Line5Column);
            }
         }
      }
      catch (Exception excp)
      {
         internalMsgCtrl.err(errKey.FatalError, false, "Error reading Element " + errStr + " in file:  " + configFilePathName);
         //excp.printStackTrace();
         xmlData = null;
      }
      finally
      {
         return xmlData;
      }
   }
}
