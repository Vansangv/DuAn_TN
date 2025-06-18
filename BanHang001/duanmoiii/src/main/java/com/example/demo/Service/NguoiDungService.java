package com.example.demo.Service;

import com.example.demo.Entity.NguoiDung;
import com.example.demo.PhanQuyen.CustomUserDetails;
import com.example.demo.Repository.NguoiDungRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NguoiDungService implements UserDetailsService {

    private final NguoiDungRepository nguoiDungRepository;

    @Autowired
    public NguoiDungService(NguoiDungRepository nguoiDungRepository) {
        this.nguoiDungRepository = nguoiDungRepository;

    }


    public void save(NguoiDung nguoiDung) {
        // KHÔNG encode ở đây nữa
        nguoiDungRepository.save(nguoiDung);
    }

    public boolean isUsernameTaken(String username) {
        return nguoiDungRepository.findByTenDangNhap(username).isPresent();
    }

    public boolean isEmailTaken(String email) {
        return nguoiDungRepository.findByEmail(email).isPresent();
    }

    public NguoiDung findByTenDangNhap(String tenDangNhap) {
        return nguoiDungRepository.findByTenDangNhap(tenDangNhap)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại!"));
    }

    public NguoiDung findByEmail(String email) {
        return nguoiDungRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với email: " + email));
    }

    public NguoiDung findById(Long id) {
        return nguoiDungRepository.findById(id).orElseThrow(() -> new RuntimeException("Người dùng không tồn tại!"));
    }


    @Override
    public UserDetails loadUserByUsername(String tenDangNhap) throws UsernameNotFoundException {
        NguoiDung nguoiDung = nguoiDungRepository.findByTenDangNhap(tenDangNhap)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng"));

        List<GrantedAuthority> authorities = nguoiDung.getVaiTros().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getTenVaiTro()))
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(
                nguoiDung.getTenDangNhap(),
                nguoiDung.getMatKhau(),
                authorities
        );
    }

}
