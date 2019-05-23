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
        int numberOfFloors = Integer.parseInt(SystemConfiguration.getConfiguration("numberOfFloors"));
        if(floor < 1 || floor > numberOfFloors) {
            throw new ElevatorSystemException("Cannot encode key for invalid floor number: " + floor);
        }
        StringBuilder sb = new StringBuilder(Integer.toString(floor));
        sb.append((direction == Direction.UP) ? "U" : "D");
        return sb.toString();
    }

    static boolean validFloorRequestKey(String key) {
        String floorPart = key.substring(0, key.length() - 1);
        String directionPart = key.substring(key.length() - 1);
        try {
            int floor = Integer.parseInt(floorPart);
            SystemConfiguration.initializeSystemConfiguration();
            int numberOfFloors = Integer.parseInt(SystemConfiguration.getConfiguration("numberOfFloors"));
            if(floor < 1 || floor > numberOfFloors) {
                return false;
            }
            if(directionPart.equals("U") || directionPart.equals("D")) {
                if((floor == 1 && directionPart.equals("D") || (floor == numberOfFloors && directionPart.equals("U")))) {
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

    public static String listToString(List list, String prefix, String separator, String suffix) {
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

    public static Direction evaluateDirection(int from, int to) {
        if(from == to) {
            return Direction.IDLE;
        }
        return (from > to) ? Direction.DOWN : Direction.UP;
    }

    public static String formatColumnString(String str, int cols) {
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

    public static String formatElapsedInstant() throws ElevatorSystemException  {//uses Instant
        long elapsed = Duration.between(Building.getInstance().getZeroInstant(), Instant.now()).toNanos();
        long elapsedSeconds = TimeUnit.SECONDS.convert(elapsed, TimeUnit.NANOSECONDS);
        long s = elapsedSeconds % 60;
        long m = ((elapsedSeconds - s) % 3600) / 60;
        long h = (elapsedSeconds - (elapsedSeconds - s) % 3600) / 60;
        return String.format("%02d:%02d:%02d", h, m, s);
    }

    public static String formatElapsedTime(long nanoTime) throws ElevatorSystemException  {//uses System.nanoTime()
        long elapsedTime = nanoTime - Building.getInstance().getZeroTime();
        long elapsedSeconds = TimeUnit.SECONDS.convert(elapsedTime, TimeUnit.NANOSECONDS);
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
        return df.format(new Long(TimeUnit.MILLISECONDS.convert(nano, TimeUnit.NANOSECONDS)).doubleValue() / 1000.0);
    }

    public static String generateReport(List<Person> list) {
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
            String direction = Utility.evaluateDirection(p.getOriginFloor(), p.getDestinationFloor()).toString();
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
        averageWaitTime = /*Arrays.stream(waitTimes).summaryStatistics().getAverage();*/new Long(totalWaitTime).doubleValue() / new Integer(TOTAL_NUMBER_OF_PEOPLE).doubleValue();
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

        return sb.toString();
    }
}

