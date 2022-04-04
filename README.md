### Charging scheduler.
This app attempts to match a charger with a list of electric vehicles that it can charge.

You can clone this repo.

#### building
```shell
./gradlew clean build

#### running
Run charging.app.Application main class. It produces a list of chargers to candidate vehichles it can chargge.
There are a couple of Scheduler implementations provided. One scheduler charges every vehicle to its full capacity.
The other charger is built to charge a vehicle to a certain percentage instead of full capacity.
This allows the app to use different scheduler types.   
```shell
./gradlew run
```

