package elevator;

import java.util.List;

class PersonsGenerator {

    private RidersGenerator generator;

    //TODO: make factory
    PersonsGenerator(RidersGenerator generator) {
        this.generator = generator;
    }

   public List<Rider> generateRiders(int floorNumber, int destination) throws ElevatorSystemException {
        return generator.generate(floorNumber, destination);
    }
}
