package com.example.demo.Repository;

import com.example.demo.Entity.LogHeThong;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LogHeThongRepository extends JpaRepository<LogHeThong, Long> {
    List<LogHeThong> findByNguoiDungId(Long nguoiDungId);
}
