package com.example.demo.Offline.Controller;
import com.example.demo.Entity.MaGiamGia;
import com.example.demo.Offline.Repository.OnlineMaGiamGiaRepository;
import com.example.demo.Offline.Repository.SuDungMaGiamGiaRepository;
import com.example.demo.Service.NguoiDungService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class KhuyenMaiController {

    @Autowired
    private OnlineMaGiamGiaRepository maGiamGiaRepository;
    @Autowired
    private NguoiDungService nguoiDungService;
    @Autowired
    private SuDungMaGiamGiaRepository suDungMaGiamGiaRepository;

    @GetMapping("/khuyen-mai")
    public String hienThiKhuyenMai(Model model) {
        List<MaGiamGia> danhSachKhuyenMai = maGiamGiaRepository.findAllByTrangThaiTrueAndSoLuongGreaterThan(0);
        model.addAttribute("danhSachKhuyenMai", danhSachKhuyenMai);
        return "BanHangOnline/khuyen-mai"; // Gọi tới file khuyen-mai.html
    }

}
