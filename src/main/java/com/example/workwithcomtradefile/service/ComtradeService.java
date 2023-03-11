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

//    private MultipartFile comtrade;
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
    // тут я хочу создать Mwasurement и засетить в него массив из даблов
        parseFile(file);
        log.info("Succsefully addeed measurements");
    }
    @Override
    public String defineShortCircuit(int start, int end){
        List<Measurement> list = comtradeRepo.getMeasurements(start,end);
        String resStr ="";
        list.forEach(el-> log.debug("id = {},  ia = {}, ib = {}, ic = {}  ",el.getId(), el.getIb(), el.getIb(), el.getIc()));
        for (Measurement m:list){
//            if (m.getIa() / Math.sqrt(2)>currentSetting){
//                resStr = resStr + "A";
//            }else if (m.getIb() / Math.sqrt(2) >currentSetting){
//                resStr = resStr + "B";
//            }else if (m.getIc() / Math.sqrt(2) >currentSetting){
//                resStr = resStr + "C";
//
//            }

          resStr =   compareWithSetting(resStr, Double::compare,"A", m.getIa());
          resStr =  compareWithSetting(resStr, Double::compare,"B", m.getIb());
          resStr =   compareWithSetting(resStr, Double::compare,"C", m.getIc());


        }


        return deleteRepeatingInStr(resStr);
// мне нужно

    }


    // тут я хочу вернуть массив из даблов
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
//              Measurement measurement = new Measurement();

              Function<Measurement,Measurement> setMeasurementFieldsFunction = el->{
                  el.setIa(measurArr[0]);
                  el.setIb(measurArr[1]);
                  el.setIc(measurArr[2]);
                  return el;

              };

//              measurement.setIa(measurArr[0]);
//                measurement.setIb(measurArr[1]);
//                measurement.setIc(measurArr[2]);


                // сделаьб три паралл потока (чтобы понимтаь для какой фазы добавляю) в которых поменять параметры measurement и затем применитть sava
//                   Arrays.stream(stringParts).skip(0).limit(3).map(el->Double.parseDouble(el)).forEach(el->);


                    comtradeRepo.save(setMeasurementFieldsFunction.apply(new Measurement()));
//                String time = stringParts[0];
//                String ia = stringParts[1];
//                String ib = stringParts[2];
//                String ic = stringParts[3];
//                log.info("Time = {} , ia ={}, ib ={}, ic={}", time, ia,ib,ic);
            }

            line = bufferedReader.readLine();
        }
        bufferedReader.close();


//        log.info("FIle read successfully");




    }

//    private <T extends Measurement> void setObjFields(T t, Consumer<T> consumer, Double[] doubles){
//
//
//            consumer.accept(t.setIa(doubles[0]));
//
//    }

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
