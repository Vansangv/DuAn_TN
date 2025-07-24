package com.example.demo.Repository;

import com.example.demo.Entity.XacThucQuenMatKhau;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface XacThucQuenMatKhauRepository extends JpaRepository<XacThucQuenMatKhau,Long> {
    Optional<XacThucQuenMatKhau> findByMaToken(String maToken);
}
