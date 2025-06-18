package com.example.demo.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "lich_su_dang_nhap")
public class LoginHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // Sử dụng Long vì trường 'id' trong bảng là BIGINT

    @Column(name = "nguoi_dung_id")
    private Integer nguoiDungId;  // ID người dùng

    @Column(name = "dia_chi_ip")
    private String diaChiIp;  // Địa chỉ IP

    @Column(name = "thiet_bi")
    private String thietBi;  // Thiết bị sử dụng

    @Column(name = "thoi_gian_dang_nhap")
    @Temporal(TemporalType.TIMESTAMP)
    private Date thoiGianDangNhap;  // Thời gian đăng nhập

    @Column(name = "thanh_cong")
    private Boolean thanhCong;  // Kết quả đăng nhập (true nếu thành công, false nếu thất bại)

    @ManyToOne
    @JoinColumn(name = "nguoi_dung_id", referencedColumnName = "id", insertable = false, updatable = false)
    private NguoiDung nguoiDung;  // Liên kết với đối tượng User (người dùng)

}