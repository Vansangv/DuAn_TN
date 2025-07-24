package com.example.demo.Online;
import com.example.demo.Entity.MauSac;
import com.example.demo.Repository.MauSacRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/mau-sac")
public class MauSacController {

    @Autowired
    private MauSacRepository repository;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("listMauSac", repository.findAll());
        model.addAttribute("page", "mau-sac");
        return "mau_sac/list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("mauSac", new MauSac());
        model.addAttribute("pageGroup", "san-pham");
        model.addAttribute("page", "mau-sac");
        return "mau_sac/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute MauSac mauSac) {
        repository.save(mauSac);
        return "redirect:/mau-sac";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        MauSac mauSac = repository.findById(id).orElse(null);
        if (mauSac == null) {
            return "redirect:/mau-sac";
        }
        model.addAttribute("mauSac", mauSac);
        model.addAttribute("pageGroup", "san-pham");
        model.addAttribute("page", "mau-sac");
        return "mau_sac/form";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        repository.deleteById(id);
        return "redirect:/mau-sac";
    }

}
