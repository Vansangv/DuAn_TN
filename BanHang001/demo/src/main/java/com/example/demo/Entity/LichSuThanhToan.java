package com.example.demo.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "lich_su_thanh_toan")
public class LichSuThanhToan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "nguoi_dung_id", nullable = false)
    private NguoiDung nguoiDung;

    @ManyToOne
    @JoinColumn(name = "don_hang_id", referencedColumnName = "id")
    private DonHang donHang;

    @Column(name = "so_tien")
    private Integer soTien;

    @Column(name = "phuong_thuc", length = 50)
    private String phuongThuc;

    @Column(name = "thoi_gian")
    private LocalDateTime thoiGian;
}
