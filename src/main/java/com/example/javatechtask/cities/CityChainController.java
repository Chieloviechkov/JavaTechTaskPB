package com.example.javatechtask.cities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;

@Controller
public class CityChainController {

    private final CityChainService cityChainService;

    @Value("${cities.file.path}")
    private String citiesFilePath;

    public CityChainController(CityChainService cityChainService) {
        this.cityChainService = cityChainService;
    }

    @GetMapping("/")
    public String cityChain(Model model) {
        try {
            String longestChain = cityChainService.findLongestCityChain(citiesFilePath);
            model.addAttribute("chain", longestChain);
        } catch (IOException e) {
            model.addAttribute("error", "Не удалось прочитать файл: " + e.getMessage());
        }
        return "cityChain";
    }
}
