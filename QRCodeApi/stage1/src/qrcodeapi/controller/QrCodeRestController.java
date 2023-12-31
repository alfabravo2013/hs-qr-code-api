package qrcodeapi.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class QrCodeRestController {

    @GetMapping(path = "/api/health")
    @ResponseStatus(HttpStatus.OK)
    public void ping() {

    }

    @GetMapping(path = "/api/qrcode")
    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    public void getImage() {

    }
}
