package com.example.demo.Online;

import com.example.demo.Entity.KichCo;
import com.example.demo.Repository.KichCoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/kich-co")
public class KichCoController {

    @Autowired
    private KichCoRepository repository;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("listKichCo", repository.findAll());
        model.addAttribute("page", "kich-co");
        return "kich_co/list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("kichCo", new KichCo());
        model.addAttribute("pageGroup", "san-pham");
        model.addAttribute("page", "kich-co");
        return "kich_co/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute KichCo kichCo) {
        repository.save(kichCo);
        return "redirect:/kich-co";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        KichCo kichCo = repository.findById(id).orElse(null);
        if (kichCo == null) return "redirect:/kich-co";
        model.addAttribute("kichCo", kichCo);
        model.addAttribute("pageGroup", "san-pham");
        model.addAttribute("page", "kich-co");
        return "kich_co/form";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        repository.deleteById(id);
        return "redirect:/kich-co";
    }

}
