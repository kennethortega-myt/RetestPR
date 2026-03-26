package pe.gob.onpe.pradminbackend.config;

import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class RabbitMQConfig {
	
	@Value("${app.sce-batch-pr.transmision.send-queue}")
    private String sendQueueData;

    @Value("${app.sce-batch-pr.transmision.reply-queue}")
    private String replyQueueData;
    
    @Value("${app.sce-batch-pr.transmision.send-queue-file}")
    private String sendQueueFile;

    @Value("${app.sce-batch-pr.transmision.reply-queue-file}")
    private String replyQueueFile; 
	
	@Bean
    public Queue requestQueueData() {
        return new Queue(sendQueueData);
    }
	
	@Bean
    public Queue responseQueueData() {
        return new Queue(replyQueueData);
    }
	
	@Bean
    public Queue requestQueueFile() {
        return new Queue(sendQueueFile);
    }
	
	@Bean
    public Queue responseQueueFile() {
        return new Queue(replyQueueFile);
    }
	
	@Bean
	public Queue responseQueueValidar() {
		return new Queue("validar",false);
	}

    @Bean
    public AmqpAdmin amqpAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }
	
}
