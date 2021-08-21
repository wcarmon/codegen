# Overview
- Managing PostgreSQL containers


# Install
```
docker pull postgres:13;
```


# Run
```
docker run \
--name test-pg1 \
-d \
-e POSTGRES_PASSWORD=test1 \
-p 127.0.0.1:15432:5432/tcp \
postgres:13;
```

## Verify Run
```
docker ps;
docker logs test-pg1;
sudo netstat -pant | grep 5432;
```


# Connect client (locally)
- See https://www.postgresql.org/docs/13/app-psql.html
```
/usr/bin/psql \
--host=127.0.0.1 \
--port=15432 \
--username=postgres \
--dbname=postgres;
```

## Verify Database
```
\dt
\dt+

\q
```


# Debug
```
docker run \
-it \
--rm postgres:13 \
bash;
```


# Cleanup
```
docker stop test-pg1;
docker rm $(docker ps -a -q) || true;
docker ps;
```
