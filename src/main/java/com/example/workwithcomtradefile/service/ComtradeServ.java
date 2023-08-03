package com.example.workwithcomtradefile.service;

import org.springframework.web.multipart.MultipartFile;

public interface ComtradeServ {
    void addMeasurements(MultipartFile file);

    String defineShortCircuit(int start, int end);

    void setCurrentSetting(double currentSetting);

    String getJsonMeasurements();
}
