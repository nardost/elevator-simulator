package elevator;

import java.util.List;

public interface RidersGenerator {
    List<Rider> generate(int floorNumber, int destination) throws ElevatorSystemException;
}
