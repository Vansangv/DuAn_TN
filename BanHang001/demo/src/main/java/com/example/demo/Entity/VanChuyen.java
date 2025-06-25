package com.example.demo.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "van_chuyen")
@Data
public class VanChuyen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "don_hang_id")
    private DonHang donHang;

    @Column(name = "dia_chi_giao", length = 200)
    private String diaChiGiao;

    @Column(name = "trang_thai", length = 50)
    private String trangThai = "Đang xử lý";

    @Column(name = "ngay_giao_du_kien")
    private LocalDateTime ngayGiaoDuKien;

    @Column(name = "ngay_giao_thuc_te")
    private LocalDateTime ngayGiaoThucTe;

    @Column(name = "ghi_chu", length = 500)
    private String ghiChu;

}