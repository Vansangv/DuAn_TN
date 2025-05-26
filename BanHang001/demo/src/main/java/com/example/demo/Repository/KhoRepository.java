package com.example.demo.Repository;

import com.example.demo.Entity.Kho;

import com.example.demo.Entity.SanPhamChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;


public interface KhoRepository extends JpaRepository<Kho, Long> {

    Kho findBySanPhamChiTiet(SanPhamChiTiet sanPhamChiTiet);
}
