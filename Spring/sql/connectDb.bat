@echo off
podman cp session.sql oracle-free:/home/oracle
podman exec -i oracle-free sqlplus / as sysdba @/home/oracle/session.sql
podman exec -i oracle-free rm /home/oracle/session.sql