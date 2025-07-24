package com.example.demo.Offline.Repository;

import com.example.demo.Entity.NguoiDung;
import com.example.demo.Entity.ViDienTu;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ViDienTuRepository extends JpaRepository<ViDienTu,Long> {
    ViDienTu findByNguoiDungId(NguoiDung nguoiDung);
}
