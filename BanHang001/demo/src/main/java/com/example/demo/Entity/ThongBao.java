package com.example.demo.Entity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "thong_bao_nguoi_dung")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ThongBao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "nguoi_dung_id", nullable = false)
    private NguoiDung nguoiDung;

    @Column(name = "tieu_de", length = 200)
    private String tieuDe;

    @Column(name = "noi_dung", columnDefinition = "TEXT")
    private String noiDung;

    @Column(name = "da_doc")
    private Boolean daDoc = false;

    @Column(name = "thoi_gian_gui", columnDefinition = "DATETIME")
    private LocalDateTime thoiGianGui;

    @PrePersist
    public void prePersist() {
        if (thoiGianGui == null) {
            thoiGianGui = LocalDateTime.now();
        }
        if (daDoc == null) {
            daDoc = false;
        }
    }
}
