package com.example.demo.Offline.Controller;

import com.example.demo.Entity.DiaChiGiaoHang;
import com.example.demo.Entity.NguoiDung;
import com.example.demo.Offline.Repository.DiaChiGiaoHangRepository;
import com.example.demo.Service.NguoiDungService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/dia-chi-giao-hang")
public class DiaChiGiaoHangController {

    @Autowired
    private DiaChiGiaoHangRepository diaChiGiaoHangRepository;

    @Autowired
    private NguoiDungService nguoiDungService;

    @GetMapping
    public String danhSachDiaChi(Authentication authentication, Model model) {
        String username = authentication.getName();
        NguoiDung nguoiDung = nguoiDungService.findByTenDangNhap(username);
        List<DiaChiGiaoHang> danhSach = diaChiGiaoHangRepository.findByNguoiDung(nguoiDung);
        model.addAttribute("danhSach", danhSach);
        model.addAttribute("diaChiMoi", new DiaChiGiaoHang());
        return "BanHangOnline/dia-chi-giao-hang";
    }

    @PostMapping("/them")
    public String themDiaChi(@ModelAttribute DiaChiGiaoHang diaChiMoi, Authentication authentication) {
        String username = authentication.getName();
        NguoiDung nguoiDung = nguoiDungService.findByTenDangNhap(username);
        diaChiMoi.setNguoiDung(nguoiDung);
        diaChiGiaoHangRepository.save(diaChiMoi);
        return "redirect:/dia-chi-giao-hang";
    }

    @GetMapping("/xoa/{id}")
    public String xoaDiaChi(@PathVariable Long id) {
        diaChiGiaoHangRepository.deleteById(id);
        return "redirect:/dia-chi-giao-hang";
    }

    @PostMapping("/cap-nhat")
    public String capNhatDiaChi(@ModelAttribute DiaChiGiaoHang diaChiCapNhat, Authentication authentication) {
        String username = authentication.getName();
        NguoiDung nguoiDung = nguoiDungService.findByTenDangNhap(username);
        diaChiCapNhat.setNguoiDung(nguoiDung);
        diaChiGiaoHangRepository.save(diaChiCapNhat);
        return "redirect:/dia-chi-giao-hang";
    }

    @GetMapping("/mac-dinh/{id}")
    public String chonMacDinh(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        NguoiDung nguoiDung = nguoiDungService.findByTenDangNhap(username);
        List<DiaChiGiaoHang> danhSach = diaChiGiaoHangRepository.findByNguoiDung(nguoiDung);
        danhSach.forEach(d -> {
            d.setMacDinh(d.getId().equals(id));
            diaChiGiaoHangRepository.save(d);
        });
        return "redirect:/dia-chi-giao-hang";
    }

}