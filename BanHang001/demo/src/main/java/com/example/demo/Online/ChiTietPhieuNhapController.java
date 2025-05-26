package com.example.demo.Online;


import com.example.demo.Entity.*;
import com.example.demo.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/chi-tiet-phieu-nhap")
public class ChiTietPhieuNhapController {

    @Autowired
    private ChiTietPhieuNhapRepository chiTietPhieuNhapRepository;

    @Autowired
    private SanPhamChiTietRepository sanPhamChiTietRepository;

    @Autowired
    private PhieuNhapRepository phieuNhapRepository;

    @Autowired
    private MauSacRepository mauSacRepository;

    @Autowired
    private KichCoRepository kichCoRepository;

    // Danh sách chi tiết phiếu nhập
    @GetMapping
    public String list(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ChiTietPhieuNhap> chiTietPhieuNhaps;

        if (keyword != null && !keyword.trim().isEmpty()) {
            chiTietPhieuNhaps = chiTietPhieuNhapRepository
                    .findBySanPhamChiTiet_SanPham_TenSanPhamContainingIgnoreCase(keyword.trim(), pageable);
        } else {
            chiTietPhieuNhaps = chiTietPhieuNhapRepository.findAll(pageable);
        }

        model.addAttribute("chiTietPhieuNhaps", chiTietPhieuNhaps.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", chiTietPhieuNhaps.getTotalPages());
        model.addAttribute("keyword", keyword);

        return "chi_tiet_phieu_nhap/list";
    }


    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("chiTietPhieuNhap", new ChiTietPhieuNhap());
        model.addAttribute("phieuNhaps", phieuNhapRepository.findAll());

        List<SanPhamChiTiet> sanPhamChiTiets = sanPhamChiTietRepository.findAll();

        // Lấy tất cả sản phẩm từ chi tiết
        List<SanPham> sanPhams = sanPhamChiTiets.stream()
                .map(SanPhamChiTiet::getSanPham)
                .distinct()
                .collect(Collectors.toList());

        // ✅ Lấy tất cả màu sắc và kích cỡ từ repository (thay vì từ chi tiết sản phẩm)
        List<MauSac> mauSacs = mauSacRepository.findAll();
        List<KichCo> kichCos = kichCoRepository.findAll();

        // Lọc danh sách người nhập không trùng
        List<NguoiDung> nguoiNhaps = phieuNhapRepository.findAll().stream()
                .map(PhieuNhap::getNguoiNhap)
                .distinct()
                .collect(Collectors.toList());

        model.addAttribute("sanPhams", sanPhams);
        model.addAttribute("mauSacs", mauSacs);
        model.addAttribute("kichCos", kichCos);
        model.addAttribute("nguoiNhaps", nguoiNhaps);

        return "chi_tiet_phieu_nhap/add";
    }



    // Lưu chi tiết phiếu nhập
    @PostMapping("/save")
    public String save(@ModelAttribute ChiTietPhieuNhap chiTietPhieuNhap) {
        PhieuNhap phieuNhap = chiTietPhieuNhap.getPhieuNhap();

        if (phieuNhap != null && phieuNhap.getId() == null) {
            // Gán ngày nhập tự động
            phieuNhap.setNgayNhap(LocalDateTime.now());
            phieuNhapRepository.save(phieuNhap);
        }

        // Lưu chi tiết phiếu nhập
        chiTietPhieuNhapRepository.save(chiTietPhieuNhap);

        // Cập nhật số lượng sản phẩm chi tiết
        SanPhamChiTiet sanPhamChiTiet = chiTietPhieuNhap.getSanPhamChiTiet();
        int soLuongMoi = sanPhamChiTiet.getSoLuong() + chiTietPhieuNhap.getSoLuong();
        sanPhamChiTiet.setSoLuong(soLuongMoi);
        sanPhamChiTietRepository.save(sanPhamChiTiet);

        return "redirect:/chi-tiet-phieu-nhap";
    }



    // Xóa chi tiết phiếu nhập
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        chiTietPhieuNhapRepository.deleteById(id);
        return "redirect:/chi-tiet-phieu-nhap";
    }
}
