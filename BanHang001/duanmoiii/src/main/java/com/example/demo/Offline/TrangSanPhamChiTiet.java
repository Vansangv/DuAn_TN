package com.example.demo.Offline;


import com.example.demo.Entity.*;

import com.example.demo.Offline.DTO.KichCoDTO;
import com.example.demo.Offline.DTO.MauSacDTO;
import com.example.demo.Offline.Repository.OnlineChiTietDonHangRepository;
import com.example.demo.Offline.Repository.OnlineDanhGiaSanPhamRepository;
import com.example.demo.Offline.Repository.OnlineSanPhamChiTietRepository;
import com.example.demo.Repository.KichCoRepository;
import com.example.demo.Repository.MauSacRepository;
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
    @Autowired
    private MauSacRepository mauSacRepository;
    @Autowired
    private KichCoRepository kichCoRepository;
    @Autowired
    private OnlineChiTietDonHangRepository chiTietDonHangRepository;



    @GetMapping("/chi-tiet/{id}")
    public String hienThiChiTietSanPham(@PathVariable("id") Long id, Model model, Authentication authentication) {


        // Lấy người dùng
        if (authentication != null) {
            String username = authentication.getName();
            NguoiDung nguoiDung = nguoiDungService.findByTenDangNhap(username);
            model.addAttribute("tenNguoiDung", nguoiDung != null ? nguoiDung.getHoTen() : "Khách");
        }


        // Lấy thông tin sản phẩm
        SanPham sanPham = sanPhamRepository.findById(id).orElse(null);

        if (sanPham == null) {
            return "redirect:/trangchinh"; // Nếu sản phẩm không tồn tại, quay lại trang chủ
        }

        // Lấy chi tiết sản phẩm (màu sắc, kích cỡ)
        List<SanPhamChiTiet> chiTietSanPham = sanPhamChiTietRepository.findBySanPhamIdIn(List.of(id));
        Integer gia = chiTietSanPham.isEmpty() ? 0 : chiTietSanPham.get(0).getGia();

        // Lấy đánh giá
        List<DanhGiaSanPham> danhGiaList = danhGiaSanPhamRepository.findBySanPham_Id(id);
        double diemTrungBinh = danhGiaList.stream().mapToInt(DanhGiaSanPham::getDiem).average().orElse(0.0);

        int tongNhanXet = danhGiaList.size();

        // Tính tổng số lượng
        int tongSoLuong = chiTietSanPham.stream()
                .mapToInt(SanPhamChiTiet::getSoLuong)
                .sum();

        // Lấy chi tiết đầu tiên để xác định Kích Cỡ đang chọn
        SanPhamChiTiet chiTietDauTien = chiTietSanPham.isEmpty() ? null : chiTietSanPham.get(0);

        // Lấy toàn bộ kích cỡ từ DB
        List<KichCo> allKichCo = kichCoRepository.findAll();

        // Map sang DTO và đánh dấu selected nếu trùng với chi tiết sản phẩm đầu tiên
        List<KichCoDTO> danhSachKichCo = allKichCo.stream()
                .map(k -> {
                    KichCoDTO dto = new KichCoDTO();
                    dto.setId(k.getId());
                    dto.setTenKichCo(k.getTenKichCo());
                    dto.setSelected(chiTietDauTien != null && k.getId().equals(chiTietDauTien.getKichCo().getId()));
                    return dto;
                })
                .collect(Collectors.toList());

        model.addAttribute("danhSachKichCo", danhSachKichCo);


        List<MauSac> allMauSac = mauSacRepository.findAll();
        List<MauSacDTO> danhSachMauSac = allMauSac.stream()
                .map(m -> {
                    MauSacDTO dto = new MauSacDTO();
                    dto.setId(m.getId());
                    dto.setTenMau(m.getTenMau());
                    dto.setSelected(chiTietDauTien != null && m.getId().equals(chiTietDauTien.getMauSac().getId()));
                    return dto;
                })
                .collect(Collectors.toList());
        model.addAttribute("danhSachMauSac", danhSachMauSac);

        int soLuongDaBan = chiTietDonHangRepository.tongSoLuongDaBanTheoSanPham(id);
        model.addAttribute("soLuongDaBan", soLuongDaBan);

        // Truyền dữ liệu ra view
        model.addAttribute("sanPham", sanPham);
        model.addAttribute("gia", gia);
        model.addAttribute("chiTietSanPham", chiTietSanPham);
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
        // Nếu chưa đăng nhập, chuyển hướng về trang login
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        // Lấy tên đăng nhập từ Authentication
        String username = authentication.getName();

        // Lấy đối tượng người dùng từ tên đăng nhập
        NguoiDung nguoiDung = nguoiDungService.findByTenDangNhap(username);

        // Lấy sản phẩm cần đánh giá
        SanPham sanPham = sanPhamRepository.findById(sanPhamId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        // Tạo đối tượng đánh giá mới
        DanhGiaSanPham danhGia = new DanhGiaSanPham();
        danhGia.setNguoiDung(nguoiDung);
        danhGia.setSanPham(sanPham);
        danhGia.setDiem(diem);
        danhGia.setNhanXet(nhanXet);
        danhGia.setNgayDanhGia(LocalDateTime.now());

        // Lưu vào DB
        danhGiaSanPhamRepository.save(danhGia);

        // Chuyển về trang chi tiết sản phẩm
        return "redirect:/chi-tiet/" + sanPhamId;


    }

}
