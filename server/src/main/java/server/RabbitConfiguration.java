package server;

import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfiguration {

    public static final String AMQP_EXCHANGE_NAME = "jserra";

    @Bean
    public FanoutExchange jserraExchange() {
        return new FanoutExchange(AMQP_EXCHANGE_NAME);
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public CommandLineRunner initializeRabbit(RabbitAdmin rabbitAdmin) {
        return args -> rabbitAdmin.initialize();
    }
}
