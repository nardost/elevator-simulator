package elevator;

class Test1RiderGenerator implements RidersGenerator {

    @Override
    public Rider generate() throws ElevatorSystemException {
        //TODO: some random generation algorithm
        return null;
    }

    @Override
    public Rider generate(int origin, int destination) throws ElevatorSystemException {
        return new Person(origin, destination);
    }
}
