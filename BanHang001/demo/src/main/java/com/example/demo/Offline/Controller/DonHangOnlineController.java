package com.example.demo.Offline.Controller;

import com.example.demo.Entity.DonHang;
import com.example.demo.Entity.NguoiDung;
import com.example.demo.Offline.Repository.OnlineDonHangRepository;
import com.example.demo.Service.NguoiDungService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/don-hang")
public class DonHangOnlineController {
    @Autowired
    private OnlineDonHangRepository donHangRepository;

    @Autowired
    private NguoiDungService nguoiDungService;

    @GetMapping("/cua-toi")
    public String hienThiDonHangNguoiDung(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login-online";
        }

        String username = authentication.getName();
        NguoiDung nguoiDung = nguoiDungService.findByTenDangNhap(username);

        List<DonHang> donHangs = donHangRepository.findByNguoiDung_Id(nguoiDung.getId());

        model.addAttribute("danhSachDonHang", donHangs);
        return "BanHangOnline/don_hang_nguoi_dung"; // Tên file Thymeleaf
    }
}
