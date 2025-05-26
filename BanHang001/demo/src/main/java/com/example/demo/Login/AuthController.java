package com.example.demo.Login;

import com.example.demo.Entity.NguoiDung;
import com.example.demo.Entity.PasswordReset;
import com.example.demo.Repository.SanPhamRepository;
import com.example.demo.Service.EmailService;
import com.example.demo.Service.NguoiDungService;
import com.example.demo.Service.PasswordResetService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.UUID;

@Controller
public class AuthController {

    @Autowired
    private SanPhamRepository sanPhamRepository;

    private final NguoiDungService nguoiDungService;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetService passwordResetService;
    private final EmailService emailService;


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

    @GetMapping("/home")
    public String home(Model model, Principal principal) {
        if (principal != null) {
            String username = principal.getName();
            NguoiDung nguoiDung = nguoiDungService.findByTenDangNhap(username);
            model.addAttribute("nguoiDung", nguoiDung);
            model.addAttribute("vaiTros", nguoiDung.getVaiTros());
        }
        return "menutrangchu/menu";
    }



//    @GetMapping("/home")
//    public String home(Model model) {
//        model.addAttribute("message", "Chào mừng bạn đến với hệ thống!");
//        return "dangnhap/home";
//    }


//    @GetMapping("/home")
//    public String home(Model model, Authentication authentication) {
//        // Lấy người dùng
//        if (authentication != null) {
//            String username = authentication.getName();
//            NguoiDung nguoiDung = nguoiDungService.findByTenDangNhap(username);
//            model.addAttribute("tenNguoiDung", nguoiDung != null ? nguoiDung.getHoTen() : "Khách");
//        }
//
//        // Lấy danh sách sản phẩm
//        model.addAttribute("danhSachSanPham", sanPhamRepository.findAll());
//
//        return "index";
//    }


    /// đăng ký

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("nguoiDung", new NguoiDung());
        return "dangnhap/register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("nguoiDung") NguoiDung nguoiDung, BindingResult result, Model model) {
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

        // Mã hóa mật khẩu tại đây
        nguoiDung.setMatKhau(passwordEncoder.encode(nguoiDung.getMatKhau()));
        nguoiDungService.save(nguoiDung);
        return "redirect:/login?success";
    }

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

    @GetMapping("/forgot-password")
    public String showForgotPasswordPage() {
        return "dangnhap/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email, Model model) {
        NguoiDung nguoiDung = nguoiDungService.findByEmail(email);
        if (nguoiDung == null) {
            model.addAttribute("error", "Không tìm thấy email này trong hệ thống.");
            return "dangnhap/forgot-password";
        }

        // Tạo mã token cho yêu cầu đặt lại mật khẩu
        String token = UUID.randomUUID().toString();  // Tạo một mã token ngẫu nhiên
        PasswordReset passwordReset = new PasswordReset();
        passwordReset.setNguoiDungId(nguoiDung.getId().intValue());
        passwordReset.setMaToken(token);
        passwordReset.setThoiGianTao(LocalDateTime.now());
        passwordReset.setThoiGianHetHan(LocalDateTime.now().plusHours(1));  // Mã hết hạn sau 1 giờ

        // Lưu token vào cơ sở dữ liệu (cần tạo repository và service cho PasswordReset)
        passwordResetService.save(passwordReset);

        // Gửi email với liên kết đặt lại mật khẩu
        String resetUrl = "http://localhost:8080/reset-password?token=" + token;
        emailService.sendPasswordResetEmail(email, resetUrl);

        model.addAttribute("message", "Một email chứa liên kết đặt lại mật khẩu đã được gửi tới bạn.");
        return "dangnhap/forgot-password";
    }

    @GetMapping("/reset-password")
    public String showResetPasswordPage(@RequestParam("token") String token, Model model) {
        PasswordReset passwordReset = passwordResetService.findByToken(token);
        if (passwordReset == null || passwordReset.getThoiGianHetHan().isBefore(LocalDateTime.now())) {
            model.addAttribute("error", "Mã token không hợp lệ hoặc đã hết hạn.");
            return "dangnhap/reset-password";
        }

        model.addAttribute("token", token);
        return "dangnhap/reset-password";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam("token") String token, @RequestParam("newPassword") String newPassword, Model model) {
        PasswordReset passwordReset = passwordResetService.findByToken(token);
        if (passwordReset == null || passwordReset.getThoiGianHetHan().isBefore(LocalDateTime.now())) {
            model.addAttribute("error", "Mã token không hợp lệ hoặc đã hết hạn.");
            return "dangnhap/reset-password";
        }

        NguoiDung nguoiDung = nguoiDungService.findById((long) passwordReset.getNguoiDungId());
        if (nguoiDung == null) {
            model.addAttribute("error", "Người dùng không tồn tại.");
            return "dangnhap/reset-password";
        }

        // Mã hóa mật khẩu mới và lưu lại
        nguoiDung.setMatKhau(passwordEncoder.encode(newPassword));
        nguoiDungService.save(nguoiDung);

        passwordReset.setDaSuDung(true);  // Đánh dấu token là đã sử dụng
        passwordResetService.save(passwordReset);

        model.addAttribute("success", "Mật khẩu của bạn đã được thay đổi thành công.");
        return "dangnhap/reset-password";
    }



}