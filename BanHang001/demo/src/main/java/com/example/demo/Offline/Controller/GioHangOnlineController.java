package com.example.demo.Offline.Controller;



import com.example.demo.Entity.*;
import com.example.demo.Offline.Repository.*;
import com.example.demo.Repository.LichSuThanhToanRepository;
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
import java.util.Map;
import java.util.Optional;


@Controller
public class GioHangOnlineController {


    @Autowired
    private OnlineGioHangRepository gioHangRepository;

    @Autowired
    private OnlineSanPhamTrongGioHangRepository sanPhamTrongGioHangRepository;

    @Autowired
    private OnlineSanPhamChiTietRepository sanPhamChiTietRepository;

    @Autowired
    private OnlineNguoiDungRepository nguoiDungRepository; // Nếu có

    @Autowired
    private NguoiDungService nguoiDungService;

    @Autowired
    private OnlineDonHangRepository donHangRepository;

    @Autowired
    private OnlineChiTietDonHangRepository chiTietDonHangRepository;

    @Autowired
    private OnlineMaGiamGiaRepository maGiamGiaRepository;

    @Autowired
    private DiaChiGiaoHangRepository diaChiGiaoHangRepository;

    @Autowired
    private LichSuThanhToanRepository lichSuThanhToanRepository;

    @Autowired
    private OnlineDonHangRepository onlineDonHangRepository;

    @Autowired
    private VanChuyenRepository vanChuyenRepository;

    @Autowired
    private OnlineChiTietDonHangRepository onlineChiTietDonHangRepository;


    @PostMapping("/them-vao-gio-hang/{id}")
    public String themVaoGioHang(@PathVariable("id") Long sanPhamChiTietId,
                                 Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login-online";
        }

        String username = authentication.getName();
        NguoiDung nguoiDung = nguoiDungService.findByTenDangNhap(username);
        if (nguoiDung == null) {
            return "redirect:/login-online";
        }

        // ✅ Chỉ tìm giỏ hàng có trạng thái = 3
        GioHang gioHang = gioHangRepository.findByNguoiDung_IdAndTrangThai(nguoiDung.getId(), 3)
                .orElse(null);

        // Nếu không có, tạo mới giỏ hàng có trạng thái = 3
        if (gioHang == null) {
            gioHang = new GioHang();
            gioHang.setNguoiDung(nguoiDung);
            gioHang.setNgayTao(LocalDateTime.now());
            gioHang.setTrangThai(3);
            gioHang = gioHangRepository.save(gioHang);
        }

        // Tìm sản phẩm chi tiết
        SanPhamChiTiet sanPhamChiTiet = sanPhamChiTietRepository.findById(sanPhamChiTietId).orElse(null);
        if (sanPhamChiTiet == null || sanPhamChiTiet.getSoLuong() <= 0) {
            return "redirect:/online-home"; // hết hàng hoặc không tìm thấy
        }

        // Kiểm tra xem sản phẩm đã có trong giỏ hàng chưa
        SanPhamTrongGioHang sanPhamTrongGioHang = sanPhamTrongGioHangRepository
                .findByGioHangAndSanPhamChiTiet(gioHang, sanPhamChiTiet)
                .orElse(null);

        if (sanPhamTrongGioHang != null) {
            // Nếu đã tồn tại, tăng số lượng (chỉ nếu còn hàng)
            if (sanPhamChiTiet.getSoLuong() > 0) {
                sanPhamTrongGioHang.setSoLuong(sanPhamTrongGioHang.getSoLuong() + 1);
                sanPhamChiTiet.setSoLuong(sanPhamChiTiet.getSoLuong() - 1); // Trừ kho
            }
        } else {
            // Nếu chưa có, tạo mới
            sanPhamTrongGioHang = new SanPhamTrongGioHang();
            sanPhamTrongGioHang.setGioHang(gioHang);
            sanPhamTrongGioHang.setSanPhamChiTiet(sanPhamChiTiet);
            sanPhamTrongGioHang.setSoLuong(1);
            sanPhamChiTiet.setSoLuong(sanPhamChiTiet.getSoLuong() - 1); // Trừ kho
        }

        // Lưu lại vào DB
        sanPhamChiTietRepository.save(sanPhamChiTiet); // cập nhật tồn kho
        sanPhamTrongGioHangRepository.save(sanPhamTrongGioHang); // cập nhật chi tiết giỏ

