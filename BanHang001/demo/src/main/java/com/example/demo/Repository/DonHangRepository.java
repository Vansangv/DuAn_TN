package com.example.demo.Repository;

import com.example.demo.Entity.DonHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface DonHangRepository extends JpaRepository<DonHang, Long> {

    // dùng sql

//    // 1. Đếm số đơn hàng trong ngày hôm nay
//    @Query(value = "SELECT COUNT_BIG(*) FROM don_hang WHERE CAST(ngay_tao AS DATE) = CAST(GETDATE() AS DATE)", nativeQuery = true)
//    long countTodayOrders();
//
//    // 2. Tổng doanh thu hôm nay
//    @Query(value = "SELECT SUM(so_tien) FROM lich_su_thanh_toan WHERE CAST(thoi_gian AS DATE) = CAST(GETDATE() AS DATE)", nativeQuery = true)
//    Integer sumTodayRevenue();
//
//    // 3. Doanh thu theo từng ngày trong tháng
//    @Query(value = """
//            SELECT DAY(dh.ngay_tao) AS thoiGian,
//                   SUM(lstt.so_tien) AS tongTien
//            FROM lich_su_thanh_toan lstt
//            JOIN don_hang dh ON lstt.don_hang_id = dh.id
//            WHERE MONTH(dh.ngay_tao) = :month AND YEAR(dh.ngay_tao) = :year
//            GROUP BY DAY(dh.ngay_tao)
//            ORDER BY DAY(dh.ngay_tao)
//            """, nativeQuery = true)
//    List<Map<String, Object>> getRevenueByDay(@Param("month") int month, @Param("year") int year);
//
//    // 4. Doanh thu theo từng tháng trong năm
//    @Query(value = """
//            SELECT MONTH(dh.ngay_tao) AS thoiGian,
//                   SUM(lstt.so_tien) AS tongTien
//            FROM lich_su_thanh_toan lstt
//            JOIN don_hang dh ON lstt.don_hang_id = dh.id
//            WHERE YEAR(dh.ngay_tao) = :year
//            GROUP BY MONTH(dh.ngay_tao)
//            ORDER BY MONTH(dh.ngay_tao)
//            """, nativeQuery = true)
//    List<Map<String, Object>> getRevenueByMonth(@Param("year") int year);
//
//    // 5. Doanh thu theo từng năm
//    @Query(value = """
//            SELECT YEAR(dh.ngay_tao) AS thoiGian,
//                   SUM(lstt.so_tien) AS tongTien
//            FROM lich_su_thanh_toan lstt
//            JOIN don_hang dh ON lstt.don_hang_id = dh.id
//            GROUP BY YEAR(dh.ngay_tao)
//            ORDER BY YEAR(dh.ngay_tao)
//            """, nativeQuery = true)
//    List<Map<String, Object>> getRevenueByYear();
//
//    // 6. Danh sách sản phẩm bán chạy
//    @Query(value = """
//            SELECT sp.ten_san_pham AS tenSanPham,
//                   SUM(ctdh.so_luong) AS tongSoLuong
//            FROM chi_tiet_don_hang ctdh
//            JOIN san_pham_chi_tiet spct ON ctdh.san_pham_chi_tiet_id = spct.id
//            JOIN san_pham sp ON spct.san_pham_id = sp.id
//            GROUP BY sp.ten_san_pham
//            ORDER BY tongSoLuong DESC
//            """, nativeQuery = true)
//    List<Map<String, Object>> findTopSellingProducts();


    ///dùng mysql

    @Query("SELECT COUNT(d) FROM DonHang d WHERE DATE(d.ngayTao) = CURRENT_DATE")
    long countTodayOrders();

    @Query("SELECT SUM(p.soTien) FROM LichSuThanhToan p WHERE DATE(p.thoiGian) = CURRENT_DATE")
    Integer sumTodayRevenue();

    @Query("SELECT new map(FUNCTION('DAY', d.ngayTao) as thoiGian, SUM(p.soTien) as tongTien) " +
            "FROM LichSuThanhToan p " +
            "JOIN p.donHang d " +
            "WHERE FUNCTION('MONTH', d.ngayTao) = :month AND FUNCTION('YEAR', d.ngayTao) = :year " +
            "GROUP BY FUNCTION('DAY', d.ngayTao) ORDER BY FUNCTION('DAY', d.ngayTao)")
    List<Map<String, Object>> getRevenueByDay(@Param("month") int month, @Param("year") int year);

    @Query("SELECT new map(FUNCTION('MONTH', d.ngayTao) as thoiGian, SUM(p.soTien) as tongTien) " +
            "FROM LichSuThanhToan p " +
            "JOIN p.donHang d " +
            "WHERE FUNCTION('YEAR', d.ngayTao) = :year " +
            "GROUP BY FUNCTION('MONTH', d.ngayTao) ORDER BY FUNCTION('MONTH', d.ngayTao)")
    List<Map<String, Object>> getRevenueByMonth(@Param("year") int year);

    @Query("SELECT new map(FUNCTION('YEAR', d.ngayTao) as thoiGian, SUM(p.soTien) as tongTien) " +
            "FROM LichSuThanhToan p " +
            "JOIN p.donHang d " +
            "GROUP BY FUNCTION('YEAR', d.ngayTao) ORDER BY FUNCTION('YEAR', d.ngayTao)")
    List<Map<String, Object>> getRevenueByYear();

    // Truy vấn lấy danh sách sản phẩm bán chạy
    @Query("SELECT new map(sp.tenSanPham as tenSanPham, SUM(ct.soLuong) as tongSoLuong) " +
            "FROM ChiTietDonHang ct " +
            "JOIN ct.sanPhamChiTiet spct " +
            "JOIN spct.sanPham sp " +
            "GROUP BY sp.tenSanPham " +
            "ORDER BY tongSoLuong DESC")
    List<Map<String, Object>> findTopSellingProducts();



}
