package FloraLog.aep;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AepApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void deveIniciarAplicacao() {
		AepApplication.main(new String[]{"--server.port=0"});
	}

}
