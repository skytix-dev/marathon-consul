package com.skytix.mconsul.services.mesos;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.skytix.mconsul.event.TaskStatus;
import com.skytix.mconsul.services.marathon.MarathonService;
import com.skytix.mconsul.services.mesos.rest.MesosApi;
import com.skytix.mconsul.services.mesos.rest.MesosTask;
import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by marc on 13/11/2016.
 */
@Component
public class MesosService {
    @Autowired
    private ObjectMapper mObjectMapper;
    @Autowired
    private MarathonService mMarathonService;

    private final LoadingCache<String, MesosApi> mMesosCache = CacheBuilder.newBuilder().build(new CacheLoader<String, MesosApi>() {

        @Override
        public MesosApi load(String key) throws Exception {
            return Feign.builder()
                    .decoder(new JacksonDecoder(mObjectMapper))
                    .encoder(new JacksonEncoder(mObjectMapper))
                    .target(MesosApi.class, key);
        }
    });

    private MesosApi getRestInterface() {

        try {
            return mMesosCache.get(mMarathonService.getMesosLeader());

        } catch (ExecutionException aE) {
            throw new RuntimeException(aE);
        }

    }

    public TaskStatus getTaskState(String aTaskId) {
        final List<MesosTask> tasks = getRestInterface().getTasks().getTasks();

        for (MesosTask task : tasks) {

            if (task.getId().equals(aTaskId)) {
                return TaskStatus.valueOf(task.getState());
            }

        }

        return null;
    }

}
