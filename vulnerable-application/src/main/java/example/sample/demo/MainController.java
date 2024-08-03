package example.sample.demo;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Controller
public class MainController {

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("message", "Hello, Spring!");
        return "index";  // 템플릿 파일 이름을 반환
    }
}

@RestController
class LogController {

    private static final Logger logger = LogManager.getLogger("DoTTak");

    @GetMapping("/log")
    public String index(@RequestHeader("msg") String msg) throws IOException {
        logger.info("Received a log message: " + msg);
        return "Log: " + msg + "\n";
    }
}