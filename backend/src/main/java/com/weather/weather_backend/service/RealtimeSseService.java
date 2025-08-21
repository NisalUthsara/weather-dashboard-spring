package com.weather.weather_backend.service;

import com.weather.weather_backend.dto.ObservationDto;
import com.weather.weather_backend.model.WeatherObservation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class RealtimeSseService {

    //Map locationId -> list of SseEmitters
    private final Map<Long, List<SseEmitter>> subscribers = new ConcurrentHashMap<>();

    /**
     * Subscribe to a location's updates. Caller must remove emitter on disconnect.
     */
    public SseEmitter subscribe(Long locationId){
        //set a generous timeout (30 min)
        SseEmitter emitter = new SseEmitter(30 * 60 * 1000L);

        //register emitter
        subscribers.compute(locationId, (k, list) -> {
            if (list == null) list = Collections.synchronizedList(new ArrayList<>());
            list.add(emitter);
            return list;
        });

        //cleanup on completion/timeout/error
        emitter.onCompletion(() -> removeEmitter(locationId, emitter));
        emitter.onTimeout(() -> removeEmitter(locationId, emitter));
        emitter.onError((ex) -> removeEmitter(locationId, emitter));

        return emitter;

    }

    private void removeEmitter(Long locationId, SseEmitter emitter){
        List<SseEmitter> list = subscribers.get(locationId);
        if (list != null){
            list.remove(emitter);
            if (list.isEmpty()) subscribers.remove(locationId);
        }
    }

    /**
     * push an update to all subscribers of a location.
     */
    public void pushUpdate(Long locationId, WeatherObservation obs){
        List<SseEmitter> list = subscribers.get(locationId);
        if (list == null || list.isEmpty()) return;

        ObservationDto dto = ObservationDto.builder()
                .locationId(locationId)
                .observedAt(obs.getObservedAt())
                .temperature(obs.getTemperature())
                .humidity(obs.getHumidity())
                .windSpeed(obs.getWindSpeed())
                .pressure(obs.getPressure())
                .conditionText(obs.getConditionText())
                .ageSeconds(0L) //fresh event
                .build();

        synchronized (list) {
            Iterator<SseEmitter> it = list.iterator();
            while (it.hasNext()){
                SseEmitter emitter = it.next();
                try {
                    emitter.send(SseEmitter.event().name("observation").data(dto));
                }catch (IOException ex) {
                    log.warn("Removing stale emitter for location {}", locationId);
                    it.remove();
                }
            }
        }
    }

}
