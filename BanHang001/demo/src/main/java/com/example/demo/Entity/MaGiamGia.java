package com.example.demo.Entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@Entity
@Table(name = "ma_giam_gia")
public class MaGiamGia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // AUTO_INCREMENT in MySQL is equivalent to @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "ma")
    private String ma;

    @Column(name = "mo_ta", unique = true)
    private String moTa;

    @Column(name = "loai_giam")
    private String loaiGiam;

    @Column(name = "phan_tram_giam")
    private Integer phanTramGiam;

    @Column(name = "so_tien_giam")
    private Integer soTienGiam;

    @Column(name = "ngay_bat_dau")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private Date ngayBatDau;

    @Column(name = "ngay_ket_thuc")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private Date ngayKetThuc;


    @Column(name = "trang_thai")
    private Boolean trangThai = true;

    @Column(name = "so_luong")
    private int soLuong;

}
