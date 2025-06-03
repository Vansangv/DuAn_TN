package com.example.demo.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "san_pham")
public class SanPham {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ten_san_pham", length = 100)
    private String tenSanPham;

    @Column(name = "mo_ta", length = 500)
    private String moTa;

    @ManyToOne
    @JoinColumn(name = "loai_san_pham_id")
    private LoaiSanPham loaiSanPham;

    @Column(name = "anh_dai_dien", length = 255)
    private String anhDaiDien;

    @Column(name = "trang_thai", length = 50)
    private String trangThai = "Còn hàng";

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao ;




}
