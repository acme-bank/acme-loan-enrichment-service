package com.acme.bank.loan.service.config;

import com.acme.bank.loan.domain.converter.CountryConverter;
import com.acme.bank.loan.domain.converter.GenderConverter;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = {"com.acme.bank.loan.service.repository"})
@EntityScan(basePackageClasses = {
        Jsr310JpaConverters.class,
        CountryConverter.class,
        GenderConverter.class},
        basePackages = {"com.acme.bank.loan.domain.entity"})
@Configuration
public class DatabaseConfig {

}
