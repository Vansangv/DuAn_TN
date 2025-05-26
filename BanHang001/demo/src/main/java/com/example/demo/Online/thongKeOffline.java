package com.example.demo.Online;

import com.example.demo.Repository.DonHangRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/thong-ke")
public class thongKeOffline {

    @Autowired
    private DonHangRepository donHangRepository;

    @GetMapping
    public String thongKe(@RequestParam(name = "filter", defaultValue = "day") String filter, Model model) {
        long todayOrders = donHangRepository.countTodayOrders();
        Integer todayRevenue = donHangRepository.sumTodayRevenue();
        if (todayRevenue == null) todayRevenue = 0;

        List<Map<String, Object>> topProducts = donHangRepository.findTopSellingProducts();

        int currentMonth = LocalDate.now().getMonthValue();
        int currentYear = LocalDate.now().getYear();

        List<Map<String, Object>> doanhThuNgay = null;
        List<Map<String, Object>> doanhThuThang = null;
        List<Map<String, Object>> doanhThuNam = null;

        switch (filter) {
            case "day":
                doanhThuNgay = donHangRepository.getRevenueByDay(currentMonth, currentYear);
                break;
            case "month":
                doanhThuThang = donHangRepository.getRevenueByMonth(currentYear);
                break;
            case "year":
                doanhThuNam = donHangRepository.getRevenueByYear();
                break;
            default:
                doanhThuNgay = donHangRepository.getRevenueByDay(currentMonth, currentYear);
        }

        model.addAttribute("todayOrders", todayOrders);
        model.addAttribute("todayRevenue", todayRevenue);
        model.addAttribute("topSellingProducts", topProducts);
        model.addAttribute("doanhThuNgay", doanhThuNgay);
        model.addAttribute("doanhThuThang", doanhThuThang);
        model.addAttribute("doanhThuNam", doanhThuNam);
        model.addAttribute("filter", filter);

        return "thongke/thong-ke";
    }
}