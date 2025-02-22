package integration.telex.airbyte.telexairbyteintegration.controller;

import integration.telex.airbyte.telexairbyteintegration.service.TelexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/webhook")
public class WebhookController {
    private final TelexService telexService;

    @Autowired
    public WebhookController(TelexService telexService) {
        this.telexService = telexService;
    }

    @PostMapping(value = "/airbyte", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void airbyteWebhook(@RequestBody String payload) {
        telexService.processPayload(payload);
    }
}
