package com.example.demo.Offline.Controller;

import com.example.demo.Entity.DonHang;
import com.example.demo.Entity.NguoiDung;
import com.example.demo.Entity.VanChuyen;
import com.example.demo.Offline.Repository.OnlineDonHangRepository;
import com.example.demo.Offline.Repository.VanChuyenRepository;
import com.example.demo.Repository.DonHangRepository;
import com.example.demo.Service.NguoiDungService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Controller
@RequestMapping("/loc-theo-trangthai")
public class DonHangVanChuyenController {
    @Autowired
    private OnlineDonHangRepository donHangRepository;

    @Autowired
    private VanChuyenRepository vanChuyenRepository;

    @Autowired
    private NguoiDungService nguoiDungService;

    @GetMapping
    public String locTrangThai(@RequestParam(defaultValue = "donhang") String loai,
                               @RequestParam(defaultValue = "Chờ xác nhận") String trangThai,
                               Model model,
                               Authentication authentication) {
        model.addAttribute("loai", loai);
        model.addAttribute("trangThai", trangThai);

        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            NguoiDung nguoiDung = nguoiDungService.findByTenDangNhap(username);

            if (nguoiDung != null) {
                Long nguoiDungId = nguoiDung.getId();

                if ("donhang".equals(loai)) {
                    List<DonHang> danhSach = donHangRepository.findByTrangThaiAndNguoiDungId(trangThai, nguoiDungId);
                    model.addAttribute("donHangList", danhSach);
                } else if ("vanchuyen".equals(loai)) {
                    List<VanChuyen> danhSach = vanChuyenRepository.findByTrangThaiAndNguoiDungId(trangThai, nguoiDungId);
                    model.addAttribute("vanChuyenList", danhSach);
                }

                model.addAttribute("tenNguoiDung", nguoiDung.getHoTen());
            } else {
                model.addAttribute("tenNguoiDung", "Khách");
            }
        } else {
            model.addAttribute("tenNguoiDung", "Khách");
        }

        return "BanHangOnline/trang-thai-donhang";
    }
}
