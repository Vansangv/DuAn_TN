package com.example.demo.Online;


import com.example.demo.Entity.LogHeThong;
import com.example.demo.Entity.NguoiDung;
import com.example.demo.Entity.PhieuNhap;
//import com.example.demo.PhanQuyen.CustomUserDetails;
import com.example.demo.Repository.LogHeThongRepository;
import com.example.demo.Repository.NguoiDungRepository;
import com.example.demo.Repository.PhieuNhapRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/phieu-nhap")
public class PhieuNhapController {

    @Autowired
    private PhieuNhapRepository phieuNhapRepo;

    @Autowired
    private NguoiDungRepository nguoiDungRepo;

    @Autowired
    private LogHeThongRepository logHeThongRepo;

//    private Long getNguoiDungId() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
//            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
//            return userDetails.getId();
//        }
//        return null;
//    }



    private String getClientIp() {
        // Mô phỏng lấy IP từ request
        return "127.0.0.1";
    }

    // Hàm ghi log trực tiếp
    private void ghiLog(Long nguoiDungId, String hanhDong, String ip) {
        // Lấy đối tượng NguoiDung từ repository theo ID
        NguoiDung nguoiDung = nguoiDungRepo.findById(nguoiDungId).orElse(null);

        // Kiểm tra xem người dùng có tồn tại không
        if (nguoiDung != null) {
            LogHeThong log = new LogHeThong();
            log.setNguoiDung(nguoiDung);  // Gán đối tượng NguoiDung
            log.setHanhDong(hanhDong);
            log.setThoiGian(LocalDateTime.now());
            log.setIp(ip);

            logHeThongRepo.save(log);  // Lưu log vào cơ sở dữ liệu
        } else {
            // Xử lý nếu không tìm thấy người dùng
            System.out.println("Người dùng không tồn tại.");
        }
    }


    @GetMapping
    public String list(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size,
            @RequestParam(value = "search", defaultValue = "") String search,
            Model model
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PhieuNhap> phieuNhaps;

        if (search.isEmpty()) {
            // Nếu không có tìm kiếm, lấy tất cả phiếu nhập với phân trang
            phieuNhaps = phieuNhapRepo.findAll(pageable);
        } else {
            // Nếu có tìm kiếm, thực hiện tìm kiếm theo tên người nhập hoặc ghi chú
            phieuNhaps = phieuNhapRepo.findByNguoiNhap_HoTenContainingOrGhiChuContaining(search, search, pageable);
        }

        //ghiLog(getNguoiDungId(), "Xem danh sách phiếu nhập", getClientIp());


        model.addAttribute("phieuNhaps", phieuNhaps);
        model.addAttribute("nguoiDungs", nguoiDungRepo.findAll());
        model.addAttribute("search", search);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", phieuNhaps.getTotalPages());
        model.addAttribute("totalItems", phieuNhaps.getTotalElements());

        return "phieunhap/list";
    }


    @GetMapping("/them")
    public String showForm(Model model) {
        model.addAttribute("phieuNhap", new PhieuNhap());
        model.addAttribute("nguoiDungs", nguoiDungRepo.findAll());
        return "phieunhap/form"; // <-- KHÔNG có dấu gạch ngang (theo folder)
    }

    @PostMapping("/luu")
    public String save(@ModelAttribute PhieuNhap phieuNhap) {
        if (phieuNhap.getNgayNhap() == null) {
            phieuNhap.setNgayNhap(LocalDateTime.now());
        }
        phieuNhapRepo.save(phieuNhap);

        // Ghi log khi thêm/sửa phiếu nhập
        //ghiLog(getNguoiDungId(), "Thêm phiếu nhập", getClientIp());
        return "redirect:/phieu-nhap";
    }

    @GetMapping("/xoa/{id}")
    public String delete(@PathVariable Long id) {
        phieuNhapRepo.deleteById(id);

        // Ghi log khi xoá phiếu nhập
       // ghiLog(getNguoiDungId(), "Xoá phiếu nhập ID: " + id, getClientIp());

        return "redirect:/phieu-nhap";
    }

    @GetMapping("/chi-tiet/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("phieuNhap", phieuNhapRepo.findById(id).orElse(null));
        // Ghi log khi xem chi tiết phiếu nhập
       // ghiLog(getNguoiDungId(), "Xem chi tiết phiếu nhập ID: " + id, getClientIp());
        return "phieunhap/detail";
    }
}