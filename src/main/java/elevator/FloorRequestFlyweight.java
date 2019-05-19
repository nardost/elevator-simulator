package elevator;

public interface FloorRequestFlyweight {
    void relayFloorRequest(int personId, long time) throws ElevatorSystemException;
}
