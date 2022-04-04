package charging.app;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

class ChargingSchedulerTest {

    static Logger log = LoggerFactory.getLogger(ChargingSchedulerTest.class);

    ChargingScheduler underTest;

    @BeforeEach
    void setup() {
        underTest = new ChargingScheduler();
    }

    @Test
    void schedule() {
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

        Map<String, List<String>> schedule = null;
        try {
            schedule = underTest.schedule(chargerList, truckList, totalChargingTime);
        } catch (CannotScheduleException cse) {
            log.error("Problem scheduling chargers with trucks: " + cse.getMessage());
            Assertions.fail("There should be no error", cse);
        }

        Assertions.assertEquals(Arrays.asList("114"), schedule.get("111"));
    }

    @Test
    void sortTrucksByChargingLeftToFullCapacity() {
        List<Truck> truckList = Arrays.asList(
                new Truck("111", 100, 24),
                new Truck("112", 100, 75),
                new Truck("113", 100, 95),
                new Truck("114", 100, 5)
        );
        List<Map.Entry<String, Integer>> sorted = underTest.sortTrucksByChargeRequired(truckList);

        Assertions.assertEquals(Map.entry("113",5), sorted.get(0));
    }

    @Test
    void sortTrucksForDifferentBatteryCapacity() {
        List<Truck> truckList = Arrays.asList(
                new Truck("111", 500, 490),
                new Truck("112", 200, 150),
                new Truck("113", 20, 10),
                new Truck("114", 100, 5)
        );
        List<Map.Entry<String, Integer>> sorted = underTest.sortTrucksByChargeRequired(truckList);

        Assertions.assertEquals(Map.entry("111",10), sorted.get(0));
        Assertions.assertEquals(Map.entry("113",10), sorted.get(1));

    }

    @Test
    void sortChargers() {
        List<Charger> chargerList = Arrays.asList(
          Charger.builder().chargerId("111").chargingRate(120).build(),
          Charger.builder().chargerId("112").chargingRate(200).build(),
          Charger.builder().chargerId("113").chargingRate(150).build()
        );

        List<Charger> sorted = underTest.sortChargers(chargerList);

        Assertions.assertEquals(
                Charger.builder().chargerId("112").chargingRate(200).build(),
                sorted.get(0));
    }

    @Test
    void getTotalKiloWattHours() {
        List<Charger> chargerList = Arrays.asList(
                Charger.builder().chargerId("111").chargingRate(100).build(),
                Charger.builder().chargerId("112").chargingRate(200).build(),
                Charger.builder().chargerId("113").chargingRate(50).build()
        );
        int totalTime = 3;
        for (Charger charger: chargerList) {
            Assertions.assertEquals(charger.getChargingRate() * totalTime,
                    underTest.getTotalKiloWattHours(charger, totalTime));
        }
    }

    @Test
    void getCandidateTrucks() {
        List<Map.Entry<String, Integer>> truckList = Arrays.asList(
                Map.entry("111", 10),
                Map.entry("112", 30),
                Map.entry("113", 50),
                Map.entry("114", 80)
        );

        int maxCharge = 40;
        List<String> candidateTrucks = underTest.candidateTrucksForCharger(truckList, maxCharge);
        List<String> expectedList = Arrays.asList("111", "112");

        Assertions.assertEquals(expectedList, candidateTrucks);

        maxCharge = 60;
        candidateTrucks = underTest.candidateTrucksForCharger(truckList, maxCharge);
        expectedList = Arrays.asList("111", "113");

        Assertions.assertEquals(expectedList, candidateTrucks);
    }
}