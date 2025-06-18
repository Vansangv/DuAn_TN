package com.example.demo.Repository;

import com.example.demo.Entity.GioHang;
import com.example.demo.Entity.SanPhamTrongGioHang;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SanPhamTrongGioHangRepository extends JpaRepository<SanPhamTrongGioHang, Long> {

    @Query("SELECT s FROM SanPhamTrongGioHang s " +
            "JOIN FETCH s.gioHang g " +
            "JOIN FETCH s.sanPhamChiTiet sp " +
            "JOIN FETCH sp.sanPham p " +
            "WHERE LOWER(p.tenSanPham) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<SanPhamTrongGioHang> search(@Param("keyword") String keyword, Pageable pageable);


    List<SanPhamTrongGioHang> findByGioHangId(Long gioHangId);

    @Query("SELECT s FROM SanPhamTrongGioHang s WHERE s.gioHang.nguoiDung.id = :nguoiDungId")

    List<SanPhamTrongGioHang> findByGioHang_NguoiDung_Id(Long nguoiDungId);

    List<SanPhamTrongGioHang> findByGioHang(GioHang gioHang);


    SanPhamTrongGioHang findByGioHangIdAndSanPhamChiTietId(Long gioHangId, Long sanPhamChiTietId);

}