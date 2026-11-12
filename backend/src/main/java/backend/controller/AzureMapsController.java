
package backend.controller;

import backend.model.Coordinates;
import backend.service.AzureMapsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/maps")
public class AzureMapsController {

    private final AzureMapsService azureMapsService;

    public AzureMapsController(AzureMapsService azureMapsService) {
        this.azureMapsService = azureMapsService;
    }

    
@GetMapping("/geocode")
public Coordinates geocode(@RequestParam String address) {
    return azureMapsService.geocodeAddress(address);
}

}
