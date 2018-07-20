package ind.bielu.redis.dump;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Properties;

/**
 * @author: bielu
 * @desc: RedisProperties
 * @date: 2018/5/30 17:26
 */
@Component
public class RedisProperties {

    private String from;

    private String to;

    Properties properties = new Properties();

    public RedisProperties() throws IOException {
        String configFile = "redis-dump.config";
        InputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(configFile));
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage() + ": cannot find redis-dump.config.");
        }
        if(in != null)
            properties.load(in);

        this.setFrom(properties.getProperty("redis.dump.from"));
        this.setTo(properties.getProperty("redis.dump.to"));

    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}