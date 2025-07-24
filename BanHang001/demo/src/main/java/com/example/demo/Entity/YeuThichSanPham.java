package com.example.demo.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "yeu_thich_san_pham")
public class YeuThichSanPham {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "nguoi_dung_id", nullable = false)
    private NguoiDung nguoiDung;

    @ManyToOne
    @JoinColumn(name = "san_pham_chi_tiet_id", nullable = false)
    private SanPhamChiTiet sanPhamChiTiet;

    @Column(name = "ngay_them")
    private LocalDateTime ngayThem;

}
