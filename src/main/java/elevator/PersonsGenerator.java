package elevator;

import java.util.List;

public class PersonsGenerator {

    private RidersGenerator generator;

    //TODO: make factory
    PersonsGenerator(RidersGenerator generator) {
        this.generator = generator;
    }

   public List<Rider> generateRiders(String id, Floor floor) throws ElevatorSystemException {
        return generator.generate(id, floor);
    }
}
