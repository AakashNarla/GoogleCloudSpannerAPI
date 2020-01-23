package com.google.spanner.config

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.servlet.view.RedirectView
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping('/')
class RestSwaggerController {

    @RequestMapping(method = RequestMethod.GET)
    RedirectView redirectBaseUrl() {
        new RedirectView('/swagger-ui.html', true, false)
    }

}
