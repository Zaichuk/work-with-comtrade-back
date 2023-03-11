package com.example.workwithcomtradefile.repository;

import com.example.workwithcomtradefile.model.Measurement;

import java.util.List;

public interface ComtradeRepository {
    public void   save(Measurement measurement);
    public List<Measurement> getMeasurements(int start, int end);

}
