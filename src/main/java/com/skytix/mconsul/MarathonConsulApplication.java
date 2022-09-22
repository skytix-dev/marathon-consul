package com.skytix.mconsul;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skytix.mconsul.utils.ValueHolder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import reactor.netty.tcp.SslProvider;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.http.HttpClient;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

/**
 * Created by marcde on 7/10/2015.
 */
@SpringBootApplication
@EnableScheduling
public class MarathonConsulApplication {
    private static final Logger log = LoggerFactory.getLogger(MarathonConsulApplication.class);
    private static final ValueHolder<Boolean> started = new ValueHolder<>(false);

    @Value("${disableSSLTrust:false}")
    private boolean disableSSLTrust;

    public static void main(String[] aArgs) {
        ToStringBuilder.setDefaultStyle(ToStringStyle.NO_CLASS_NAME_STYLE);
        new SpringApplicationBuilder(MarathonConsulApplication.class).web(WebApplicationType.NONE).run(aArgs);
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

    @Bean
    public HttpClient httpClient(SSLContext sslContext) {
        final HttpClient.Builder httpClientBuilder = HttpClient.newBuilder();

        if (disableSSLTrust) {
            httpClientBuilder.sslContext(sslContext);
        }

        return httpClientBuilder.build();
    }

    @Bean
    public SSLContext sslContext() throws Exception {
        final SSLContext sslContext;

        if (disableSSLTrust) {
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new SecureRandom());

        } else {
            sslContext = SSLContext.getDefault();
        }

        return sslContext;
    }

    @Bean
    public reactor.netty.http.client.HttpClient nettyHttpClient() throws Exception {
        final reactor.netty.http.client.HttpClient client = reactor.netty.http.client.HttpClient.create();

        if (disableSSLTrust) {

            final SslContext sslContext = SslContextBuilder
                    .forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE)
                    .build();

            return client.secure(SslProvider.builder()
                    .sslContext(sslContext)
                    .build()
            );

        }

        return client;
    }

    private static TrustManager[] trustAllCerts = new TrustManager[] {

            new X509TrustManager() {

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(
                        X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(
                        X509Certificate[] certs, String authType) {
                }
            }
    };

}
