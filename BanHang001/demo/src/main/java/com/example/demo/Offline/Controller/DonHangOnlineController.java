package com.example.demo.Offline.Controller;

import com.example.demo.Entity.*;
import com.example.demo.Offline.Repository.OnlineChiTietDonHangRepository;
import com.example.demo.Offline.Repository.OnlineDonHangRepository;
import com.example.demo.Offline.Repository.OnlineSanPhamChiTietRepository;
import com.example.demo.Offline.Repository.VanChuyenRepository;
import com.example.demo.Service.NguoiDungService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/don-hang")
public class DonHangOnlineController {

    @Autowired
    private OnlineDonHangRepository onlineDonHangRepository;
    @Autowired
    private VanChuyenRepository vanChuyenRepository;
    @Autowired
    private OnlineChiTietDonHangRepository onlineChiTietDonHangRepository;
    @Autowired
    private OnlineSanPhamChiTietRepository onlineSanPhamChiTietRepository;

    @GetMapping("/cho-xac-nhan")
    public String danhSachChoXacNhan(Model model) {
        model.addAttribute("donHangs", onlineDonHangRepository.findByTrangThai("CHỜ XÁC NHẬN"));
        model.addAttribute("page", "don-hang-cho-xac-nhan");
        return "BanHangOnline/donhang-cho-xacnhan";
    }

    // Xác nhận đơn hàng
    @PostMapping("/xac-nhan/{id}")
    public String xacNhanDonHang(@PathVariable Long id) {
        DonHang donHang = onlineDonHangRepository.findById(id).orElseThrow();
        donHang.setTrangThai("Đã xác nhận");
        onlineDonHangRepository.save(donHang);

        // Tạo thông tin vận chuyển
        VanChuyen vanChuyen = new VanChuyen();
        vanChuyen.setDonHang(donHang);
        vanChuyen.setDiaChiGiao(donHang.getDiaChiGiaoHang().getDiaChi());
        vanChuyen.setTrangThai("Đang vận chuyển");
        vanChuyen.setNgayGiaoDuKien(LocalDateTime.now().plusDays(3));
        vanChuyenRepository.save(vanChuyen);

        return "redirect:/don-hang/cho-xac-nhan";
    }

    @PostMapping("/huy-hang/{id}")
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

        return "redirect:/don-hang/cho-xac-nhan";
    }


}
