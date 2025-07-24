package com.example.demo.Offline.Controller;

import com.example.demo.Entity.ChiTietDonHang;
import com.example.demo.Entity.DonHang;
import com.example.demo.Entity.SanPhamChiTiet;
import com.example.demo.Entity.TraHang;
import com.example.demo.Offline.Repository.OnlineChiTietDonHangRepository;
import com.example.demo.Offline.Repository.OnlineDonHangRepository;
import com.example.demo.Offline.Repository.OnlineSanPhamChiTietRepository;
import com.example.demo.Offline.Repository.TraHangRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin/tra-hang")
public class TraHangAdminController {

    @Autowired
    private TraHangRepository traHangRepository;
    @Autowired
    private OnlineChiTietDonHangRepository onlineChiTietDonHangRepository;
    @Autowired
    private OnlineSanPhamChiTietRepository onlineSanPhamChiTietRepository;
    @Autowired
    private OnlineDonHangRepository onlineDonHangRepository;

    // Danh sách yêu cầu trả hàng chờ xử lý
    @GetMapping("/cho-xu-ly")
    public String danhSachChoXuLy(Model model) {
        List<TraHang> traHangs = traHangRepository.findByTrangThai("Chờ xử lý");
        model.addAttribute("traHangs", traHangs);
        model.addAttribute("page", "tra-hang");
        return "BanHangOnline/admin-cho-xu-ly";
    }

    // Xác nhận trả hàng
    @PostMapping("/xac-nhan/{id}")
    public String xacNhanTraHang(@PathVariable Long id) {
        TraHang traHang = traHangRepository.findById(id).orElse(null);
        if (traHang != null) {
            traHang.setTrangThai("Đã xác nhận");
            traHangRepository.save(traHang);

            DonHang donHang = traHang.getDonHang(); // Giả sử TraHang có liên kết với DonHang

            if (donHang != null) {
                // Đổi trạng thái đơn hàng
                donHang.setTrangThai("Đã trả hàng");

                // Hoàn tiền nếu thanh toán bằng chuyển khoản
                if ("Chuyển khoản ngân hàng".equalsIgnoreCase(donHang.getPhuongThucThanhToan())) {
                    donHang.setHoantien(donHang.getTongTien());
                }

                // Hoàn số lượng sản phẩm
                List<ChiTietDonHang> chiTietDonHangs = onlineChiTietDonHangRepository.findByDonHang(donHang);
                for (ChiTietDonHang chiTiet : chiTietDonHangs) {
                    SanPhamChiTiet spct = chiTiet.getSanPhamChiTiet();
                    spct.setSoLuong(spct.getSoLuong() + chiTiet.getSoLuong());
                    onlineSanPhamChiTietRepository.save(spct);
                }

                onlineDonHangRepository.save(donHang);
            }
        }
        return "redirect:/admin/tra-hang/cho-xu-ly";
    }

    // Từ chối trả hàng
    @PostMapping("/tu-choi/{id}")
    public String tuChoiTraHang(@PathVariable Long id) {
        TraHang traHang = traHangRepository.findById(id).orElse(null);
        if (traHang != null) {
            traHang.setTrangThai("Từ chối");
            traHangRepository.save(traHang);
        }
        return "redirect:/admin/tra-hang/cho-xu-ly";
    }



}
