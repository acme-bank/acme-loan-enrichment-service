package com.acme.bank.loan.enrichment.service.config;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan({
        "com.acme.bank.loan.enrichment.service"
})
@Configurable
public class ServiceAutoConfig {

}
