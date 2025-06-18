package com.example.demo.Online;
import com.example.demo.Entity.*;
import com.example.demo.Repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Controller
@RequestMapping("/san-pham-chi-tiet")
public class SanPhamChiTietController {

    @Autowired
    private SanPhamChiTietRepository sanPhamChiTietRepository;

    @Autowired
    private SanPhamRepository sanPhamRepository;

    @Autowired
    private MauSacRepository mauSacRepository;

    @Autowired
    private KichCoRepository kichCoRepository;


    @GetMapping
    public String getAllSanPhamChiTiet(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")); // Sắp xếp giảm dần theo id
        Page<SanPhamChiTiet> sanPhamChiTiets = sanPhamChiTietRepository
                .findBySanPham_TenSanPhamContainingIgnoreCase(keyword, pageable);

        model.addAttribute("sanPhamChiTiets", sanPhamChiTiets.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", sanPhamChiTiets.getTotalPages());
        model.addAttribute("keyword", keyword);

        return "sanphamchitiet/list";
    }


    // Hiển thị form thêm mới
    @GetMapping("/add")
    public String addSanPhamChiTietForm(Model model) {
        model.addAttribute("sanPhamChiTiet", new SanPhamChiTiet());
        model.addAttribute("sanPhams", sanPhamRepository.findAll());
        model.addAttribute("mauSacs", mauSacRepository.findAll());
        model.addAttribute("kichCos", kichCoRepository.findAll());
        return "sanphamchitiet/add";
    }

    // Xử lý thêm mới
    @PostMapping("/add")
    public String addSanPhamChiTiet(@ModelAttribute SanPhamChiTiet sanPhamChiTiet) {
        sanPhamChiTietRepository.save(sanPhamChiTiet);
        return "redirect:/san-pham-chi-tiet";
    }

    // Hiển thị form sửa
    @GetMapping("/edit/{id}")
    public String editSanPhamChiTietForm(@PathVariable Long id, Model model) {
        SanPhamChiTiet sanPhamChiTiet = sanPhamChiTietRepository.findById(id).orElseThrow();
        model.addAttribute("sanPhamChiTiet", sanPhamChiTiet);
        model.addAttribute("sanPhams", sanPhamRepository.findAll());
        model.addAttribute("mauSacs", mauSacRepository.findAll());
        model.addAttribute("kichCos", kichCoRepository.findAll());
        return "sanphamchitiet/edit";
    }

    // Xử lý sửa
    @PostMapping("/edit/{id}")
    public String editSanPhamChiTiet(@PathVariable Long id, @ModelAttribute SanPhamChiTiet sanPhamChiTiet) {
        sanPhamChiTiet.setId(id);
        sanPhamChiTietRepository.save(sanPhamChiTiet);
        return "redirect:/san-pham-chi-tiet";
    }

    // Xóa
    @GetMapping("/delete/{id}")
    public String deleteSanPhamChiTiet(@PathVariable Long id) {
        sanPhamChiTietRepository.deleteById(id);
        return "redirect:/san-pham-chi-tiet";
    }

    // Hiển thị chi tiết
    @GetMapping("/view/{id}")
    public String viewSanPhamChiTiet(@PathVariable Long id, Model model) {
        SanPhamChiTiet sanPhamChiTiet = sanPhamChiTietRepository.findById(id).orElseThrow();
        model.addAttribute("sanPhamChiTiet", sanPhamChiTiet);
        model.addAttribute("sanPhams", sanPhamRepository.findAll());
        model.addAttribute("mauSacs", mauSacRepository.findAll());
        model.addAttribute("kichCos", kichCoRepository.findAll());
        return "sanphamchitiet/view";
    }

}
