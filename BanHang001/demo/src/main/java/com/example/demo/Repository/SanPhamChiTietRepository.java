package com.example.demo.Repository;

import com.example.demo.Entity.SanPham;
import com.example.demo.Entity.SanPhamChiTiet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SanPhamChiTietRepository extends JpaRepository<SanPhamChiTiet, Long> {

    List<SanPhamChiTiet> findBySoLuongGreaterThan(int soLuong);

    @Query("SELECT DISTINCT spct.sanPham FROM SanPhamChiTiet spct")
    List<SanPham> findDistinctSanPham();

    List<SanPhamChiTiet> findBySanPhamIdIn(List<Long> sanPhamIds);

    Optional<SanPhamChiTiet> findBySanPhamId(Long sanPhamId);

    // Tìm kiếm theo tên sản phẩm (hoặc thêm theo màu/kích cỡ nếu muốn)
    @Query("SELECT s FROM SanPhamChiTiet s WHERE " +
            "LOWER(s.sanPham.tenSanPham) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<SanPhamChiTiet> searchByTenSanPham(@Param("keyword") String keyword, Pageable pageable);

    Page<SanPhamChiTiet> findBySanPham_TenSanPhamContainingIgnoreCase(String keyword, Pageable pageable);
    Page<SanPhamChiTiet> findAll(Pageable pageable);
}
