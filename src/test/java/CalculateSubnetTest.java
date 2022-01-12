import com.SubnetCalculator.CIDRCalculator;
import org.junit.jupiter.api.Test;
import java.net.UnknownHostException;
import static org.junit.jupiter.api.Assertions.*;


public class CalculateSubnetTest {
    @Test
    void simpleCase() {
        CIDRCalculator.SubnetInfo result = CIDRCalculator.calculateSubnetInfo("192.168.0.10","24");
        assertEquals("192.168.0.0", result.NetId);
        assertEquals("255.255.255.0", result.Subnetmask);
        assertEquals("192.168.0.1",result.First);
        assertEquals("192.168.0.254", result.Last);
        assertEquals("192.168.0.255", result.Broadcast);
        assertEquals(254, result.Available);
    }

    @Test
    void largerSubnet() {
        CIDRCalculator.SubnetInfo result = CIDRCalculator.calculateSubnetInfo("0.0.0.0","15");
        assertEquals("0.0.0.0", result.NetId);
        assertEquals("255.254.0.0", result.Subnetmask);
        assertEquals("0.0.0.1",result.First);
        assertEquals("0.1.255.254", result.Last);
        assertEquals("0.1.255.255", result.Broadcast);
        assertEquals(131070, result.Available);
    }

    @Test
    void checkIfPrefixIsNotFirstIP() {
        CIDRCalculator.SubnetInfo result = CIDRCalculator.calculateSubnetInfo("192.168.5.10","26");
        assertEquals("192.168.5.0", result.NetId);
        assertEquals("255.255.255.192", result.Subnetmask);
        assertEquals("192.168.5.1",result.First);
        assertEquals("192.168.5.62", result.Last);
        assertEquals("192.168.5.63", result.Broadcast);
        assertEquals(62, result.Available);
    }


}
