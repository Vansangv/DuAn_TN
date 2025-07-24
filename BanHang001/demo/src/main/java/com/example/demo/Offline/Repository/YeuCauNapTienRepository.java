package com.example.demo.Offline.Repository;

import com.example.demo.Entity.YeuCauNapTien;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface YeuCauNapTienRepository extends JpaRepository<YeuCauNapTien, Long> {
    Optional<YeuCauNapTien> findByMaGiaoDich(String maGiaoDich);
}