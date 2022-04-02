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

}