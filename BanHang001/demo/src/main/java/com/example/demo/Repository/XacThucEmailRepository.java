package com.example.demo.Repository;

import com.example.demo.Entity.XacThucEmail;
import org.springframework.data.jpa.repository.JpaRepository;
public interface XacThucEmailRepository extends JpaRepository<XacThucEmail, Long> {
    XacThucEmail findTopByEmailOrderByThoiGianTaoDesc(String email);
}
