package com.example.demo.Online;

import com.example.demo.Entity.SanPham;
import com.example.demo.Repository.LoaiSanPhamRepository;
import com.example.demo.Repository.SanPhamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/sanpham")
public class SanPhamController {

    @Autowired
    private SanPhamRepository sanPhamRepository;

    @Autowired
    private LoaiSanPhamRepository loaiSanPhamRepository;

    @GetMapping
    public String listSanPham(@RequestParam(defaultValue = "") String searchTerm,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "5") int size,
                              Model model) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("ngayTao").descending());   // Phân trang theo ID
        Page<SanPham> sanPhamPage;

        if (searchTerm.isEmpty()) {
            // Nếu không tìm kiếm, lấy tất cả sản phẩm
            sanPhamPage = sanPhamRepository.findAll(pageable);
        } else {
            // Nếu có tìm kiếm, tìm kiếm theo tên sản phẩm
            sanPhamPage = sanPhamRepository.findByTenSanPhamContaining(searchTerm, pageable);
        }

        model.addAttribute("listSanPham", sanPhamPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", sanPhamPage.getTotalPages());
        model.addAttribute("searchTerm", searchTerm);
        return "sanpham/list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("sanPham", new SanPham());
        model.addAttribute("loaiSanPhams", loaiSanPhamRepository.findAll());
        return "sanpham/add";
    }

    @PostMapping("/add")
    public String addSanPham(@ModelAttribute SanPham sanPham,
                             @RequestParam("fileAnh") MultipartFile fileAnh) throws IOException {

        sanPham.setNgayTao(LocalDateTime.now());

        // Lưu sản phẩm trước để lấy ID
        SanPham savedSanPham = sanPhamRepository.save(sanPham);

        if (!fileAnh.isEmpty()) {
            String originalFileName = fileAnh.getOriginalFilename();
            String fileNameWithId = savedSanPham.getId() + "_" + originalFileName;

            // Lưu ảnh vào static/uploads
            String uploadDir = new ClassPathResource("static/uploads").getFile().getAbsolutePath();
            Path path = Paths.get(uploadDir + File.separator + fileNameWithId);
            Files.copy(fileAnh.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            // Cập nhật ảnh
            savedSanPham.setAnhDaiDien(fileNameWithId);
            sanPhamRepository.save(savedSanPham);
        }

        return "redirect:/sanpham";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        SanPham sanPham = sanPhamRepository.findById(id).orElse(null);
        if (sanPham != null) {
            model.addAttribute("sanPham", sanPham);
            model.addAttribute("loaiSanPhams", loaiSanPhamRepository.findAll());
            return "sanpham/edit";
        }
        return "redirect:/sanpham";
    }

    @PostMapping("/edit")
    public String editSanPham(@ModelAttribute SanPham sanPham,
                              @RequestParam("fileAnhMoi") MultipartFile fileAnhMoi) throws IOException {
        SanPham existingSanPham = sanPhamRepository.findById(sanPham.getId()).orElse(null);
        if (existingSanPham != null) {
            existingSanPham.setTenSanPham(sanPham.getTenSanPham());
            existingSanPham.setMoTa(sanPham.getMoTa());
            existingSanPham.setTrangThai(sanPham.getTrangThai());
            existingSanPham.setLoaiSanPham(sanPham.getLoaiSanPham());

            if (!fileAnhMoi.isEmpty()) {
                String originalFileName = fileAnhMoi.getOriginalFilename();
                String fileNameWithId = existingSanPham.getId() + "_" + originalFileName;

                // Lưu ảnh mới vào static/uploads
                String uploadDir = new ClassPathResource("static/uploads").getFile().getAbsolutePath();
                Path path = Paths.get(uploadDir + File.separator + fileNameWithId);
                Files.copy(fileAnhMoi.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                existingSanPham.setAnhDaiDien(fileNameWithId);
            }

            sanPhamRepository.save(existingSanPham);
        }

        return "redirect:/sanpham";
    }

    @GetMapping("/delete/{id}")
    public String deleteSanPham(@PathVariable Long id) {
        sanPhamRepository.deleteById(id);
        return "redirect:/sanpham";
    }
}
