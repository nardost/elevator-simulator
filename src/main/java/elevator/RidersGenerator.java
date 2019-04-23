package elevator;

public interface RidersGenerator {
    Rider generate() throws ElevatorSystemException;
    Rider generate(int floorNumber, int destination) throws ElevatorSystemException;
}
