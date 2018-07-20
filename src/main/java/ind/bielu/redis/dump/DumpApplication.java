package ind.bielu.redis.dump;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.shell.jline.PromptProvider;

/**
 * @author: bielu
 * @desc: DumpApplication
 * @date: 2018/5/30 17:20
 */
@SpringBootApplication
public class DumpApplication {

	public static void main(String[] args) {
		SpringApplication.run(DumpApplication.class, args);
	}

	@Bean
	public PromptProvider promptProvider() {
		return () -> new AttributedString("redis_dump> ",
				AttributedStyle.DEFAULT.foreground(AttributedStyle.BLUE));
	}
}
