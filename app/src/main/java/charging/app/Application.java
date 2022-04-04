package charging.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Application {

    static Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        List<Truck> truckList = Arrays.asList(
                new Truck("111", 500, 390),
                new Truck("115", 200, 30),
                new Truck("112", 400, 200),
                new Truck("113", 200, 180),
                new Truck("114", 100, 65)
        );

        List<Charger> chargerList = Arrays.asList(
                Charger.builder().chargerId("111").chargingRate(200).build(),
                Charger.builder().chargerId("112").chargingRate(250).build()
        );

        int totalChargingTime = 2;

        Scheduler chargingScheduler = new ChargingScheduler();
        Map<String, List<String>> schedule = null;
        try {
            schedule = chargingScheduler.schedule(chargerList, truckList, totalChargingTime);
        } catch (CannotScheduleException cse) {
            log.error("Problem scheduling chargers with trucks: " + cse.getMessage());
            throw new RuntimeException(cse);
        }

        for (Map.Entry<String, List<String>> entry : schedule.entrySet()) {
            log.info("{}: {}", entry.getKey(), String.join(",", entry.getValue()));
        }
    }

    public static void main2(String[] args) {
        List<Truck> truckList = Arrays.asList(
                new Truck("111", 200, 50),
                new Truck("112", 200, 70),
                new Truck("113", 200, 80),
                new Truck("114", 200, 75),
                new Truck("115", 200, 90),
                new Truck("116", 200, 110),
                new Truck("117", 200, 130)
        );

        List<Charger> chargerList = Arrays.asList(
                Charger.builder().chargerId("111").chargingRate(165).build(),
                Charger.builder().chargerId("112").chargingRate(160).build()
        );

        int totalChargingTime = 6;
        int percent = 50;

        Scheduler chargingScheduler = new PercentageChargingScheduler(percent);
        Map<String, List<String>> schedule = null;
        try {
            schedule = chargingScheduler.schedule(chargerList, truckList, totalChargingTime);
        } catch (CannotScheduleException cse) {
            log.error("Problem scheduling chargers with trucks: " + cse.getMessage());
            throw new RuntimeException(cse);
        }

        for (Map.Entry<String, List<String>> entry : schedule.entrySet()) {
            log.info("{}: {}", entry.getKey(), String.join(",", entry.getValue()));
        }
    }

}
