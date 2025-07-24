package com.example.demo.Offline.Repository;

import com.example.demo.Entity.GioHang;
import com.example.demo.Entity.NguoiDung;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OnlineGioHangRepository extends JpaRepository<GioHang, Long> {

    Optional<GioHang> findByNguoiDung_Id(Long nguoiDungId);

    GioHang findByNguoiDung(NguoiDung nguoiDung);
    //List<GioHang> findByNguoiDung(NguoiDung nguoiDung);

    Optional<GioHang> findByNguoiDung_IdAndTrangThai(Long nguoiDungId, Integer trangThai);
    Optional<GioHang> findByNguoiDungAndTrangThai(NguoiDung nguoiDung, Integer trangThai);
    //Optional<GioHang> findFirstByNguoiDungAndTrangThai(NguoiDung nguoiDung, int trangThai);


}
