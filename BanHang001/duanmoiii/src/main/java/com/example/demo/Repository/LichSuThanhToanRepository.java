package com.example.demo.Repository;

import com.example.demo.Entity.LichSuThanhToan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LichSuThanhToanRepository extends JpaRepository<LichSuThanhToan, Long> {

    Page<LichSuThanhToan> findByNguoiDung_HoTenContainingIgnoreCase(String hoTen, Pageable pageable);

}
