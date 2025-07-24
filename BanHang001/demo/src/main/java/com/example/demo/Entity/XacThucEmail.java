package com.example.demo.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "xac_thuc_email")
public class XacThucEmail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(name = "ma_xac_thuc", nullable = false, length = 10)
    private String maXacThuc;

    @Column(name = "thoi_gian_tao", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime thoiGianTao;

    @Column(name = "thoi_gian_het_han", nullable = false)
    private LocalDateTime thoiGianHetHan;

    @Column(name = "da_xac_thuc", columnDefinition = "TINYINT(1) DEFAULT 0")
    private Boolean daXacThuc = false;
}
