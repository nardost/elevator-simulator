package elevator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
public class UtilityEncodeFloorRequestKeyTest {
    int floor;
    Direction direction;
    String expected;

    public UtilityEncodeFloorRequestKeyTest(int floor, Direction direction, String expected) {
        this.floor = floor;
        this.direction = direction;
        this.expected = expected;
    }

    @Parameters(name = "floor-request_{index}: {0} !-> {1}")
    public static Collection<Object[]> data() {
        int max = 1000;
        try {
            SystemConfiguration.initializeSystemConfiguration();
            max = Integer.parseInt(SystemConfiguration.getConfiguration("numberOfFloors"));
        } catch(ElevatorSystemException ese) {
            fail();
        }
        return Stream.of(new Object[][] {
                { 1, Direction.UP, "1U" },
                { max, Direction.DOWN, max + "D" }
        }).collect(Collectors.toList());
    }
    @Test
    public void encode_valid_floor_request_key() {
        try {
            assertEquals(expected, Utility.encodeFloorRequestKey(floor, direction));
        } catch(ElevatorSystemException ese) {
            fail();
        }
    }
}
