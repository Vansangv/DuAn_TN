package com.example.demo.PhanQuyen;

import com.example.demo.Entity.NguoiDung;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

public class CustomUserDetails implements UserDetails {
    private final NguoiDung nguoiDung;

    public CustomUserDetails(NguoiDung nguoiDung) {
        this.nguoiDung = nguoiDung;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return nguoiDung.getVaiTros().stream()
                .map(vaiTro -> new SimpleGrantedAuthority(vaiTro.getTenVaiTro()))
                .collect(Collectors.toList());
    }



    public Long getId() {
        return nguoiDung.getId();
    }

    public NguoiDung getNguoiDung() {
        return nguoiDung;
    }

    @Override
    public String getPassword() {
        return nguoiDung.getMatKhau();
    }

    @Override
    public String getUsername() {
        return nguoiDung.getTenDangNhap();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return nguoiDung.getTrangThai(); // hoặc true nếu không cần bật/tắt tài khoản
    }
}
