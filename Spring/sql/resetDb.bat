@echo off
podman cp db.sql kangu-db-1:/home/oracle
echo exit | podman exec -i kangu-db-1 sqlplus / as sysdba @/home/oracle/db.sql
podman exec -i kangu-db-1 rm /home/oracle/db.sql
updateDb
podman cp data.sql kangu-db-1:/home/oracle
echo exit | podman exec -i kangu-db-1 sqlplus / as sysdba @/home/oracle/data.sql
podman exec -i kangu-db-1 rm /home/oracle/data.sql