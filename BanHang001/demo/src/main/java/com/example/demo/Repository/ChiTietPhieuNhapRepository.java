package com.example.demo.Repository;

import com.example.demo.Entity.ChiTietPhieuNhap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChiTietPhieuNhapRepository extends JpaRepository<ChiTietPhieuNhap, Long> {
    Page<ChiTietPhieuNhap> findBySanPhamChiTiet_SanPham_TenSanPhamContainingIgnoreCase(String tenSanPham, Pageable pageable);

}