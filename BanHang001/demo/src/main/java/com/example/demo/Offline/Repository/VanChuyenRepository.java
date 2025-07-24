package com.example.demo.Offline.Repository;

import com.example.demo.Entity.DonHang;
import com.example.demo.Entity.VanChuyen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VanChuyenRepository extends JpaRepository<VanChuyen,Long> {
    VanChuyen findByDonHang(DonHang donHang);
    List<VanChuyen> findByTrangThai(String trangThai);
    //List<VanChuyen> findByTrangThaiAndNguoiDungId(String trangThai, Long nguoiDungId);

    @Query("SELECT v FROM VanChuyen v WHERE v.trangThai = :trangThai AND v.donHang.nguoiDung.id = :nguoiDungId")
    List<VanChuyen> findByTrangThaiAndNguoiDungId(@Param("trangThai") String trangThai,
                                                  @Param("nguoiDungId") Long nguoiDungId);


}
