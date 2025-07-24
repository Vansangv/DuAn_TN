package com.example.demo.Offline.Repository;

import com.example.demo.Entity.DonHang;
import com.example.demo.Entity.NguoiDung;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OnlineDonHangRepository extends JpaRepository<DonHang,Long> {
    List<DonHang> findByTrangThai(String trangThai);
    List<DonHang> findByNguoiDungAndTrangThaiIgnoreCase(NguoiDung nguoiDung, String trangThai);
    List<DonHang> findByNguoiDungAndTrangThai(NguoiDung nguoiDung, String trangThai);
    List<DonHang> findByTrangThaiAndNguoiDungId(String trangThai, Long nguoiDungId);


}
