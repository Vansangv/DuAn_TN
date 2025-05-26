package com.example.demo.Repository;

import com.example.demo.Entity.PhieuNhap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhieuNhapRepository extends JpaRepository<PhieuNhap, Long> {
    Page<PhieuNhap> findByNguoiNhap_HoTenContainingOrGhiChuContaining(String nguoiNhap, String ghiChu, Pageable pageable);
}
