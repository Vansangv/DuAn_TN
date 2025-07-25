package com.example.demo.Offline.Controller;

import com.example.demo.Entity.NguoiDung;
import com.example.demo.Entity.SanPhamChiTiet;
import com.example.demo.Entity.YeuThichSanPham;
import com.example.demo.Offline.Repository.YeuThichSanPhamRepository;
import com.example.demo.PhanQuyen.BaseController;
import com.example.demo.Repository.NguoiDungRepository;
import com.example.demo.Repository.SanPhamChiTietRepository;
import com.example.demo.Service.NguoiDungService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class YeuThichSanPhamController extends BaseController {

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
    public ResponseEntity<Map<String, Object>> themYeuThich(@RequestParam("sanPhamChiTietId") Long sanPhamChiTietId, Principal principal) {
        Map<String, Object> response = new HashMap<>();

        Optional<NguoiDung> optionalNguoiDung = nguoiDungRepository.findByTenDangNhap(principal.getName());
        Optional<SanPhamChiTiet> optionalSanPhamChiTiet = sanPhamChiTietRepository.findById(sanPhamChiTietId);

        if (optionalNguoiDung.isPresent() && optionalSanPhamChiTiet.isPresent()) {
            NguoiDung nguoiDung = optionalNguoiDung.get();
            SanPhamChiTiet sanPhamChiTiet = optionalSanPhamChiTiet.get();

            boolean daTonTai = yeuThichSanPhamRepository.existsByNguoiDungAndSanPhamChiTiet(nguoiDung, sanPhamChiTiet);
            if (daTonTai) {
                long tongSoLuong = yeuThichSanPhamRepository.countByNguoiDung_Id(nguoiDung.getId());
                response.put("trangThai", "daCo");
                response.put("soLuong", tongSoLuong);
                return ResponseEntity.ok(response);
            }

            YeuThichSanPham yeuThich = new YeuThichSanPham();
            yeuThich.setNguoiDung(nguoiDung);
            yeuThich.setSanPhamChiTiet(sanPhamChiTiet);
            yeuThich.setNgayThem(LocalDateTime.now());
            yeuThichSanPhamRepository.save(yeuThich);

            long tongSoLuong = yeuThichSanPhamRepository.countByNguoiDung_Id(nguoiDung.getId());

            response.put("trangThai", "themMoi");
            response.put("soLuong", tongSoLuong);

            return ResponseEntity.ok(response);
        } else {
            response.put("trangThai", "loi");
            response.put("message", "Không tìm thấy người dùng hoặc sản phẩm");
            return ResponseEntity.badRequest().body(response);
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
