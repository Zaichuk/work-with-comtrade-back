package com.example.workwithcomtradefile.repository;

import com.example.workwithcomtradefile.model.Measurement;

import java.util.List;

public interface ComtradeRepository {
    void save(Measurement measurement);

    List<Measurement> getMeasurements(int start, int end);

    List<Measurement> getAllMeasurements();


}
