package com.kthowns.mobidic.api.controller.term;

import com.kthowns.mobidic.api.dto.common.term.TermDto;
import com.kthowns.mobidic.api.term.service.TermService;
import com.kthowns.mobidic.api.term.type.TermType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class TermViewController {
    private final TermService termService;

    @GetMapping("/terms/{type}")
    public String getTermPage(
            @PathVariable String type,
            @RequestParam(required = false) String version,
            Model model
    ) {
        TermDto term = termService.getTerm(TermType.valueOf(type.toUpperCase()), version);
        model.addAttribute("term", term);

        return "term/term";
    }
}
