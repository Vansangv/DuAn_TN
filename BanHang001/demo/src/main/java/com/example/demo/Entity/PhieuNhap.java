package com.example.demo.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "phieu_nhap")
public class PhieuNhap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "nguoi_nhap_id")
    private NguoiDung nguoiNhap;

    @Column(name = "ngay_nhap")
    private LocalDateTime ngayNhap ;

    @Column(name = "ghi_chu", length = 500)
    private String ghiChu;

}