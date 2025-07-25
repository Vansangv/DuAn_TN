package com.example.demo.Offline.Repository;

import com.example.demo.Entity.TraHang;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TraHangRepository extends JpaRepository<TraHang, Long> {
    List<TraHang> findByTrangThai(String trangThai);
    List<TraHang> findByDonHang_NguoiDung_Id(Long nguoiDungId);

}