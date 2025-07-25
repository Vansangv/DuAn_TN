package com.example.demo.Login;

import com.example.demo.Entity.*;
import com.example.demo.Offline.Repository.OnlineGioHangRepository;
import com.example.demo.Offline.Repository.OnlineSanPhamChiTietRepository;
import com.example.demo.Offline.Repository.OnlineSanPhamTrongGioHangRepository;
import com.example.demo.Offline.Repository.YeuThichSanPhamRepository;
import com.example.demo.PhanQuyen.BaseController;
import com.example.demo.Repository.*;
import com.example.demo.Service.EmailService;
import com.example.demo.Service.NguoiDungService;
import com.example.demo.Service.PasswordResetService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;

@Controller
public class AuthController extends BaseController {

    @Autowired
    private OnlineSanPhamChiTietRepository sanPhamChiTietRepository;
    @Autowired
    private OnlineSanPhamTrongGioHangRepository sanPhamTrongGioHangRepository;

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    @Autowired
    private JavaMailSenderImpl mailSender;

    @Autowired
    private VaiTroOfflineRepository vaiTroRepository;

    @Autowired
    private XacThucQuenMatKhauRepository tokenRepository;

    private final NguoiDungService nguoiDungService;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetService passwordResetService;
    private final EmailService emailService;

    @Autowired
    private XacThucEmailRepository xacThucEmailRepository;
    @Autowired
    private YeuThichSanPhamRepository yeuThichSanPhamRepository;

    @Value("${email.service.base-url}")
    private String emailServiceBaseUrl;


    @Autowired
    public AuthController(NguoiDungService nguoiDungService, PasswordEncoder passwordEncoder, PasswordResetService passwordResetService, EmailService emailService) {
        this.nguoiDungService = nguoiDungService;
        this.passwordEncoder = passwordEncoder;
        this.passwordResetService = passwordResetService;
        this.emailService = emailService;
    }


    @GetMapping("/login")
    public String login() {
        return "dangnhap/login";
    }


//    @PreAuthorize("hasAnyAuthority('ADMIN', 'NHÂN VIÊN')")
    @GetMapping("/home")
    public String home(Model model, Principal principal) {
        if (principal != null) {
            String username = principal.getName();
            NguoiDung nguoiDung = nguoiDungService.findByTenDangNhap(username);
            model.addAttribute("nguoiDung", nguoiDung);
            model.addAttribute("vaiTros", nguoiDung.getVaiTros());
        }
        //return "menutrangchu/menu";
        return "layout";
    }


///////////////////


    @GetMapping("/login-online")
    public String loginOnline() {
        return "dangnhap/login-online"; // Trỏ đến login-online.html
    }


    @GetMapping("/online-home")
    public String onlineHome(Model model) {
        model.addAttribute("danhSachSanPham", sanPhamChiTietRepository.findByNoiBatTrue());
        return "index";
    }




