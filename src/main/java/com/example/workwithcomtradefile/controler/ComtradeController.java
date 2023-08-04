package com.example.workwithcomtradefile.controler;

import com.example.workwithcomtradefile.service.ComtradeServ;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class ComtradeController {
    @Autowired
    ComtradeServ comtradeService;

    @PostMapping("/data/upload")
    public void uploadComtradeFile(
            @RequestBody MultipartFile file
    ) {
        comtradeService.addMeasurements(file);
    }

    @GetMapping("/data/findFault")
    public String getMeasuringRange(
            @RequestParam int startIndex,
            @RequestParam int endIndex
    ) {
        return comtradeService.defineShortCircuit(
                startIndex,
                endIndex
        );
    }

    @GetMapping("/saveSetPoint")
    public void getCurrentSetting(
            @RequestParam double setPoint
    ) {
        comtradeService.setCurrentSetting(setPoint);
    }

    @GetMapping("/getComtrade")
    public String getComtradeFile() {
        return comtradeService.getJsonMeasurements();
    }
}
