package concurrency.blog;

import concurrency.stage2.controller.Sihyung92VelogController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

/**
 * https://velog.io/@sihyung92/how-does-springboot-handle-multiple-requests
 */
public class Sihyung92VelogTest {

    private Logger log = LoggerFactory.getLogger(Sihyung92VelogTest.class);

    @DisplayName("요청 5개 보내기")
    @Test
    void test() {
        RestTemplate restTemplate = new RestTemplate();
        for (int i = 0; i < 5; i++) {
            Thread thread = new Thread(() -> {
                log.info("발사!");
                String result = restTemplate
                        .getForObject("http://localhost:5000/sihyung92-velog", String.class);
                log.info(result);
            });
            thread.start();
        }
    }

    @Test
    void test2() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getForObject("http://localhost:5000/sihyung92-velog", String.class);
        restTemplate.getForObject("http://localhost:5000/sihyung92-velog", String.class);
        restTemplate.getForObject("http://localhost:5000/sihyung92-velog", String.class);
        restTemplate.getForObject("http://localhost:5000/sihyung92-velog", String.class);
        restTemplate.getForObject("http://localhost:5000/sihyung92-velog", String.class);
    }

    @Test
    void test3() {
        RestTemplate restTemplate = new RestTemplate();

        Runnable rn = () -> {
            log.info("발사!");
            String result = restTemplate
                    .getForObject("http://localhost:5000/sihyung92-velog", String.class);
            log.info(result);
        };

        final Thread[] threads = new Thread[5];
        for (int i = 0; i < 5; i++) {
            threads[i] = new Thread(rn);
        }

        for (int i = 0; i < 5; i++) {
            threads[i].start();
        }
    }
}
