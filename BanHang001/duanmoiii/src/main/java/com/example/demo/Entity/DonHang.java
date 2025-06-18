package com.example.demo.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "don_hang")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DonHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_dung_id")
    private NguoiDung nguoiDung;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dia_chi_giao_hang_id")
    private DiaChiGiaoHang diaChiGiaoHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_giam_gia_id")
    private MaGiamGia maGiamGia;

    @Column(name = "tong_tien", nullable = false)
    private Integer tongTien;

    @Column(name = "hinh_thuc_mua", length = 50)
    private String hinhThucMua;

    @Column(name = "phuong_thuc_thanh_toan", length = 50)
    private String phuongThucThanhToan;

    @Column(name = "trang_thai", length = 50)
    private String trangThai ;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

}
