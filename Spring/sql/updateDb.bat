@echo off
podman cp schema.sql oracle-free:/home/oracle
echo exit | podman exec -i oracle-free sqlplus / as sysdba @/home/oracle/schema.sql
podman exec -i oracle-free rm /home/oracle/schema.sql