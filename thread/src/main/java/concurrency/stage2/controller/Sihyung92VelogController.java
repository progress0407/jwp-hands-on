package concurrency.stage2.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class Sihyung92VelogController {

    private Logger log = LoggerFactory.getLogger(Sihyung92VelogController.class);

    @RequestMapping("/sihyung92-velog")
    public ResponseEntity<String> hello() throws InterruptedException {
        log.info("start");
        Thread.sleep(1500);
        log.info("end");
        return ResponseEntity.ok("hello response");
    }
}
