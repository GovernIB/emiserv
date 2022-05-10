package es.caib.emiserv.api.externa.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.annotations.ApiIgnore;

@Controller
public class ApiRestDocumentationController {


    @ApiIgnore
    @RequestMapping(value = {"/rest"}, method = RequestMethod.GET)
    public String documentacio() {
        return "redirect:/swagger-ui.html";
    }

}
