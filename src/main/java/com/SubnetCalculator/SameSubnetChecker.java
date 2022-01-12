package com.SubnetCalculator;

import com.google.common.net.InetAddresses;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SameSubnetChecker {
    static JSONObject inputValidityCheck(String ipV4Addr1String, String ipV4Addr2String, String subnetMaskString) {
        JSONObject jo = new JSONObject();
        jo.put("validityResult", true);
        List<String> errorMessage = new ArrayList<String>();
        if (!InetAddresses.isInetAddress(ipV4Addr1String)) {
            errorMessage.add("first IP address");
            jo.put("validityResult", false);
        }

        if (!InetAddresses.isInetAddress(ipV4Addr2String)) {
            errorMessage.add("second IP address");
            jo.put("validityResult", false);
        }

        if (!InetAddresses.isInetAddress(subnetMaskString)) {
            errorMessage.add("Subnetmask");
            jo.put("validityResult", false);
        }
        return jo.put("errorMessage", errorMessage);
    }

    private static boolean compareBits(CIDRCalculator.FixedBitSet ipV4Addr1, CIDRCalculator.FixedBitSet ipV4Addr2, CIDRCalculator.FixedBitSet subnetMask) {
        for (int i = 0; i < 32; i++) {
            if (subnetMask.get(i)) {
                if (ipV4Addr1.get(i) != ipV4Addr2.get(i)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean inSameSubnet(String ipV4Addr1String, String ipV4Addr2String, String subnetMaskString) {
        inputValidityCheck(ipV4Addr1String, ipV4Addr2String, subnetMaskString);
        CIDRCalculator.FixedBitSet ipV4Addr1 = Util.strToBitSet(ipV4Addr1String);
        CIDRCalculator.FixedBitSet ipV4Addr2 = Util.strToBitSet(ipV4Addr2String);
        CIDRCalculator.FixedBitSet subnetMask = Util.strToBitSet(subnetMaskString);
        return compareBits(ipV4Addr1, ipV4Addr2, subnetMask);
    }
}
