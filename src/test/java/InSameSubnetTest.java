import com.SubnetCalculator.SameSubnetChecker;
import org.junit.jupiter.api.Test;
import java.net.UnknownHostException;
import static org.junit.jupiter.api.Assertions.*;

class InSameSubnetTest {


    @Test
    void inSameSubnetValidTrueCase() throws UnknownHostException {
        assertTrue(SameSubnetChecker.inSameSubnet("192.168.0.10", "192.168.0.210", "255.255.255.0"));
    }

    @Test
    void inSameSubnetValidFalseCase() throws UnknownHostException {
        assertFalse(SameSubnetChecker.inSameSubnet("192.168.0.10", "193.168.0.210", "255.255.255.0"));
    }


}