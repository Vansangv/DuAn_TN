package com.example.demo.Offline.Repository;

import com.example.demo.Entity.GioHang;
import com.example.demo.Entity.SanPhamChiTiet;
import com.example.demo.Entity.SanPhamTrongGioHang;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OnlineSanPhamTrongGioHangRepository extends JpaRepository<SanPhamTrongGioHang, Long> {
}