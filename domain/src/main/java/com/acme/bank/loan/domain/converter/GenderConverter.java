package com.acme.bank.loan.domain.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import java.util.Optional;

import com.acme.bank.loan.domain.model.Gender;

@Converter(autoApply = true)
public class GenderConverter implements AttributeConverter<Gender, String> {

    @Override
    public String convertToDatabaseColumn(Gender gender) {
        return Optional.ofNullable(gender).map(Gender::name).orElse(null);
    }

    @Override
    public Gender convertToEntityAttribute(String gender) {
        return Gender.valueOf(gender);
    }
}
