package pe.gob.onpe.consultaopcron.config;

import java.util.concurrent.TimeUnit;

import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestemplateConfig {
    private static final int TIMEOUT_FIRMA = 10000;

    @Bean(name="servicioExterno")
    public RestTemplate restTemplateServicioExterno() {
        RestTemplate restTemplate = new RestTemplate(getClientHttpRequestFactory());
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        return restTemplate;
    }

    private ClientHttpRequestFactory getClientHttpRequestFactory() {
        RequestConfig config = null;
            config = RequestConfig
                    .custom()
                    .setConnectionRequestTimeout(TIMEOUT_FIRMA, TimeUnit.MILLISECONDS)
                    .setResponseTimeout(TIMEOUT_FIRMA, TimeUnit.MILLISECONDS)
                    .build();

        CloseableHttpClient client = HttpClientBuilder
                .create()
                .setDefaultRequestConfig(config)
                .build();

        return new HttpComponentsClientHttpRequestFactory(client);
    }

}
