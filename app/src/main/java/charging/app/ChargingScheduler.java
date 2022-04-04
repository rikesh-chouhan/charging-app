package charging.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class ChargingScheduler implements Scheduler {

    static Logger log = LoggerFactory.getLogger(ChargingScheduler.class);

    // find candidate trucks which when charged would total up
    // close to a chargers capacity
    List<String> candidateTrucksForCharger(List<Map.Entry<String, Integer>> toBeChargedList, int maxCharge) {

        // Container to store candidate trucks
        Map.Entry<Integer, List<String>> optimum = Map.entry(0, new ArrayList<>());

        HashSet<Map.Entry<Integer, List<String>>> candidates = new HashSet<>();
        candidates.add(optimum);

        for (Map.Entry<String, Integer> chargeRequired : toBeChargedList) {
            Set<Map.Entry<Integer, List<String>>> newEntries = new HashSet<>();

            for (Map.Entry<Integer, List<String>> currentCandidateList : candidates) {
                List<String> newSublist = new ArrayList<>(currentCandidateList.getValue());
                newSublist.add(chargeRequired.getKey());
                Map.Entry<Integer, List<String>> newTotal =
                        Map.entry(currentCandidateList.getKey() + chargeRequired.getValue(), newSublist);

                if (newTotal.getKey() <= maxCharge) {
                    newEntries.add(newTotal);

                    // the `=` allows replacement of the older
                    if (newTotal.getValue().size() >= optimum.getValue().size()) {
                        optimum = newTotal;
                    }
                }
            }

            candidates.addAll(newEntries);
        }

        // placeholder
        return optimum.getValue();
    }

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

        List<Map.Entry<String, Integer>> chargerToKwhCapacity = sortedChargers.stream()
                .map(charger -> Map.entry(charger.getChargerId(), getTotalKiloWattHours(charger, totalTime)))
                .collect(Collectors.toList());

        // Take chargers and find trucks that can be charged for the given amount of charge.
        // initialize list to be used to determine candidate trucks for a charger.
        List<Map.Entry<String, Integer>> truckToChargeRemaining = new ArrayList<>(sortedTrucks);
        for (Map.Entry<String, Integer> aChargerCapacity : chargerToKwhCapacity) {
            List<String> candidateTrucks = candidateTrucksForCharger(truckToChargeRemaining, aChargerCapacity.getValue());
            scheduled.put(aChargerCapacity.getKey(), candidateTrucks);
            List<Map.Entry<String, Integer>> trucksPendingEvaluation = truckToChargeRemaining.stream()
                    // if the entry is not present in the current candidate then it
                    // can pass through for the next charger.
                    .filter(truckToFilter -> !candidateTrucks.contains(truckToFilter.getKey()))
                    .collect(Collectors.toList());
            truckToChargeRemaining = trucksPendingEvaluation;
        }

        return scheduled;
    }

    List<Map.Entry<String, Integer>> sortTrucksByChargeRequired(final List<Truck> truckList) {

        return truckList.stream()
                .map(truck -> Map.entry(truck.getId(),
                        truck.getBatteryCapacity() - truck.getCurrentCharge()))
                // sort the entries by ascending order for truck to be fully charged.
                .sorted(Comparator.comparingInt(Map.Entry::getValue))
                .collect(Collectors.toList());

    }

    List<Charger> sortChargers(final List<Charger> chargers) {
        return chargers.stream()
                .sorted(Comparator.comparingInt(Charger::getChargingRate).reversed())
                .collect(Collectors.toList());
    }

    int getTotalKiloWattHours(final Charger charger, final int timeAvailable) {
        return charger.getChargingRate() * timeAvailable;
    }

}
