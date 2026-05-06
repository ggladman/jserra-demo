package server;

import org.springframework.amqp.core.FanoutExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfiguration {

    public static final String AMQP_EXCHANGE_NAME = "jserra";

    @Bean
    public FanoutExchange jserraExchange() {
        return new FanoutExchange(AMQP_EXCHANGE_NAME);
    }
}
