package com.skytix.mconsul;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skytix.mconsul.utils.ValueHolder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Created by marcde on 7/10/2015.
 */
@SpringBootApplication
@EnableScheduling
public class MarathonConsulApplication {
    private static final Logger log = LoggerFactory.getLogger(MarathonConsulApplication.class);
    private static final ValueHolder<Boolean> started = new ValueHolder<>(false);

    public static void main(String[] aArgs) {
        ToStringBuilder.setDefaultStyle(ToStringStyle.NO_CLASS_NAME_STYLE);

        final SpringApplication app = new SpringApplication(MarathonConsulApplication.class);
        app.run(aArgs);
        log.info("marathon-consul is now running...");
        started.setValue(true);
    }

    @Bean
    public ObjectMapper createObjectMapper() {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        return mapper;
    }

    @Bean
    public ValueHolder<Boolean> appStartedValue() {
        return started;
    }

}
