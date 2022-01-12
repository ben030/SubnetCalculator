package com.SubnetCalculator;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.BitSet;

import com.google.common.net.InetAddresses;
import com.google.common.primitives.Ints;
import org.json.JSONObject;


public class CIDRCalculator {

    public static class SubnetInfo {
        public String NetId;
        public String First;
        public String Last;
        public String Broadcast;
        public String Subnetmask;
        public int Available;
    }

    /* BitSet is always minimum length = 64, this gets complicated if we want to convert a 32 bit long BitSet back to a IPaddress
    FixedBitSet allows 32 bit long BitSets
    reference: https://stackoverflow.com/a/3755536/7089139
    @author casper.bang@gmail.com */
    public static class FixedBitSet extends BitSet {
        int fixedLength;

        public FixedBitSet(int fixedLength) {
            super(fixedLength);
            this.fixedLength = fixedLength;
        }

        @Override
        public int length() {
            return fixedLength;
        }
    }

    // --- helper functions of CIDR Calculator
    public static void printBitSet(BitSet bits) {
        for (int i = 0; i < 32; i++) {
            if (bits.get(i))
                System.out.print(1);
            else
                System.out.print(0);
        }
        System.out.println();
    }

    public static byte[] fixedBitSetToByteArr(FixedBitSet input) {
        byte[] IPByteArr = new byte[4];
        byte[] IPByteArrTemp = input.toByteArray();
        // copy Array to make sure byte number is always 4, even if one byte is empty
        System.arraycopy(IPByteArrTemp, 0, IPByteArr, 0, IPByteArrTemp.length);
        return IPByteArr;
    }

    public static String bitSetToIPString(FixedBitSet IPv4Bits) {
        byte[] IPByteArr = new byte[4];
        byte[] IPByteArrTemp = IPv4Bits.toByteArray();
        // copy Array to make sure byte number is always 4, even if one byte is empty
        System.arraycopy(IPByteArrTemp, 0, IPByteArr, 0, IPByteArrTemp.length);
        InetAddress IPByteAddress = Util.getByAddress(IPByteArr);

        return IPByteAddress.toString().split("/")[1];
    }

    public static String IntToIPString(int value) {
        return bitSetToIPString((FixedBitSet) FixedBitSet.valueOf(Ints.toByteArray(value)));
    }

    // helper functions end
    private static FixedBitSet generateSubnetMaskBit(int suffix) {
        // TODO there has to be a better way to implement this
        // TODO maybe with two for loops, one in each direction?
        FixedBitSet result = new FixedBitSet(32);
        int k = 0;
        for (int i = 0; i < 32; i++) {
            if (i < suffix) {
                result.set(7 - (i % 8) + k * 8,true);
            } else {
                result.set(7 - (i % 8) + k * 8,false);
            }

            if (i % 8 == 7) {
                k++;
            }
        }
        return result;
    }

    private static FixedBitSet calculateNetID(FixedBitSet cidrIPBits, FixedBitSet subnetMaskBits) {
        FixedBitSet result = new FixedBitSet(32);
        for (int i = 0; i < cidrIPBits.size(); i++) {
            if (subnetMaskBits.get(i) && cidrIPBits.get(i)) {
                result.set(i, true);
            } else {
                result.set(i, false);
            }
        }
        return result;
    }

    private static FixedBitSet calculateBroadcast(FixedBitSet netIDBits, FixedBitSet subnetMaskBits) {
        FixedBitSet result = new FixedBitSet(32);
        for (int i = 0; i < 32; i++) {
            /* the broadcast address is "the last" address which belongs to the subnet
            now we calculate the last IP, therefore if subnetMask is zero we always set broadcast bit to one
            or if netID and subnetMask bits are equal */
            if (!subnetMaskBits.get(i) | netIDBits.get(i) == subnetMaskBits.get(i)) {
                result.set(i, true);
            } else {
                result.set(i, false);
            }
        }
        return result;
    }

    private static int FixedBitSetToInt(FixedBitSet input) {
        return Ints.fromByteArray(fixedBitSetToByteArr(input));
    }

    private static FixedBitSet ArithmeticIPAddress(FixedBitSet netId, int value, boolean addition) {
        int iPInt = FixedBitSetToInt(netId);
        int result;
        if (addition) {
            result = iPInt + value;
        } else {
            result = iPInt - value;
        }
        return Util.bitSetToFixedBitSet(BitSet.valueOf(Ints.toByteArray(result)));
    }

    private static int calculateGapBetweenIPBits(FixedBitSet minuend, FixedBitSet subtrahend) {
        return FixedBitSetToInt(minuend) - FixedBitSetToInt(subtrahend) + 1;
    }

    static JSONObject inputValidityCheck(String ip, String suffix) {
        JSONObject jo = new JSONObject();
        if (!Util.isNumeric(suffix)) {
            jo.put("validityResult", false);
            return jo.put("errorMessage", "Suffix is not a Number");
        }

        int suffixInt = Integer.parseInt(suffix);
        if (suffixInt < 1 || suffixInt > 32) {
            jo.put("validityResult", false);
            return jo.put("errorMessage", "Suffix has to be between 1 and 32");
        }

        if (!InetAddresses.isInetAddress(ip)) {
            jo.put("validityResult", false);
            return jo.put("errorMessage", "Input is not an IP address");
        }
            return jo.put("validityResult", true);
    }

   public static SubnetInfo calculateSubnetInfo(String ip, String inputSuffix) {
        SubnetInfo sInf = new SubnetInfo();

        int suffix = Integer.parseInt(inputSuffix);
        FixedBitSet subnetMaskBits = generateSubnetMaskBit(suffix);
        FixedBitSet netIdBits =  calculateNetID(Util.strToBitSet(ip), subnetMaskBits);
        FixedBitSet broadcast = calculateBroadcast(netIdBits, subnetMaskBits);
        FixedBitSet first = ArithmeticIPAddress(netIdBits, 1, true);
        FixedBitSet last = ArithmeticIPAddress(broadcast, 1, false);

        sInf.Subnetmask = bitSetToIPString(subnetMaskBits);
        sInf.NetId = bitSetToIPString(netIdBits);
        sInf.First = bitSetToIPString(first);
        sInf.Broadcast = bitSetToIPString(broadcast);
        sInf.Last = bitSetToIPString(last);
        sInf.Available = calculateGapBetweenIPBits(last, first);
        return sInf;
    }
}
