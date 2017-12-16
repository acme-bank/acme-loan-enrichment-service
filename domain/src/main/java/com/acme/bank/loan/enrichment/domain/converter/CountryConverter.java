package com.acme.bank.loan.enrichment.domain.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import java.util.Optional;

import com.acme.bank.loan.enrichment.domain.model.Country;

@Converter(autoApply = true)
public class CountryConverter implements AttributeConverter<Country, String> {

    @Override
    public String convertToDatabaseColumn(Country country) {
        return Optional.ofNullable(country)
                .map(Country::name)
                .orElse(null);
    }

    @Override
    public Country convertToEntityAttribute(String code) {
        return Country.valueOf(code);
    }
}
