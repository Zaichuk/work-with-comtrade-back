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
//        em.createNamedQuery("select m from Measurement m where   m.id > :startrSet and m.id<:endSet", Measurement.class);
//        List<Measurement> result = em.createNativeQuery("select m from Measurement m where   m.id > :startrSet ","select m from Measurement m where   m.id < :endSet ")
        List<Measurement> result = em.createQuery("select m from Measurement m where   m.id > :startSet and m.id<:endSet", Measurement.class)
                .setParameter("startSet", start)
                .setParameter("endSet", end)
                .getResultList();
        return result;
    }





}
