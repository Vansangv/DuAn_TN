package com.example.demo.Offline.Controller;

import com.example.demo.Entity.ChiTietDonHang;
import com.example.demo.Entity.DonHang;
import com.example.demo.Entity.NguoiDung;
import com.example.demo.Entity.SanPhamChiTiet;
import com.example.demo.Offline.Repository.OnlineChiTietDonHangRepository;
import com.example.demo.Offline.Repository.OnlineDonHangRepository;
import com.example.demo.Offline.Repository.OnlineSanPhamChiTietRepository;

import com.example.demo.Service.NguoiDungService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/don-hang-huy")
public class HuyDonHang {

    @Autowired
    private OnlineDonHangRepository onlineDonHangRepository;

    @Autowired
    private OnlineChiTietDonHangRepository onlineChiTietDonHangRepository;
    @Autowired
    private OnlineSanPhamChiTietRepository onlineSanPhamChiTietRepository;
    @Autowired
    private NguoiDungService nguoiDungService;

    @GetMapping("/danh-sach-don-hang")
    public String danhSachChoXacNhan(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            // Nếu chưa đăng nhập thì chuyển hướng sang trang login
            return "redirect:/login-online";
        }

        // Lấy tên đăng nhập (username) hiện tại
        String username = authentication.getName();

        // Tìm người dùng theo username
        NguoiDung nguoiDung = nguoiDungService.findByTenDangNhap(username);

        if (nguoiDung == null) {
            // Nếu không tìm thấy người dùng, hiển thị trang lỗi hoặc redirect
            return "redirect:/login-online";
        }

        // Lấy danh sách đơn hàng theo người dùng và trạng thái "CHỜ XÁC NHẬN"
        List<DonHang> donHangList = onlineDonHangRepository.findByNguoiDungAndTrangThai(nguoiDung, "CHỜ XÁC NHẬN");

        model.addAttribute("donHangs", donHangList);
        model.addAttribute("tenNguoiDung", nguoiDung.getHoTen());

        return "BanHangOnline/huydonhang"; // Trang view hiển thị danh sách
    }

    @PostMapping("/huy-don-hang/{id}")
    public String huyDonHang(@PathVariable Long id) {
        DonHang donHang1 = onlineDonHangRepository.findById(id).orElseThrow();

        // Kiểm tra trạng thái đơn hàng
        if ("CHỜ XÁC NHẬN".equalsIgnoreCase(donHang1.getTrangThai()) ||
                "Chờ xác nhận".equalsIgnoreCase(donHang1.getTrangThai())) {

            // Đánh dấu đơn hàng đã hủy
            donHang1.setTrangThai("Đã hủy");

            // Nếu thanh toán bằng chuyển khoản -> hoàn tiền
            if ("Chuyển khoản ngân hàng".equalsIgnoreCase(donHang1.getPhuongThucThanhToan())) {
                donHang1.setHoantien(donHang1.getTongTien());
            }

            onlineDonHangRepository.save(donHang1);

            // Hoàn lại số lượng sản phẩm
            List<ChiTietDonHang> chiTietDonHangs = onlineChiTietDonHangRepository.findByDonHang(donHang1);
            for (ChiTietDonHang chiTiet : chiTietDonHangs) {
                SanPhamChiTiet sanPhamChiTiet = chiTiet.getSanPhamChiTiet();
                sanPhamChiTiet.setSoLuong(sanPhamChiTiet.getSoLuong() + chiTiet.getSoLuong());
                onlineSanPhamChiTietRepository.save(sanPhamChiTiet);
            }
        }

        return "redirect:/don-hang-huy/danh-sach-don-hang";
    }

}
