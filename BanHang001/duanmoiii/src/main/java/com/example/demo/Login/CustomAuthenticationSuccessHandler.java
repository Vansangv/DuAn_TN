package com.example.demo.Login;

import com.example.demo.Entity.LogHeThong;
import com.example.demo.Entity.NguoiDung;
import com.example.demo.Service.LogHeThongService;
import com.example.demo.Service.LoginHistoryService;
import com.example.demo.Service.NguoiDungService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final NguoiDungService nguoiDungService;
    private final LoginHistoryService loginHistoryService;
    private final LogHeThongService logHeThongService;

    @Autowired
    public CustomAuthenticationSuccessHandler(NguoiDungService nguoiDungService,
                                              LoginHistoryService loginHistoryService,
                                              LogHeThongService logHeThongService) {
        this.nguoiDungService = nguoiDungService;
        this.loginHistoryService = loginHistoryService;
        this.logHeThongService = logHeThongService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        // Lấy tên người dùng từ thông tin đăng nhập
        String username = authentication.getName();

        // Tìm người dùng trong cơ sở dữ liệu
        NguoiDung nguoiDung = nguoiDungService.findByTenDangNhap(username);

        // Nếu người dùng tồn tại
        if (nguoiDung != null) {
            // Lấy địa chỉ IP và thông tin trình duyệt của người dùng
            String ip = request.getRemoteAddr();
            String userAgent = request.getHeader("User-Agent");

            // Lưu lịch sử đăng nhập
            loginHistoryService.saveLoginHistory(nguoiDung.getId().intValue(), ip, userAgent, true);

            // Lấy tất cả hoạt động của người dùng này
            List<LogHeThong> logs = logHeThongService.findLogsByUserId(nguoiDung.getId());

            // Lưu danh sách log vào model
            request.setAttribute("logs", logs);
        }

//        // Điều hướng người dùng đến trang chủ sau khi đăng nhập thành công
//        response.sendRedirect("/home");

        // Điều hướng người dùng đến trang chủ tùy theo giao diện login
        String referer = request.getHeader("Referer");

        if (referer != null && referer.contains("/login-online")) {
            response.sendRedirect("/home-online");
        } else {
            response.sendRedirect("/home");
        }
    }


}
