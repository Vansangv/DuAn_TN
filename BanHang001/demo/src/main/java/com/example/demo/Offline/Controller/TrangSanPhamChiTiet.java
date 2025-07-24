package com.example.demo.Offline.Controller;


import com.example.demo.Entity.*;

import com.example.demo.Offline.DTO.KichCoDTO;
import com.example.demo.Offline.DTO.MauSacDTO;
import com.example.demo.Offline.Repository.*;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


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

    @Autowired
    private OnlineSanPhamTrongGioHangRepository sanPhamTrongGioHangRepository;

    @Autowired
    private OnlineGioHangRepository gioHangRepository;

    @GetMapping("/chi-tiet/{id}")
    public String hienThiChiTietSanPham(@PathVariable("id") Long id,
                                        @RequestParam(value = "full", required = false, defaultValue = "false") boolean full,
                                        Model model, Authentication authentication) {


        int soLuongTrongGio = 0;
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            NguoiDung nguoiDung = nguoiDungService.findByTenDangNhap(username);

            if (nguoiDung != null) {
                model.addAttribute("tenNguoiDung", nguoiDung.getHoTen());
                soLuongTrongGio = sanPhamTrongGioHangRepository.demSoLuongSanPhamTrongGio(nguoiDung.getId());
            } else {
                model.addAttribute("tenNguoiDung", "Khách");
            }
        } else {
            model.addAttribute("tenNguoiDung", "Khách");
        }

        model.addAttribute("soLuongTrongGio", soLuongTrongGio);


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

        List<DanhGiaSanPham> hienThiDanhGiaList = full
                ? danhGiaList
                : danhGiaList.stream().limit(4).collect(Collectors.toList());

        // Truyền dữ liệu ra view
        model.addAttribute("sanPham", sanPham);
        model.addAttribute("gia", gia);
        model.addAttribute("chiTietSanPham", chiTietSanPham);
        model.addAttribute("tongSoLuong", tongSoLuong);  // Thêm số lượng tổng vào model
        model.addAttribute("danhGiaList", danhGiaList);
        model.addAttribute("diemTrungBinh", diemTrungBinh);
        model.addAttribute("tongNhanXet", tongNhanXet);

        // Chỉ add list cần hiển thị ra view!
        model.addAttribute("danhGiaList", hienThiDanhGiaList); // ✅ FIXED
        model.addAttribute("daHienThiHet", full || danhGiaList.size() <= 4); // ✅ FIXED


        return "BanHangOnline/chi-tiet-san-pham"; // Trả về trang chi tiết sản phẩm
    }


    @PostMapping("/danh-gia-san-pham")
    public String danhGiaSanPham(@RequestParam Long sanPhamId,
                                 @RequestParam int diem,
                                 @RequestParam String nhanXet,
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String username = authentication.getName();
        NguoiDung nguoiDung = nguoiDungService.findByTenDangNhap(username);

        if (nguoiDung == null) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng đăng nhập để đánh giá.");
            return "redirect:/chi-tiet/" + sanPhamId;
        }

        // ✅ Kiểm tra đã mua hàng chưa
        long soLuong = chiTietDonHangRepository.countDonMuaSanPham(nguoiDung.getId(), sanPhamId);
        if (soLuong <= 0) {
            redirectAttributes.addFlashAttribute("error", "Bạn cần mua sản phẩm trước khi đánh giá.");
            return "redirect:/chi-tiet/" + sanPhamId;
        }

        // ✅ Đã mua → Lưu đánh giá
        SanPham sanPham = sanPhamRepository.findById(sanPhamId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        DanhGiaSanPham danhGia = new DanhGiaSanPham();
        danhGia.setNguoiDung(nguoiDung);
        danhGia.setSanPham(sanPham);
        danhGia.setDiem(diem);
        danhGia.setNhanXet(nhanXet);
        danhGia.setNgayDanhGia(LocalDateTime.now());

        danhGiaSanPhamRepository.save(danhGia);

        redirectAttributes.addFlashAttribute("success", "Cảm ơn bạn đã đánh giá sản phẩm!");
        return "redirect:/chi-tiet/" + sanPhamId;
    }




    @PostMapping("/them-vao-gio-hang-chi-tiet")
    public String themVaoGioHangChiTiet(@RequestParam("sanPhamId") Long sanPhamId,
                                        @RequestParam("kichCoId") Long kichCoId,
                                        @RequestParam("mauSacId") Long mauSacId,
                                        Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login-online";
        }

        String username = authentication.getName();
        NguoiDung nguoiDung = nguoiDungService.findByTenDangNhap(username);
        if (nguoiDung == null) {
            return "redirect:/login-online";
        }

        // Tìm giỏ hàng trạng thái = 3
        GioHang gioHang = gioHangRepository.findByNguoiDung_IdAndTrangThai(nguoiDung.getId(), 3)
                .orElse(null);

        if (gioHang == null) {
            gioHang = new GioHang();
            gioHang.setNguoiDung(nguoiDung);
            gioHang.setNgayTao(LocalDateTime.now());
            gioHang.setTrangThai(3);
            gioHang = gioHangRepository.save(gioHang);
        }

        // Tìm sản phẩm chi tiết theo sản phẩm, màu và kích cỡ
        SanPhamChiTiet sanPhamChiTiet = sanPhamChiTietRepository
                .findBySanPham_IdAndMauSac_IdAndKichCo_Id(sanPhamId, mauSacId, kichCoId)
                .orElse(null);

        if (sanPhamChiTiet == null || sanPhamChiTiet.getSoLuong() <= 0) {
            return "redirect:/chi-tiet/" + sanPhamId;
        }

        // Kiểm tra đã có trong giỏ chưa
        SanPhamTrongGioHang spTrongGio = sanPhamTrongGioHangRepository
                .findByGioHangAndSanPhamChiTiet(gioHang, sanPhamChiTiet)
                .orElse(null);

        if (spTrongGio != null) {
            if (sanPhamChiTiet.getSoLuong() > 0) {
                spTrongGio.setSoLuong(spTrongGio.getSoLuong() + 1);
                sanPhamChiTiet.setSoLuong(sanPhamChiTiet.getSoLuong() - 1);
            }
        } else {
            spTrongGio = new SanPhamTrongGioHang();
            spTrongGio.setGioHang(gioHang);
            spTrongGio.setSanPhamChiTiet(sanPhamChiTiet);
            spTrongGio.setSoLuong(1);
            sanPhamChiTiet.setSoLuong(sanPhamChiTiet.getSoLuong() - 1);
        }

        sanPhamChiTietRepository.save(sanPhamChiTiet);
        sanPhamTrongGioHangRepository.save(spTrongGio);

        return "redirect:/chi-tiet/" + sanPhamId;
    }



}
