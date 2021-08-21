# Overview
- Managing MySQL containers
- for MySQL, Schema == Database

# Install
- See https://hub.docker.com/_/mysql
```
docker pull mysql:8.0;
```


# Run
```
docker run \
--name test-mysql1 \
-d \
-e MYSQL_DATABASE=testdb \
-e MYSQL_PASSWORD=test1 \
-e MYSQL_ROOT_PASSWORD=test1 \
-e MYSQL_USER=test \
-p 127.0.0.1:13306:3306/tcp \
mysql:8.0 \
--default-authentication-plugin=mysql_native_password;
```


# Connect client (locally)
```bash
docker exec -it test-mysql1 \
  mysql --port=3306 --host=127.0.0.1 --user=test --password=test1 testdb;

# or
mysql --port=13306 --host=127.0.0.1 --user=test --password=test1 testdb;
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
mysql --port=13306 --host=127.0.0.1 --user=test --password=test1 testdb < $SQL_INIT_FILE
```
TODO: mount volume to /docker-entrypoint-initdb.d


# Debug
## Mysql Server
```bash
docker exec \
-it \
test-mysql1 \
mysql --user=root --password=test1;
```

## Container
```bash
docker exec -it test-mysql1 /bin/bash;
```


# Cleanup
```bash
docker stop test-mysql1;
docker rm $(docker ps -a -q) || true;
docker ps;
```
