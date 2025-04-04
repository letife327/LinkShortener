package az.texnoera.link_shortener.controller;

import az.texnoera.link_shortener.request.UrlRequest;
import az.texnoera.link_shortener.service.UrlService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequestMapping
@RestController
@RequiredArgsConstructor
@CrossOrigin
@Validated
public class UrlController {
    private final UrlService urlService;

    @PostMapping("/change-url")
    public String changeUrl(@RequestBody @Valid UrlRequest urlRequest) {
        return urlService.changeUrl(urlRequest);
    }

    @GetMapping("/{short-code}")
    public void getShortenerUrl(@PathVariable(value = "short-code") String shortCode,
                                                HttpServletResponse response) {
         urlService.getShortenerUrl(shortCode,response);
    }
}
