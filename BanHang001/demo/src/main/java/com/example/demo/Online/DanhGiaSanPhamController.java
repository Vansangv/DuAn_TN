package com.example.demo.Online;

import com.example.demo.Entity.DanhGiaSanPham;
import com.example.demo.Offline.Repository.OnlineDanhGiaSanPhamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/xem-danh-gia")
public class DanhGiaSanPhamController {
    @Autowired
    private OnlineDanhGiaSanPhamRepository danhGiaSanPhamRepository;

    @GetMapping
    public String hienThiDanhGia(Model model) {
        List<DanhGiaSanPham> danhSachDanhGia = danhGiaSanPhamRepository.findAll();
        model.addAttribute("danhSachDanhGia", danhSachDanhGia);
        model.addAttribute("page", "danh-gia-san-pham");
        return "danh-gia/danh-sach"; // trỏ đến file danh-sach.html trong thư mục danh-gia
    }
}
