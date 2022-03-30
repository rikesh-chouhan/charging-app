package charging.app;

import com.opencsv.bean.CsvToBeanBuilder;
import lombok.extern.slf4j.Slf4j;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

@Slf4j
public class ChargingApp {

    private final Date startTime = Calendar.getInstance().getTime();
    private Date endTime;

    public static void main(String[] args) {
        if (args.length < 3 ) {
            log.info("Please provide path for csv file with trucks info");
        } else {
            String chargingTime = args[0];
            String fileName = args[1];
            try {
                int timeToCharge = Integer.parseInt(chargingTime);

                // read and convert provided truck csv file to truck objects.
                List<Truck> truckList = new CsvToBeanBuilder(new FileReader(fileName))
                        .withType(Truck.class)
                        .build()
                        .parse();

                Set<String> ids = new HashSet();
                int errors = 0;

                // check truck for obvious errors.
                for (Truck truck : truckList) {
                    if (truck.getBatteryCapacity() < 0) {
                        errors++;
                        log.error("truck: {} battery capacity - {} should be greater than 0",
                                truck.getId(), truck.getBatteryCapacity());
                    }
                    if (ids.contains(truck.getId())) {
                        errors++;
                        log.error("truck: {} has already been set earlier in file", truck.getId());
                    } else {
                        ids.add(truck.getId());
                    }
                    log.info("Truck details: {}", truck.toString());
                }
                // show error and exit if applicable
                checkErrors(errors);

                if (truckList.size() == 0) {
                    // no trucks provided in file. Terminate
                    log.warn("No truck records found in file: {}", fileName);
                    System.exit(1);
                }

                // reset errors and ids for reuse
                errors = 0;
                ids.clear();

                List<Charger> chargerList = null;
                if (args.length > 2) {
                    fileName = args[2];
                    log.info("Reading charger file: {}", fileName);
                    chargerList = new CsvToBeanBuilder(new FileReader(fileName))
                            .withType(Charger.class)
                            .build()
                            .parse();

                    for (Charger charger: chargerList) {
                        if (ids.contains(charger.getChargerId())) {
                            errors++;
                            log.error("Charger: {} is repeated", charger.getChargerId());
                        }
                        if (charger.getChargingRate() <= 0) {
                            errors++;
                            log.error("Charger: {} charging rate is below 0: ", charger.getChargerId(), charger.getChargingRate());
                        }
                    }

                    checkErrors(errors);

                    if (chargerList.size() == 0) {
                        // no chargers provided in file. Terminate
                        log.warn("No charger records found in file: {}", fileName);
                        System.exit(1);
                    }

                } else {
                    chargerList = new ArrayList();
                    chargerList.add(Charger.builder().chargerId("111").chargingRate(10).build());
                    chargerList.add(Charger.builder().chargerId("112").chargingRate(10).build());
                }

                if (truckList.size() == 0) {
                    // no trucks provided in file. Terminate
                    log.warn("No truck records found in file: {}", fileName);
                    System.exit(1);
                }

                ChargingScheduler chargingScheduler = new ChargingScheduler();
                Map<String, List<String>> chargerToTrucks =
                        chargingScheduler.schedule(chargerList, truckList, timeToCharge);

                log.info("Output for charging trucks ");
                chargerToTrucks.entrySet().stream()
                        .forEach(entry -> log.info("{}: {}", entry.getKey(), entry.getValue()));

            } catch (IOException ioException) {
                log.error("Could not find file: {} error: {}", fileName, ioException.getMessage());
            } catch (CannotScheduleException cse) {
                log.error("Error scheduling data.", cse);
            }
        }
    }

    public static void checkErrors(int errors) {
        if (errors > 0) {
            log.error("Terminating program. Goodbye");
            System.exit(1);
        }
    }

    public Date startTime() {
        return startTime;
    }

    public Date endTime() {
        if (endTime == null) {
            endTime = Calendar.getInstance().getTime();
        }
        System.out.println("Current time: " + endTime);

        return endTime;
    }

}
