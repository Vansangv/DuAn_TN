package com.example.demo.Online;

import com.example.demo.Entity.LoginHistory;
import com.example.demo.Service.LoginHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class LichSuDangNhapController {

    @Autowired
    private LoginHistoryService lichSuDangNhapService;

    @GetMapping("/lich-su-dang-nhap")
    public String hienThiLichSuDangNhap(Model model,
                                        @AuthenticationPrincipal UserDetails userDetails) {
        String tenDangNhap = userDetails.getUsername();
        List<LoginHistory> lichSu = lichSuDangNhapService.layLichSuTheoTenDangNhap(tenDangNhap);
        model.addAttribute("lichSuDangNhap", lichSu);
        return "lichsudangnhap/lich-su-dang-nhap";
    }
}
