package chkZipCodesPack;

import libPack.Addr;
import libPack.Shared;

import java.util.ArrayList;
import java.util.Arrays;

public class Report_ZipCodes {
    public Report_ZipCodes(Shared shared) {
        this.shared = shared;
    }

    Shared shared = null;

    public String buildReportStr(Addr[] addrAry, ArrayList<Integer> missingIntAL) {
        // AddrAry IS SORTED in zip order, build two synchronized list 1) int zip, 2 string tray
        int[] zipInt = new int[addrAry.length];
        String[] trayStr = new String[addrAry.length];
        for (int inx = 0; inx < addrAry.length; inx++) {
            zipInt[inx] = Integer.valueOf(addrAry[inx].zip);
            trayStr[inx] = addrAry[inx].trayTag;
        }
        String zipCodePath = shared.getZipCodesFilePathName();

        StringBuilder sb = new StringBuilder();
        sb.append("The following zip codes, and tray, need to be added to\n");
        sb.append(zipCodePath + "\n");
        for (int inx = 0; inx < missingIntAL.size(); inx++) {
            int index = Arrays.binarySearch(zipInt, missingIntAL.get(inx));
            sb.append(zipInt[index] + ", ");
            sb.append(trayStr[index] + "\n");
        }
        //String dbStr = sb.toString();

        return sb.toString();
    }
}
