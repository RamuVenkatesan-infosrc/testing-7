package com.banking.api.config;

import com.banking.account.service.AccountService;
import com.banking.transaction.service.TransactionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

@Configuration
public class ServiceConfig implements WebMvcConfigurer {

    @Bean
    public AccountService accountService() {
        return new AccountService();
    }

    @Bean
    public TransactionService transactionService(AccountService accountService) {
        return new TransactionService(accountService);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("X-CSRF-TOKEN")
                .allowCredentials(true)
                .maxAge(3600);
    }
}