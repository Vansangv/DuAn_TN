package com.example.demo.Offline.Repository;

import com.example.demo.Entity.SanPhamTrongGioHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OnlineChiTietDonHangRepository extends JpaRepository<SanPhamTrongGioHang, Long> {
    @Query("SELECT COALESCE(SUM(c.soLuong), 0) " +
            "FROM ChiTietDonHang c " +
            "WHERE c.sanPhamChiTiet.sanPham.id = :sanPhamId")
    int tongSoLuongDaBanTheoSanPham(@Param("sanPhamId") Long sanPhamId);

}