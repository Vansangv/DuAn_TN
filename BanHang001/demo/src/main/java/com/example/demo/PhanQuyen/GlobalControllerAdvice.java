package com.example.demo.PhanQuyen;

import com.example.demo.Entity.NguoiDung;
import com.example.demo.Service.NguoiDungService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {
    @Autowired
    private NguoiDungService nguoiDungService;

    @ModelAttribute
    public void addCommonAttributes(Model model, Authentication authentication) {
        if (authentication != null) {
            String username = authentication.getName();
            NguoiDung nguoiDung = nguoiDungService.findByTenDangNhap(username);
            model.addAttribute("tenNguoiDung", nguoiDung != null ? nguoiDung.getHoTen() : "Khách");
        } else {
            model.addAttribute("tenNguoiDung", "Khách");
        }
    }
}
