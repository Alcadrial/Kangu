@echo off
updateDb
podman cp data.sql oracle-free:/home/oracle
echo exit | podman exec -i oracle-free sqlplus / as sysdba @/home/oracle/data.sql
podman exec -i oracle-free rm /home/oracle/data.sql