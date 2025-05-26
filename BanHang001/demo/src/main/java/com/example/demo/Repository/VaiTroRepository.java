package com.example.demo.Repository;

import com.example.demo.Entity.VaiTro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VaiTroRepository extends JpaRepository<VaiTro,Long> {
    Optional<VaiTro> findByTenVaiTro(String tenVaiTro);
}
