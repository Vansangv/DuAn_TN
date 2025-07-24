package com.example.demo.Offline.Repository;

import com.example.demo.Entity.TraHang;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TraHangRepository extends JpaRepository<TraHang, Long> {
    List<TraHang> findByTrangThai(String trangThai);

}