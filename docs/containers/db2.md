# Overview
- Info for dealing with legacy software (DB2)
- Note: DB2INSTANCE env var defines the username ("db2inst1")
- case insensitive


# Install
- See https://hub.docker.com/r/ibmcom/db2
```bash
docker pull ibmcom/db2:11.5.5.1;
docker pull ibmcom/db2:11.5.4.0;
```


# Run
```bash
docker run \
--name test-db2 \
--privileged=true \
-d \
-e ARCHIVE_LOGS=false \
-e AUTOCONFIG=false \
-e BLU=false \
-e DB2INST1_PASSWORD=test1 \
-e DB2INSTANCE=db2inst1 \
-e DBNAME=testdb \
-e ENABLE_ORACLE_COMPATIBILITY=false \
-e HADR_ENABLED=false \
-e LICENSE=accept \
-e SAMPLEDB=true \
-e TEXT_SEARCH=true \
-e UPDATEAVAIL=NO \
-p 127.0.0.1:50000:50000/tcp \
ibmcom/db2:11.5.4.0;
```
# NOTE: user: DB2INST1
# NOTE: you have to wait 5 minutes!  (for this slow software)


## Verify
```bash
docker logs -f test-db2;
```


# Connect client (locally)
TODO: might require JCE ciphers
```bash
docker exec \
-it \
test-db2 \
bash -c "su - db2inst1";
```


# Hydrate
TODO

## Verify
TODO


# Debug
```bash
docker exec -it test-db2 /bin/bash;
```


# Cleanup
```bash
docker stop test-db2;
docker rm $(docker ps -a -q) || true;
docker ps;
```
