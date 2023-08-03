package com.example.workwithcomtradefile.repository;

import com.example.workwithcomtradefile.model.Measurement;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
@Transactional
public class ComtradeRepo implements ComtradeRepository {
    @PersistenceContext
    private EntityManager em;

    @Override
    public void   save(Measurement measurement){
        if (measurement.getId()==0){
            em.persist(measurement);
        }else {
             em.merge(measurement);
        }
    }

    @Override
    public List<Measurement> getMeasurements(int start, int end){
        return em.createQuery(
                "select m from Measurement m where   m.id > :startSet and m.id<:endSet", Measurement.class
                ).setParameter("startSet", start)
                .setParameter("endSet", end)
                .getResultList();
    }

    @Override
    public List<Measurement> getAllMeasurements() {
      return em.createQuery(
                "select  m from  Measurement m", Measurement.class
        ).getResultList();
    }
}
