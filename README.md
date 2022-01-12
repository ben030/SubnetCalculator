# CIDR Subnet Calculator

This is a small tool to calculate by a CIDR notation subnet string the number of hosts belonging to the subnet, the broadcast address and IP range.



### Setup
#### Prerequisites:
* Install the latest version of [Java](https://java.com) and [Maven](https://maven.apache.org/download.html).

1. Clone the repo


   ```sh
   git clone https://github.com/ben030/SubnetCalculator
   ```
2. Build
   ```sh
   mvn package
   ```
3. Run
   ```sh
   java -jar .\target\cidrJava-1.0-SNAPSHOT-jar-with-dependencies.jar
   ```
3. Open Browser on: `localhost:7080`
4. Click Buttons to see results

(Monitoring: [Prometheus](https://prometheus.io) endpoint with default JettyStatistics is provided on `localhost:7070`)
### Tests
JUnit Tests are in `\src\test\java\`
