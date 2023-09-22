package com.cooba.webauthn.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ReactAppController {

    @RequestMapping(value = {"/"})
    public String getIndex(HttpServletRequest request) {
        return "/index.html";
    }
}
