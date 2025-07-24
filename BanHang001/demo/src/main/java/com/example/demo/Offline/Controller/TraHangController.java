package com.example.demo.Offline.Controller;

import com.example.demo.Entity.DonHang;
import com.example.demo.Entity.NguoiDung;
import com.example.demo.Entity.TraHang;
import com.example.demo.Offline.Repository.OnlineDonHangRepository;
import com.example.demo.Offline.Repository.TraHangRepository;
import com.example.demo.Service.NguoiDungService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/tra-hang")
public class TraHangController {


    @Autowired
    private OnlineDonHangRepository donHangRepository;
    @Autowired
    private TraHangRepository traHangRepository;
    @Autowired
    private NguoiDungService nguoiDungService;

    @GetMapping
    public String danhSachDonHang(Model model, Authentication authentication) {
        String username = authentication.getName();
        NguoiDung nguoiDung = nguoiDungService.findByTenDangNhap(username);
        //List<DonHang> donHangs = donHangRepository.findByNguoiDung(nguoiDung);
        // Chỉ lấy đơn hàng trạng thái "Đã giao"
        List<DonHang> donHangs = donHangRepository.findByNguoiDungAndTrangThaiIgnoreCase(nguoiDung, "Đã giao");
        model.addAttribute("donHangs", donHangs);
        return "BanHangOnline/danh-sach-don-hang";
    }

    // Hiển thị form trả hàng
    @GetMapping("/yeu-cau/{donHangId}")
    public String hienThiFormTraHang(@PathVariable Long donHangId, Model model) {
        model.addAttribute("donHangId", donHangId);
        model.addAttribute("traHang", new TraHang());
        return "BanHangOnline/tra-hang-form";
    }

    // Xử lý gửi yêu cầu trả hàng
    @PostMapping("/yeu-cau")
    public String yeuCauTraHang(@RequestParam("donHangId") Long donHangId,
                                @RequestParam("lyDo") String lyDo) {
        DonHang donHang = donHangRepository.findById(donHangId).orElse(null);
        if (donHang != null) {
            TraHang traHang = new TraHang();
            traHang.setDonHang(donHang);
            traHang.setLyDo(lyDo);
            traHang.setNgayTra(LocalDateTime.now());
            traHang.setTrangThai("Chờ xử lý");
            traHangRepository.save(traHang);
        }
        return "redirect:/tra-hang?success";
    }





}
