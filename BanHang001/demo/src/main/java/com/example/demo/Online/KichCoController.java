package com.example.demo.Online;

import com.example.demo.Entity.KichCo;
import com.example.demo.Repository.KichCoRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/kich-co")
public class KichCoController {

    @Autowired
    private KichCoRepository kichCoRepository;

    // Hiển thị danh sách kích cỡ
    @GetMapping
    public String listKichCo(Model model) {
        List<KichCo> list = kichCoRepository.findAll();
        model.addAttribute("kichCoList", list);
        return "kichco/list";  // Tương tự bạn tạo view như mã giảm giá
    }

    // Form thêm kích cỡ mới
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("kichCo", new KichCo());
        return "kichco/create";
    }

    // Xử lý lưu kích cỡ mới
    @PostMapping("/save")
    public String saveKichCo(@Valid @ModelAttribute("kichCo") KichCo kichCo, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "kichco/add";
        }
        kichCoRepository.save(kichCo);
        return "redirect:/kich-co";
    }

    // Form sửa kích cỡ theo id
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Optional<KichCo> kichCoOpt = kichCoRepository.findById(id);
        if (kichCoOpt.isEmpty()) {
            // Nếu không tìm thấy thì redirect về list
            return "redirect:/kich-co";
        }
        model.addAttribute("kichCo", kichCoOpt.get());
        return "kichco/edit";
    }

    // Xử lý lưu chỉnh sửa
    @PostMapping("/update/{id}")
    public String updateKichCo(@PathVariable("id") Long id, @Valid @ModelAttribute("kichCo") KichCo kichCo, BindingResult result) {
        if (result.hasErrors()) {
            return "kichco/edit";
        }
        kichCo.setId(id);
        kichCoRepository.save(kichCo);
        return "redirect:/kich-co";
    }

    // Xóa kích cỡ
    @GetMapping("/delete/{id}")
    public String deleteKichCo(@PathVariable("id") Long id) {
        kichCoRepository.deleteById(id);
        return "redirect:/kich-co";
    }

}