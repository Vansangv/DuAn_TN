package com.example.demo.Online;

import com.example.demo.Entity.NguoiDung;
import com.example.demo.Entity.VaiTro;
import com.example.demo.Repository.NguoiDungRepository;
import com.example.demo.Repository.VaiTroRepository;
import com.example.demo.Security.AppConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
@RequestMapping("/nguoi-dung")
public class NguoiDungController {

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    @Autowired
    private VaiTroRepository vaiTroRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @GetMapping
    public String danhSachNguoiDung(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("ngayTao").descending());
        Page<NguoiDung> nguoiDungs;

        if (search != null && !search.isEmpty()) {
            nguoiDungs = nguoiDungRepository.findByTenDangNhapContainingIgnoreCaseOrHoTenContainingIgnoreCaseOrEmailContainingIgnoreCaseOrSoDienThoaiContainingIgnoreCase(
                    search, search, search, search, pageable);
        } else {
            nguoiDungs = nguoiDungRepository.findAll(pageable);
        }

        model.addAttribute("nguoiDungs", nguoiDungs);
        model.addAttribute("search", search);  // Thêm tham số search vào model
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", nguoiDungs.getTotalPages());
        model.addAttribute("page", "nguoi-dung");
        return "nguoidung/index";
    }




    // Trang thêm mới
    @GetMapping("/them")
    public String hienThiFormThem(Model model) {
        model.addAttribute("nguoiDung", new NguoiDung());
        model.addAttribute("pageGroup", "nguoi-dung");
        model.addAttribute("page", "nguoi-dung");
        return "nguoidung/them";
    }

    // Xử lý thêm mới
    @PostMapping("/them")
    public String xuLyThem(@ModelAttribute("nguoiDung") NguoiDung nguoiDung) {
        // Mã hóa mật khẩu
        nguoiDung.setMatKhau(passwordEncoder.encode(nguoiDung.getMatKhau()));
        nguoiDung.setNgayTao(LocalDateTime.now());

        // Gán vai trò mặc định là "Khách hàng"
        VaiTro vaiTroMacDinh = vaiTroRepository.findByTenVaiTro("Khách hàng")
                .orElseThrow(() -> new RuntimeException("Vai trò 'Khách hàng' không tồn tại"));
        nguoiDung.getVaiTros().add(vaiTroMacDinh);

        nguoiDungRepository.save(nguoiDung);
        return "redirect:/nguoi-dung";
    }


    // Trang cập nhật
    @GetMapping("/sua/{id}")
    public String hienThiFormSua(@PathVariable Long id, Model model) {
        Optional<NguoiDung> optional = nguoiDungRepository.findById(id);
        if (optional.isPresent()) {
            model.addAttribute("nguoiDung", optional.get());
            return "nguoidung/sua";
        }
        model.addAttribute("pageGroup", "nguoi-dung");
        model.addAttribute("page", "nguoi-dung");
        return "redirect:/nguoi-dung";
    }

    // Xử lý cập nhật
    @PostMapping("/sua")
    public String xuLySua(@ModelAttribute("nguoiDung") NguoiDung nguoiDung) {
        Optional<NguoiDung> existingOptional = nguoiDungRepository.findById(nguoiDung.getId());
        if (existingOptional.isPresent()) {
            NguoiDung existing = existingOptional.get();

            // Giữ nguyên các trường không được cập nhật
            nguoiDung.setMatKhau(existing.getMatKhau()); // giữ nguyên mật khẩu
            nguoiDung.setNgayTao(existing.getNgayTao()); // giữ nguyên ngày tạo

            // Cập nhật ngày cập nhật mới
            nguoiDung.setNgayCapNhat(LocalDateTime.now());

            // Lưu lại người dùng đã chỉnh sửa
            nguoiDungRepository.save(nguoiDung);
        }
        return "redirect:/nguoi-dung";
    }


    // Xoá
    @GetMapping("/xoa/{id}")
    public String xoaNguoiDung(@PathVariable Long id) {
        nguoiDungRepository.deleteById(id);
        return "redirect:/nguoi-dung";
    }
}