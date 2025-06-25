package com.example.demo.Offline.Repository;

import com.example.demo.Entity.DiaChiGiaoHang;
import com.example.demo.Entity.NguoiDung;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiaChiGiaoHangRepository extends JpaRepository<DiaChiGiaoHang, Long> {
    DiaChiGiaoHang findByNguoiDungAndMacDinhTrue(NguoiDung nguoiDung);

    List<DiaChiGiaoHang> findByNguoiDung(NguoiDung nguoiDung);
}