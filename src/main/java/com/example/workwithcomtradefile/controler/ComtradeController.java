package com.example.workwithcomtradefile.controler;

import com.example.workwithcomtradefile.service.ComtradeServ;
import com.example.workwithcomtradefile.service.ComtradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class ComtradeController {

    @Autowired
    ComtradeServ comtradeService;


    @PostMapping("/data/upload")
    public void getComtradeFile(@RequestBody MultipartFile file){
//        comtradeService.setComtrade(file);
        comtradeService.addMeasurements(file);
    }

    @GetMapping("/data/findFault")
    public  String getMeasuringRange(@RequestParam int startIndex, @RequestParam int endIndex){
    return comtradeService.defineShortCircuit(startIndex,endIndex);
}
    @GetMapping("/saveSetPoint")
    public void getCurrentSetting(@RequestParam double setPoint){
        comtradeService.setCurrentSetting(setPoint);
    }




}
