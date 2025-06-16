package com.example.demo.Offline;

import com.example.demo.Entity.SanPham;
import com.example.demo.Entity.SanPhamChiTiet;

import com.example.demo.Offline.Repository.OnlineSanPhamChiTietRepository;
import com.example.demo.Repository.SanPhamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class TrangSanPham {

    @Autowired
    private OnlineSanPhamChiTietRepository sanPhamChiTietRepository;

    @GetMapping("/san-pham")
    public String hienThiDanhSachSanPham(Model model) {
        List<SanPhamChiTiet> danhSachSanPham = sanPhamChiTietRepository.findAll();
        model.addAttribute("danhSachSanPham", danhSachSanPham);
        return "BanHangOnline/san-pham"; // Trả về view san-pham.html
    }

}
