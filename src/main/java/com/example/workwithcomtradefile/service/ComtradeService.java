package com.example.workwithcomtradefile.service;

import com.example.workwithcomtradefile.model.Measurement;
import com.example.workwithcomtradefile.repository.ComtradeRepo;
import com.example.workwithcomtradefile.repository.ComtradeRepository;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Service
@Slf4j
public class ComtradeService implements ComtradeServ {

    @Autowired
    private ComtradeRepository comtradeRepo;
    private double currentSetting;

    @Override
    public void setCurrentSetting(double currentSetting) {
        this.currentSetting = currentSetting;
        log.info("Succsefully addeed currentSetting");

    }

    @Override
    public void addMeasurements(MultipartFile file) {
        parseFile(file);
        log.info("Succsefully addeed measurements");
    }

    @Override
    public String defineShortCircuit(int start, int end) {
        List<Measurement> list = new ArrayList<>(comtradeRepo.getMeasurements(start, end));

        list.forEach(el -> log.debug("id = {},  ia = {}, ib = {}, ic = {}  ", el.getId(), el.getIa(), el.getIb(), el.getIc()));


        Measurement maxA = list.stream().max((el0, el1) -> Double.compare(Math.abs(el0.getIa()), Math.abs(el1.getIa()))).get();
        log.info("Max value in phase A is {}", maxA);

        Measurement maxB = list.stream().max((el0, el1) -> Double.compare(Math.abs(el0.getIb()), Math.abs(el1.getIb()))).get();
        log.info("Max value in phase B is {}", maxB);

        Measurement maxC = list.stream().max((el0, el1) -> Double.compare(Math.abs(el0.getIc()), Math.abs(el1.getIc()))).get();
        log.info("Max value in phase C is {}", maxC);

        String resStr = "";

        if (compareWithSetting(maxA.getIa())) {
            resStr = resStr + "A";
        } if (compareWithSetting(maxB.getIb())){
            resStr = resStr + "B";
        } if (compareWithSetting(maxC.getIc())){
            resStr = resStr + "C";
        }


        return resStr;

    }



    @SneakyThrows
    private void parseFile(MultipartFile file) {
        List<Double> doubles = new ArrayList<>();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));

        bufferedReader.lines().skip(1)
                .flatMap(el -> Arrays.stream(el.split(","))).map(Double::parseDouble)
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

        if (Double.compare(getRms(max), currentSetting) == 1) {

            return true;
        }else {
            return false;
        }

    }

    private double getRms(Double max) {
        return Math.abs(max) / Math.sqrt(2);
    }

}
