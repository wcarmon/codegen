# Overview
- Managing local SQLite database


# Install
```bash
sudo apt install sqlite3;
```

## Verify
```bash
sqlite3 –version
```


# Run
(Not required)


# Connect client (locally)
- See https://www.sqlite.org/cli.html
```bash
sqlite3 ~/tmp/my-foo.db;

.tables
.schema table1

.exit
```

# Hydrate
## Option-1
```bash
sqlite3 ~/tmp/my-foo.db -init ~/tmp/sqlite-init-script.sql
```


## Option-2
1. connect first
```
.read ~/tmp/sqlite-init-script.sql
```

## Verify
```
.tables
.schema table1
```

# Debug
TODO


# Cleanup
```bash
rm ~/tmp/my-foo.db;
```
