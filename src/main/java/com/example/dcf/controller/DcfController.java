package com.example.dcf.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class DcfController {

    @GetMapping("/")
    public String showForm() {
        return "form";
    }

    @PostMapping("/calculate")
    public String calculateDCF(@RequestParam int years,
                               @RequestParam String cashFlows,
                               @RequestParam double discountRate,
                               @RequestParam double terminalGrowthRate,
                               Model model) {

        String[] flowStrings = cashFlows.split(",");
        if (flowStrings.length != years) {
            model.addAttribute("error", "Number of cash flows must match forecast years.");
            return "form";
        }

        double[] flows = new double[years];
        try {
            for (int i = 0; i < years; i++) {
                flows[i] = Double.parseDouble(flowStrings[i].trim());
            }
        } catch (NumberFormatException e) {
            model.addAttribute("error", "Invalid cash flow input.");
            return "form";
        }

        discountRate /= 100.0;
        terminalGrowthRate /= 100.0;

        double presentValue = 0.0;
        for (int i = 0; i < years; i++) {
            presentValue += flows[i] / Math.pow(1 + discountRate, i + 1);
        }

        double terminalValue = (flows[years - 1] * (1 + terminalGrowthRate)) / (discountRate - terminalGrowthRate);
        double terminalPV = terminalValue / Math.pow(1 + discountRate, years);

        double enterpriseValue = presentValue + terminalPV;

        model.addAttribute("presentValue", String.format("%.2f", presentValue));
        model.addAttribute("terminalValue", String.format("%.2f", terminalPV));
        model.addAttribute("enterpriseValue", String.format("%.2f", enterpriseValue));

        return "result";
    }
}