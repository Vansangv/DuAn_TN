package com.example.demo.Repository;

import com.example.demo.Entity.VaiTro;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VaiTroOfflineRepository extends JpaRepository<VaiTro, Long> {
    VaiTro findByTenVaiTro(String tenVaiTro);
}
