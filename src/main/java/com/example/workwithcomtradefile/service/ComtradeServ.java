package com.example.workwithcomtradefile.service;

import org.springframework.web.multipart.MultipartFile;

public interface ComtradeServ {
    public void addMeasurements(MultipartFile file);
    public String defineShortCircuit(int start, int end);
    public void setCurrentSetting(double currentSetting);
}
