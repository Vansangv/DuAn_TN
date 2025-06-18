package com.example.demo.Repository;

import com.example.demo.Entity.SanPham;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SanPhamRepository extends JpaRepository<SanPham, Long> {

    @Query("SELECT s FROM SanPham s ORDER BY s.ngayTao DESC")
    List<SanPham> findSanPhamNoiBat();

    Page<SanPham> findByTenSanPhamContaining(String tenSanPham, Pageable pageable);


}
