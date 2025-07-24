package com.example.demo.Online;

import com.example.demo.Entity.*;
import com.example.demo.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.*;


@Controller
@RequestMapping("/gio-hang")
public class BanHangController {


    @Autowired
    private GioHangRepository gioHangRepository;
    @Autowired
    private NguoiDungRepository nguoiDungRepository;
    @Autowired
    private SanPhamTrongGioHangRepository sanPhamTrongGioHangRepository;
    @Autowired
    private SanPhamChiTietRepository sanPhamChiTietRepository;
    @Autowired
    private DonHangRepository donHangRepository;
    @Autowired
    private ChiTietDonHangRepository chiTietDonHangRepository;
    @Autowired
    private LichSuThanhToanRepository lichSuThanhToanRepository;
    @Autowired
    private MaGiamGiaRepository maGiamGiaRepository;

    // Giao diện chọn người dùng ban đầu
    @GetMapping("/chon-nguoi-dung")
    public String chonNguoiDung(Model model) {
        List<NguoiDung> danhSachNguoiDung = nguoiDungRepository.findAll();
        model.addAttribute("danhSachNguoiDung", danhSachNguoiDung);
        return "banhang/gio_hang";
    }

    // Xác nhận người dùng và hiển thị danh sách giỏ hàng trạng thái = 0
    @PostMapping("/xac-nhan")
    public String xacNhanNguoiDung(@RequestParam(value = "nguoiDungId", required = false) Long nguoiDungId,
                                   RedirectAttributes redirectAttributes) {
        NguoiDung nguoiDung;

        if (nguoiDungId == null || nguoiDungId == 0) {
            // Tìm hoặc tạo mới "Khách lẻ"
            nguoiDung = nguoiDungRepository.findByHoTen("Khách lẻ");
            if (nguoiDung == null) {
                NguoiDung khachLe = new NguoiDung();
                khachLe.setHoTen("Khách lẻ");
                khachLe.setEmail("khachle@example.com");
                khachLe.setSoDienThoai("0000000000");
                khachLe.setTenDangNhap("khachle" + System.currentTimeMillis());
                khachLe.setMatKhau("123456");
                nguoiDungRepository.save(khachLe);
                nguoiDung = khachLe;
            }
        } else {
            nguoiDung = nguoiDungRepository.findById(nguoiDungId).orElse(null);
        }

        if (nguoiDung != null) {
            List<GioHang> danhSachGioHang = gioHangRepository.findByNguoiDungIdAndTrangThai(nguoiDung.getId(), (byte) 1);
            if (!danhSachGioHang.isEmpty()) {
                // Lấy giỏ hàng mới nhất
                GioHang gioHangMoiNhat = danhSachGioHang.stream()
                        .max(Comparator.comparing(GioHang::getNgayTao))
                        .orElse(danhSachGioHang.get(0));

                return "redirect:/gio-hang/xem-san-pham?gioHangId=" + gioHangMoiNhat.getId()
                        + "&nguoiDungId=" + nguoiDung.getId()
                        + "&page=0&size=5";
            } else {
                // Nếu chưa có giỏ hàng => tạo mới rồi redirect
                GioHang gioHangMoi = new GioHang();
                gioHangMoi.setNguoiDung(nguoiDung);
                gioHangMoi.setNgayTao(LocalDateTime.now());
                gioHangMoi.setTrangThai((byte) 1); // Trạng thái "đang chờ"
                gioHangRepository.save(gioHangMoi);

                return "redirect:/gio-hang/xem-san-pham?gioHangId=" + gioHangMoi.getId()
                        + "&nguoiDungId=" + nguoiDung.getId()
                        + "&page=0&size=5";
            }
        }

        redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy người dùng.");
        return "redirect:/gio-hang/chon-nguoi-dung";
    }


