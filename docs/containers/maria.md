# Overview
- Managing MariaDB containers
- for MariaDB, Schema == Database

# Install
- See https://hub.docker.com/_/mariadb
```
docker pull mariadb:10.6;
```


# Run
```
docker run \
--name test-mariadb1 \
-d \
-e MYSQL_DATABASE=testdb \
-e MYSQL_PASSWORD=test1 \
-e MYSQL_ROOT_PASSWORD=test1 \
-e MYSQL_USER=test \
-p 127.0.0.1:13307:3306/tcp \
mariadb:10.6 \
--default-authentication-plugin=mysql_native_password;
```


# Connect client (locally)
```bash
docker exec -it test-mariadb1 \
  mysql --port=3306 --host=127.0.0.1 --user=test --password=test1 testdb;

# or
mysql --port=13307 --host=127.0.0.1 --user=test --password=test1 testdb;
```

## Verify
```
SHOW DATABASES;
USE testdb;
SHOW TABLES;

exit;
```


# Hydrate
```bash
SQL_INIT_FILE=~/tmp/my-sql-statements.sql;
SQL_INIT_FILE=/home/wcarmon/git-repos/modern-jvm/sandbox/src/gen/java/com/wcarmon/chrono/create-tables.maria.sql
mysql --port=13307 --host=127.0.0.1 --user=test --password=test1 testdb < $SQL_INIT_FILE
```
TODO: mount volume to /docker-entrypoint-initdb.d


# Debug
## MariaDB Server
```bash
docker exec \
-it \
test-mariadb1 \
mysql --user=root --password=test1;
```

## Container
```bash
docker exec -it test-mariadb1 /bin/bash;
```


# Cleanup
```bash
docker stop test-mariadb1;
docker rm $(docker ps -a -q) || true;
docker ps;
```
