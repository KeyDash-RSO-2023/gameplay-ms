package si.fri.rso.samples.imagecatalog.services.beans;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.UriInfo;
import java.time.Instant;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;

import org.eclipse.microprofile.metrics.annotation.Timed;

import si.fri.rso.samples.imagecatalog.lib.TypingSession;
import si.fri.rso.samples.imagecatalog.lib.TypingSessionProgress;
import si.fri.rso.samples.imagecatalog.models.converters.TypingSessionConverter;
import si.fri.rso.samples.imagecatalog.models.entities.TypingSessionEntity;


@RequestScoped
public class TypingSessionBean {

    private Logger log = Logger.getLogger(TypingSessionBean.class.getName());

    @Inject
    private EntityManager em;

    public List<TypingSession> getAllRecordsForTypingSession(long typingSessionId) {
        TypedQuery<TypingSessionEntity> query = em.createQuery(
                "SELECT t FROM TypingSessionEntity t WHERE t.typingSessionId = :typingSessionId", TypingSessionEntity.class);
        query.setParameter("typingSessionId", typingSessionId);

        List<TypingSessionEntity> records = query.getResultList();
        System.out.println("Size of the updates:" + records.size());
        return records.stream()
                .map(TypingSessionConverter::toDto)
                .collect(Collectors.toList());
    }



    public TypingSession createTypingSession(TypingSession ts) {
       TypingSessionEntity tsEntity = TypingSessionConverter.toEntity(ts);

       try {
           beginTx();
           em.persist(tsEntity);
           commitTx();
       }
       catch (Exception e) {
           rollbackTx();
       }

       return TypingSessionConverter.toDto(tsEntity);
   }

    public TypingSession getTypingSession(long typingSessionId) {

        TypingSessionEntity tsEntity = em.find(TypingSessionEntity.class, typingSessionId);


        if (tsEntity == null) {
            throw new NotFoundException();
        }

        TypingSession ts = TypingSessionConverter.toDto(tsEntity);

        return ts;
    }

    public TypingSession updateTypingSession(TypingSession ts, boolean valid, double wpm, double accuracy) {
        TypingSessionEntity tsEntity = TypingSessionConverter.toEntity(ts);
        TypingSessionEntity c = em.find(TypingSessionEntity.class, ts.getTypingSessionId());


        if (!valid) {
            tsEntity.setStatus("invalid");
        }

        if (tsEntity.getLastUpdateTime() == null) {
            System.out.println("Changing starting time");
            tsEntity.setStartTime(Instant.now());
        }
        tsEntity.setWpm(wpm);
        tsEntity.setAccuracy(accuracy);
        tsEntity.setLastUpdateTime(Instant.now());

        try {
            beginTx();
            tsEntity.setTypingSessionId(c.getTypingSessionId());
            tsEntity = em.merge(tsEntity);
            commitTx();

//            beginTx();
//            em.persist(tsEntity);
//            commitTx();
        }
        catch (Exception e) {
            rollbackTx();
        }

        TypingSession updatedTs = TypingSessionConverter.toDto(tsEntity);
        return updatedTs;
    }
    public TypingSession endTypingSession(TypingSession ts) {

        TypingSessionEntity tsEntity = TypingSessionConverter.toEntity(ts);
        TypingSessionEntity c = em.find(TypingSessionEntity.class, ts.getTypingSessionId());


        tsEntity.setStatus("ended");
        tsEntity.setEndTime(Instant.now());

        try {
            beginTx();
            tsEntity.setTypingSessionId(c.getTypingSessionId());
            em.merge(tsEntity);
            commitTx();
        }
        catch (Exception e) {
            rollbackTx();
        }

        TypingSession endedTs = TypingSessionConverter.toDto(tsEntity);

        return endedTs;
    }

   public TypingSession cancelTypingSession(TypingSession ts) {
       TypingSessionEntity tsEntity = TypingSessionConverter.toEntity(ts);

       tsEntity.setStatus("cancelled");
       tsEntity.setEndTime(Instant.now());

       try {
           beginTx();
           em.persist(tsEntity);
           commitTx();
       }
       catch (Exception e) {
           rollbackTx();
       }

       TypingSession cancelledts = TypingSessionConverter.toDto(tsEntity);
       return cancelledts;
   }

    private void beginTx() {
        if (!em.getTransaction().isActive()) {
            em.getTransaction().begin();
        }
    }

    private void commitTx() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().commit();
        }
    }

    private void rollbackTx() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
    }
}
