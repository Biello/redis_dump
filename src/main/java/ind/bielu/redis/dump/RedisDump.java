package ind.bielu.redis.dump;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author: bielu
 * @desc: RedisDump
 * @date: 2018/5/30 16:56
 */
@ShellComponent
public class RedisDump {

    @Autowired
    private JedisTool jedisTool;

    @PostConstruct
    public void init() {
        jedisTool.init();
    }

    @ShellMethod("Say hi")
    public String sayHi(String name) {
        return String.format("Hi %s", name);
    }


    @ShellMethod("list all keys matches the given pattern from FROM cluster")
    public String keysFrom(String pattern) throws IOException {
        Set<String> keySet = jedisTool.keysFrom(pattern);
        return keySet.stream().collect(Collectors.joining("\n")) + "\nTotal: " + keySet.size();
    }

    @ShellMethod("list all keys matches the given pattern from TO cluster")
    public String keysTo(String pattern) throws IOException {
        Set<String> keySet = jedisTool.keysTo(pattern);
        return keySet.stream().collect(Collectors.joining("\n")) + "\nTotal: " + keySet.size();
    }

    @ShellMethod("dump all key-value entrys by key pattern from redis cluster to dump file")
    public String dump(String pattern) throws IOException {

        Set<String> keySet = jedisTool.keysFrom(pattern);
        StringBuilder sb = new StringBuilder((keySet.size() + 2) * 64);
        sb.append(jedisTool.getHostsDesc());
        sb.append("Dump pattern: ").append(pattern).append("\t").append("total: ").append(keySet.size()).append("\n");
        keySet.forEach(e -> sb.append(e).append(" ").append(jedisTool.getFrom(e)).append("\n"));

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM-dd-HH:mm:ss");
        String filename = LocalDateTime.now().format(dtf) + ".dump";
        writeFile(filename, sb.toString());
        return keySet.size() + " entrys dumped to file: " + filename;
    }

    @ShellMethod("restore all key-value entrys from a dump file to the redis cluster")
    public String restore(String filename) throws IOException {
        Scanner sc = new Scanner(new FileInputStream(filename));
        int count = 0;
        String line = "";
        while(sc.hasNextLine()) {
            count++;
            line = sc.nextLine();
            if(count > 3) { // 头部3行
                String[] entey = line.split(" ");
                jedisTool.setTo(entey[0], entey[1]);
            }
        }
        sc.close();
        return count-3 + " entrys restored from file: " + filename;
    }


    @ShellMethod("get value by key from the FROM cluster")
    public String getFrom(String key) throws IOException{
        return jedisTool.getFrom(key);
    }

    @ShellMethod("get value by key from the TO cluster")
    public String getTo(String key) throws IOException {
        return jedisTool.getTo(key);
    }

    @ShellMethod("del all key-value entrys by key pattern from the FROM cluster")
    public String delFrom(String pattern) throws IOException {
        Long count = jedisTool.delFrom(pattern);
        return count + " entrys has been deleted";
    }

    @ShellMethod("del all key-value entrys by key pattern from the TO cluster")
    public String delTo(String pattern) throws IOException {
        Long count = jedisTool.delTo(pattern);
        return count + " entrys has been deleted";
    }


    private void writeFile(String filename, String content) throws IOException {
        File dump = new File(filename);
        FileWriter writer = new FileWriter(dump);
        writer.write(content);
        writer.flush();
        writer.close();
    }

}
