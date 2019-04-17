package elevator;

import java.util.ArrayList;
import java.util.List;

public class Type1RiderGenerator implements RidersGenerator {
    @Override
    public List<Rider> generate(String id, Floor floor) throws ElevatorSystemException {
        Person p = new Person(id, floor);//TODO: generate N persons
        List<Rider> riders = new ArrayList<Rider>();
        riders.add(p);
        return riders;//TODO: some algorithm here
    }
}
