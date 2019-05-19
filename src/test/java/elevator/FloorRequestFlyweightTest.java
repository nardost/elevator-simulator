package elevator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
public class FloorRequestFlyweightTest {

    private int firstFloor;
    private Direction firstDirection;
    private int secondFloor;
    private Direction secondDirection;

    public FloorRequestFlyweightTest(int floor, Direction direction) {
        this.firstFloor = floor;
        this.firstDirection = direction;
    }

    @Before
    public void setup() {
    }

    @Parameters(name = "identical-flyweights_{index}")
    public static Collection<Object[]> data() {
        int max = 1000;
        try {
            SystemConfiguration.initializeSystemConfiguration();
            max = Integer.parseInt(SystemConfiguration.getConfiguration("numberOfFloors"));
        } catch(ElevatorSystemException ese) {
            fail();
        }

        return Stream.of(new Object[][] {
                { 1, Direction.UP },
                { max, Direction.DOWN }
        }).collect(Collectors.toList());
    }

    @Test
    public void flyweight_factory_should_not_produce_duplicate_flyweights() {
        try {
            String key = Utility.encodeFloorRequestKey(firstFloor, firstDirection);
            assertSame(
                    FloorRequestFlyweightFactory.getInstance().getFloorRequest(key),
                    FloorRequestFlyweightFactory.getInstance().getFloorRequest(key)
            );
        } catch(ElevatorSystemException ese) {
            fail();
        }
    }
}
