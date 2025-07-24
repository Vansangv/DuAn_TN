package com.example.demo.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "vi_dien_tu")
public class ViDienTu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "nguoi_dung_id", nullable = false, unique = true)
    private NguoiDung nguoiDungId;

    @Column(name = "so_du", nullable = false)
    private Integer soDu = 0;

    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;

}
