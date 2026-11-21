@echo off
podman cp schema.sql kangu-db-1:/home/oracle
echo exit | podman exec -i kangu-db-1 sqlplus / as sysdba @/home/oracle/schema.sql
podman exec -i kangu-db-1 rm /home/oracle/schema.sql