package server;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;

public class RabbitConfiguration {

    private static final String AMQP_HOST_NAME = "192.168.99.1";
    public static final String AMQP_EXCHANGE_NAME = "jserra";
    public static final String AMQP_USER_NAME = "xoom";
    public static final String AMQP_PASSWORD = "xoom123";

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory =
                new CachingConnectionFactory(AMQP_HOST_NAME);
        connectionFactory.setUsername(AMQP_USER_NAME);
        connectionFactory.setPassword(AMQP_PASSWORD);
        return connectionFactory;
    }

    @Bean
    public AmqpAdmin amqpAdmin() {
        return new RabbitAdmin(connectionFactory());
    }

    @Bean
    public RabbitTemplate rabbitTemplate() {
        return new RabbitTemplate(connectionFactory());
    }
}
