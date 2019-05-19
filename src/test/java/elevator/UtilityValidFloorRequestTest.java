package elevator;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RunWith(Parameterized.class)
public class UtilityValidFloorRequestTest {

    private String input;

    public UtilityValidFloorRequestTest(String input) {
        this.input = input;
    }

    @Parameters(name = "invalid-floor-request_{index}: {0}")
    public static Collection<Object[]> data() {
        int max = 1000;
        try {
            SystemConfiguration.initializeSystemConfiguration();
            max = Integer.parseInt(SystemConfiguration.getConfiguration("numberOfFloors"));
        } catch(ElevatorSystemException ese) {
            fail();
        }
        return Stream.of(new Object[][] {
                {max + "U"},
                {(max + 1) + "D"},
                {"1D"},
                {"0U"},
                {"A20U"},
                {"2"},
                {"U"},
                {"D"},
                {"UP"},
                {"DOWN"},
                {"1u"},
                {max + "d"},
                {"&*%U"},
                {"*(^D"},
                {"D" + max},
                {"U1"}
        }).collect(Collectors.toList());
    }

    @Test
    public void validFloorRequest() {
        assertFalse(Utility.validFloorRequestKey(input));
    }
}
