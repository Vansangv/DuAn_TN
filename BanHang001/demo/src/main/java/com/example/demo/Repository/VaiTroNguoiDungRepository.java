package com.example.demo.Repository;

import com.example.demo.Entity.VaiTroNguoiDung;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VaiTroNguoiDungRepository extends JpaRepository<VaiTroNguoiDung ,Long> {
    Page<VaiTroNguoiDung> findByNguoiDungHoTenContainingAndVaiTroTenVaiTroContaining(
            String hoTen, String tenVaiTro, Pageable pageable);
}