    // Tạo giỏ hàng khi nhấn "Tạo giỏ hàng"
    @PostMapping("/tao")
    public String taoGioHangVaHienThi(@RequestParam("nguoiDungId") Long nguoiDungId, Model model) {
        NguoiDung nguoiDung = nguoiDungRepository.findById(nguoiDungId).orElse(null);
        if (nguoiDung != null) {
            // Lấy danh sách giỏ hàng trạng thái = 1
            List<GioHang> danhSachGioHangTrangThai1 = gioHangRepository.findByNguoiDungIdAndTrangThai(nguoiDungId, (byte) 1);

            if (danhSachGioHangTrangThai1.size() >= 3) {
                model.addAttribute("errorMessage", "Người dùng đã có tối đa 3 giỏ hàng với trạng thái 'đang chờ'!");
            } else {
                // Tạo giỏ hàng mới và thêm vào danh sách
                GioHang gioHang = new GioHang();
                gioHang.setNguoiDung(nguoiDung);
                gioHang.setNgayTao(LocalDateTime.now());
                gioHang.setTrangThai((byte) 1);  // Trạng thái "đang chờ"
                gioHangRepository.save(gioHang);

                // Cập nhật danh sách giỏ hàng trạng thái 1
                danhSachGioHangTrangThai1 = gioHangRepository.findByNguoiDungIdAndTrangThai(nguoiDungId, (byte) 1);
            }

            model.addAttribute("hoTenNguoiDung", nguoiDung.getHoTen());
            model.addAttribute("danhSachGioHang", danhSachGioHangTrangThai1); // Chỉ hiển thị trạng thái 1
            model.addAttribute("selectedNguoiDungId", nguoiDungId);
        }

        model.addAttribute("danhSachNguoiDung", nguoiDungRepository.findAll());
        return "banhang/gio_hang";
    }



    // Xóa giỏ hàng
    @PostMapping("/xoa")
    public String xoaGioHang(@RequestParam("gioHangId") Long gioHangId,
                             @RequestParam("nguoiDungId") Long nguoiDungId,
                             Model model) {
        gioHangRepository.deleteById(gioHangId);

        // Sau khi xóa, nạp lại dữ liệu trạng thái = 0
        NguoiDung nguoiDung = nguoiDungRepository.findById(nguoiDungId).orElse(null);
        if (nguoiDung != null) {
            List<GioHang> danhSachGioHang = gioHangRepository.findByNguoiDungIdAndTrangThai(nguoiDungId, (byte) 1);
            model.addAttribute("hoTenNguoiDung", nguoiDung.getHoTen());
            model.addAttribute("danhSachGioHang", danhSachGioHang);
            model.addAttribute("selectedNguoiDungId", nguoiDungId);
        }

        List<NguoiDung> danhSachNguoiDung = nguoiDungRepository.findAll();
        model.addAttribute("danhSachNguoiDung", danhSachNguoiDung);

        return "banhang/gio_hang";
    }

    // Thêm người dùng mới
    @GetMapping("/them-nguoi-dung01")
    public String showAddNguoiDungForm01(Model model) {
        model.addAttribute("nguoiDung", new NguoiDung());  // Tạo đối tượng NguoiDung trống
        return "banhang/addNguoiDung";  // Tên view cho form thêm người dùng
    }


    // Xử lý thêm người dùng mới
    @PostMapping("/them-nguoi-dung01")
    public String addNguoiDung01(@ModelAttribute NguoiDung nguoiDung, RedirectAttributes redirectAttributes) {
        nguoiDungRepository.save(nguoiDung);  // Lưu người dùng mới vào DB
        redirectAttributes.addFlashAttribute("message", "Thêm người dùng thành công!");
        return "redirect:/gio-hang/chon-nguoi-dung";  // Quay lại trang danh sách người dùng
    }


    // method này ở cuối file
    @GetMapping("/xem-san-pham")
    public String xemSanPhamTrongGioHang(@RequestParam("gioHangId") Long gioHangId,
                                         @RequestParam("nguoiDungId") Long nguoiDungId,
                                         @RequestParam(name = "page", defaultValue = "0") int page,
                                         @RequestParam(name = "size", defaultValue = "5") int size,
                                         @RequestParam(name = "keyword", required = false) String keyword,
                                         Model model) {
        // Lấy thông tin người dùng
        NguoiDung nguoiDung = nguoiDungRepository.findById(nguoiDungId).orElse(null);
        if (nguoiDung != null) {
            model.addAttribute("hoTenNguoiDung", nguoiDung.getHoTen());
            model.addAttribute("selectedNguoiDungId", nguoiDungId);

            List<GioHang> danhSachGioHang = gioHangRepository.findByNguoiDungIdAndTrangThai(nguoiDungId, (byte) 1);
            model.addAttribute("danhSachGioHang", danhSachGioHang);

            // Lấy sản phẩm trong giỏ hàng đã chọn
            List<SanPhamTrongGioHang> danhSachSanPham = sanPhamTrongGioHangRepository.findByGioHangId(gioHangId);
            model.addAttribute("danhSachSanPham", danhSachSanPham);

            model.addAttribute("selectedGioHangId", gioHangId);
        }

        model.addAttribute("danhSachNguoiDung", nguoiDungRepository.findAll());

        // Lấy danh sách sản phẩm chi tiết phân trang & tìm kiếm (giống method chonSanPhamChiTiet)
        Page<SanPhamChiTiet> pageSanPhamChiTiet;
        if (keyword != null && !keyword.trim().isEmpty()) {
            pageSanPhamChiTiet = sanPhamChiTietRepository.findBySanPham_TenSanPhamContainingIgnoreCase(keyword, org.springframework.data.domain.PageRequest.of(page, size));
            model.addAttribute("keyword", keyword);
        } else {
            pageSanPhamChiTiet = sanPhamChiTietRepository.findAll(org.springframework.data.domain.PageRequest.of(page, size));
        }

        model.addAttribute("pageSanPhamChiTiet", pageSanPhamChiTiet);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageSanPhamChiTiet.getTotalPages());
        model.addAttribute("pageSize", size);
        model.addAttribute("hienThiFormChonSanPham", true); // <- DÒNG NÀY ĐỂ HIỆN BẢNG SẢN PHẨM CHI TIẾT

