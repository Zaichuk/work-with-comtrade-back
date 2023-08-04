package com.example.workwithcomtradefile.service;

import com.example.workwithcomtradefile.model.Measurement;
import com.example.workwithcomtradefile.repository.ComtradeRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
public class ComtradeService implements ComtradeServ {
    @Autowired
    private ComtradeRepository comtradeRepo;
    private double currentSetting;

    @Override
    public void setCurrentSetting(double currentSetting) {
        this.currentSetting = currentSetting;
        log.info("Successfully added currentSetting");
    }

    @Override
    public String getJsonMeasurements() {
        List<Measurement> measurements = new ArrayList<>(
                comtradeRepo.getAllMeasurements()
        );

        ObjectMapper mapper = new ObjectMapper();
        String dataInJson = null;
        try {
            dataInJson = mapper.writeValueAsString(measurements);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return dataInJson;
    }

    @Override
    public void addMeasurements(MultipartFile file) {
        parseFile(file);
        log.info("Successfully added measurements");
    }

    @Override
    public String defineShortCircuit(int start, int end) {
        List<Measurement> measurements = new ArrayList<>(
                comtradeRepo.getMeasurements(start, end)
        );
        measurements.forEach(el ->
                log.debug(
                        "id = {},  ia = {}, ib = {}, ic = {}  ",
                        el.getId(),
                        el.getIa(),
                        el.getIb(),
                        el.getIc()
                ));

        Measurement maxA = getAmplitude(
                measurements,
                Comparator.comparingDouble(el0 -> Math.abs(el0.getIa()))
        );
        log.info("Max value in phase A is {} which Id is {}", maxA.getIa(), maxA.getId());


        Measurement maxB = getAmplitude(
                measurements,
                Comparator.comparingDouble(el0 -> Math.abs(el0.getIb()))
        );
        log.info("Max value in phase B is {} which Id is {}", maxB.getIb(), maxB.getId());

        Measurement maxC = getAmplitude(
                measurements,
                Comparator.comparingDouble(el0 -> Math.abs(el0.getIc()))
        );
        log.info("Max value in phase C is {} which Id is {}", maxC.getIc(), maxC.getId());

        String resStr = "";
        if (compareWithSetting(maxA.getIa())) {
            resStr = resStr + "A";
        }
        if (compareWithSetting(maxB.getIb())) {
            resStr = resStr + "B";
        }
        if (compareWithSetting(maxC.getIc())) {
            resStr = resStr + "C";
        }

        return resStr;
    }

    @SneakyThrows
    private void parseFile(MultipartFile file) {
        List<Double> doubles = new ArrayList<>();
        InputStreamReader inputStreamReader = new InputStreamReader(
                file.getInputStream(),
                StandardCharsets.UTF_8
        );

        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        bufferedReader.lines().skip(1)
                .flatMap(el -> Arrays.stream(el.split(",")))
                .map(Double::parseDouble)
                .forEach(el -> {
                    doubles.add(el);
                    if (doubles.size() == 5) {
                        Measurement measurement = new Measurement();
                        measurement.setIa(doubles.get(1));
                        measurement.setIb(doubles.get(2));
                        measurement.setIc(doubles.get(3));
                        doubles.clear();
                        comtradeRepo.save(measurement);
                    }
                });
        bufferedReader.close();
    }

    private boolean compareWithSetting(Double max) {
        return Double.compare(getRms(max), currentSetting) == 1;
    }

    private Measurement getAmplitude(
            List<Measurement> list,
            Comparator<Measurement> comparator
    ) {
        return list.stream().max(comparator).get();
    }

    private double getRms(Double max) {
        return Math.abs(max) / Math.sqrt(2);
    }
}
