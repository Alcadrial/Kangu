@echo off
podman cp session.sql kangu-db-1:/home/oracle
podman exec -i kangu-db-1 sqlplus / as sysdba @/home/oracle/session.sql
podman exec -i kangu-db-1 rm /home/oracle/session.sql