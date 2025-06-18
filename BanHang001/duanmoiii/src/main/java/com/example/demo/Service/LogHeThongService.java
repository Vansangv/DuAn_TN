package com.example.demo.Service;

import com.example.demo.Entity.LogHeThong;
import com.example.demo.Repository.LogHeThongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LogHeThongService {

    @Autowired
    private LogHeThongRepository logHeThongRepository;

    @Autowired
    private NguoiDungService nguoiDungService;

    public List<LogHeThong> findAllLogs() {
        return logHeThongRepository.findAll();
    }

    public List<LogHeThong> findLogsByUserId(Long userId) {
        return logHeThongRepository.findByNguoiDungId(userId);
    }
    public Long findUserIdByUsername(String username) {
        return nguoiDungService.findByTenDangNhap(username).getId();
    }
}
