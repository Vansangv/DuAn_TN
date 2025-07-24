package com.example.demo.Entity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "xac_thuc_quen_mat_khau")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class XacThucQuenMatKhau {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "nguoi_dung_id", nullable = false)
    private NguoiDung nguoiDung;

    @Column(name = "ma_token", unique = true, nullable = false)
    private String maToken;

    @Column(name = "thoi_gian_tao", columnDefinition = "DATETIME")
    private LocalDateTime thoiGianTao;

    @Column(name = "thoi_gian_het_han", columnDefinition = "DATETIME", nullable = false)
    private LocalDateTime thoiGianHetHan;

    @Column(name = "da_su_dung")
    private Boolean daSuDung = false;

    @PrePersist
    public void prePersist() {
        if (thoiGianTao == null) {
            thoiGianTao = LocalDateTime.now();
        }
        if (daSuDung == null) {
            daSuDung = false;
        }
    }

}
