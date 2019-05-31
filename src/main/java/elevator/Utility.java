package elevator;

import java.text.DecimalFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Utility {
    static int decodeFloorRequestFloor(String key) throws ElevatorSystemException {
        if(validFloorRequestKey(key)) {
            String d = key.substring(key.length() - 1);
            return Integer.parseInt(key.substring(0, key.length() - 1));
        }
        throw new ElevatorSystemException("Invalid FloorRequest key " + key);
    }

    static Direction decodeFloorRequestDirection(String key) throws ElevatorSystemException {
        if(validFloorRequestKey(key)) {
            return (key.substring(key.length() - 1).equals("U")) ? Direction.UP : Direction.DOWN;
        }
        throw new ElevatorSystemException("Invalid FloorRequest key " + key);
    }
    static String encodeFloorRequestKey(int floor, Direction direction) throws ElevatorSystemException {
        Validator.validateFloorNumber(floor);
        StringBuilder sb = new StringBuilder(Integer.toString(floor));
        sb.append((direction == Direction.UP) ? "U" : "D");
        return sb.toString();
    }

    static boolean validFloorRequestKey(String key) {
        String floorPart = key.substring(0, key.length() - 1);
        String directionPart = key.substring(key.length() - 1);
        try {
            SystemConfiguration.initializeSystemConfiguration();
            final int NUMBER_OF_FLOORS = Integer.parseInt(SystemConfiguration.getConfiguration("numberOfFloors"));
            int floor = Integer.parseInt(floorPart);
            if(floor < 1 || floor > NUMBER_OF_FLOORS) {
                return false;
            }
            if(directionPart.equals("U") || directionPart.equals("D")) {
                if((floor == 1 && directionPart.equals("D") || (floor == NUMBER_OF_FLOORS && directionPart.equals("U")))) {
                    return false;
                }
                return true;
            }
        } catch(NumberFormatException nfe) {
            return false;
        } catch(ElevatorSystemException ese) {
            ese.getMessage();
        }
        return false;
    }

    public static String listToString(List list, String prefix, String separator, String suffix) throws ElevatorSystemException {
        try {
            Validator.validateNotNull(list);
        } catch (ElevatorSystemException ese) {
            throw new ElevatorSystemException("cannot stringify null list.");
        }
        StringBuilder sb = new StringBuilder();
        list.forEach(x -> {
            sb.append(prefix);
            sb.append(x.toString());
            sb.append(suffix);
            sb.append(separator);
        });
        int l = sb.length();
        int s = separator.length();
        if(l > s) {
            for(int i = 1; i <= s; i++) {
                sb.deleteCharAt(l - i);
            }
        }
        return sb.toString();
    }

    public static Direction evaluateDirection(int from, int to) throws ElevatorSystemException {
        Validator.validateFloorNumber(from);
        Validator.validateFloorNumber(to);
        if(from == to) {
            return Direction.IDLE;
        }
        return (from > to) ? Direction.DOWN : Direction.UP;
    }

    public static String formatColumnString(String str, int cols) {
        try {
            //any exception must be caught here because method is used in a lambda expression.
            Validator.validateGreaterThanZero(cols);
        } catch(ElevatorSystemException ese) {
            System.out.printf(ese.getMessage());
        }
        StringBuilder sb = new StringBuilder();
        if(str.length() < cols) {
            for(int i = 0; i < cols - str.length(); i++) {
                sb.append(" ");
            }
            sb.append(str);
            return sb.toString();
        }
        return str.substring(0, cols);
    }

    public static String formatElapsedTime(long milliTime) throws ElevatorSystemException  {//uses System.currentTimeMillis()
        long elapsedTime = milliTime - Building.getInstance().getZeroTime();
        long elapsedSeconds = TimeUnit.SECONDS.convert(elapsedTime, TimeUnit.MILLISECONDS);
        long s = elapsedSeconds % 60;
        long m = ((elapsedSeconds - s) % 3600) / 60;
        long h = (elapsedSeconds - (elapsedSeconds - s) % 3600) / 60;
        return String.format("%02d:%02d:%02d", h, m, s);
    }

    public static String nanoToRoundedSeconds(long nano, int decimalPlaces) {
        StringBuilder sb = new StringBuilder("0.");
        for(int i = 0; i < decimalPlaces; i++) {
            sb.append("0");
        }
        DecimalFormat df = new DecimalFormat(sb.toString());
        return df.format(new Long(TimeUnit.MILLISECONDS.convert(nano, TimeUnit.MILLISECONDS)).doubleValue() / 1000.0);
    }

    public static String generateReport(List<Person> list) throws ElevatorSystemException {
        final int TOTAL_NUMBER_OF_PEOPLE = list.size();
        long waitTimes[] = new long[TOTAL_NUMBER_OF_PEOPLE];
        long minWaitTime;
        long maxWaitTime;
        long totalWaitTime;
        double averageWaitTime;
        int personWithMinWaitTime;
        int personWithMaxWaitTime;
        long rideTimes[] = new long[TOTAL_NUMBER_OF_PEOPLE];
        long minRideTime;
        long maxRideTime;
        long totalRideTime;
        double averageRideTime;
        int personWithMinRideTime;
        int personWithMaxRideTime;
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append("Person\tStart Floor\tEnd Floor\tDirection\tWait Time\tRide Time\tTotal Time\n");
        sb.append("------\t-----------\t---------\t---------\t---------\t---------\t----------\n");
        list.forEach(rider -> {
            Person p = (Person) rider;
            long waitTime = p.getBoardingTime() - p.getCreatedTime();
            long rideTime = p.getExitTime() - p.getBoardingTime();
            long totalTime = p.getExitTime() - p.getCreatedTime();

            waitTimes[p.getId() - 1] = waitTime;
            rideTimes[p.getId() - 1] = rideTime;

            String id = "P" + p.getId();
            String origin = Integer.toString(p.getOriginFloor());
            String destination = Integer.toString(p.getDestinationFloor());
            String direction = ((p.getOriginFloor() < p.getDestinationFloor()) ? Direction.UP : Direction.DOWN).toString();
            String waitTimeStr = Utility.nanoToRoundedSeconds(waitTime, 1);
            String rideTimeStr = Utility.nanoToRoundedSeconds(rideTime, 1);
            String totalTimeStr = Utility.nanoToRoundedSeconds(totalTime, 1);
            sb.append(
                    Utility.formatColumnString(id, 6) + "\t" +
                            Utility.formatColumnString(origin, 11) + "\t" +
                            Utility.formatColumnString(destination, 9) + "\t" +
                            Utility.formatColumnString(direction, 9) + "\t" +
                            Utility.formatColumnString(waitTimeStr, 9) + "\t" +
                            Utility.formatColumnString(rideTimeStr, 9) + "\t" +
                            Utility.formatColumnString(totalTimeStr, 10) + "\n");
        });

        minWaitTime = Arrays.stream(waitTimes).summaryStatistics().getMin();
        maxWaitTime = Arrays.stream(waitTimes).summaryStatistics().getMax();
        totalWaitTime = Arrays.stream(waitTimes).summaryStatistics().getSum();
        averageWaitTime = new Long(totalWaitTime).doubleValue() / new Integer(TOTAL_NUMBER_OF_PEOPLE).doubleValue();
        minRideTime = Arrays.stream(rideTimes).min().getAsLong();//.summaryStatistics().getMin();//.min().getAsLong();
        maxRideTime = Arrays.stream(rideTimes).summaryStatistics().getMax();//.max().getAsLong();
        totalRideTime = Arrays.stream(rideTimes).sum();
        averageRideTime = /*Arrays.stream(rideTimes).summaryStatistics().getAverage();*/new Long(totalRideTime).doubleValue() / new Integer(TOTAL_NUMBER_OF_PEOPLE).doubleValue();

        StringBuilder psb = new StringBuilder("(P");

        sb.append("\n");
        sb.append("Average Wait Time: " + Utility.formatColumnString(Double.toString(averageWaitTime), 6) + " sec\n");
        sb.append("Average Ride Time: " + Utility.formatColumnString(Double.toString(averageRideTime), 6) + " sec\n");
        sb.append("\n");
        sb.append("Minimum Wait Time: " + Utility.formatColumnString(Utility.nanoToRoundedSeconds(minWaitTime, 1), 6) + " sec (P...)\n");
        sb.append("Minimum Ride Time: " + Utility.formatColumnString(Utility.nanoToRoundedSeconds(minRideTime, 1), 6) + " sec (P...)\n");
        sb.append("\n");
        sb.append("Maximum Wait Time: " + Utility.formatColumnString(Utility.nanoToRoundedSeconds(maxWaitTime, 1), 6) + " sec (P...)\n");
        sb.append("Maximum Ride Time: " + Utility.formatColumnString(Utility.nanoToRoundedSeconds(maxRideTime, 1), 6) + " sec (P...)\n");
        sb.append(Arrays.toString(waitTimes));
        sb.append("\n");
        sb.append(Arrays.toString(rideTimes));
        sb.append("\n");

        sb.append("\n");
        sb.append("Unhandled floor requests: " + ElevatorController.getInstance().unhandledFloorRequests());

        return sb.toString();
    }
}

