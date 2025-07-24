package com.example.demo.Online;


import com.example.demo.Entity.GioHang;
import com.example.demo.Repository.GioHangRepository;
import com.example.demo.Repository.NguoiDungRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/gio-hang-off")
public class GioHangController {

    @Autowired
    private GioHangRepository gioHangRepository;

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("gioHangs", gioHangRepository.findAll());
        model.addAttribute("gioHang", new GioHang());
        model.addAttribute("nguoiDungs", nguoiDungRepository.findAll());
        model.addAttribute("page", "gio-hang");
        return "giohang/list";
    }

    @GetMapping("/trangthai")
    public String list_trangthai(Model model) {
        model.addAttribute("gioHangs", gioHangRepository.findAll());
        model.addAttribute("gioHang", new GioHang());
        model.addAttribute("nguoiDungs", nguoiDungRepository.findAll());
        return "giohang/trangthai";
    }

    @PostMapping("/them")
    public String create(@ModelAttribute GioHang gioHang) {
        gioHang.setNgayTao(LocalDateTime.now());
        gioHang.setTrangThai(1); // 👈 mặc định trạng thái = 1
        gioHangRepository.save(gioHang);
        return "redirect:/gio-hang-off";
    }

    @GetMapping("/xoa/{id}")
    @Transactional
    public String delete(@PathVariable Long id) {
        gioHangRepository.deleteById(id);
        return "redirect:/gio-hang-off";
    }

}
