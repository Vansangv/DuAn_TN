package com.example.demo.Offline.Repository;

import com.example.demo.Entity.SanPhamChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OnlineSanPhamChiTietRepository extends JpaRepository<SanPhamChiTiet, Long> {

    Optional<SanPhamChiTiet> findBySanPhamId(Long sanPhamId);
    List<SanPhamChiTiet> findBySanPhamIdIn(List<Long> sanPhamIds);
}