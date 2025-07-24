package com.example.demo.Offline.Controller;

import com.example.demo.Entity.ChiTietDonHang;
import com.example.demo.Entity.DonHang;
import com.example.demo.Entity.VanChuyen;
import com.example.demo.Offline.Repository.OnlineChiTietDonHangRepository;
import com.example.demo.Offline.Repository.OnlineDonHangRepository;
import com.example.demo.Offline.Repository.VanChuyenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class TrangThaiDonHang {

    @Autowired
    private OnlineDonHangRepository onlineDonHangRepository;
    @Autowired
    private OnlineChiTietDonHangRepository onlineChiTietDonHangRepository;
    @Autowired
    private VanChuyenRepository vanChuyenRepository;

    @GetMapping("/don-hang/{id}")
    public String chiTietDonHang(@PathVariable("id") Long donHangId, Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login-online";
        }

        DonHang donHang = onlineDonHangRepository.findById(donHangId).orElse(null);
        if (donHang == null) {
            return "redirect:/don-hang";
        }

        List<ChiTietDonHang> chiTietList = onlineChiTietDonHangRepository.findByDonHang(donHang);
        VanChuyen vanChuyen = vanChuyenRepository.findByDonHang(donHang);

        model.addAttribute("donHang", donHang);
        model.addAttribute("chiTietList", chiTietList);
        model.addAttribute("vanChuyen", vanChuyen);

        return "BanHangOnline/trang-thai"; // trỏ đến file `chitiet.html`
    }

}
