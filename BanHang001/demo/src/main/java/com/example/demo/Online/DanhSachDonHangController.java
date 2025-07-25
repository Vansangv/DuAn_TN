package com.example.demo.Online;

import com.example.demo.Entity.ChiTietDonHang;
import com.example.demo.Entity.DonHang;
import com.example.demo.Entity.LichSuThanhToan;
import com.example.demo.Repository.DonHangRepository;
import com.example.demo.Repository.LichSuThanhToanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class DanhSachDonHangController {
    @Autowired
    private DonHangRepository donHangRepository;

    @Autowired
    private LichSuThanhToanRepository lichSuThanhToanRepository;

    @GetMapping("/danh-sach-don-hang")
    public String getAllDonHang(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "keyword", required = false) String keyword,
            Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")); // sắp xếp id mới nhất lên đầu
        Page<DonHang> donHangPage;

        if (keyword != null && !keyword.trim().isEmpty()) {
            donHangPage = donHangRepository.findByNguoiDung_HoTenContainingIgnoreCase(keyword, pageable);
        } else {
            donHangPage = donHangRepository.findAll(pageable);
        }

        model.addAttribute("donHangPage", donHangPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", donHangPage.getTotalPages());
        model.addAttribute("page", "danh-sach-don-hang");

        return "donhang/danhsach";
    }




    @GetMapping("/don-hang/chi-tiet/{id}")
    public String xemChiTietDonHang(@PathVariable Long id, Model model) {
        DonHang donHang = donHangRepository.findById(id).orElse(null);

        if (donHang == null) {
            return "redirect:/danh-sach-don-hang";
        }

        List<ChiTietDonHang> chiTietList = donHang.getChiTietDonHangs();
        List<LichSuThanhToan> lichSuThanhToans = lichSuThanhToanRepository.findByDonHangId(id);

        model.addAttribute("donHang", donHang);
        model.addAttribute("chiTietDonHangs", chiTietList);
        model.addAttribute("lichSuThanhToans", lichSuThanhToans);

        return "donhang/chitiet";
    }

}
