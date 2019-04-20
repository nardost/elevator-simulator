package elevator;

import java.util.ArrayList;
import java.util.List;

class Type1RiderGenerator implements RidersGenerator {
    @Override
    public List<Rider> generate(int floorNumber, int destination) throws ElevatorSystemException {
        Person p = new Person(floorNumber, destination);//TODO: generate N persons
        List<Rider> riders = new ArrayList<Rider>();
        riders.add(p);
        return riders;//TODO: some algorithm here
    }
}
