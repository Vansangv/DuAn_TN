package com.example.demo.Offline.Repository;

import com.example.demo.Entity.MaGiamGia;
import com.example.demo.Entity.NguoiDung;
import com.example.demo.Entity.SuDungMaGiamGia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SuDungMaGiamGiaRepository extends JpaRepository<SuDungMaGiamGia,Long> {
    Optional<SuDungMaGiamGia> findByNguoiDungAndMaGiamGia(NguoiDung nguoiDung, MaGiamGia maGiamGia);
    List<SuDungMaGiamGia> findByNguoiDung(NguoiDung nguoiDung);
    boolean existsByNguoiDungAndMaGiamGia(NguoiDung nguoiDung, MaGiamGia maGiamGia);
}
