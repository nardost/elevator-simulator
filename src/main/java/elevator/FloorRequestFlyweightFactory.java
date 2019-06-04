package elevator;

import java.util.HashMap;
import java.util.Map;

class FloorRequestFlyweightFactory {

    private Map<String, FloorRequestFlyweight> floorRequestFlyweights = new HashMap();

    private static FloorRequestFlyweightFactory factoryInstance = null;
    private FloorRequestFlyweightFactory() {

    }

    static FloorRequestFlyweightFactory getInstance() {

        if(factoryInstance == null) {
            synchronized(FloorRequestFlyweightFactory.class) {
                if(factoryInstance == null) {
                    factoryInstance = new FloorRequestFlyweightFactory();
                }
            }
        }
        return factoryInstance;

    }

    FloorRequestFlyweight getFloorRequest(String key) throws ElevatorSystemException {
        if(!Utility.validFloorRequestKey(key)) {
            throw new ElevatorSystemException("Cannot get FloorRequest with invalid key: " + key);
        }
        if(floorRequestFlyweights.containsKey(key)) {
            return floorRequestFlyweights.get(key);
        }
        int floor = Utility.decodeFloorRequestFloor(key);
        Direction direction = Utility.decodeFloorRequestDirection(key);
        try {
            FloorRequestFlyweight flyweight= new FloorRequest(floor, direction);
            floorRequestFlyweights.put(key, flyweight);
            return flyweight;
        } catch(ElevatorSystemException ese) {
            throw new ElevatorSystemException("This should not be happening here, but reported invalid FloorRequest Key " + key);
        }
    }
}
