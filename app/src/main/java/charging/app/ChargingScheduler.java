package charging.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ChargingScheduler implements Scheduler {

    static Logger log = LoggerFactory.getLogger(ChargingScheduler.class);

    @Override
    public Map<String, List<String>> schedule(final List<Charger> chargerList,
                                              final List<Truck> truckList, int totalTime)
            throws CannotScheduleException {
        Map<String, List<String>> scheduled = new HashMap();
        if (chargerList.isEmpty() || truckList.isEmpty()) {
            throw new CannotScheduleException("There are no chargers or trucks to schedule");
        }

        List<Map.Entry<String, Integer>> sortedTrucks = sortTrucksByChargeRequired(truckList);
        log.info("Trucks sorted by charge to full capacity: {}", sortedTrucks);

        List<Charger> sortedChargers = sortChargers(chargerList);
        log.info("Chargers sorted by charge rate: {}", sortedChargers);

        
        return scheduled;
    }

    List<Map.Entry<String, Integer>> sortTrucksByChargeRequired(final List<Truck> truckList) {

        List<Map.Entry<String, Integer>> sortedTrucks = truckList.stream()
                .map(truck -> Map.entry(truck.getId(),
                        truck.getBatteryCapacity() - truck.getCurrentCharge()))
                // sort the entries by ascending order for truck to be fully charged.
                .sorted(Comparator.comparingInt(Map.Entry::getValue))
                .collect(Collectors.toList());


        return sortedTrucks;
    }

    List<Charger> sortChargers(final List<Charger> chargers) {
        return chargers.stream()
                .sorted(Comparator.comparingInt(Charger::getChargingRate).reversed())
                .collect(Collectors.toList());
    }
}
