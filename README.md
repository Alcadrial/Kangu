# Kangu WIP

A simple message app using Spring, Angular, Kafka for realtime messages delivery and OracleDb,
nginx is exposed on port `4220` as a reverse proxy in order to work with same-origin.

## Running Locally

1. Configure the Oracle free ai database following instructions in [this link](https://container-registry.oracle.com/ords/f?p=113:10::::::)
   to run an `Oracle Database Free` with `Full` image, to connect use [this script](Spring/sql/connectDb.bat) (replace podman with docker if the
   database was pulled to docker and enable pulling from third party repositories) and [this script](Spring/sql/resetDb.bat) to initialize the db schema.
2. Create a new user `spring` by executing [this sql script](Spring/sql/db.sql) and set the password inside the application.properties file of
   spring replacing the line `spring.datasource.password=Ed8J7LmNZdWWQC4drF4elP_RbAsaNP92qFg5CvQHmgsO`. 
3. Pull kafka broker docker image using `podman pull apache/kafka:latest` and run with `podman run -d --name broker apache/kafka:latest`.
4. Start the oracle database using `podman start oracle-free` and the kafka broker using `podman start broker`.
5. Now start the Spring Boot Application from the IDE or a jar.
6. Install Angular's dependencies with `npm install`.
7. Start Angular using `npm start`.
8. Install nginx from [this link](https://nginx.org/en/download.html) on Windows or using `sudo apt install nginx` on Linux if not already installed.
9. Run nginx with `start nginx -c nginx/nginx.conf` on Windows or `sudo nginx -c nginx/nginx.conf` on Linux.
10. Open the browser to `localhost:4220` and navigate the site.

## Tests

Spring integration tests require a running spring boot environment that require a running OracleDb and Kafka Broker, see the "Running" section
for how to start spring boot and it's dependencies. Spring tests can be run easly from the IDE and Angular tests can be run using `npm test`.
