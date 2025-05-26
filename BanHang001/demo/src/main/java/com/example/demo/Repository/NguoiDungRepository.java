package com.example.demo.Repository;

import com.example.demo.Entity.NguoiDung;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NguoiDungRepository extends JpaRepository<NguoiDung, Long> {

    Optional<NguoiDung> findByTenDangNhap(String tenDangNhap);
    // Phương thức trả về Optional
    Optional<NguoiDung> findByEmail(String email);
    NguoiDung findByHoTen(String hoTen);


    Page<NguoiDung> findByTenDangNhapContainingIgnoreCaseOrHoTenContainingIgnoreCaseOrEmailContainingIgnoreCaseOrSoDienThoaiContainingIgnoreCase(
            String tenDangNhap,
            String hoTen,
            String email,
            String soDienThoai,
            Pageable pageable);
}
