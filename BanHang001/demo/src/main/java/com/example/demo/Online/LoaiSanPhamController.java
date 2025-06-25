package com.example.demo.Online;
import com.example.demo.Entity.LoaiSanPham;
import com.example.demo.Entity.LogHeThong;
import com.example.demo.Entity.NguoiDung;
import com.example.demo.PhanQuyen.CustomUserDetails;
import com.example.demo.Repository.LoaiSanPhamRepository;
import com.example.demo.Repository.LogHeThongRepository;
import com.example.demo.Repository.NguoiDungRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @Autowired
    private NguoiDungRepository nguoiDungRepo;

    @Autowired
    private LogHeThongRepository logHeThongRepo;


    private Long getNguoiDungId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            return userDetails.getId();
        }
        return null;
    }


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
        Long nguoiDungId = getNguoiDungId();
        String ip = getClientIp();

        if (nguoiDungId != null) {
            if (loai.getId() == null) {
                loai.setNgayTao(LocalDateTime.now());
                repository.save(loai);
                ghiLog(nguoiDungId, "Thêm mới loại sản phẩm: " + loai.getTenLoai(), ip);
            } else {
                repository.save(loai);
                ghiLog(nguoiDungId, "Cập nhật loại sản phẩm ID: " + loai.getId(), ip);
            }
        } else {
            System.out.println("Không xác định được người dùng để ghi log");
        }

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
        Long nguoiDungId = getNguoiDungId();
        String ip = getClientIp();

        repository.deleteById(id);
        ghiLog(nguoiDungId, "Xóa loại sản phẩm ID: " + id, ip);

        return "redirect:/loai-san-pham";
    }


}
