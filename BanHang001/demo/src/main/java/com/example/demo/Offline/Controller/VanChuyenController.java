package com.example.demo.Offline.Controller;

import com.example.demo.Entity.*;
import com.example.demo.Offline.Repository.OnlineChiTietDonHangRepository;
import com.example.demo.Offline.Repository.OnlineDonHangRepository;
import com.example.demo.Offline.Repository.OnlineSanPhamChiTietRepository;
import com.example.demo.Offline.Repository.VanChuyenRepository;
import com.example.demo.Repository.LichSuThanhToanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/van-chuyen")
public class VanChuyenController {


    @Autowired
    private OnlineDonHangRepository onlineDonHangRepository;
    @Autowired
    private VanChuyenRepository vanChuyenRepository;
    @Autowired
    private LichSuThanhToanRepository lichSuThanhToanRepository;
    @Autowired
    private OnlineChiTietDonHangRepository onlineChiTietDonHangRepository;
    @Autowired
    private OnlineSanPhamChiTietRepository onlineSanPhamChiTietRepository;

    // Trang danh sách vận chuyển
    @GetMapping("/danh-sach")
    public String danhSachVanChuyen(Model model) {
        model.addAttribute("vanchuyens", vanChuyenRepository.findAll());
        model.addAttribute("page", "van-chuyen");
        return "BanHangOnline/vanchuyen-list";
    }

    // Cập nhật trạng thái vận chuyển thành công
    @PostMapping("/giao-thanh-cong/{id}")
    public String giaoThanhCong(@PathVariable Long id) {
        VanChuyen vanChuyen = vanChuyenRepository.findById(id).orElseThrow();
        vanChuyen.setTrangThai("Vận chuyển thành công");
        vanChuyen.setNgayGiaoThucTe(LocalDateTime.now());
        vanChuyen.setGhiChu("Hoàn thành");
        vanChuyenRepository.save(vanChuyen);

        DonHang donHang = vanChuyen.getDonHang();
        donHang.setTrangThai("Đã giao");
        onlineDonHangRepository.save(donHang);

        // Ghi lại lịch sử thanh toán
        LichSuThanhToan lichSu = new LichSuThanhToan();
        lichSu.setNguoiDung(donHang.getNguoiDung());
        lichSu.setDonHang(donHang);
        lichSu.setSoTien(donHang.getTongTien());
        lichSu.setPhuongThuc(donHang.getPhuongThucThanhToan());
        lichSu.setThoiGian(LocalDateTime.now());
        lichSuThanhToanRepository.save(lichSu);

        return "redirect:/van-chuyen/danh-sach";
    }


    @PostMapping("/huy-van-chuyen/{id}")
    public String huyVanChuyen(@PathVariable Long id,
                               @RequestParam("ghiChu") String ghiChu) {
        VanChuyen vanChuyen = vanChuyenRepository.findById(id).orElseThrow();

        // Cập nhật trạng thái & ghi chú
        vanChuyen.setTrangThai("Đã hủy vận chuyển");
        vanChuyen.setGhiChu(ghiChu);
        vanChuyenRepository.save(vanChuyen);

        DonHang donHang = vanChuyen.getDonHang();
        if (!"Đã giao".equalsIgnoreCase(donHang.getTrangThai())) {
            donHang.setTrangThai("Đã hủy");

            if ("Chuyển khoản ngân hàng".equalsIgnoreCase(donHang.getPhuongThucThanhToan())) {
                donHang.setHoantien(donHang.getTongTien());
            }
            onlineDonHangRepository.save(donHang);

            List<ChiTietDonHang> chiTietList = onlineChiTietDonHangRepository.findByDonHang(donHang);
            for (ChiTietDonHang chiTiet : chiTietList) {
                SanPhamChiTiet spct = chiTiet.getSanPhamChiTiet();
                spct.setSoLuong(spct.getSoLuong() + chiTiet.getSoLuong());
                onlineSanPhamChiTietRepository.save(spct);
            }
        }

        return "redirect:/van-chuyen/danh-sach";
    }





}
