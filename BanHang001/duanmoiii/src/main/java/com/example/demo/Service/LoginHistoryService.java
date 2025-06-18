package com.example.demo.Service;

import com.example.demo.Entity.LoginHistory;
import com.example.demo.Repository.LoginHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class LoginHistoryService {

    @Autowired
    private LoginHistoryRepository loginHistoryRepository;

    public void saveLoginHistory(Integer nguoiDungId, String diaChiIp, String thietBi, boolean thanhCong) {
        LoginHistory history = new LoginHistory();
        history.setNguoiDungId(nguoiDungId);
        history.setDiaChiIp(diaChiIp);
        history.setThietBi(thietBi);
        history.setThoiGianDangNhap(new Date());
        history.setThanhCong(thanhCong);
        loginHistoryRepository.save(history);
    }

    public List<LoginHistory> layLichSuTheoTenDangNhap(String tenDangNhap) {
        return loginHistoryRepository.findByNguoiDungTenDangNhap(tenDangNhap);
    }
}
