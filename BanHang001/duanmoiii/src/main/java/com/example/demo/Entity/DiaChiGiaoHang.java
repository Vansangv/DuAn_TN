package com.example.demo.Entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "dia_chi_giao_hang")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiaChiGiaoHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_dung_id")
    private NguoiDung nguoiDung;

    @Column(name = "dia_chi", length = 200)
    private String diaChi;

    @Column(name = "tinh_thanh", length = 100)
    private String tinhThanh;

    @Column(name = "quan_huyen", length = 100)
    private String quanHuyen;

    @Column(name = "phuong_xa", length = 100)
    private String phuongXa;

    @Column(name = "so_dien_thoai", length = 20)
    private String soDienThoai;

    @Column(name = "mac_dinh", columnDefinition = "TINYINT(1) DEFAULT 0")
    private Boolean macDinh = false;

}