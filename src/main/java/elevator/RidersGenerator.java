package elevator;

import java.util.List;

public interface RidersGenerator {
    List<Rider> generate(String id, Floor floor) throws ElevatorSystemException;
}
