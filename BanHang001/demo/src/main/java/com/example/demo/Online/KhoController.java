package com.example.demo.Online;


import com.example.demo.Entity.Kho;
import com.example.demo.Entity.SanPhamChiTiet;
import com.example.demo.Repository.KhoRepository;

import com.example.demo.Repository.SanPhamChiTietRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.*;


@Controller
@RequestMapping("/kho")
public class KhoController {

    @Autowired
    private KhoRepository khoRepository;

    @Autowired
    private SanPhamChiTietRepository sanPhamChiTietRepository;


    @GetMapping("/create")
    public String formThemKho(Model model) {
        List<SanPhamChiTiet> listSanPhamChiTiet = sanPhamChiTietRepository.findBySoLuongGreaterThan(0);

        model.addAttribute("kho", new Kho());
        model.addAttribute("listSanPhamChiTiet", listSanPhamChiTiet);
        return "kho/form";
    }


    // Xử lý submit form thêm kho, trừ số lượng sản phẩm chi tiết
    @PostMapping("/store")
    @Transactional
    public String luuKho(@ModelAttribute Kho kho, Model model) {
        SanPhamChiTiet spct = sanPhamChiTietRepository.findById(kho.getSanPhamChiTiet().getId())
                .orElse(null);

        if (spct == null) {
            model.addAttribute("error", "Sản phẩm chi tiết không tồn tại");
            model.addAttribute("listSanPhamChiTiet", sanPhamChiTietRepository.findAll());
            return "kho/form";
        }

        if (spct.getSoLuong() < kho.getSoLuong()) {
            model.addAttribute("error", "Số lượng trong sản phẩm chi tiết không đủ");
            model.addAttribute("listSanPhamChiTiet", sanPhamChiTietRepository.findAll());
            return "kho/form";
        }

        spct.setSoLuong(spct.getSoLuong() - kho.getSoLuong());
        sanPhamChiTietRepository.save(spct);

        khoRepository.save(kho);

        return "redirect:/kho/list?success";
    }

    // Danh sách Kho (get all)
    @GetMapping("/list")
    public String danhSachKho(Model model) {
        List<Kho> listKho = khoRepository.findAll();
        model.addAttribute("listKho", listKho);
        return "kho/list";
    }


}
