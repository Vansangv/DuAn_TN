package com.example.demo.Offline.Repository;

import com.example.demo.Entity.DanhGiaSanPham;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OnlineDanhGiaSanPhamRepository extends JpaRepository<DanhGiaSanPham, Long> {
    List<DanhGiaSanPham> findBySanPham_Id(Long sanPhamId);
}
