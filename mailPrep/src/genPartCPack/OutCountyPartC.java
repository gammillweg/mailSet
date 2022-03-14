package genPartCPack;


// Copied from original code in VS BulletinSlicer
// Added 141004
// Parts A and B of PS Form 3541 are Zone oriented, and the Bulletin Master List spreadsheet
// is decidedly Zone oriented.  It works very well to fill out parts A and B; and had been
// working well for Part C.  But, with the addition of a zip code that the fell into Mixed
// ADC (MXD) yet showed up in zone 5, thing came apart.  (()Added here and now) The total
// of part B must equal the total of part C.)
//
// Here, I generate the valued for Part C that are to be added into the workbooks ZipCount
// ValuesTable (which is copied to and referenced by the two PS3541 sheets).

// The output is a file in output folder:  PSForm3541PartC.csv

import configPack.XMLData;
import libPack.*;

import java.util.ArrayList;
import java.util.Arrays;

public class OutCountyPartC
{

    Shared shared = null;
    InternalMsgCtrl internalMsgCtrl = null;
    MailPrepUtils mailPrepUtils = null;
    XMLData xmlData = null;
    //ArrayList<Parting> d5AL;   // 5-digit zip codes
    int[] icCnt;              // in count list
    ClassesPack classesPack = null;
    ClassesPack.AddrZipComparator addrZipComparator;


    public OutCountyPartC(Shared shared) {
        this.shared = shared;
        internalMsgCtrl = shared.getInternalMsgCtrl();
        xmlData = shared.getXMLData();
        mailPrepUtils = shared.getMailPrepUtils();
        renewalDateStr = shared.getRenewalDateStr();
        classesPack = new ClassesPack();
        addrZipComparator = classesPack.instantiateAddrZipComparator();
    }

    // Build a csv formatted table to be pasted into sheet ZipCounts, accessed
    // to fill out PartC of form PS3541
    public ArrayList<String> outCountyPartC_Main(Addr[] addrAry) {

        // More information than is needed was initially computed:
        //      first for debugging
        //      Then was chosen to put much of it into the output csv file for spreadsheet error check.
        // Get an In County list of zip codes from the config file (configXML)
        String[] icStrAry = xmlData.get_ic();
        int[] icIntAry = mailPrepUtils.StrAryToIntAry(icStrAry);
        String[] zipStrAry = extractZipStrs(addrAry);
        int[] zipIntAddrAry = mailPrepUtils.StrAryToIntAry(zipStrAry);
        Integer[] outCountyZipAry = outCounty(zipIntAddrAry, icIntAry, addrAry);

        // Mixed ADC:  contents of tray MXD
        // ADC:        contents of trays DEN_SCF + OMX
        // 3 Digit:    all out of county 3 digit trays
        // 5 Digit:    all out of county 5 digit trays

        // All are counted in a state machine
        counterSM(outCountyZipAry, zipIntAddrAry, addrAry, renewalDateStr);
        if (cntUnknown != 0)
        {
            internalMsgCtrl.err(InternalMsgCtrl.errKey.Error, false,
                    "There were " + cntUnknown + " unknown zips counted.");
            internalMsgCtrl.err(InternalMsgCtrl.errKey.Error, false,
                    "You should research this problem.");
        }

//        System.out.println("cntMixedADCBalance:  " + cntMixedADCBalance);
//        System.out.println("cntMixedADCRenewal:  " + cntMixedADCRenewal);
//        System.out.println("cntADCBalance:  " + cntADCBalance);
//        System.out.println("cntADCRenewal:  " + cntADCRenewal);
//        System.out.println("cntD3Balance:  " + cntD3Balance);
//        System.out.println("cntD3Renewal:  " + cntD3Renewal);
//        System.out.println("cntD5Balance:  " + cntD5Balance);
//        System.out.println("cntD5Renewal:  " + cntD5Renewal);
//        System.out.println("cntOutCountyBalance:  " + cntOutCountyBalance);
//        System.out.println("cntOutCountyRenewal:  " + cntOutCountyRenewal);
//        System.out.println("cntOutCountyBalance:  " + cntInCountyBalance);
//        System.out.println("cntOutCountyRenewal:  " + cntInCountyRenewal);
//        System.out.println("cntUnknown:  " + cntUnknown);

        ArrayList<String> csvAL = formatCSV();

        return csvAL;
    }

