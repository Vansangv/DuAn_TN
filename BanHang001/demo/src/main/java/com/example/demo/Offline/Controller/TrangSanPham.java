package com.example.demo.Offline.Controller;

import com.example.demo.Entity.*;

import com.example.demo.Offline.Repository.OnlineGioHangRepository;
import com.example.demo.Offline.Repository.OnlineSanPhamChiTietRepository;
import com.example.demo.Offline.Repository.OnlineSanPhamTrongGioHangRepository;
import com.example.demo.PhanQuyen.BaseController;
import com.example.demo.Repository.SanPhamRepository;
import com.example.demo.Service.NguoiDungService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class TrangSanPham extends BaseController {

    @Autowired
    private OnlineSanPhamChiTietRepository sanPhamChiTietRepository;
    @Autowired
    private NguoiDungService nguoiDungService;
    @Autowired
    private OnlineSanPhamTrongGioHangRepository sanPhamTrongGioHangRepository;
    @Autowired
    private OnlineGioHangRepository gioHangRepository;

    @GetMapping("/san-pham-online")
    public String hienThiDanhSachSanPham(Model model,
                                         @RequestParam(required = false) Integer min,
                                         @RequestParam(required = false) Integer max,
                                         @RequestParam(required = false) String loai,
                                         @RequestParam(defaultValue = "1") int page,
                                         @RequestParam(defaultValue = "8") int size) {

        int pageIndex = Math.max(page - 1, 0);
        Pageable pageable = PageRequest.of(pageIndex, size);

        String loaiTimKiem = (loai != null && !loai.trim().isEmpty()) ? loai : null;

        Page<SanPhamChiTiet> danhSachSanPham;

        if (loaiTimKiem != null || min != null || max != null) {
            // Tìm theo điều kiện nếu có
            danhSachSanPham = sanPhamChiTietRepository
                    .findByLoaiSanPhamAndGia(loaiTimKiem, min, max, pageable);
        } else {
            // Không có điều kiện, trả về toàn bộ có phân trang
            danhSachSanPham = sanPhamChiTietRepository.findAll(pageable);
        }

        model.addAttribute("danhSachSanPham", danhSachSanPham);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", danhSachSanPham.getTotalPages());
        model.addAttribute("loai", loai);
        return "BanHangOnline/san-pham";
    }


    @GetMapping("/tim-kiem")
    public String timKiemSanPham(@RequestParam("keyword") String keyword, Model model) {
        List<SanPhamChiTiet> danhSachSanPham = sanPhamChiTietRepository.searchByTenSanPham(keyword);
        model.addAttribute("danhSachSanPham", danhSachSanPham);
        return "BanHangOnline/san-pham";
    }


    // ✅ Thêm vào giỏ hàng
    @PostMapping("/them-vao-gio-hangg/{id}")
    public String themVaoGioHang(@PathVariable("id") Long sanPhamChiTietId,
                                 Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login-online";
        }

        String username = authentication.getName();
        NguoiDung nguoiDung = nguoiDungService.findByTenDangNhap(username);
        if (nguoiDung == null) {
            return "redirect:/login-online";
        }

        GioHang gioHang = gioHangRepository.findByNguoiDung_IdAndTrangThai(nguoiDung.getId(), 3)
                .orElse(null);

        if (gioHang == null) {
            gioHang = new GioHang();
            gioHang.setNguoiDung(nguoiDung);
            gioHang.setNgayTao(LocalDateTime.now());
            gioHang.setTrangThai(3);
            gioHang = gioHangRepository.save(gioHang);
        }

        SanPhamChiTiet sanPhamChiTiet = sanPhamChiTietRepository.findById(sanPhamChiTietId).orElse(null);
        if (sanPhamChiTiet == null || sanPhamChiTiet.getSoLuong() <= 0) {
            return "redirect:/san-pham-online"; // hết hàng hoặc không tìm thấy
        }

        SanPhamTrongGioHang sanPhamTrongGioHang = sanPhamTrongGioHangRepository
                .findByGioHangAndSanPhamChiTiet(gioHang, sanPhamChiTiet)
                .orElse(null);

        if (sanPhamTrongGioHang != null) {
            if (sanPhamChiTiet.getSoLuong() > 0) {
                sanPhamTrongGioHang.setSoLuong(sanPhamTrongGioHang.getSoLuong() + 1);
                sanPhamChiTiet.setSoLuong(sanPhamChiTiet.getSoLuong() - 1);
            }
        } else {
            sanPhamTrongGioHang = new SanPhamTrongGioHang();
            sanPhamTrongGioHang.setGioHang(gioHang);
            sanPhamTrongGioHang.setSanPhamChiTiet(sanPhamChiTiet);
            sanPhamTrongGioHang.setSoLuong(1);
            sanPhamChiTiet.setSoLuong(sanPhamChiTiet.getSoLuong() - 1);
        }

        sanPhamChiTietRepository.save(sanPhamChiTiet);
        sanPhamTrongGioHangRepository.save(sanPhamTrongGioHang);

        return "redirect:/san-pham-online";
    }


}
