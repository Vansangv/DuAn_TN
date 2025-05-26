package com.example.demo.Repository;

import com.example.demo.Entity.ChiTietDonHang;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChiTietDonHangRepository extends JpaRepository<ChiTietDonHang, Long> {
    List<ChiTietDonHang> findByDonHang_Id(Long donHangId);
}
