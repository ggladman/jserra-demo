package server.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Random;

@Configuration
public class JSerraDemoServiceConfiguration {

    @Value("${minimumInitialBalance:20}")
    private Integer minimumInitialBalance;

    @Value("${maximumInitialBalance:200}")
    private Integer maximumInitialBalance;

    @Bean
    public Random random() {
        return new Random();
    }

    @Bean
    public RandomIntegerGenerator randomIntegerGenerator(final Random random) {
        return new RandomIntegerGeneratorImpl(random);
    }

    @Bean
    public RandomBalanceGenerator randomBalanceGenerator(final RandomIntegerGenerator randomIntegerGenerator) {
        return new RandomBalanceGeneratorImpl(minimumInitialBalance, maximumInitialBalance, randomIntegerGenerator);
    }

    @Bean
    public BalanceService balanceService(final RandomBalanceGenerator randomBalanceGenerator) {
        return new BalanceServiceImpl(randomBalanceGenerator);
    }

    @Bean
    public UserRegistryService userRegistryService(final BalanceService balanceService) {
        return new UserRegistryServiceImpl(balanceService);
    }

}