    /// đăng ký

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("nguoiDung", new NguoiDung());
        return "dangnhap/register";
    }

    // Xử lý đăng ký
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("nguoiDung") NguoiDung nguoiDung,
                               BindingResult result,
                               Model model,
                               HttpSession session) {
        if (result.hasErrors()) {
            return "dangnhap/register";
        }

        if (nguoiDungService.isUsernameTaken(nguoiDung.getTenDangNhap())) {
            model.addAttribute("usernameError", "Tên đăng nhập đã tồn tại!");
            return "dangnhap/register";
        }

        if (nguoiDungService.isEmailTaken(nguoiDung.getEmail())) {
            model.addAttribute("emailError", "Email đã tồn tại!");
            return "dangnhap/register";
        }

        // 🔹 Gán vai trò mặc định "NHAN_VIEN"
        VaiTro vaiTroNhanVien = vaiTroRepository.findByTenVaiTro("NHÂN VIÊN");
        if (vaiTroNhanVien == null) {
            throw new RuntimeException("Vai trò NHÂN VIÊN không tồn tại trong hệ thống.");
        }
        nguoiDung.getVaiTros().add(vaiTroNhanVien);

        // Lưu thông tin vào session
        session.setAttribute("nguoiDungTamThoi", nguoiDung);
        session.setAttribute("emailXacThuc", nguoiDung.getEmail());

        // Mã xác thực
        String maXacThuc = String.format("%06d", new Random().nextInt(999999));
        XacThucEmail xacThuc = new XacThucEmail();
        xacThuc.setEmail(nguoiDung.getEmail());
        xacThuc.setMaXacThuc(maXacThuc);
        xacThuc.setThoiGianTao(LocalDateTime.now());
        xacThuc.setThoiGianHetHan(LocalDateTime.now().plusMinutes(3));
        xacThuc.setDaXacThuc(false);
        xacThucEmailRepository.save(xacThuc);

        emailService.sendVerificationEmail(nguoiDung.getEmail(), maXacThuc);

        return "redirect:/verify-email";
    }



    // Hiển thị form xác thực email
    @GetMapping("/verify-email")
    public String showVerifyEmailForm(HttpSession session, Model model) {
        String email = (String) session.getAttribute("emailXacThuc");
        if (email == null) {
            return "redirect:/register";
        }
        model.addAttribute("email", email);
        return "dangnhap/verify-email";
    }

    // Xử lý xác thực email
    @PostMapping("/verify-email")
    public String verifyEmail(@RequestParam("maXacThuc") String maXacThuc,
                              HttpSession session,
                              Model model) {
        String email = (String) session.getAttribute("emailXacThuc");

        if (email == null) {
            model.addAttribute("error", "Không tìm thấy email cần xác thực.");
            return "dangnhap/verify-email";
        }

        XacThucEmail xacThuc = xacThucEmailRepository.findTopByEmailOrderByThoiGianTaoDesc(email);
        if (xacThuc == null || xacThuc.getDaXacThuc() || LocalDateTime.now().isAfter(xacThuc.getThoiGianHetHan())) {
            model.addAttribute("error", "Mã xác thực không hợp lệ hoặc đã hết hạn.");
            model.addAttribute("email", email);
            return "dangnhap/verify-email";
        }

        if (!xacThuc.getMaXacThuc().equals(maXacThuc)) {
            model.addAttribute("error", "Mã xác thực không đúng.");
            model.addAttribute("email", email);
            return "dangnhap/verify-email";
        }

        // Đánh dấu xác thực thành công
        xacThuc.setDaXacThuc(true);
        xacThucEmailRepository.save(xacThuc);

        // Lấy người dùng từ session
        NguoiDung nguoiDung = (NguoiDung) session.getAttribute("nguoiDungTamThoi");
        if (nguoiDung == null || !nguoiDung.getEmail().equals(email)) {
            model.addAttribute("error", "Không tìm thấy thông tin người dùng.");
            return "dangnhap/verify-email";
        }

        // Mã hóa mật khẩu và lưu vào DB
        nguoiDung.setMatKhau(passwordEncoder.encode(nguoiDung.getMatKhau()));
        nguoiDung.setTrangThai(true);
        nguoiDungService.save(nguoiDung);

        // Xóa dữ liệu tạm trong session
        session.removeAttribute("nguoiDungTamThoi");
        session.removeAttribute("emailXacThuc");

        return "redirect:/login?verified";
    }


    @PostMapping("/resend-verification-code")
    public String resendVerificationCode(HttpSession session, Model model) {
        String email = (String) session.getAttribute("emailXacThuc");

        if (email == null) {
            return "redirect:/register";
        }

        // Tạo mã xác thực mới
        String newCode = String.format("%06d", new Random().nextInt(999999));

        XacThucEmail newXacThuc = new XacThucEmail();
        newXacThuc.setEmail(email);
        newXacThuc.setMaXacThuc(newCode);
        newXacThuc.setThoiGianTao(LocalDateTime.now());
        newXacThuc.setThoiGianHetHan(LocalDateTime.now().plusMinutes(3));
        newXacThuc.setDaXacThuc(false);

        xacThucEmailRepository.save(newXacThuc);

        // Gửi mã qua email
        emailService.sendVerificationEmail(email, newCode);

        model.addAttribute("email", email);
        model.addAttribute("message", "Mã xác thực mới đã được gửi tới email của bạn.");
        return "dangnhap/verify-email";
    }


