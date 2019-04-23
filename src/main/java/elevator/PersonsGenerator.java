package elevator;

class PersonsGenerator {

    private RidersGenerator generator;

    private PersonsGenerator(RidersGenerator generator) {
        setGenerator(generator);
    }

    public static PersonsGenerator getInstance() {
        String typeOfGenerator = SystemConfiguration.getConfig("rider-generator");
        switch(typeOfGenerator) {
            case "test1":
            default:
                return new PersonsGenerator(new Test1RiderGenerator());
        }
    }

    public Rider generateRider() throws ElevatorSystemException {
        return generator.generate();
    }

    public Rider generateRider(int floorNumber, int destination) throws ElevatorSystemException {
        return generator.generate(floorNumber, destination);
    }

    private void setGenerator(RidersGenerator generator) {
        this.generator = generator;
    }
}
