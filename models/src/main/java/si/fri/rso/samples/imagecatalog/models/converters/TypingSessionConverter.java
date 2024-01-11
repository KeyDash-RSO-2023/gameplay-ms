package si.fri.rso.samples.imagecatalog.models.converters;

import si.fri.rso.samples.imagecatalog.lib.TypingSession;
import si.fri.rso.samples.imagecatalog.models.entities.TypingSessionEntity;

public class TypingSessionConverter {

    public static TypingSession toDto(TypingSessionEntity entity) {

        TypingSession ts = new TypingSession();
        ts.setTypingSessionId(entity.getTypingSessionId());
        ts.setTextToType(entity.getTextToType());
        ts.setLanguage(entity.getLanguage());
        ts.setLength(entity.getLength());
        ts.setPunctuation(entity.isPunctuation());
        ts.setStartTime(entity.getStartTime());
        ts.setEndTime(entity.getEndTime());
        ts.setLastUpdateime(entity.getLastUpdateTime());
        ts.setStatus(entity.getStatus());
        ts.setWpm(entity.getWpm());
        ts.setAccuracy(entity.getAccuracy());

        return ts;

    }

    public static TypingSessionEntity toEntity(TypingSession ts) {

        TypingSessionEntity entity = new TypingSessionEntity();
        entity.setTextToType(ts.getTextToType());
        entity.setLanguage(ts.getLanguage());
        entity.setLength(ts.getLength());
        entity.setPunctuation(ts.isPunctuation());
        entity.setStartTime(ts.getStartTime());
        entity.setLastUpdateTime(ts.getLastUpdateTime());
        entity.setEndTime(ts.getEndTime());
        entity.setStatus(ts.getStatus());
        entity.setWpm(ts.getWpm());
        entity.setAccuracy(ts.getAccuracy());


        return entity;

    }

}
