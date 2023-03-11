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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

@Service
@Slf4j
public class ComtradeService implements ComtradeServ{

    @Autowired
    private ComtradeRepository comtradeRepo;
    private double currentSetting;

    @Override
    public void setCurrentSetting(double currentSetting) {
        this.currentSetting = currentSetting;
        log.info("Succsefully addeed currentSetting");

    }

    @Override
    public void addMeasurements(MultipartFile file){
        parseFile(file);
        log.info("Succsefully addeed measurements");
    }
    @Override
    public String defineShortCircuit(int start, int end){
        List<Measurement> list = comtradeRepo.getMeasurements(start,end);
        String resStr ="";
        list.forEach(el-> log.debug("id = {},  ia = {}, ib = {}, ic = {}  ",el.getId(), el.getIb(), el.getIb(), el.getIc()));
        for (Measurement m:list){


          resStr =   compareWithSetting(resStr, Double::compare,"A", m.getIa());
          resStr =  compareWithSetting(resStr, Double::compare,"B", m.getIb());
          resStr =   compareWithSetting(resStr, Double::compare,"C", m.getIc());


        }


        return deleteRepeatingInStr(resStr);

    }


    @SneakyThrows
    private void parseFile(MultipartFile file) {
        InputStream inputStream = file.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        String line = bufferedReader.readLine();
        line = bufferedReader.readLine();
        while (line != null){
            String[] stringParts = line.split(",");
            if (stringParts.length > 3){
         Double[] measurArr =  Arrays.stream(stringParts).skip(0)
                 .limit(3).map(Double::parseDouble).toArray(Double[]::new);

              Function<Measurement,Measurement> setMeasurementFieldsFunction = el->{
                  el.setIa(measurArr[0]);
                  el.setIb(measurArr[1]);
                  el.setIc(measurArr[2]);
                  return el;

              };

                    comtradeRepo.save(setMeasurementFieldsFunction.apply(new Measurement()));
            }

            line = bufferedReader.readLine();
        }
        bufferedReader.close();






    }



    private String compareWithSetting(String str, Comparator<Double> comparator, String phase, Double measure){
            measure = Math.abs(measure / Math.sqrt(2));
            if (comparator.compare(measure,currentSetting)==1){
                str = str + phase;
            }

        return str;
    }

    private String deleteRepeatingInStr(String string){
        return string.chars()
                .sorted().distinct()
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();
    }


}
