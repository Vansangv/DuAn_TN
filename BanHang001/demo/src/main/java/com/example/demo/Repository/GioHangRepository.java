package com.example.demo.Repository;

import com.example.demo.Entity.GioHang;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface GioHangRepository extends JpaRepository<GioHang, Long> {

    Page<GioHang> findByNguoiDungIdOrderByNgayTaoDesc(Long nguoiDungId, Pageable pageable);
    long countByNguoiDungId(Long nguoiDungId);

    @Query("SELECT gh FROM GioHang gh WHERE gh.nguoiDung.id = :nguoiDungId AND gh.trangThai = 1 ORDER BY gh.ngayTao DESC")
    Page<GioHang> findGioHangByNguoiDungAndTrangThai(Long nguoiDungId, Pageable pageable);


    //Optional<GioHang> findByNguoiDungId(Long nguoiDungId);
    List<GioHang> findByNguoiDungId(Long nguoiDungId);

    List<GioHang> findByNguoiDungIdAndTrangThai(Long nguoiDungId, Byte trangThai);

}
