package com.example.demo.Online;
import jakarta.transaction.Transactional;
import com.example.demo.Entity.NguoiDung;
import com.example.demo.Offline.Repository.YeuThichRepository;
import com.example.demo.Offline.Repository.YeuThichView;
import com.example.demo.Repository.NguoiDungRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Optional;

@Controller
public class YeuThichController {

    @Autowired
    private YeuThichRepository yeuThichRepository;

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    @GetMapping("/yeu-thich")
    public String hienThiDanhSachYeuThich(Model model, Authentication authentication) {
        if (authentication != null) {
            String tenDangNhap = authentication.getName();

            Optional<NguoiDung> optionalNguoiDung = nguoiDungRepository.findByTenDangNhap(tenDangNhap);
            NguoiDung nguoiDung = optionalNguoiDung.orElse(null);
            if (nguoiDung != null) {
                List<YeuThichView> danhSachYeuThich = yeuThichRepository.findAllByNguoiDungId(nguoiDung.getId().intValue());
                model.addAttribute("danhSachYeuThich", danhSachYeuThich);
            }
        }
        return "BanHangOnline/yeuthich";
    }


    @PostMapping("/yeu-thich/xoa/{id}")
    @Transactional // BẮT BUỘC để cho phép gọi delete
    public String xoaYeuThich(@PathVariable("id") Integer sanPhamId, Authentication authentication) {
        if (authentication != null) {
            String tenDangNhap = authentication.getName();
            Optional<NguoiDung> optional = nguoiDungRepository.findByTenDangNhap(tenDangNhap);
            optional.ifPresent(nguoiDung -> {
                yeuThichRepository.deleteByNguoiDungIdAndSanPhamId(nguoiDung.getId().intValue(), sanPhamId);
            });
        }
        return "redirect:/yeu-thich";


    }}

