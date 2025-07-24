package com.example.demo.Entity;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "su_dung_ma_giam_gia")
public class SuDungMaGiamGia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "nguoi_dung_id")
    private NguoiDung nguoiDung;

    @ManyToOne
    @JoinColumn(name = "ma_giam_gia_id")
    private MaGiamGia maGiamGia;

    @Column(name = "thoi_gian_su_dung")
    private LocalDateTime thoiGianSuDung;

}