        return "redirect:/online-home";
    }





    @GetMapping("/gio-hang")
    public String hienThiGioHang(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login-online";
        }

        String username = authentication.getName();
        NguoiDung nguoiDung = nguoiDungService.findByTenDangNhap(username);

        if (nguoiDung == null) {
            return "redirect:/login-online";
        }

        // Chỉ lấy giỏ hàng có trạng thái = 3
        Optional<GioHang> optionalGioHang = gioHangRepository.findByNguoiDungAndTrangThai(nguoiDung, 3);

        if (optionalGioHang.isEmpty()) {
            model.addAttribute("danhSachSanPhamTrongGio", List.of());
        } else {
            GioHang gioHang = optionalGioHang.get();
            List<SanPhamTrongGioHang> ds = sanPhamTrongGioHangRepository.findByGioHang(gioHang);
            model.addAttribute("danhSachSanPhamTrongGio", ds);
        }

        return "BanHangOnline/gio-hang";
    }



    @GetMapping("/gio-hang/huy/{id}")
    public String xoaSanPhamKhoiGio(@PathVariable("id") Long id, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login-online";
        }

        String username = authentication.getName();
        NguoiDung nguoiDung = nguoiDungService.findByTenDangNhap(username);

        if (nguoiDung == null) {
            return "redirect:/login-online";
        }

        // Chỉ lấy giỏ hàng có trạng thái = 3
        Optional<GioHang> optionalGioHang = gioHangRepository.findByNguoiDungAndTrangThai(nguoiDung, 3);

        if (optionalGioHang.isEmpty()) {
            return "redirect:/gio-hang";
        }

        GioHang gioHang = optionalGioHang.get();

        // Kiểm tra xem sản phẩm có trong giỏ hàng của user hiện tại không
        Optional<SanPhamTrongGioHang> optional = sanPhamTrongGioHangRepository.findById(id);

        if (optional.isPresent()) {
            SanPhamTrongGioHang sp = optional.get();

            if (sp.getGioHang().getId().equals(gioHang.getId())) {
                SanPhamChiTiet chiTiet = sp.getSanPhamChiTiet();
                chiTiet.setSoLuong(chiTiet.getSoLuong() + sp.getSoLuong());
                sanPhamChiTietRepository.save(chiTiet);

                sanPhamTrongGioHangRepository.deleteById(id);
            }
        }

        return "redirect:/gio-hang";
    }




    // Controller Method: /xac-nhan-don-hang
    @GetMapping("/xac-nhan-don-hang")
    public String xacNhanDonHang(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login-online";
        }

        String username = authentication.getName();
        NguoiDung nguoiDung = nguoiDungService.findByTenDangNhap(username);
        if (nguoiDung == null) {
            return "redirect:/login-online";
        }

        // Sửa: dùng findFirstByNguoiDungAndTrangThai với trạng thái = 3
        Optional<GioHang> gioHangOpt = gioHangRepository.findFirstByNguoiDungAndTrangThai(nguoiDung, 3);
        if (gioHangOpt.isEmpty()) {
            return "redirect:/gio-hang";
        }
        GioHang gioHang = gioHangOpt.get();

        List<SanPhamTrongGioHang> dsSanPham = sanPhamTrongGioHangRepository.findByGioHang(gioHang);
        if (dsSanPham.isEmpty()) {
            return "redirect:/gio-hang";
        }

        int tongTienHang = dsSanPham.stream()
                .mapToInt(sp -> sp.getSoLuong() * sp.getSanPhamChiTiet().getGia())
                .sum();

        int phiVanChuyen = 30000;

        DiaChiGiaoHang diaChiMacDinh = diaChiGiaoHangRepository.findByNguoiDungAndMacDinhTrue(nguoiDung);

        List<MaGiamGia> danhSachMaGiamGia = maGiamGiaRepository.findAllActiveAndValid();

        List<String> danhSachPhuongThucThanhToan = List.of(
                "Thanh toán khi nhận hàng",
                "Chuyển khoản ngân hàng",
                "Ví điện tử"
        );

        model.addAttribute("nguoiDung", nguoiDung);
        model.addAttribute("diaChiMacDinh", diaChiMacDinh);
        model.addAttribute("danhSachSanPham", dsSanPham);
        model.addAttribute("tongTienHang", tongTienHang);
        model.addAttribute("phiVanChuyen", phiVanChuyen);
        model.addAttribute("tongThanhToan", tongTienHang + phiVanChuyen);
        model.addAttribute("danhSachMaGiamGia", danhSachMaGiamGia);
        model.addAttribute("danhSachPhuongThucThanhToan", danhSachPhuongThucThanhToan);

        return "BanHangOnline/xac-nhan-don-hang";
    }


    // Controller Method: /mua-hang
    @PostMapping("/mua-hang")
    public String muaHang(@RequestParam(name = "maGiamGia", required = false) Long maGiamGiaId,
                          @RequestParam(name = "phuongThucThanhToan") String phuongThucThanhToan,
                          Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login-online";
        }

        String username = authentication.getName();
        NguoiDung nguoiDung = nguoiDungService.findByTenDangNhap(username);
        if (nguoiDung == null) {
            return "redirect:/login-online";
        }

        Optional<GioHang> gioHangOpt = gioHangRepository.findFirstByNguoiDungAndTrangThai(nguoiDung, 3);
        if (gioHangOpt.isEmpty()) {
            return "redirect:/gio-hang";
        }
        GioHang gioHang = gioHangOpt.get();


        List<SanPhamTrongGioHang> dsSanPham = sanPhamTrongGioHangRepository.findByGioHang(gioHang);
        if (dsSanPham.isEmpty()) {
            return "redirect:/gio-hang";
        }

        int tongTienHang = dsSanPham.stream()
                .mapToInt(sp -> sp.getSoLuong() * sp.getSanPhamChiTiet().getGia())
                .sum();

        int phiVanChuyen = 30000;
        int giamGia = 0;
        MaGiamGia maGiamGia = null;

        if (maGiamGiaId != null) {
            maGiamGia = maGiamGiaRepository.findById(maGiamGiaId).orElse(null);
            if (maGiamGia != null) {
                if ("phan_tram".equals(maGiamGia.getLoaiGiam())) {
                    giamGia = tongTienHang * maGiamGia.getPhanTramGiam() / 100;
                } else {
                    giamGia = maGiamGia.getSoTienGiam();
                }
            }
        }

        int tongThanhToan = tongTienHang + phiVanChuyen - giamGia;

        DiaChiGiaoHang diaChiMacDinh = diaChiGiaoHangRepository.findByNguoiDungAndMacDinhTrue(nguoiDung);

        DonHang donHang = DonHang.builder()
                .nguoiDung(nguoiDung)
                .diaChiGiaoHang(diaChiMacDinh)
                .maGiamGia(maGiamGia)
                .tongTien(tongThanhToan)
                .hinhThucMua("Online")
                .phuongThucThanhToan(phuongThucThanhToan)
                .trangThai("Chờ xác nhận")
                .ngayTao(LocalDateTime.now())
                .build();
        donHang = onlineDonHangRepository.save(donHang);


        // Ghi lịch sử thanh toán
        LichSuThanhToan lichSu = new LichSuThanhToan();
        lichSu.setNguoiDung(nguoiDung);
        lichSu.setDonHang(donHang);
        lichSu.setSoTien(tongThanhToan);
        lichSu.setPhuongThuc(phuongThucThanhToan);
        lichSu.setThoiGian(LocalDateTime.now());
        lichSuThanhToanRepository.save(lichSu);


        for (SanPhamTrongGioHang sp : dsSanPham) {
            ChiTietDonHang chiTiet = new ChiTietDonHang();
            chiTiet.setDonHang(donHang);
            chiTiet.setSanPhamChiTiet(sp.getSanPhamChiTiet());
            chiTiet.setSoLuong(sp.getSoLuong());
            chiTiet.setGiaDonVi(sp.getSanPhamChiTiet().getGia());
            onlineChiTietDonHangRepository.save(chiTiet);

            SanPhamChiTiet chiTietSanPham = sp.getSanPhamChiTiet();
            chiTietSanPham.setSoLuong(chiTietSanPham.getSoLuong() - sp.getSoLuong());
            sanPhamChiTietRepository.save(chiTietSanPham);
        }

        sanPhamTrongGioHangRepository.deleteAll(dsSanPham);

        // Tạo thông tin vận chuyển
        VanChuyen vanChuyen = new VanChuyen();
        vanChuyen.setDonHang(donHang);
        vanChuyen.setDiaChiGiao(diaChiMacDinh != null ? diaChiMacDinh.getDiaChi() : "");
        vanChuyen.setTrangThai("0"); // 0 = Đang vận chuyển
        vanChuyen.setNgayGiaoDuKien(LocalDateTime.now().plusDays(3));
        vanChuyenRepository.save(vanChuyen);

        return "redirect:/xac-nhan-don-hang";
    }








}
