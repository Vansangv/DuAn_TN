package com.example.demo.Online;

import com.example.demo.Entity.NguoiDung;
import com.example.demo.Entity.VaiTro;
import com.example.demo.Entity.VaiTroNguoiDung;
import com.example.demo.Repository.NguoiDungRepository;
import com.example.demo.Repository.VaiTroNguoiDungRepository;
import com.example.demo.Repository.VaiTroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/vai-tro-nguoi-dung")
public class VaiTroNguoiDungController {

    @Autowired
    private VaiTroNguoiDungRepository vaiTroNguoiDungRepository;

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    @Autowired
    private VaiTroRepository vaiTroRepository;

    @GetMapping
    public String danhSachVaiTroNguoiDung(
            @RequestParam(value = "searchNguoiDung", required = false, defaultValue = "") String searchNguoiDung,
            @RequestParam(value = "searchVaiTro", required = false, defaultValue = "") String searchVaiTro,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);

        Page<VaiTroNguoiDung> vaiTroNguoiDungs = vaiTroNguoiDungRepository
                .findByNguoiDungHoTenContainingAndVaiTroTenVaiTroContaining(
                        searchNguoiDung, searchVaiTro, pageable);

        model.addAttribute("vaiTroNguoiDungs", vaiTroNguoiDungs);
        model.addAttribute("nguoiDungs", nguoiDungRepository.findAll());
        model.addAttribute("vaiTros", vaiTroRepository.findAll());

        model.addAttribute("searchNguoiDung", searchNguoiDung);
        model.addAttribute("searchVaiTro", searchVaiTro);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", vaiTroNguoiDungs.getTotalPages());

        return "vaiTroNguoiDung/index";
    }



    @PostMapping("/update/{id}")
    public String capNhatVaiTroNguoiDung(@PathVariable("id") Long id,
                                         @RequestParam("vaiTroId") Long vaiTroId) {

        VaiTroNguoiDung vaiTroNguoiDung = vaiTroNguoiDungRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("VaiTroNguoiDung not found"));


        VaiTro vaiTro = vaiTroRepository.findById(vaiTroId)
                .orElseThrow(() -> new RuntimeException("VaiTro not found"));

        vaiTroNguoiDung.setVaiTro(vaiTro);

        vaiTroNguoiDungRepository.save(vaiTroNguoiDung);

        return "redirect:/vai-tro-nguoi-dung";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("vaiTro", new VaiTro());
        return "vaiTroNguoiDung/add";
    }

    @PostMapping("/save")
    public String saveVaiTro(@ModelAttribute VaiTro vaiTro) {
        vaiTroRepository.save(vaiTro);
        return "redirect:/vai-tro-nguoi-dung";
    }

}