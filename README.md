# redis_dump
*dump and restore key-values pairs from one redis cluster to another.*

### commands
**del-from**: del all key-value entrys by key pattern from the FROM cluster <br>
**del-to**: del all key-value entrys by key pattern from the TO cluster <br>
**dump**: dump all key-value entrys by key pattern from the FROM cluster to a dump file <br>
**get-from**: get value by key from the FROM cluster <br>
**get-to**: get value by key from the TO cluster <br>
**keys-from**: list all keys matches the given pattern from the FROM cluster <br>
**keys-to**: list all keys matches the given pattern from the TO cluster <br>
**restore**: restore all key-value entrys from a dump file to the TO redis cluster <br>