        return "banhang/gio_hang";
    }


    // Hiển thị form chọn sản phẩm chi tiết để thêm vào giỏ hàng
    @GetMapping("/chon-san-pham")
    public String chonSanPhamChiTiet(
            @RequestParam("gioHangId") Long gioHangId,
            @RequestParam("nguoiDungId") Long nguoiDungId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            @RequestParam(name = "keyword", required = false) String keyword,
            Model model) {

        NguoiDung nguoiDung = nguoiDungRepository.findById(nguoiDungId).orElse(null);
        if (nguoiDung != null) {
            model.addAttribute("hoTenNguoiDung", nguoiDung.getHoTen());
            model.addAttribute("selectedNguoiDungId", nguoiDungId);
            List<GioHang> danhSachGioHang = gioHangRepository.findByNguoiDungIdAndTrangThai(nguoiDungId, (byte) 1);
            model.addAttribute("danhSachGioHang", danhSachGioHang);
            model.addAttribute("selectedGioHangId", gioHangId);
        }

        Page<SanPhamChiTiet> pageSanPhamChiTiet;
        if (keyword != null && !keyword.trim().isEmpty()) {
            pageSanPhamChiTiet = sanPhamChiTietRepository.findBySanPham_TenSanPhamContainingIgnoreCase(keyword, org.springframework.data.domain.PageRequest.of(page, size));
            model.addAttribute("keyword", keyword);
        } else {
            pageSanPhamChiTiet = sanPhamChiTietRepository.findAll(org.springframework.data.domain.PageRequest.of(page, size));
        }

        model.addAttribute("pageSanPhamChiTiet", pageSanPhamChiTiet);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageSanPhamChiTiet.getTotalPages());
        model.addAttribute("pageSize", size);
        model.addAttribute("hienThiFormChonSanPham", true);

        return "banhang/gio_hang";
    }

    @PostMapping("/them-san-pham-vao-gio")
    public String themSanPhamVaoGio(
            @RequestParam("gioHangId") Long gioHangId,
            @RequestParam("nguoiDungId") Long nguoiDungId,
            @RequestParam("sanPhamChiTietId") Long sanPhamChiTietId,
            @RequestParam("soLuong") int soLuong,
            RedirectAttributes redirectAttributes,
            Model model) {

        GioHang gioHang = gioHangRepository.findById(gioHangId).orElse(null);
        SanPhamChiTiet sanPhamChiTiet = sanPhamChiTietRepository.findById(sanPhamChiTietId).orElse(null);

        if (gioHang != null && sanPhamChiTiet != null) {
            // Kiểm tra số lượng tồn kho đủ không
            if (sanPhamChiTiet.getSoLuong() < soLuong) {
                redirectAttributes.addFlashAttribute("errorMessage", "Số lượng tồn kho không đủ!");
                return "redirect:/gio-hang/xem-san-pham?gioHangId=" + gioHangId + "&nguoiDungId=" + nguoiDungId;
            }

            // Kiểm tra sản phẩm đã tồn tại trong giỏ chưa
            SanPhamTrongGioHang spTrongGio = sanPhamTrongGioHangRepository
                    .findByGioHangIdAndSanPhamChiTietId(gioHangId, sanPhamChiTietId);
            if (spTrongGio != null) {
                // Nếu đã có thì cộng thêm số lượng
                spTrongGio.setSoLuong(spTrongGio.getSoLuong() + soLuong);
                sanPhamTrongGioHangRepository.save(spTrongGio);
            } else {
                // Nếu chưa có thì tạo mới
                SanPhamTrongGioHang spMoi = new SanPhamTrongGioHang();
                spMoi.setGioHang(gioHang);
                spMoi.setSanPhamChiTiet(sanPhamChiTiet);
                spMoi.setSoLuong(soLuong);
                sanPhamTrongGioHangRepository.save(spMoi);
            }

            // Trừ số lượng tồn kho của sản phẩm chi tiết
            sanPhamChiTiet.setSoLuong(sanPhamChiTiet.getSoLuong() - soLuong);
            sanPhamChiTietRepository.save(sanPhamChiTiet);

            redirectAttributes.addFlashAttribute("message", "Thêm sản phẩm thành công!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy giỏ hàng hoặc sản phẩm!");
        }

        // Quay về giao diện xem sản phẩm trong giỏ đã chọn
        return "redirect:/gio-hang/xem-san-pham?gioHangId=" + gioHangId + "&nguoiDungId=" + nguoiDungId;
    }

    @PostMapping("/huy")
    public String huySanPham(@RequestParam("sanPhamTrongGioHangId") Long sanPhamTrongGioHangId, RedirectAttributes redirectAttributes) {
        // Lấy sản phẩm trong giỏ hàng theo ID
        SanPhamTrongGioHang sanPhamTrongGioHang = sanPhamTrongGioHangRepository.findById(sanPhamTrongGioHangId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm trong giỏ hàng"));

        // Cập nhật lại số lượng sản phẩm trong kho
        SanPhamChiTiet sanPhamChiTiet = sanPhamTrongGioHang.getSanPhamChiTiet();
        sanPhamChiTiet.setSoLuong(sanPhamChiTiet.getSoLuong() + sanPhamTrongGioHang.getSoLuong());
        sanPhamChiTietRepository.save(sanPhamChiTiet);

        // Lưu thông tin giỏ hàng và người dùng trước khi xóa
        Long gioHangId = sanPhamTrongGioHang.getGioHang().getId();
        Long nguoiDungId = sanPhamTrongGioHang.getGioHang().getNguoiDung().getId();

        // Xóa sản phẩm khỏi giỏ hàng
        sanPhamTrongGioHangRepository.delete(sanPhamTrongGioHang);

        redirectAttributes.addFlashAttribute("message", "Đã hủy sản phẩm và trả lại số lượng vào kho!");

        return "redirect:/gio-hang/xem-san-pham?gioHangId=" + gioHangId + "&nguoiDungId=" + nguoiDungId;
    }



    @GetMapping("/thanh-toan01")
    public String hienThiTrangThanhToan(@RequestParam("gioHangId") Long gioHangId, Model model) {
        GioHang gioHang = gioHangRepository.findById(gioHangId).orElse(null);
        if (gioHang == null) {
            model.addAttribute("error", "Không tìm thấy giỏ hàng!");
            return "redirect:/gio-hang/chon-nguoi-dung";
        }

        List<SanPhamTrongGioHang> danhSachSanPham = sanPhamTrongGioHangRepository.findByGioHangId(gioHangId);
        model.addAttribute("gioHang", gioHang);
        model.addAttribute("danhSachSanPham", danhSachSanPham);

        int tongTien = 0;
        for (SanPhamTrongGioHang sp : danhSachSanPham) {
            tongTien += sp.getSoLuong() * sp.getSanPhamChiTiet().getGia(); // giả sử `gia` là kiểu int
        }
        model.addAttribute("tongTien", tongTien);

        // Lấy danh sách mã giảm giá hợp lệ (ví dụ: còn hạn, còn số lượng, trạng thái true)
        List<MaGiamGia> dsMaGiamGia = maGiamGiaRepository.findAll()
                .stream()
                .filter(mg -> mg.getTrangThai() != null && mg.getTrangThai() == true
                        && mg.getSoLuong() > 0
                        && mg.getNgayBatDau().before(new Date())
                        && mg.getNgayKetThuc().after(new Date()))
                .toList();
        model.addAttribute("dsMaGiamGia", dsMaGiamGia);

        return "banhang/xacNhanThanhToan"; // view thanh toán
    }



    @PostMapping("/xac-nhan-thanh-toan01")
    public String xacNhanThanhToan(
            @RequestParam("gioHangId") Long gioHangId,
            @RequestParam("hinhThuc") String hinhThuc,
            @RequestParam(value = "tienMat", defaultValue = "0") int tienMat,
            @RequestParam(value = "chuyenKhoan", defaultValue = "0") int chuyenKhoan,
            @RequestParam(value = "maGiamGiaId", required = false) Long maGiamGiaId,  // Mã giảm giá
            RedirectAttributes redirectAttributes) {

        GioHang gioHang = gioHangRepository.findById(gioHangId).orElse(null);
        if (gioHang == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy giỏ hàng!");
            return "redirect:/gio-hang/nguoi-dung";
        }

        List<SanPhamTrongGioHang> danhSachSanPham = sanPhamTrongGioHangRepository.findByGioHangId(gioHangId);
        if (danhSachSanPham.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Không có sản phẩm nào để thanh toán.");
            return "redirect:/gio-hang/xem-san-pham?gioHangId=" + gioHangId + "&nguoiDungId=";
        }

        int tongTien = 0;
        for (SanPhamTrongGioHang sp : danhSachSanPham) {
            tongTien += sp.getSoLuong() * sp.getSanPhamChiTiet().getGia();
        }

        // Áp dụng mã giảm giá nếu có chọn
        int soTienGiam = 0;
        MaGiamGia maGiamGia = null;
        if (maGiamGiaId != null) {
            maGiamGia = maGiamGiaRepository.findById(maGiamGiaId).orElse(null);
            if (maGiamGia != null && Boolean.TRUE.equals(maGiamGia.getTrangThai())
                    && maGiamGia.getSoLuong() > 0
                    && maGiamGia.getNgayBatDau().before(new Date())
                    && maGiamGia.getNgayKetThuc().after(new Date())) {
                if (maGiamGia.getPhanTramGiam() != null && maGiamGia.getPhanTramGiam() > 0) {
                    soTienGiam = tongTien * maGiamGia.getPhanTramGiam() / 100;
                } else if (maGiamGia.getSoTienGiam() != null && maGiamGia.getSoTienGiam() > 0) {
                    soTienGiam = maGiamGia.getSoTienGiam().intValue();
                }
                if (soTienGiam > tongTien) soTienGiam = tongTien;
                tongTien -= soTienGiam;
            }
        }

        int tongThanhToan = tienMat + chuyenKhoan;
        if (tongThanhToan < tongTien) {
            redirectAttributes.addFlashAttribute("error", "Số tiền thanh toán chưa đủ!");
            return "redirect:/gio-hang/xem-san-pham?gioHangId=" + gioHangId + "&nguoiDungId=" ;
        }

        DonHang donHang = new DonHang();
        donHang.setNguoiDung(gioHang.getNguoiDung());
        donHang.setTongTien(tongTien);
        donHang.setHinhThucMua("Mua tại quầy");
        donHang.setNgayTao(LocalDateTime.now());

        // Thêm mã giảm giá vào đơn hàng
        if (maGiamGia != null) {
            donHang.setMaGiamGia(maGiamGia);
        }

        switch (hinhThuc) {
            case "TIEN_MAT":
                donHang.setPhuongThucThanhToan("Tiền mặt");
                break;
            case "CHUYEN_KHOAN":
                donHang.setPhuongThucThanhToan("Chuyển khoản");
                break;
            case "KET_HOP":
                donHang.setPhuongThucThanhToan("Tiền mặt + Chuyển khoản");
                break;
            default:
                donHang.setPhuongThucThanhToan("Không xác định");
        }

        donHang.setTrangThai("Đã thanh toán");
        donHang = donHangRepository.save(donHang);

        for (SanPhamTrongGioHang sp : danhSachSanPham) {
            ChiTietDonHang ct = new ChiTietDonHang();
            ct.setDonHang(donHang);
            ct.setSanPhamChiTiet(sp.getSanPhamChiTiet());
            ct.setSoLuong(sp.getSoLuong());
            ct.setGiaDonVi(sp.getSanPhamChiTiet().getGia().intValue());
            chiTietDonHangRepository.save(ct);
        }

        LichSuThanhToan lichSu = new LichSuThanhToan();
        lichSu.setDonHang(donHang);
        lichSu.setNguoiDung(gioHang.getNguoiDung());
        lichSu.setSoTien(tongTien);
        lichSu.setPhuongThuc(donHang.getPhuongThucThanhToan());
        lichSu.setThoiGian(LocalDateTime.now());
        lichSuThanhToanRepository.save(lichSu);

        gioHang.setTrangThai(0);
        gioHangRepository.save(gioHang);
        sanPhamTrongGioHangRepository.deleteAll(danhSachSanPham);

        redirectAttributes.addFlashAttribute("message", "Thanh toán thành công!");
        return "redirect:/gio-hang/xem-san-pham?gioHangId=0&nguoiDungId=" + gioHang.getNguoiDung().getId() + "&page=0&size=5";
    }


}
