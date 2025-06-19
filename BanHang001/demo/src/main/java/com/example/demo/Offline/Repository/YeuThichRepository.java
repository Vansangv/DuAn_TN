package com.example.demo.Offline.Repository;

import com.example.demo.Entity.YeuThichSanPham;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository

public interface YeuThichRepository extends JpaRepository<YeuThichSanPham,Integer> {

    @Query(
            value = "SELECT s.id AS id, " +
                    "s.ten_san_pham AS tenSanPham, " +
                    "s.anh_dai_dien AS anhDaiDien, " +
                    "MIN(ct.gia) AS gia, " +
                    "MAX(y.ngay_them) AS ngayThem " +
                    "FROM yeu_thich_san_pham y " +
                    "JOIN san_pham s ON y.san_pham_id = s.id " +
                    "JOIN san_pham_chi_tiet ct ON ct.san_pham_id = s.id " +
                    "WHERE y.nguoi_dung_id = :nguoiDungId " +
                    "GROUP BY s.id, s.ten_san_pham, s.anh_dai_dien " +
                    "ORDER BY ngayThem DESC",
            nativeQuery = true
    )
    List<YeuThichView> findAllByNguoiDungId(@Param("nguoiDungId") Integer nguoiDungId);
    void deleteByNguoiDungIdAndSanPhamId(Integer nguoiDungId, Integer sanPhamId);


}
