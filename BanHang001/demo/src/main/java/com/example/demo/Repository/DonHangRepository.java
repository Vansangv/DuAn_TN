package com.example.demo.Repository;

import com.example.demo.Entity.DonHang;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface DonHangRepository extends JpaRepository<DonHang, Long> {

    Page<DonHang> findByNguoiDung_HoTenContainingIgnoreCase(String hoTen, Pageable pageable);

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
