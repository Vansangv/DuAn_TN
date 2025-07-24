package com.example.demo.Offline.Vi;

import com.example.demo.Entity.YeuCauNapTien;

import com.example.demo.Offline.Repository.YeuCauNapTienRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
public class XacNhanController {
    @Autowired
    private YeuCauNapTienRepository yeuCauNapTienRepository;

    @GetMapping("/xac-nhan-nap")
    public String xacNhanNap(@RequestParam("maGiaoDich") String maGiaoDich, Model model) {
        YeuCauNapTien yeuCau = yeuCauNapTienRepository.findByMaGiaoDich(maGiaoDich).orElse(null);

        if (yeuCau == null) {
            model.addAttribute("message", "❌ Không tìm thấy giao dịch.");
            model.addAttribute("success", false);
        } else {
            yeuCau.setTrangThai("DA_THANH_TOAN");
            yeuCau.setNgayTao(LocalDateTime.now()); // nếu cần
            yeuCauNapTienRepository.save(yeuCau);

            model.addAttribute("message", "✅ Nạp tiền thành công!");
            model.addAttribute("maGiaoDich", yeuCau.getMaGiaoDich());
            model.addAttribute("soTien", yeuCau.getSoTien());
            model.addAttribute("success", true);
        }

        return "quetma/da-quet";
    }

}
