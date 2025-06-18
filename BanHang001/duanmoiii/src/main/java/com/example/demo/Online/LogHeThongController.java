package com.example.demo.Online;

import com.example.demo.Entity.LogHeThong;
import com.example.demo.Service.LogHeThongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class LogHeThongController {

    @Autowired
    private LogHeThongService logHeThongService;

    @GetMapping("/log-he-thong")
    public String getLogsForCurrentUser(Model model, Authentication authentication) {
        String username = authentication.getName();
        Long userId = logHeThongService.findUserIdByUsername(username); // phương thức này bạn sẽ thêm ở service

        List<LogHeThong> logs = logHeThongService.findLogsByUserId(userId);
        model.addAttribute("logs", logs);
        return "lichsuhoatdong/log-he-thong";  // Giao diện hiển thị danh sách log
    }
}