//----------------------------------------------------------------------------------------------------------------------





    /// Hiển thị form đăng ký online
    @GetMapping("/register-online")
    public String showRegistrationForm1(Model model) {
        model.addAttribute("nguoiDung", new NguoiDung());
        return "dangnhap/register-online"; // ✅ Đổi tên view cho khớp URL
    }

    // Xử lý đăng ký online
    @PostMapping("/register-online")
    public String registerUser1(@Valid @ModelAttribute("nguoiDung") NguoiDung nguoiDung,
                                BindingResult result,
                                Model model,
                                HttpSession session) {
        // Kiểm tra lỗi validate
        if (result.hasErrors()) {
            return "dangnhap/register-online";
        }

        // Kiểm tra tên đăng nhập đã tồn tại chưa
        if (nguoiDungService.isUsernameTaken(nguoiDung.getTenDangNhap())) {
            model.addAttribute("usernameError", "Tên đăng nhập đã tồn tại!");
            return "dangnhap/register-online";
        }

        // Kiểm tra email đã tồn tại chưa
        if (nguoiDungService.isEmailTaken(nguoiDung.getEmail())) {
            model.addAttribute("emailError", "Email đã tồn tại!");
            return "dangnhap/register-online";
        }

        // Gán vai trò mặc định là "KHÁCH HÀNG"
        VaiTro vaiTroKhachHang = vaiTroRepository.findByTenVaiTro("KHÁCH HÀNG");
        if (vaiTroKhachHang == null) {
            throw new RuntimeException("Vai trò KHÁCH HÀNG không tồn tại trong hệ thống.");
        }
        nguoiDung.getVaiTros().add(vaiTroKhachHang);

        // Lưu thông tin người dùng tạm thời vào session
        session.setAttribute("nguoiDungTamThoi", nguoiDung);
        session.setAttribute("emailXacThuc", nguoiDung.getEmail());

        // Tạo mã xác thực ngẫu nhiên 6 chữ số
        String maXacThuc = String.format("%06d", new Random().nextInt(999999));

        // Tạo đối tượng xác thực email
        XacThucEmail xacThuc = new XacThucEmail();
        xacThuc.setEmail(nguoiDung.getEmail());
        xacThuc.setMaXacThuc(maXacThuc);
        xacThuc.setThoiGianTao(LocalDateTime.now());
        xacThuc.setThoiGianHetHan(LocalDateTime.now().plusMinutes(3));
        xacThuc.setDaXacThuc(false);

        // Lưu mã xác thực vào CSDL
        xacThucEmailRepository.save(xacThuc);

        // Gửi mã xác thực qua email
        emailService.sendVerificationEmail(nguoiDung.getEmail(), maXacThuc);

        // Chuyển hướng sang trang xác thực email
        return "redirect:/verify-email-online";
    }


    // Hiển thị form xác thực email online
    @GetMapping("/verify-email-online")
    public String showVerifyEmailForm1(HttpSession session, Model model) {
        String email = (String) session.getAttribute("emailXacThuc");
        if (email == null) {
            return "redirect:/register-online"; // ✅ Redirect đúng nếu chưa có email
        }
        model.addAttribute("email", email);
        return "dangnhap/verify-email-online"; // ✅ View mới
    }

    // Xử lý xác thực email online
    @PostMapping("/verify-email-online")
    public String verifyEmail1(@RequestParam("maXacThuc") String maXacThuc,
                              HttpSession session,
                              Model model) {
        String email = (String) session.getAttribute("emailXacThuc");

        if (email == null) {
            model.addAttribute("error", "Không tìm thấy email cần xác thực.");
            return "dangnhap/verify-email-online";
        }

        XacThucEmail xacThuc = xacThucEmailRepository.findTopByEmailOrderByThoiGianTaoDesc(email);
        if (xacThuc == null || xacThuc.getDaXacThuc() || LocalDateTime.now().isAfter(xacThuc.getThoiGianHetHan())) {
            model.addAttribute("error", "Mã xác thực không hợp lệ hoặc đã hết hạn.");
            model.addAttribute("email", email);
            return "dangnhap/verify-email-online";
        }

        if (!xacThuc.getMaXacThuc().equals(maXacThuc)) {
            model.addAttribute("error", "Mã xác thực không đúng.");
            model.addAttribute("email", email);
            return "dangnhap/verify-email-online";
        }

        // Đánh dấu xác thực thành công
        xacThuc.setDaXacThuc(true);
        xacThucEmailRepository.save(xacThuc);

        // Lấy người dùng từ session
        NguoiDung nguoiDung = (NguoiDung) session.getAttribute("nguoiDungTamThoi");
        if (nguoiDung == null || !nguoiDung.getEmail().equals(email)) {
            model.addAttribute("error", "Không tìm thấy thông tin người dùng.");
            return "dangnhap/verify-email-online";
        }

        // Mã hóa mật khẩu và lưu vào DB
        nguoiDung.setMatKhau(passwordEncoder.encode(nguoiDung.getMatKhau()));
        nguoiDung.setTrangThai(true);
        nguoiDungService.save(nguoiDung);

        // Xóa dữ liệu tạm trong session
        session.removeAttribute("nguoiDungTamThoi");
        session.removeAttribute("emailXacThuc");

        return "redirect:/login-online?verified"; // ✅ Giữ nguyên
    }


    @PostMapping("/resend-verification-code-online")
    public String resendVerificationCode1(HttpSession session, Model model) {
        String email = (String) session.getAttribute("emailXacThuc");

        if (email == null) {
            return "redirect:/register-online"; // ✅ Sửa lại redirect phù hợp
        }

        // Tạo mã xác thực mới
        String newCode = String.format("%06d", new Random().nextInt(999999));

        XacThucEmail newXacThuc = new XacThucEmail();
        newXacThuc.setEmail(email);
        newXacThuc.setMaXacThuc(newCode);
        newXacThuc.setThoiGianTao(LocalDateTime.now());
        newXacThuc.setThoiGianHetHan(LocalDateTime.now().plusMinutes(3));
        newXacThuc.setDaXacThuc(false);

        xacThucEmailRepository.save(newXacThuc);

        // Gửi mã qua email
        emailService.sendVerificationEmail(email, newCode);

        model.addAttribute("email", email);
        model.addAttribute("message", "Mã xác thực mới đã được gửi tới email của bạn.");
        return "dangnhap/verify-email-online"; // ✅ Đổi view phù hợp với online
    }








