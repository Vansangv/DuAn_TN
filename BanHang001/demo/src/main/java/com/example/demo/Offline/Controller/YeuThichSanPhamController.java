package com.example.demo.Offline.Controller;

import com.example.demo.Entity.NguoiDung;
import com.example.demo.Entity.SanPhamChiTiet;
import com.example.demo.Entity.YeuThichSanPham;
import com.example.demo.Offline.Repository.YeuThichSanPhamRepository;
import com.example.demo.Repository.NguoiDungRepository;
import com.example.demo.Repository.SanPhamChiTietRepository;
import com.example.demo.Service.NguoiDungService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@Controller
public class YeuThichSanPhamController {

    private final NguoiDungRepository nguoiDungRepository;
    private final SanPhamChiTietRepository sanPhamChiTietRepository;
    private final YeuThichSanPhamRepository yeuThichSanPhamRepository;
    private final NguoiDungService nguoiDungService;

    public YeuThichSanPhamController(NguoiDungRepository nguoiDungRepository, SanPhamChiTietRepository sanPhamChiTietRepository, YeuThichSanPhamRepository yeuThichSanPhamRepository, NguoiDungService nguoiDungService) {
        this.nguoiDungRepository = nguoiDungRepository;
        this.sanPhamChiTietRepository = sanPhamChiTietRepository;
        this.yeuThichSanPhamRepository = yeuThichSanPhamRepository;
        this.nguoiDungService = nguoiDungService;
    }

    @PostMapping("/yeu-thich/them")
    @ResponseBody
    public ResponseEntity<?> themYeuThich(@RequestParam("sanPhamChiTietId") Long sanPhamChiTietId, Principal principal) {
        Optional<NguoiDung> optionalNguoiDung = nguoiDungRepository.findByTenDangNhap(principal.getName());
        Optional<SanPhamChiTiet> optionalSanPhamChiTiet = sanPhamChiTietRepository.findById(sanPhamChiTietId);

        if (optionalNguoiDung.isPresent() && optionalSanPhamChiTiet.isPresent()) {
            NguoiDung nguoiDung = optionalNguoiDung.get();
            SanPhamChiTiet sanPhamChiTiet = optionalSanPhamChiTiet.get();

            // Kiểm tra đã tồn tại chưa (không bắt buộc)
            boolean daTonTai = yeuThichSanPhamRepository.existsByNguoiDungAndSanPhamChiTiet(nguoiDung, sanPhamChiTiet);
            if (daTonTai) {
                return ResponseEntity.ok("Đã có trong danh sách yêu thích");
            }

            YeuThichSanPham yeuThich = new YeuThichSanPham();
            yeuThich.setNguoiDung(nguoiDung);
            yeuThich.setSanPhamChiTiet(sanPhamChiTiet);
            yeuThich.setNgayThem(LocalDateTime.now());

            yeuThichSanPhamRepository.save(yeuThich);

            return ResponseEntity.ok("Đã thêm vào yêu thích");
        } else {
            return ResponseEntity.badRequest().body("Không tìm thấy người dùng hoặc sản phẩm");
        }
    }


    @GetMapping("/yeu-thich")
    public String danhSachYeuThich(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            NguoiDung nguoiDung = nguoiDungService.findByTenDangNhap(username);

            if (nguoiDung != null) {
                List<YeuThichSanPham> danhSachYeuThich = yeuThichSanPhamRepository.findByNguoiDung_Id(nguoiDung.getId());
                model.addAttribute("danhSachYeuThich", danhSachYeuThich);
            }
        } else {
            model.addAttribute("danhSachYeuThich", List.of());
        }

        return "yeuthich/danh-sach";
    }

    @PostMapping("/yeu-thich/xoa/{id}")
    public String xoaYeuThich(@PathVariable Long id) {
        yeuThichSanPhamRepository.deleteById(id);
        return "redirect:/yeu-thich";
    }

}
