package pe.gob.onpe.consultaopbackend.config;

import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

@Configuration
public class RestemplateConfig {
	private static final int TIMEOUT_MIGRACION = 500000;

    private static final int TIMEOUT_FIRMA = 10000;

    @Bean(name="servicioMigracion")
    @Primary
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate(getClientHttpRequestFactory("migracion"));
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        return restTemplate;
    }

    @Bean(name="servicioFirma")
    public RestTemplate restTemplateServicioFirma() {
        RestTemplate restTemplate = new RestTemplate(getClientHttpRequestFactory("firma"));
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        return restTemplate;
    }

    private ClientHttpRequestFactory getClientHttpRequestFactory(String tipo) {
        RequestConfig config = null;
        if(tipo.equals("migracion")) {
            config = RequestConfig
                    .custom()
                    .setConnectionRequestTimeout(TIMEOUT_MIGRACION, TimeUnit.MILLISECONDS)
                    .setResponseTimeout(TIMEOUT_MIGRACION, TimeUnit.MILLISECONDS)
                    .build();
        } else if (tipo.equals("firma")) {
            config = RequestConfig
                    .custom()
                    .setConnectionRequestTimeout(TIMEOUT_FIRMA, TimeUnit.MILLISECONDS)
                    .setResponseTimeout(TIMEOUT_FIRMA, TimeUnit.MILLISECONDS)
                    .build();
        }

        CloseableHttpClient client = HttpClientBuilder
                .create()
                .setDefaultRequestConfig(config)
                .build();

        return new HttpComponentsClientHttpRequestFactory(client);
    }

}
