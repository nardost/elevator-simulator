package elevator;

import gui.ElevatorDisplay;

public class Test5 {
    public static void main(String[] args) {
        try {
            EventLogger.print("......................................TEST Starting Up................................");

            int numFloor = Building.getInstance().getNumberOfFloors();
            int numElev = Building.getInstance().getNumberOfElevators();
            ElevatorDisplay.getInstance().initialize(numFloor);
            for (int i = 1; i <= numElev; i++) {
                ElevatorDisplay.getInstance().addElevator(i, 1);
            }
            Building.getInstance().generatePerson(3, 7);
            SystemConfiguration.initializeSystemConfiguration();
            Building building = Building.getInstance();
            ElevatorController ec = ElevatorController.getInstance();
            int numberOfElevators = Integer.parseInt(SystemConfiguration.getConfiguration("numberOfElevators"));
            Thread threads[] = new Thread[numberOfElevators];
            for(int i = 1; i <= numberOfElevators; i++) {
                Elevator e = ElevatorController.getInstance().getElevatorById(i);
                threads[i - 1] = new Thread(() -> e.run());
                threads[i - 1].setName("THREAD_ELEVATOR_" + i);
            }
            Thread buildingThread = new Thread(() -> building.run());
            buildingThread.setName("THREAD_BUILDING");
            Thread controllerThread = new Thread(() -> ec.run());
            controllerThread.setName("THREAD_CONTROLLER");

            for(int i = 1; i <= numberOfElevators; i++) {
                threads[i - 1].start();
            }
            buildingThread.start();
            //controllerThread.start();

            try {
                for(int i = 1; i <= numberOfElevators; i++) {
                    threads[i - 1].join();
                }
                buildingThread.join();
                //controllerThread.join();
            } catch(InterruptedException ie) {
                ie.printStackTrace();
            }
            EventLogger.print(Building.getInstance().generateReport());
            EventLogger.print("......................................TEST DONE.......................................");
        } catch (ElevatorSystemException ese) {
            System.out.println(ese.getMessage());
        }
    }
}
