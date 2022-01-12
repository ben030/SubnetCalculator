package com.SubnetCalculator;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.BitSet;

public class Util {
    static InetAddress getByAddress(byte[] IPByteArr) {
        InetAddress inetAddress = null;
        try {
            inetAddress =  InetAddress.getByAddress(IPByteArr);
        } catch (UnknownHostException e) {
            JavalinServerMain.logger.error("converting byteArr to InetAddress", e);
        }
        return(inetAddress);
    }


    static CIDRCalculator.FixedBitSet bitSetToFixedBitSet(BitSet input) {
        CIDRCalculator.FixedBitSet output = new CIDRCalculator.FixedBitSet(32);
        for (int i = 0; i < input.length(); i++) {
            if (input.get(i)) {
                output.set(i);
            }
        }
        return output;
    }

    static CIDRCalculator.FixedBitSet strToBitSet(String IPv4String) {
        BitSet bs = null;
        try {
            bs = BitSet.valueOf(InetAddress.getByName(IPv4String).getAddress());
        } catch (UnknownHostException e) {
            JavalinServerMain.logger.error("converting String to IPAdress", e);
        }
        return bitSetToFixedBitSet(bs);
    }
    

    // reference:  https://www.baeldung.com/java-check-string-number
    static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            Integer.parseInt(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }


}
