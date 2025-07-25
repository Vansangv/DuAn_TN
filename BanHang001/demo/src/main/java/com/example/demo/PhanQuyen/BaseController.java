package com.example.demo.PhanQuyen;
import com.example.demo.Entity.NguoiDung;
import com.example.demo.Offline.Repository.OnlineSanPhamTrongGioHangRepository;
import com.example.demo.Offline.Repository.YeuThichSanPhamRepository;
import com.example.demo.Service.NguoiDungService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.HashSet;
import java.util.Set;

@Controller
public class BaseController {


    @Autowired
    private NguoiDungService nguoiDungService;

    @Autowired
    private OnlineSanPhamTrongGioHangRepository sanPhamTrongGioHangRepository;

    @Autowired
    private YeuThichSanPhamRepository yeuThichSanPhamRepository;

    @ModelAttribute
    public void addCommonAttributes(Model model, Authentication authentication) {
        int soLuongTrongGio = 0;
        long soLuongYeuThich = 0;
        Set<Long> danhSachIdYeuThich = new HashSet<>();
        String tenNguoiDung = "Khách";

        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            NguoiDung nguoiDung = nguoiDungService.findByTenDangNhap(username);
            if (nguoiDung != null) {
                tenNguoiDung = nguoiDung.getHoTen();
                soLuongTrongGio = sanPhamTrongGioHangRepository.demSoLuongSanPhamTrongGio(nguoiDung.getId());
                soLuongYeuThich = yeuThichSanPhamRepository.countByNguoiDung_Id(nguoiDung.getId());
                danhSachIdYeuThich = yeuThichSanPhamRepository.findSanPhamChiTietIdsByNguoiDung_Id(nguoiDung.getId());
            }
        }

        model.addAttribute("tenNguoiDung", tenNguoiDung);
        model.addAttribute("soLuongTrongGio", soLuongTrongGio);
        model.addAttribute("soLuongYeuThich", soLuongYeuThich);
        model.addAttribute("danhSachIdYeuThich", danhSachIdYeuThich);
    }
}
