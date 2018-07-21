# redis_dump
dump and restore key-values pairs from one redis cluster to another.

del-from: del all key-value entrys by key pattern from the FROM cluster
del-to: del all key-value entrys by key pattern from the TO cluster
dump: dump all key-value entrys by key pattern from redis cluster to dump file
get-from: get value by key from the FROM cluster
get-to: get value by key from the TO cluster
keys-from: list all keys matches the given pattern from the FROM cluster
keys-to: list all keys matches the given pattern from the TO cluster
restore: restore all key-value entrys from a dump file to the TO redis cluster