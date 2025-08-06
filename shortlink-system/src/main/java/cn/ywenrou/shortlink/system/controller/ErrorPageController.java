package cn.ywenrou.shortlink.system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 错误页面控制器
 */
@Controller
@RequestMapping("/page")
public class ErrorPageController {

    /**
     * 显示404错误页面
     */
    @GetMapping("/notfound")
    public String notFound() {
        return "notfound";
    }
} 