    // Build csv formatted content
    private ArrayList<String> formatCSV()
    {
        // Originally was one long String (one StringBuilder).
        // But, my FileWork.writeFile wants an ArrayList, so
        // I made two strings and stored them in an AL just for
        // fileWork.writeFile().

        ArrayList<String> csvAL = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        final String comma = ",";
        sb.append("MixedADC" + comma);
        sb.append("ADC" + comma);
        sb.append("3-Digit" + comma);
        sb.append("5-Digit" + comma);
        sb.append(comma);
        sb.append("In County" + comma);
        sb.append("Out County" + comma);
        csvAL.add(sb.toString());
        sb.delete(0, sb.length());
        sb.append(cntMixedADCBalance + comma);
        sb.append(cntADCBalance + comma);
        sb.append(cntD3Balance + comma);
        sb.append(cntD5Balance + comma);
        sb.append(comma);
        sb.append(cntInCountyBalance + comma);
        sb.append(cntOutCountyBalance + comma);
        csvAL.add(sb.toString());
        sb.delete(0, sb.length());
        sb.append(cntMixedADCRenewal + comma);
        sb.append(cntADCRenewal + comma);
        sb.append(cntD3Renewal + comma);
        sb.append(cntD5Renewal + comma);
        sb.append(comma);
        sb.append(cntInCountyRenewal + comma);
        sb.append(cntOutCountyRenewal + comma);
        csvAL.add(sb.toString());
        return csvAL;
    }
    
    // Find all out county zip codes by subtracting in county codes from
    // the address list
    private Integer[] outCounty(int[] zipIntAddrAry, int[] icIntAry, Addr[] AddrAry) {
        // Not all IC zips will be found in addr zips.
        // Hence, zipIntAddrAry.length - inIntAry.length != out county count

        ArrayList<Integer> al = new ArrayList<>();
        for (int inx = 0; inx < zipIntAddrAry.length; inx++){
            int zip = zipIntAddrAry[inx];
            int index = Arrays.binarySearch(icIntAry, zip);
            if (index > 0) {
                // is in County
                Addr addr = AddrAry[inx];
                String renewalStr = addr.renewal;
                if (renewalStr.equals(renewalDateStr)) {
                    cntInCountyRenewal++;
                } else {
                    cntInCountyBalance++;
                }
                continue;
            }
            else {
                // is out county
                al.add(zip);
            }
        }
        Integer[] outCountyZipAry = al.toArray(new Integer[0]);

        return outCountyZipAry;
    }

    private String[] extractZipStrs(Addr[] addrAry) {
        String[] strAry = new String[addrAry.length];
        for (int inx = 0; inx < addrAry.length; inx++) strAry[inx] = addrAry[inx].zip;
        return strAry;
    }

    //------------------------------------------
    // Mixed ADC:  contents of tray MXD
    // ADC:        contents of trays DEN_SCF + OMX
    // 3 Digit:    all out of county 3 digit trays
    // 5 Digit:    all out of county 5 digit trays

    static int cntMixedADCBalance = 0;
    static int cntMixedADCRenewal =  0;
    static int cntADCBalance = 0;
    static int cntADCRenewal = 0;
    static int cntD3Balance = 0;
    static int cntD3Renewal = 0;
    static int cntD5Balance = 0;
    static int cntD5Renewal = 0;
    static int cntOutCountyBalance = 0;
    static int cntOutCountyRenewal = 0;
    static int cntInCountyBalance = 0;  // counted in outCounty()
    static int cntInCountyRenewal = 0;  // counted in outCount()
    static int cntUnknown = 0;

    static int addrIndex = 0;
    static int ocIndex = 0; // oc -- Out County
    static int ocZip = 0;
    static int addrZip = 0;
    static Addr addrObj = null;
    static String trayTag = "";
    String renewalDateStr = "";  // filled in the constructor
    static int headInx = 0;
    static int tailInx = 0;
    static boolean renewalFlg = false;

    private void counterSM(Integer[] outCountyZipAry, int[] zipIntAddrAry,
                           Addr[] addrAry, String renewalDateStr) {

        State state = State.OCZIP;
        while (state != State.HALT) {
            state = state.execute(outCountyZipAry, zipIntAddrAry, addrAry, renewalDateStr);
        }
    }
    // Mixed ADC:  contents of tray MXD
    // ADC:        contents of trays DEN_SCF + OMX
    // 3 Digit:    all out of county 3 digit trays
    // 5 Digit:    all out of county 5 digit trays

