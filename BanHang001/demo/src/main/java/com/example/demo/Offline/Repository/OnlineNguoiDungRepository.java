package com.example.demo.Offline.Repository;

import com.example.demo.Entity.GioHang;
import com.example.demo.Entity.NguoiDung;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OnlineNguoiDungRepository extends JpaRepository<NguoiDung, Long> {


}