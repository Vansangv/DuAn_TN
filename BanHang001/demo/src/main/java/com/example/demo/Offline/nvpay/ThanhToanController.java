package com.example.demo.Offline.nvpay;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/thanh-toan")
public class ThanhToanController {
    @GetMapping("/trang-thai")
    public String trangThaiThanhToan(@RequestParam("vnp_Amount") String amount,
                                     @RequestParam("vnp_BankCode") String bankCode,
                                     @RequestParam("vnp_BankTranNo") String bankTranNo,
                                     @RequestParam("vnp_CardType") String cardType,
                                     @RequestParam("vnp_OrderInfo") String orderInfo,
                                     @RequestParam("vnp_PayDate") String payDate,
                                     @RequestParam("vnp_ResponseCode") String responseCode,
                                     @RequestParam("vnp_TmnCode") String tmnCode,
                                     @RequestParam("vnp_TransactionNo") String transactionNo,
                                     @RequestParam("vnp_TransactionStatus") String transactionStatus,
                                     @RequestParam("vnp_TxnRef") String txnRef,
                                     @RequestParam("vnp_SecureHash") String secureHash,
                                     Model model
    ) {

        long amountVND = Long.parseLong(amount) / 100;

        // Xử lý ngày giờ
        String payDateStr = payDate.substring(6,8) + "/" + payDate.substring(4,6) + "/" + payDate.substring(0,4)
                + " " + payDate.substring(8,10) + ":" + payDate.substring(10,12);

        // Đưa dữ liệu sang View
        model.addAttribute("amount", amountVND);
        model.addAttribute("bankCode", bankCode);
        model.addAttribute("bankTranNo", bankTranNo);
        model.addAttribute("cardType", cardType);
        model.addAttribute("orderInfo", orderInfo);
        model.addAttribute("payDate", payDateStr);
        model.addAttribute("responseCode", responseCode);
        model.addAttribute("tmnCode", tmnCode);
        model.addAttribute("transactionNo", transactionNo);
        model.addAttribute("transactionStatus", transactionStatus);
        model.addAttribute("txnRef", txnRef);
        return "BanHangOnline/thanh-toan-thanh-cong";
    }
}
