package com.example.demo.Repository;

import com.example.demo.Entity.MaGiamGia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface MaGiamGiaRepository extends JpaRepository<MaGiamGia, Long> {

    Page<MaGiamGia> findByTrangThaiTrueAndMaContainingIgnoreCase(String keyword, Pageable pageable);
    Page<MaGiamGia> findByTrangThaiFalseAndMaContainingIgnoreCase(String keyword, Pageable pageable);

    MaGiamGia findByMa(String ma);
    //Optional<MaGiamGia> findByMa(String ma);

    @Query("SELECT m FROM MaGiamGia m WHERE m.trangThai = true AND m.soLuong > 0 AND m.ngayBatDau <= :now AND m.ngayKetThuc >= :now")
    List<MaGiamGia> findAllHieuLuc(@Param("now") Date now);
}
