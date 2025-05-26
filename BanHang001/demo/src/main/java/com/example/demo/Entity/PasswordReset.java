package com.example.demo.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "xac_thuc_quen_mat_khau")
public class PasswordReset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;  // Sử dụng Integer vì trường 'id' trong bảng là INT

    @Column(name = "nguoi_dung_id")
    private Integer nguoiDungId;  // ID người dùng

    @Column(name = "ma_token", unique = true)
    private String maToken;  // Mã token cho việc xác thực

    @Column(name = "thoi_gian_tao")
    private LocalDateTime thoiGianTao;

    @Column(name = "thoi_gian_het_han")
    private LocalDateTime thoiGianHetHan;

    @Column(name = "da_su_dung")
    private Boolean daSuDung = false;  // Trạng thái đã sử dụng hay chưa (true nếu đã sử dụng)

    // Quan hệ với bảng người dùng
    @ManyToOne
    @JoinColumn(name = "nguoi_dung_id", referencedColumnName = "id", insertable = false, updatable = false)
    private NguoiDung nguoiDung;  // Liên kết với đối tư

}
