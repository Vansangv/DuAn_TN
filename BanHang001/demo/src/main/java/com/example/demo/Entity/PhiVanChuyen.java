package com.example.demo.Entity;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "phi_van_chuyen")
@Data
public class PhiVanChuyen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "don_hang_id")
    private DonHang donHang;

    @Column(name = "so_tien")
    private Integer soTien;

    @Column(name = "mo_ta", length = 255)
    private String moTa;

    @Column(name = "nguoi_tra", length = 20)
    private String nguoiTra;
}