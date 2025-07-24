package com.example.demo.Offline.Repository;

import com.example.demo.Entity.ChiTietDonHang;
import com.example.demo.Entity.DonHang;
import com.example.demo.Entity.SanPhamTrongGioHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OnlineChiTietDonHangRepository extends JpaRepository<ChiTietDonHang, Long> {
    @Query("SELECT COALESCE(SUM(c.soLuong), 0) " +
            "FROM ChiTietDonHang c " +
            "WHERE c.sanPhamChiTiet.sanPham.id = :sanPhamId")
    int tongSoLuongDaBanTheoSanPham(@Param("sanPhamId") Long sanPhamId);

    List<ChiTietDonHang> findByDonHang(DonHang donHang);

    @Query("""
    SELECT COUNT(ct)
    FROM ChiTietDonHang ct
    JOIN ct.donHang dh
    JOIN ct.sanPhamChiTiet spct
    JOIN spct.sanPham sp
    WHERE dh.nguoiDung.id = :nguoiDungId
    AND sp.id = :sanPhamId
    AND dh.trangThai = 'Đã giao'
""")
    long countDonMuaSanPham(@Param("nguoiDungId") Long nguoiDungId,
                            @Param("sanPhamId") Long sanPhamId);

}