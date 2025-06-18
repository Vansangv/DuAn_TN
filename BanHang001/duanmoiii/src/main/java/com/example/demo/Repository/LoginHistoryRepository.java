package com.example.demo.Repository;

import com.example.demo.Entity.LoginHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoginHistoryRepository extends JpaRepository<LoginHistory, Long> {
    List<LoginHistory> findByNguoiDungTenDangNhap(String tenDangNhap);
}
