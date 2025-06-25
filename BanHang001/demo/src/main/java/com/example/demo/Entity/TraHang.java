package com.example.demo.Entity;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "tra_hang")
@Data
public class TraHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "don_hang_id")
    private DonHang donHang;

    @Column(name = "ly_do", length = 500)
    private String lyDo;

    @Column(name = "ngay_tra")
    private LocalDateTime ngayTra = LocalDateTime.now();

    @Column(name = "trang_thai", length = 50)
    private String trangThai = "Chờ xử lý";
}
