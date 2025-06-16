package com.part3.deokhugam.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class FrontendController {
  @RequestMapping({ "/",
      "/books", "/books/**",
      "/reviews", "/reviews/**" })
  public String forwardIndex() {
    return "forward:/index.html";
  }
}