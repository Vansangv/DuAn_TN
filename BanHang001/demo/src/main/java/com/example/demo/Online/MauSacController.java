package com.example.demo.Online;

import com.example.demo.Entity.MauSac;
import com.example.demo.Repository.MauSacRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/mau-sac") // ✅ Đây là base URL
public class MauSacController {

    @Autowired
    private MauSacRepository mauSacRepository;

    // Hiển thị danh sách màu sắc
    @GetMapping
    public String listMauSac(Model model) {
        List<MauSac> mauSacList = mauSacRepository.findAll();
        model.addAttribute("mauSacList", mauSacList);
        return "mausac/list"; // ✅ Giao diện: src/main/resources/templates/mausac/list.html
    }

    // Trang thêm màu sắc mới
    @GetMapping("/add")
    public String addMauSacForm(Model model) {
        model.addAttribute("mauSac", new MauSac());
        return "mausac/create"; // ✅ Giao diện: src/main/resources/templates/mausac/create.html
    }

    // Xử lý lưu màu sắc mới
    @PostMapping("/save")
    public String saveMauSac(@ModelAttribute("mauSac") MauSac mauSac) {
        mauSacRepository.save(mauSac);
        return "redirect:/mau-sac"; // ✅ Sửa tại đây: redirect đúng URL base
    }

    // Trang chỉnh sửa màu sắc
    @GetMapping("/edit/{id}")
    public String editMauSacForm(@PathVariable Long id, Model model) {
        MauSac mauSac = mauSacRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid id: " + id));
        model.addAttribute("mauSac", mauSac);
        return "mausac/edit";
    }

    // Xử lý cập nhật màu sắc
    @PostMapping("/update/{id}")
    public String updateMauSac(@PathVariable Long id, @ModelAttribute("mauSac") MauSac mauSac) {
        mauSac.setId(id);
        mauSacRepository.save(mauSac);
        return "redirect:/mau-sac"; // ✅ Sửa tại đây luôn nếu cần
    }

    // Xóa màu sắc
    @GetMapping("/delete/{id}")
    public String deleteMauSac(@PathVariable Long id) {
        mauSacRepository.deleteById(id);
        return "redirect:/mau-sac"; // ✅ Sửa tại đây luôn nếu cần
    }
}
