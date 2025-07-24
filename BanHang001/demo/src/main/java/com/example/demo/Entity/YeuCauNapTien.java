package com.example.demo.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "yeu_cau_nap_tien")
public class YeuCauNapTien {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nguoi_dung_id", nullable = false)
    private Long nguoiDungId;

    @Column(name = "so_tien", nullable = false)
    private Long soTien;

    @Column(name = "ma_giao_dich", unique = true, nullable = false)
    private String maGiaoDich;

    @Column(name = "trang_thai", length = 20)
    private String trangThai; // CHO_THANH_TOAN, DA_THANH_TOAN, HUY

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;
}
