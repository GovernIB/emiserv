package es.caib.emiserv.back.controller.mock;

import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Profile("mock")
@RestController
@RequestMapping(path = "/mock/desti")
public class MockDestController {

    @PostMapping(path = "/ok")
    public ResponseEntity<String> postOk() {
        return ResponseEntity
                .ok()
                .contentType(MediaType.TEXT_XML)
                .body("<ok/>");
    }

    @PostMapping(path = "/ko")
    public ResponseEntity<String> postKo() {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.TEXT_XML)
                .body("<ko/>");
    }

    @GetMapping(path = "/ok")
    public ResponseEntity<String> getOk() {
        return ResponseEntity
                .ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body("OK");
    }

    @GetMapping(path = "/ko")
    public ResponseEntity<String> getKo() {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.TEXT_PLAIN)
                .body("KO");
    }
}