    protected enum State {
        OCZIP {  // get the out county zip code
            @Override
            protected State execute(Integer[] outCountyZipAry, int[] zipIntAddrAry, Addr[] addrAry,
                                    String renewalDateStr) {

                if (ocIndex == outCountyZipAry.length) return HALT;

                // I process all of any one zip, so if multiples in outCountyZipAry
                // just run through them
                if (outCountyZipAry[ocIndex] == ocZip) {
                    ocIndex++;
                    return OCZIP;
                }

                ocZip = outCountyZipAry[ocIndex];
                return ZIPINOC;
            }
        },
        ZIPINOC { // find the index into addrAry of that zip code
            // There is a one to one relationship between zipIntAddrAry and addrAry
            @Override
            protected State execute(Integer[] outCountyZipAry, int[] zipIntAddrAry, Addr[] addrAry, String renewalDateStr) {
                addrIndex = Arrays.binarySearch(zipIntAddrAry, ocZip);
                if (addrIndex < 0) {
                    // The out county zip was not found in the addrAry
                    ocIndex++;
                    return OCZIP;
                }
                addrZip = zipIntAddrAry[addrIndex];
                return RANGE;
            }
        },
        RANGE {
            @Override
            protected State execute(Integer[] outCountyZipAry, int[] zipIntAddrAry, Addr[] addrAry, String renewalDateStr) {
                // BinarySearch: if multiple instances, there is no guarantee which will be found.
                // Find the first and last index.

                // find the tail first
                tailInx = addrIndex - 1;
                do {
                    if (zipIntAddrAry[tailInx+1] == addrZip) { tailInx++; }
                    else { break; }
                } while((tailInx+1) != zipIntAddrAry.length);

                headInx = tailInx;
                // find the head
                do {
                    if (zipIntAddrAry[headInx] == addrZip) { headInx--; }
                    else { headInx++; break; }
                } while (headInx >= 0);
                if (headInx <= 0) headInx = tailInx;
                addrIndex = headInx;

                return ADDROBJ;
            }
        },
        ADDROBJ {  // get the Addr object
            @Override
            protected State execute(Integer[] outCountyZipAry, int[] zipIntAddrAry, Addr[] addrAry, String renewalDateStr) {
                if (addrIndex == addrAry.length)  return HALT;

                // addrIndex is incremented in RENEWAL
                addrObj = addrAry[addrIndex];

                if (addrIndex == headInx) { return RENEWAL; } // At the Head
                if (addrIndex < tailInx ) { return RENEWAL; } // in the middle
                if (addrIndex == tailInx) { return RENEWAL; } // At the tail
                return OCZIP;
             }
        },
        RENEWAL { // get the current renewal date and set the renewal flag in is a renewal
            @Override
            protected State execute(Integer[] outCountyZipAry, int[] zipIntAddrAry, Addr[] addrAry, String renewalDateStr) {
                addrIndex++;
                String currentRenewalDate = addrObj.renewal;
                renewalFlg = renewalDateStr.equals(currentRenewalDate) ? true : false;

                if (renewalFlg)
                {
                    cntOutCountyRenewal++;
                } else {
                    cntOutCountyBalance++;
                }

                //if (renewalFlg) System.out.println(ocZip + ", " + addrObj.csv);
                return TRAYTAG;
            }
        },
        TRAYTAG { // get the tray tag (OMX, MXD, SCF, etc)
            @Override
            protected State execute(Integer[] outCountyZipAry, int[] zipIntAddrAry, Addr[] addrAry, String renewalDateStr) {
                trayTag = addrObj.trayTag;
                return BRANCHONTAG;
            }
        },
        BRANCHONTAG {  // branch on the tray tag
            @Override
            protected State execute(Integer[] outCountyZipAry, int[] zipIntAddrAry, Addr[] addrAry, String renewalDateStr) {
                // we have ocIndex, addrObj, trayTag
                //System .out.println(ocZip + ", " + trayTag );
                if (trayTag.equals("MXD")) return MIXED_ADC;
                if (trayTag.equals("SCF")) return ADC;
                if (trayTag.equals("OMX")) return ADC;
                if (trayTag.length() == 3) return D3;
                if (trayTag.length() == 5) return D5;

                // Should never get here
                cntUnknown++;
                return ADDROBJ;
            }
        },
        MIXED_ADC { // accumulate Mixed ADC (MXD) counts
            @Override
            protected State execute(Integer[] outCountyZipAry, int[] zipIntAddrAry, Addr[] addrAry, String renewalDateStr) {
                if (renewalFlg) {
                    cntMixedADCRenewal++;
                } else {
                    cntMixedADCBalance++;
                }
                return ADDROBJ;
            }
        },
        ADC { // accumulate ADC (SCF and OMX) counts
            @Override
            protected State execute(Integer[] outCountyZipAry, int[] zipIntAddrAry, Addr[] addrAry, String renewalDateStr) {
                if (renewalFlg) {
                    cntADCRenewal++;
                } else {
                    cntADCBalance++;
                }
                return ADDROBJ;
            }
        },
        D3 { // accumulate 3-digit counts
            @Override
            protected State execute(Integer[] outCountyZipAry, int[] zipIntAddrAry, Addr[] addrAry, String renewalDateStr) {
                if (renewalFlg) {
                    cntD3Renewal++;
                } else {
                    cntD3Balance++;
                }
                return ADDROBJ;
            }
        },
        D5 { // accumulate 5-digit counts
            @Override
            protected State execute(Integer[] outCountyZipAry, int[] zipIntAddrAry, Addr[] addrAry, String renewalDateStr) {
                if (renewalFlg) {
                    cntD5Renewal++;
                } else {
                    cntD5Balance++;
                }
                return ADDROBJ;
            }
        },
        HALT { // DONE and out of here
            @Override
            protected State execute(Integer[] outCountyZipAry, int[] zipIntAddrAry, Addr[] addrAry, String renewalDateStr) {
                return null; }
        };

        // All cases above must implement this abstract
        protected abstract State execute(Integer[] outCountyZipAry, int[] zipIntAddrAry,
                                         Addr[] addrAry, String renewalDateStr);
    }
}
