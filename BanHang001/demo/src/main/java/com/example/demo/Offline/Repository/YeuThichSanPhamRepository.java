package com.example.demo.Offline.Repository;

import com.example.demo.Entity.NguoiDung;
import com.example.demo.Entity.SanPhamChiTiet;
import com.example.demo.Entity.YeuThichSanPham;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface YeuThichSanPhamRepository extends JpaRepository<YeuThichSanPham, Long> {
    boolean existsByNguoiDungAndSanPhamChiTiet(NguoiDung nguoiDung, SanPhamChiTiet sanPhamChiTiet);
    long countByNguoiDung_Id(Long nguoiDungId);
    List<YeuThichSanPham> findByNguoiDung_Id(Long nguoiDungId);
    @Query("SELECT y.sanPhamChiTiet.id FROM YeuThichSanPham y WHERE y.nguoiDung.id = :nguoiDungId")
    Set<Long> findSanPhamChiTietIdsByNguoiDung_Id(@Param("nguoiDungId") Long nguoiDungId);

}
