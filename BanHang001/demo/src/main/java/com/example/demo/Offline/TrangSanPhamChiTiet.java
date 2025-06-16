package com.example.demo.Offline;


import com.example.demo.Entity.DanhGiaSanPham;
import com.example.demo.Entity.NguoiDung;
import com.example.demo.Entity.SanPham;
import com.example.demo.Entity.SanPhamChiTiet;

import com.example.demo.Offline.Repository.OnlineDanhGiaSanPhamRepository;
import com.example.demo.Offline.Repository.OnlineSanPhamChiTietRepository;
import com.example.demo.Repository.SanPhamRepository;
import com.example.demo.Service.NguoiDungService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class TrangSanPhamChiTiet {

    @Autowired
    private SanPhamRepository sanPhamRepository;
    @Autowired
    private OnlineSanPhamChiTietRepository sanPhamChiTietRepository;
    @Autowired
    private OnlineDanhGiaSanPhamRepository danhGiaSanPhamRepository;
    @Autowired
    private NguoiDungService nguoiDungService;


    @GetMapping("/trangchinh")
    public String hienThiTrangChu(Model model) {
        // Lấy danh sách sản phẩm nổi bật từ repository
        List<SanPham> danhSachSanPham = sanPhamRepository.findSanPhamNoiBat();
        model.addAttribute("danhSachSanPham", danhSachSanPham);
        return "index";
    }

    @GetMapping("/chi-tiet/{id}")
    public String hienThiChiTietSanPham(@PathVariable("id") Long id, Model model) {
        // Lấy thông tin sản phẩm
        SanPham sanPham = sanPhamRepository.findById(id).orElse(null);

        if (sanPham == null) {
            return "redirect:/trangchinh"; // Nếu sản phẩm không tồn tại, quay lại trang chủ
        }

        // Lấy chi tiết sản phẩm (màu sắc, kích cỡ)
        List<SanPhamChiTiet> chiTietSanPham = sanPhamChiTietRepository.findBySanPhamIdIn(List.of(id));
        // Lấy đánh giá
        List<DanhGiaSanPham> danhGiaList = danhGiaSanPhamRepository.findBySanPham_Id(id);
        double diemTrungBinh = danhGiaList.stream().mapToInt(DanhGiaSanPham::getDiem).average().orElse(0.0);

        int tongNhanXet = danhGiaList.size();

        // Tính tổng số lượng
        int tongSoLuong = chiTietSanPham.stream()
                .mapToInt(SanPhamChiTiet::getSoLuong)
                .sum();

        // Tách danh sách màu sắc không trùng
        List<String> danhSachMauSac = chiTietSanPham.stream()
                .map(ct -> ct.getMauSac().getTenMau())
                .distinct()
                .collect(Collectors.toList());

        // Tách danh sách kích cỡ không trùng
        List<String> danhSachKichCo = chiTietSanPham.stream()
                .map(ct -> ct.getKichCo().getTenKichCo())
                .distinct()
                .collect(Collectors.toList());

        // 👉 Lấy giá đầu tiên (hoặc mặc định = 0 nếu không có)
        Integer gia = chiTietSanPham.isEmpty() ? 0 : chiTietSanPham.get(0).getGia();

        // Truyền dữ liệu ra view
        model.addAttribute("sanPham", sanPham);
        model.addAttribute("gia", gia);
        model.addAttribute("chiTietSanPham", chiTietSanPham);
        model.addAttribute("danhSachMauSac", danhSachMauSac);
        model.addAttribute("danhSachKichCo", danhSachKichCo);
        model.addAttribute("tongSoLuong", tongSoLuong);  // Thêm số lượng tổng vào model
        model.addAttribute("danhGiaList", danhGiaList);
        model.addAttribute("diemTrungBinh", diemTrungBinh);
        model.addAttribute("tongNhanXet", tongNhanXet);
        return "BanHangOnline/chi-tiet-san-pham"; // Trả về trang chi tiết sản phẩm
    }



    @PostMapping("/danh-gia-san-pham")
    public String danhGiaSanPham(@RequestParam Long sanPhamId,
                                 @RequestParam int diem,
                                 @RequestParam String nhanXet,
                                 Authentication authentication) {
        if (authentication == null) return "redirect:/login";

        String username = authentication.getName();
        NguoiDung nguoiDung = nguoiDungService.findByTenDangNhap(username);
        SanPham sanPham = sanPhamRepository.findById(sanPhamId).orElse(null);

        if (nguoiDung != null && sanPham != null) {
            DanhGiaSanPham danhGia = new DanhGiaSanPham();
            danhGia.setNguoiDung(nguoiDung);
            danhGia.setSanPham(sanPham);
            danhGia.setDiem(diem);
            danhGia.setNhanXet(nhanXet);
            danhGia.setNgayDanhGia(LocalDateTime.now());
            danhGiaSanPhamRepository.save(danhGia);
        }

        return "redirect:/chi-tiet/" + sanPhamId;
    }

}
