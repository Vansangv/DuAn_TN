package com.example.demo.Offline.Controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ChiDanController {
    @GetMapping("/chi-dan/hh1")
    public String chiDanHH1() {
        return "BanHangOnline/vitri"; // Tương ứng với templates/chi-dan/hh1-duong-dinh-nghe.html
    }
}
