package com.google.spanner.config

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.servlet.view.RedirectView

@org.springframework.web.bind.annotation.RestController
@RequestMapping("/")
class RestController {

    @RequestMapping(method = RequestMethod.GET)
     RedirectView redirectBaseUrl() {
        return new RedirectView("/swagger-ui.html", true, false)
    }


}

