package ind.bielu.redis.dump;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import redis.clients.jedis.*;

/**
 * @author: bielu
 * @desc: JedisTool
 * @date: 2018/5/30 17:20
 */
@Component
public class JedisTool {

    // from jedis list and cluster
    private List<Jedis> fromList = new ArrayList<>();;
    private JedisCluster fjc;

    // to jedis list and cluster
    private List<Jedis> toList = new ArrayList<>();;
    private JedisCluster tjc;

    private static int TIMEOUT = 2000;

    @Autowired
    private RedisProperties redisProperties;


    public void init() {
        // init the FROM jedis list and cluster
        String fromHosts = redisProperties.getFrom();
        if(fromHosts.length() == 0) {
            throw new RuntimeException("ERROR: config redis.dump.from is empty in application.yml, " +
                    "make sure the value is set correctly");
        }
        this.fjc = buildJedisListAndCluster(fromHosts, fromList);

        // init the TO jedis list and cluster
        String toHosts = redisProperties.getTo();
        if(toHosts.length() == 0) {
            System.out.println("WARN: config redis.dump.to is empty in application.yml which leads to the restore " +
                    "comand not useable. Make sure the value is set correctly if you want to use that.");
        }else {
            this.tjc = buildJedisListAndCluster(toHosts, toList);
        }
    }

    private JedisCluster buildJedisListAndCluster(String hosts, List list) {
        String[] hostAttr = hosts.split(",");
        Set<HostAndPort> jedisNodes = new HashSet();
        for(int i = 0; i < hostAttr.length; ++i) {
            String host = hostAttr[i];
            String ip = host.split(":")[0];
            int port = Integer.parseInt(host.split(":")[1]);
            list.add(new Jedis(ip, port, TIMEOUT));
            jedisNodes.add(new HostAndPort(ip, port));
        }
        return new JedisCluster(jedisNodes, TIMEOUT);
    }

    public String getHostsDesc() {
        return "From hosts: " + redisProperties.getFrom() + "\nTo hosts: " + redisProperties.getTo() + "\n";
    }

    /**
     * keys from the FROM cluster
     * @param pattern
     * @return keys
     */
    public Set<String> keysFrom(String pattern) {
        Set<String> keySet = new HashSet<>();
        for(Jedis jedis : fromList) {
            keySet.addAll(jedis.keys(pattern));
        }
        return keySet;
    }

    /**
     * get value from the FROM cluster
     * @param key
     * @return value
     */
    public String getFrom(String key) {
        return fjc.get(key);
    }

    /**
     * set value to the FROM cluster
     * @param key
     * @param value
     * @return value
     */
    public String setFrom(String key, String value) {
        return fjc.set(key, value);
    }

    /**
     * del keys from the FROM cluster
     * @param pattern
     * @return count
     */
    public Long delFrom(String pattern) {
        Set<String> keySet = keysFrom(pattern);
        Long count = 0L;
        for(String key : keySet) {
            count += fjc.del(key);
        }
        return count;
    }


    /* ------------------------  FROM above  ---------------  TO below  -------------------*/


    /**
     * keys from the TO cluster
     * @param pattern
     * @return keys
     */
    public Set<String> keysTo(String pattern) {
        Set<String> keySet = new HashSet<>();
        for(Jedis jedis : toList) {
            keySet.addAll(jedis.keys(pattern));
        }
        return keySet;
    }

    /**
     * get value from the TO cluster
     * @param key
     * @return value
     */
    public String getTo(String key) {
        return tjc.get(key);
    }

    /**
     * set value to the TO cluster
     * @param key
     * @param value
     * @return value
     */
    public String setTo(String key, String value) {
        return tjc.set(key, value);
    }

    /**
     * del keys from the FROM cluster
     * @param pattern
     * @return count
     */
    public Long delTo(String pattern) {
        Set<String> keySet = keysTo(pattern);
        Long count = 0L;
        for(String key : keySet) {
            count += tjc.del(key);
        }
        return count;
    }

    public void close() {
        try {
            for(Jedis jedis : fromList) {
                if(jedis != null)   jedis.close();
            }
            for(Jedis jedis : toList) {
                if(jedis != null)   jedis.close();
            }
            if(fjc != null) fjc.close();
            if(tjc != null) tjc.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
