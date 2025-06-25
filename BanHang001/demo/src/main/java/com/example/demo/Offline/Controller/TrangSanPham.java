package com.example.demo.Offline.Controller;

import com.example.demo.Entity.NguoiDung;
import com.example.demo.Entity.SanPham;
import com.example.demo.Entity.SanPhamChiTiet;

import com.example.demo.Offline.Repository.OnlineSanPhamChiTietRepository;
import com.example.demo.Repository.SanPhamRepository;
import com.example.demo.Service.NguoiDungService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class TrangSanPham {

    @Autowired
    private OnlineSanPhamChiTietRepository sanPhamChiTietRepository;
    @Autowired
    private NguoiDungService nguoiDungService;

    @GetMapping("/san-pham")
    public String hienThiDanhSachSanPham(Model model, Authentication authentication,
                                         @RequestParam(required = false) Integer min,
                                         @RequestParam(required = false) Integer max,
                                         @RequestParam(required = false) String loai) {

        // Lấy người dùng
        if (authentication != null) {
            String username = authentication.getName();
            NguoiDung nguoiDung = nguoiDungService.findByTenDangNhap(username);
            model.addAttribute("tenNguoiDung", nguoiDung != null ? nguoiDung.getHoTen() : "Khách");
        }

        // Nếu người dùng không chọn loại thì truyền null
        String loaiTimKiem = (loai != null && !loai.isEmpty()) ? loai : null;

        List<SanPhamChiTiet> danhSachSanPham = sanPhamChiTietRepository
                .findByLoaiSanPhamAndGia(loaiTimKiem, min, max);

        model.addAttribute("danhSachSanPham", danhSachSanPham);
        return "BanHangOnline/san-pham";
    }


    @GetMapping("/tim-kiem")
    public String timKiemSanPham(@RequestParam("keyword") String keyword, Model model) {
        List<SanPhamChiTiet> danhSachSanPham = sanPhamChiTietRepository.searchByTenSanPham(keyword);
        model.addAttribute("danhSachSanPham", danhSachSanPham);
        return "BanHangOnline/san-pham";
    }

}
