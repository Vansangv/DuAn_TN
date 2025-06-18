package com.example.demo.Online;

import com.example.demo.Entity.MaGiamGia;
import com.example.demo.Repository.MaGiamGiaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/ma-giam-gia")
public class MaGiamGiaController {

    @Autowired
    private MaGiamGiaRepository maGiamGiaRepository;

    @GetMapping
    public String getAllMaGiamGia(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model) {

        // Thêm Sort để đảm bảo các bản ghi mới hoặc sửa được đưa lên đầu
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("id")));  // Thay "id" bằng trường muốn sắp xếp

        Page<MaGiamGia> pageResult = maGiamGiaRepository.findByTrangThaiTrueAndMaContainingIgnoreCase(keyword, pageable);

        model.addAttribute("maGiamGiaList", pageResult.getContent());
        model.addAttribute("currentPage", pageResult.getNumber());
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("keyword", keyword);
        return "ma-giam-gia/list";
    }


    @GetMapping("/trangthai")
    public String getAllMaGiamGia_trangthai(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model) {

        // Thêm Sort để đảm bảo các bản ghi mới hoặc sửa được đưa lên đầu
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("id")));  // Thay "id" bằng trường muốn sắp xếp
        Page<MaGiamGia> pageResult = maGiamGiaRepository.findByTrangThaiFalseAndMaContainingIgnoreCase(keyword, pageable);

        model.addAttribute("maGiamGiaList1", pageResult.getContent());
        model.addAttribute("currentPage", pageResult.getNumber());
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("keyword", keyword);
        return "ma-giam-gia/trangthai";
    }


    // Hiển thị form tạo mã giảm giá mới
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("maGiamGia", new MaGiamGia());
        return "ma-giam-gia/create";
    }

    // Lưu mã giảm giá
    @PostMapping("/save")
    public String saveMaGiamGia(@ModelAttribute MaGiamGia maGiamGia) {
        maGiamGiaRepository.save(maGiamGia);
        return "redirect:/ma-giam-gia";
    }

    // Hiển thị form chỉnh sửa mã giảm giá
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        model.addAttribute("maGiamGia", maGiamGiaRepository.findById(id).orElse(new MaGiamGia()));
        return "ma-giam-gia/edit";
    }

    // Cập nhật mã giảm giá
    @PostMapping("/update")
    public String updateMaGiamGia(@ModelAttribute MaGiamGia maGiamGia) {
        maGiamGiaRepository.save(maGiamGia);
        return "redirect:/ma-giam-gia";
    }

    // Xóa mã giảm giá
    @GetMapping("/delete/{id}")
    public String deleteMaGiamGia(@PathVariable("id") Long id) {
        maGiamGiaRepository.deleteById(id);
        return "redirect:/ma-giam-gia";
    }


}