package com.example.demo.Offline.Repository;

import java.time.LocalDateTime;

public interface YeuThichView {
    Integer getId();
    String getTenSanPham();
    String getAnhDaiDien();
    Integer getGia();

    LocalDateTime getNgayThem(); // thêm cái này vì có alias

}

