package com.example.demo.Offline.Vi;

import com.example.demo.Entity.NguoiDung;
import com.example.demo.Entity.ViDienTu;
import com.example.demo.Entity.YeuCauNapTien;
import com.example.demo.Offline.Repository.ViDienTuRepository;
import com.example.demo.Offline.Repository.YeuCauNapTienRepository;
import com.example.demo.PhanQuyen.BaseController;
import com.example.demo.Service.NguoiDungService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping("/nap-tien")
public class NapTienController extends BaseController {
    @Autowired
    private YeuCauNapTienRepository yeuCauNapTienRepository;

    @Autowired
    private NguoiDungService nguoiDungService;
    @Autowired
    private QRCodeService qrCodeService;

    @Autowired
    private ViDienTuRepository viDienTuRepository;

    // Thay đổi tại đây: IP thật của máy tính để điện thoại có thể truy cập được
    private final String SERVER_IP = "10.2.22.102"; // 👈 Bạn cần thay đổi dòng này thành IP thật của máy tính bạn!

    // Hiển thị form nhập số tiền
    @GetMapping
    public String showForm(Authentication authentication, Model model) {
        model.addAttribute("soTien", 10000);

        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            NguoiDung nguoiDung = nguoiDungService.findByTenDangNhap(username);
            if (nguoiDung != null) {
                ViDienTu vi = viDienTuRepository.findByNguoiDungId(nguoiDung); // SỬA Ở ĐÂY
                if (vi != null) {
                    model.addAttribute("soDu", vi.getSoDu());
                } else {
                    model.addAttribute("soDu", 0);
                }
            }
        }

        return "quetma/form";
    }

    // Xử lý form và tạo mã QR
    @PostMapping
    public String submitForm(@RequestParam("soTien") Long soTien,
                             Authentication authentication,
                             Model model) throws Exception {

        // ✅ Lấy username từ authentication
        String username = authentication.getName();

        // ✅ Truy xuất người dùng từ DB
        NguoiDung nguoiDung = nguoiDungService.findByTenDangNhap(username);
        if (nguoiDung == null) {
            throw new RuntimeException("Không tìm thấy người dùng: " + username);
        }

        Long nguoiDungId = nguoiDung.getId();

        // ✅ Phần xử lý mã giao dịch và tạo QR giữ nguyên
        String maGiaoDich = "GD" + System.currentTimeMillis();

        YeuCauNapTien yeuCau = new YeuCauNapTien();
        yeuCau.setNguoiDungId(nguoiDungId);
        yeuCau.setSoTien(soTien);
        yeuCau.setMaGiaoDich(maGiaoDich);
        yeuCau.setTrangThai("CHO_THANH_TOAN");
        yeuCau.setNgayTao(java.time.LocalDateTime.now());
        yeuCauNapTienRepository.save(yeuCau);

        // Lấy IP LAN thật sự để điện thoại quét được
        String ip = "127.0.0.1";
        try {
            java.util.Enumeration<java.net.NetworkInterface> interfaces = java.net.NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                java.net.NetworkInterface ni = interfaces.nextElement();
                if (ni.isUp() && !ni.isLoopback() && !ni.getName().contains("docker") && !ni.getName().contains("br-")) {
                    java.util.Enumeration<java.net.InetAddress> addresses = ni.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        java.net.InetAddress addr = addresses.nextElement();
                        if (addr instanceof java.net.Inet4Address && !addr.isLoopbackAddress()) {
                            ip = addr.getHostAddress();
                        }
                    }
                }
            }
        } catch (java.net.SocketException e) {
            e.printStackTrace();
        }

        String qrData = "http://" + ip + ":9999/xac-nhan-nap?maGiaoDich=" + maGiaoDich;
        String qrBase64 = qrCodeService.generateBase64QRCode(qrData, 300, 300);

        model.addAttribute("qrCode", qrBase64);
        model.addAttribute("maGiaoDich", maGiaoDich);
        model.addAttribute("soTien", soTien);
        return "quetma/qr";
    }

}
