package com.example.demo.Online;

import com.example.demo.Entity.ChiTietDonHang;
import com.example.demo.Entity.DonHang;
import com.example.demo.Entity.LichSuThanhToan;
import com.example.demo.Repository.ChiTietDonHangRepository;
import com.example.demo.Repository.DonHangRepository;
import com.example.demo.Repository.LichSuThanhToanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class LichSuThanhToanController {

    @Autowired
    private LichSuThanhToanRepository lichSuThanhToanRepository;

    @Autowired
    private DonHangRepository donHangRepository;

    @Autowired
    private ChiTietDonHangRepository chiTietDonHangRepository;

    @GetMapping("/lich-su-thanh-toan")
    public String hienThiLichSuThanhToan(
            @RequestParam(name = "pageLichSu", defaultValue = "0") int pageLichSu,
            @RequestParam(name = "pageDonHang", defaultValue = "0") int pageDonHang,
            @RequestParam(name = "pageChiTiet", defaultValue = "0") int pageChiTiet,
            @RequestParam(name = "keyword", required = false, defaultValue = "") String keyword,
            Model model) {

        int sizeLichSuDonHang = 5;
        int sizeChiTiet = 10;

        Pageable pageableLichSu = PageRequest.of(pageLichSu, sizeLichSuDonHang);
        Page<LichSuThanhToan> lichSuPage = lichSuThanhToanRepository
                .findByNguoiDung_HoTenContainingIgnoreCase(keyword, pageableLichSu);

        Pageable pageableDonHang = PageRequest.of(pageDonHang, sizeLichSuDonHang);
        Page<DonHang> donHangPage = donHangRepository
                .findByNguoiDung_HoTenContainingIgnoreCase(keyword, pageableDonHang);

        Pageable pageableChiTiet = PageRequest.of(pageChiTiet, sizeChiTiet);
        Page<ChiTietDonHang> chiTietPage = chiTietDonHangRepository
                .findByDonHang_NguoiDung_HoTenContainingIgnoreCase(keyword, pageableChiTiet);

        model.addAttribute("lichSuPage", lichSuPage);
        model.addAttribute("donHangPage", donHangPage);
        model.addAttribute("chiTietPage", chiTietPage);

        model.addAttribute("keyword", keyword);

        model.addAttribute("currentPageLichSu", pageLichSu);
        model.addAttribute("totalPagesLichSu", lichSuPage.getTotalPages());

        model.addAttribute("currentPageDonHang", pageDonHang);
        model.addAttribute("totalPagesDonHang", donHangPage.getTotalPages());

        model.addAttribute("currentPageChiTiet", pageChiTiet);
        model.addAttribute("totalPagesChiTiet", chiTietPage.getTotalPages());

        return "lichsuthanhtoan/lich_su_thanh_toan";
    }
}
