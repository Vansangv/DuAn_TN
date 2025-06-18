package com.example.demo.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "san_pham_chi_tiet")
public class SanPhamChiTiet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "san_pham_id", nullable = false)
    private SanPham sanPham;

    @ManyToOne
    @JoinColumn(name = "mau_sac_id", nullable = false)
    private MauSac mauSac;

    @ManyToOne
    @JoinColumn(name = "kich_co_id", nullable = false)
    private KichCo kichCo;

    @Column(name = "so_luong")
    private int soLuong;

    @Column(name = "gia")
    private Integer gia;

}
