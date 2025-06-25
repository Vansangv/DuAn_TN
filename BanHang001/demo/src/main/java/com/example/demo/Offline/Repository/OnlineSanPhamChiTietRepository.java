package com.example.demo.Offline.Repository;

import com.example.demo.Entity.SanPhamChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OnlineSanPhamChiTietRepository extends JpaRepository<SanPhamChiTiet, Long> {

    List<SanPhamChiTiet> findBySanPhamIdIn(List<Long> sanPhamIds);

    @Query("SELECT s FROM SanPhamChiTiet s WHERE " +
            "(:tenLoai IS NULL OR s.sanPham.loaiSanPham.tenLoai = :tenLoai) AND " +
            "(:min IS NULL OR :max IS NULL OR s.gia BETWEEN :min AND :max)")
    List<SanPhamChiTiet> findByLoaiSanPhamAndGia(@Param("tenLoai") String tenLoai,
                                                 @Param("min") Integer min,
                                                 @Param("max") Integer max);

    // Thêm phương thức tìm theo từ khóa tên sản phẩm
    @Query("SELECT spct FROM SanPhamChiTiet spct " +
            "WHERE LOWER(spct.sanPham.tenSanPham) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<SanPhamChiTiet> searchByTenSanPham(@Param("keyword") String keyword);
}