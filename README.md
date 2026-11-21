# Kangu WIP

A simple message app using Spring, Angular, Kafka for realtime messages delivery and OracleDb,
nginx is exposed on port `4220` as a reverse proxy in order to work with same-origin.

## Running Locally

1. Copy `.env.default` as `.env`
2. Fill all passwords
3. Add a podman secret named as `oracle_db_secret`
4. Run `podman compose up -d` to start all service but spring and angular
5. Execute [this sql script](db/db.sql) after replacing passwords
6. Execute [this script](Spring/sql/resetDb.bat) to initialize the db schema.
7. Install Angular's dependencies with `npm install`.
8. Install nginx from [this link](https://nginx.org/en/download.html) on Windows or using `sudo apt install nginx` on Linux if not already installed.
9. Run nginx with `start nginx -c nginx/nginx.conf` on Windows or `sudo nginx -c nginx/nginx.conf` on Linux.
10. Now start the Spring Boot Application from the IDE or a jar.
11. Start Angular using `npm start`.
12. Open the browser to `localhost:4220` and navigate the site.

## Tests

Spring integration tests require a running spring boot environment that require a running OracleDb and Kafka Broker, see the "Running" section
for how to start spring boot and it's dependencies. Spring tests can be run easly from the IDE and Angular tests can be run using `npm test`.
