package com.example.demo.Online;
import com.example.demo.Entity.LoaiSanPham;
import com.example.demo.Repository.LoaiSanPhamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
@RequestMapping("/loai-san-pham")
public class LoaiSanPhamController {
    @Autowired
    private LoaiSanPhamRepository repository;

    // Danh sách
    @GetMapping
    public String list(Model model) {
        model.addAttribute("list", repository.findAll());
        return "loaisanpham/list";
    }

    // Form thêm mới
    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("loai", new LoaiSanPham());
        return "loaisanpham/form";
    }

    // Lưu mới hoặc cập nhật
    @PostMapping("/save")
    public String save(@ModelAttribute LoaiSanPham loai) {
        if (loai.getId() == null) {
            loai.setNgayTao(LocalDateTime.now());
        }
        repository.save(loai);
        return "redirect:/loai-san-pham";
    }

    // Sửa
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        Optional<LoaiSanPham> optional = repository.findById(id);
        optional.ifPresent(loai -> model.addAttribute("loai", loai));
        return "loaisanpham/form";
    }

    // Xóa
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        repository.deleteById(id);
        return "redirect:/loai-san-pham";
    }
}
