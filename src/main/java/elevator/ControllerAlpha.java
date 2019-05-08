package elevator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class ControllerAlpha implements Controller {

    private HashMap<Integer, Direction> floorRequests = new HashMap<>();

    @Override
    public void announceLocationOfElevator(int elevatorId, int elevatorLocation, Direction direction, Direction directionDispatchedFor) throws ElevatorSystemException {
        Building.getInstance().notifyObservers(elevatorId, elevatorLocation, direction, directionDispatchedFor);
    }

    @Override
    public void executeElevatorRequest(int elevatorId, int destinationFloor, int fromFloorNumber) throws ElevatorSystemException  {
        Elevator e = ElevatorController.getInstance().getElevatorById(elevatorId);
        Direction direction = (fromFloorNumber < destinationFloor) ? Direction.UP : Direction.DOWN;
        removeFloorRequest(fromFloorNumber, direction);//it has been served...
        e.addNextStop(destinationFloor);
        EventLogger.print(
                "Elevator " + e.getElevatorId() + " Rider Request made for Floor " + destinationFloor +
                " [Current Floor Requests: " + printListOfFloorRequests() + "][Current Rider Requests " + e.printListOfRiderRequests() + "]");

        e.move();
        if(e.noMoreStops()) {
            setIdleAndReturnToDefaultFloor(e.getElevatorId());
        }
    }

    @Override
    public void executeFloorRequest(int fromFloorNumber, Direction direction) throws ElevatorSystemException  {
        saveFloorRequest(fromFloorNumber, direction);
        selectElevatorAndSendToFloor(fromFloorNumber, direction);
    }

    @Override
    public void executeLocationUpdate(int elevatorId, int elevatorLocation, Direction nowGoingInDirection, Direction directionDispatchedFor) throws ElevatorSystemException {
        Elevator e = ElevatorController.getInstance().getElevatorById(elevatorId);
        EventLogger.print(
                "Elevator " + elevatorId + " moving from Floor " + elevatorLocation + " to Floor " + e.peekNextStop() +
                " [Current Floor Requests: " + printListOfFloorRequests() + "][Current Rider Requests: " + e.printListOfRiderRequests() + "]");
        if(e.noMoreStops()) {
            setIdleAndReturnToDefaultFloor(e.getElevatorId());
        } else {
            if(e.peekNextStop() == elevatorLocation) {
                /**
                 * The next if else blocks should be refactored out to stop() of Elevator.
                 */
                if(getFloorRequests().containsKey(elevatorLocation)) {
                    //Elevator will stop to pick waiting rider.
                    EventLogger.print(
                            "Elevator " + elevatorId + " has arrived at Floor " + elevatorLocation + " for Floor Request " +
                                    "[Current Floor Request: " + printListOfFloorRequests() + "][Current Rider Requests: " + e.printListOfRiderRequests() + "]");
                } else {
                    //Elevator will stop to drop a rider.
                    EventLogger.print(
                            "Elevator " + elevatorId + " has arrived at Floor " + elevatorLocation + " for Rider Request " +
                                    "[Current Floor Request: " + printListOfFloorRequests() + "][Current Rider Requests: " + e.printListOfRiderRequests() + "]");
                }
                if(elevatorLocation == e.getDispatchedForFloor()) {
                    e.setDirection(e.getDispatchedToServeDirection());
                }
                //TODO: stop, open doors, let them in, close doors and go...
                e.stop();
                e.pollNextStop();
                announceLocationOfElevator(elevatorId, elevatorLocation, nowGoingInDirection, directionDispatchedFor);
            }
        }
        //TODO: next statement was before the if block above and soe next stops were not being popped out...
        //announceLocationOfElevator(elevatorId, elevatorLocation, nowGoingInDirection, directionDispatchedFor);
    }

    public void selectElevatorAndSendToFloor(int toFloorNumber, Direction desiredDirection) throws ElevatorSystemException {
        //TODO: do some selection algorithm
        /**
         * 1. Get the locations of all elevators
         * 2.
         * */
        int selectedElevatorId;
        int secondElevatorId = 4;
        switch(Building.TEST) {
            case 2:
                selectedElevatorId = 2;
                break;
            case 3:
                selectedElevatorId = 3;
                break;
            default:
                selectedElevatorId = 1;
                break;
        }
        //TODO: First pick an elevator by some logic... The first undispatched, etc...
        Elevator e = ElevatorController.getInstance().getElevatorById(selectedElevatorId);
        Direction nowGoingInDirection = (e.getLocation() < toFloorNumber) ? Direction.UP : Direction.DOWN;
        if(Building.TEST == 4) {/** This is utter cheating!! */
            if(
                (toFloorNumber == 1 && desiredDirection == Direction.UP && Building.getInstance().getNumberOfPeopleGenerated() > 1) ||
                (toFloorNumber == 3 && desiredDirection == Direction.DOWN)
            ) {
                e = ElevatorController.getInstance().getElevatorById(secondElevatorId);
            }
            nowGoingInDirection = (e.getLocation() < toFloorNumber) ? Direction.UP : Direction.DOWN;
            e.setDispatched(true);
            e.setDispatchedForFloor(toFloorNumber);
            e.setDispatchedToServeDirection(desiredDirection);
            e.setDirection(nowGoingInDirection);
            if(!e.getNextFloorQueue().contains(toFloorNumber)) {
                getFloorRequests().put(toFloorNumber, desiredDirection);
            }
            if(e.getLocation() == toFloorNumber) {
                announceLocationOfElevator(e.getElevatorId(), e.getLocation(), e.getDirection(), e.getDispatchedToServeDirection());
            } else {
                e.addNextStop(toFloorNumber);
                EventLogger.print(
                        "Elevator " + e.getElevatorId() + " is going to Floor " + toFloorNumber + " for " + desiredDirection.toString() + " request " +
                                "[Current Floor Requests: " + printListOfFloorRequests() + "][Current Rider Requests: " + e.printListOfRiderRequests() + "]");
                e.move();
            }
            return;
        }

        //TODO: If not dispatched yet, send it to the floor...
        if(!e.isDispatched()) {
            e.setDispatched(true);
            e.setDispatchedForFloor(toFloorNumber);
            e.setDispatchedToServeDirection(desiredDirection);
            e.setDirection(nowGoingInDirection);
        } else {
            if(
                (toFloorNumber == e.getLocation() && desiredDirection != e.getDirection()) ||
                (toFloorNumber < e.getLocation() && e.getDirection() == Direction.UP) ||
                (toFloorNumber > e.getLocation() && e.getDirection() == Direction.DOWN)
            ) {//TODO: that one doesn't work. Pick another one by some logic...
                e = ElevatorController.getInstance().getElevatorById(secondElevatorId);
                e.setDispatched(true);
                e.setDispatchedForFloor(toFloorNumber);
                e.setDispatchedToServeDirection(desiredDirection);
                e.setDirection(nowGoingInDirection);

            } else if(
                    (toFloorNumber < e.getLocation() && e.getDirection() == Direction.DOWN) ||
                    (toFloorNumber > e.getLocation() && e.getDirection() == Direction.UP)
            ) {//TODO: e is approaching toFloor. Check if it goes past toFloor and if desiredDirection is same as direction of elevator.
                if(
                    (e.getDirection() == Direction.UP && e.peekNextStop() > toFloorNumber) ||
                    (e.getDirection() == Direction.DOWN && e.peekNextStop() < toFloorNumber)
                ) {//this will do. e is approaching and going further in desiredDirection
                    //TODO: do nothing.
                } else {//e is approaching but does not go farther in desiredDirection. So pick another one.
                    //TODO: get another elevator by some logic.
                    e = ElevatorController.getInstance().getElevatorById(secondElevatorId);

                    e.setDispatched(true);
                    e.setDispatchedForFloor(toFloorNumber);
                    e.setDispatchedToServeDirection(desiredDirection);
                    e.setDirection(nowGoingInDirection);
                }
            } else {//TODO: the first elevator picked is good to pick the rider...
            }
        }

        if(!e.getNextFloorQueue().contains(toFloorNumber)) {
            getFloorRequests().put(toFloorNumber, desiredDirection);
        }
        if(e.getLocation() == toFloorNumber) {
            announceLocationOfElevator(e.getElevatorId(), e.getLocation(), e.getDirection(), e.getDispatchedToServeDirection());
        } else {
            e.addNextStop(toFloorNumber);
            EventLogger.print(
                    "Elevator " + e.getElevatorId() + " is going to Floor " + toFloorNumber + " for " + desiredDirection.toString() + " request " +
                    "[Current Floor Requests: " + printListOfFloorRequests() + "][Current Rider Requests: " + e.printListOfRiderRequests() + "]");
            e.move();
        }
    }

    public void saveFloorRequest(int fromFloorNumber, Direction direction) {
        HashMap<Integer, Direction> table = getFloorRequests();
        if(table.get(fromFloorNumber) != direction) {
            table.put(fromFloorNumber, direction);
        }
    }

    public void removeFloorRequest(int fromFloorNumber, Direction direction) {
        HashMap<Integer, Direction> table = getFloorRequests();
        if(table.containsKey(fromFloorNumber)) {
            table.remove(fromFloorNumber, direction);
        }
    }

    void setIdleAndReturnToDefaultFloor(int elevatorId) throws ElevatorSystemException {
        Elevator e = ElevatorController.getInstance().getElevatorById(elevatorId);
        int defaultFloor = Integer.parseInt(SystemConfiguration.getConfiguration("defaultFloor"));
        if(e.getLocation() == defaultFloor) {
            return;
        }
        e.setIdle();
        e.addNextStop(defaultFloor);
        e.move();
    }

    private HashMap<Integer, Direction> getFloorRequests() {
        return floorRequests;
    }

    String printListOfFloorRequests() {
        List<Integer> list = new ArrayList<>(getFloorRequests().keySet());
        return Building.listToString(list, "", ", ", "");
    }

}
