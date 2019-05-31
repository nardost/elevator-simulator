package elevator;

public class Validator {

    static void validateNotNull(Object o) throws ElevatorSystemException {
        if(o == null) {
            throw new ElevatorSystemException("null object encountered.");
        }
    }

    static void validateFloorNumber(int floorNumber) throws ElevatorSystemException {

        final int NUMBER_OF_FLOORS = Integer.parseInt(SystemConfiguration.getConfiguration("numberOfFloors"));
        if(floorNumber < 1 || floorNumber > NUMBER_OF_FLOORS) {
            throw new ElevatorSystemException("Floor number should be between 1 and " + Building.getInstance().getNumberOfFloors());
        }
    }

    static void validateElevatorNumber(int elevatorNumber) throws ElevatorSystemException {

        final int NUMBER_OF_ELEVATORS = Integer.parseInt(SystemConfiguration.getConfiguration("numberOfElevators"));
        if(elevatorNumber < 1 || elevatorNumber > NUMBER_OF_ELEVATORS) {
            throw new ElevatorSystemException("Elevator number should be between 1 and " + Building.getInstance().getNumberOfElevators());
        }
    }

    static void validateGreaterThanZero(int number) throws ElevatorSystemException {
        if(number < 1) {
            throw new ElevatorSystemException("Negative number not allowed.");
        }
    }
}