//----------------------------------------------------------------------------------------------------------------------

    ///// đổi mật khẩu

    @GetMapping("/change-password")
    public String showChangePasswordPage() {
        return "dangnhap/change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(
            @RequestParam("currentPassword") String currentPassword,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            Authentication authentication,
            Model model
    ) {
        String username = authentication.getName();
        NguoiDung nguoiDung = nguoiDungService.findByTenDangNhap(username);

        if (!passwordEncoder.matches(currentPassword, nguoiDung.getMatKhau())) {
            model.addAttribute("error", "Mật khẩu hiện tại không chính xác!");
            return "dangnhap/change-password";
        }

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "Mật khẩu mới và xác nhận mật khẩu không khớp!");
            return "dangnhap/change-password";
        }

        // Mã hóa mật khẩu mới
        nguoiDung.setMatKhau(passwordEncoder.encode(newPassword));
        nguoiDungService.save(nguoiDung);

        model.addAttribute("success", "Đổi mật khẩu thành công!");
        return "dangnhap/change-password";
    }


    ///// quên mật khẩu


    @GetMapping("/quen-mat-khau")
    public String hienFormQuenMatKhau() {
        return "dangnhap/quen-mat-khau";
    }

    @PostMapping("/quen-mat-khau")
    public String xuLyQuenMatKhau(@RequestParam("email") String email, Model model, HttpSession session) {
        Optional<NguoiDung> nguoiDungOpt = nguoiDungRepository.findByEmail(email);
        if (nguoiDungOpt.isEmpty()) {
            model.addAttribute("error", "Email không tồn tại.");
            return "dangnhap/quen-mat-khau";
        }

        NguoiDung nguoiDung = nguoiDungOpt.get();
        String token = UUID.randomUUID().toString();

        XacThucQuenMatKhau xacThuc = XacThucQuenMatKhau.builder()
                .nguoiDung(nguoiDung)
                .maToken(token)
                .thoiGianHetHan(LocalDateTime.now().plusMinutes(30))
                .daSuDung(false)
                .build();
        tokenRepository.save(xacThuc);

        // Lưu token vào session
        session.setAttribute("tokenDatLaiMatKhau", token);

        // Gửi link không chứa token
        String link = "http://localhost:9999/dat-lai-mat-khau";
        String content = "Nhấn vào link sau để đặt lại mật khẩu:\n" + link;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Đặt lại mật khẩu");
        message.setText(content);
        mailSender.send(message);

        model.addAttribute("message", "Đã gửi hướng dẫn đặt lại mật khẩu tới email.");
        return "dangnhap/quen-mat-khau";
    }

    @GetMapping("/dat-lai-mat-khau")
    public String hienFormDatLaiMatKhau(HttpSession session, Model model) {
        String token = (String) session.getAttribute("tokenDatLaiMatKhau");

        if (token == null) {
            model.addAttribute("error", "Không tìm thấy token.");
            return "dangnhap/dat-lai-mat-khau";
        }

        Optional<XacThucQuenMatKhau> xacThucOpt = tokenRepository.findByMaToken(token);

        if (xacThucOpt.isEmpty()) {
            model.addAttribute("error", "Token không hợp lệ.");
            return "dangnhap/dat-lai-mat-khau";
        }

        XacThucQuenMatKhau xacThuc = xacThucOpt.get();

        // Nếu token hết hạn
        if (xacThuc.getThoiGianHetHan().isBefore(LocalDateTime.now())) {
            // Xóa token cũ
            tokenRepository.delete(xacThuc);

            // Sinh token mới
            String tokenMoi = UUID.randomUUID().toString();

            XacThucQuenMatKhau tokenMoiEntity = XacThucQuenMatKhau.builder()
                    .nguoiDung(xacThuc.getNguoiDung())
                    .maToken(tokenMoi)
                    .thoiGianHetHan(LocalDateTime.now().plusMinutes(30))
                    .daSuDung(false)
                    .build();

            tokenRepository.save(tokenMoiEntity);

            // Cập nhật token trong session
            session.setAttribute("tokenDatLaiMatKhau", tokenMoi);

            // Gửi lại email
            String link = "http://localhost:9999/dat-lai-mat-khau";
            String content = "Token cũ đã hết hạn. Đây là link mới để đặt lại mật khẩu:\n" + link;

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(xacThuc.getNguoiDung().getEmail());
            message.setSubject("Đặt lại mật khẩu - Token mới");
            message.setText(content);
            mailSender.send(message);

            model.addAttribute("message", "Token đã hết hạn. Hệ thống đã gửi lại link mới tới email.");
            return "dangnhap/quen-mat-khau";
        }

        // Nếu token còn hiệu lực, cho phép đổi mật khẩu
        return "dangnhap/dat-lai-mat-khau";
    }


    @PostMapping("/dat-lai-mat-khau")
    public String xuLyDatLaiMatKhau(@RequestParam("matKhauMoi") String matKhauMoi,
                                    HttpSession session,
                                    Model model) {
        String token = (String) session.getAttribute("tokenDatLaiMatKhau");

        if (token == null) {
            model.addAttribute("error", "Token không hợp lệ.");
            return "dangnhap/dat-lai-mat-khau";
        }

        Optional<XacThucQuenMatKhau> xacThucOpt = tokenRepository.findByMaToken(token);
        if (xacThucOpt.isEmpty()) {
            model.addAttribute("error", "Token không hợp lệ.");
            return "dangnhap/dat-lai-mat-khau";
        }

        XacThucQuenMatKhau xacThuc = xacThucOpt.get();
        if (xacThuc.getDaSuDung() || xacThuc.getThoiGianHetHan().isBefore(LocalDateTime.now())) {
            model.addAttribute("error", "Token đã hết hạn hoặc đã được sử dụng.");
            return "dangnhap/dat-lai-mat-khau";
        }

        NguoiDung nguoiDung = xacThuc.getNguoiDung();
        nguoiDung.setMatKhau(passwordEncoder.encode(matKhauMoi));
        nguoiDungRepository.save(nguoiDung);

        xacThuc.setDaSuDung(true);
        tokenRepository.save(xacThuc);

        // Xóa token khỏi session sau khi dùng xong
        session.removeAttribute("tokenDatLaiMatKhau");

        model.addAttribute("message", "Đặt lại mật khẩu thành công.");
        return "dangnhap/login";
    }




}