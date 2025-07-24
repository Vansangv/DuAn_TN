package com.example.demo.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "lich_su_giao_dich_vi")
public class LichSuGiaoDichVi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nguoi_dung_id", nullable = false)
    private Integer nguoiDungId;

    @Column(name = "loai_giao_dich", length = 20, nullable = false)
    private String loaiGiaoDich; // NAP, RUT, THANH_TOAN

    @Column(name = "so_tien", nullable = false)
    private Integer soTien;

    @Column(name = "noi_dung")
    private String noiDung;

    @Column(name = "thoi_gian")
    private LocalDateTime thoiGian;

}
