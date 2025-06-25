package com.example.demo.Offline.Repository;

import com.example.demo.Entity.DonHang;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OnlineDonHangRepository extends JpaRepository<DonHang,Long> {

    List<DonHang> findByNguoiDung_Id(Long nguoiDungId);
}
