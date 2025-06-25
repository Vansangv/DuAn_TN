package com.example.demo.Offline.Repository;

import com.example.demo.Entity.GioHang;
import com.example.demo.Entity.SanPhamChiTiet;
import com.example.demo.Entity.SanPhamTrongGioHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OnlineSanPhamTrongGioHangRepository extends JpaRepository<SanPhamTrongGioHang, Long> {
    Optional<SanPhamTrongGioHang> findByGioHangAndSanPhamChiTiet(GioHang gioHang, SanPhamChiTiet sanPhamChiTiet);
    List<SanPhamTrongGioHang> findByGioHang(GioHang gioHang);

    @Query("SELECT COUNT(sp) FROM SanPhamTrongGioHang sp WHERE sp.gioHang.nguoiDung.id = :nguoiDungId")
    int demSoLuongSanPhamTrongGio(@Param("nguoiDungId") Long nguoiDungId);

}