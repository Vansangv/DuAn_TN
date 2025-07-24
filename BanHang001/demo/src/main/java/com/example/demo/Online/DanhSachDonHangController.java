package com.example.demo.Online;

import com.example.demo.Entity.ChiTietDonHang;
import com.example.demo.Entity.DonHang;
import com.example.demo.Entity.LichSuThanhToan;
import com.example.demo.Repository.DonHangRepository;
import com.example.demo.Repository.LichSuThanhToanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class DanhSachDonHangController {
    @Autowired
    private DonHangRepository donHangRepository;

    @Autowired
    private LichSuThanhToanRepository lichSuThanhToanRepository;

    @GetMapping("/danh-sach-don-hang")
    public String getAllDonHang(Model model) {
        List<DonHang> donHangs = donHangRepository.findAll();
        model.addAttribute("donHangs", donHangs);
        model.addAttribute("page", "danh-sach-don-hang");
        return "donhang/danhsach"; // trỏ tới file donhang/list.html hoặc .jsp
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
