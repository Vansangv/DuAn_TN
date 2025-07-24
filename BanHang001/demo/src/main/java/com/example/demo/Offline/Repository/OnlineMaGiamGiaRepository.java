package com.example.demo.Offline.Repository;

import com.example.demo.Entity.MaGiamGia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OnlineMaGiamGiaRepository extends JpaRepository<MaGiamGia, Long> {

    @Query("SELECT m FROM MaGiamGia m WHERE m.trangThai = true AND m.soLuong > 0 AND CURRENT_DATE BETWEEN m.ngayBatDau AND m.ngayKetThuc")
    List<MaGiamGia> findAllActiveAndValid();

    List<MaGiamGia> findAllByTrangThaiTrueAndSoLuongGreaterThan(int soLuong);
}
