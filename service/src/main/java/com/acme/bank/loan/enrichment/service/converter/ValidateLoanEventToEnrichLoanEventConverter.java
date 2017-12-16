package com.acme.bank.loan.enrichment.service.converter;

import com.acme.bank.loan.enrichment.domain.converter.AbstractConverter;
import com.acme.bank.loan.enrichment.domain.entity.PersonEntity;
import com.acme.bank.loan.enrichment.domain.event.EnrichLoanEvent;
import com.acme.bank.loan.enrichment.domain.event.ValidateLoanEvent;
import com.acme.bank.loan.enrichment.service.repository.PersonRepository;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

@Component
public class ValidateLoanEventToEnrichLoanEventConverter extends AbstractConverter<ValidateLoanEvent, EnrichLoanEvent> {

    private final PersonRepository personRepository;

    public ValidateLoanEventToEnrichLoanEventConverter(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public EnrichLoanEvent convert(ValidateLoanEvent validateLoanEvent) {
        EnrichLoanEvent enrichloanevent = new EnrichLoanEvent();
        enrichloanevent.setUuid(validateLoanEvent.getUuid());
        enrichloanevent.setPersonalId(validateLoanEvent.getPersonalId());
        enrichloanevent.setEnrichedTimestamp(ZonedDateTime.now());

        PersonEntity person = personRepository.findByPersonalId(validateLoanEvent.getPersonalId());
        if (person != null) {
            enrichloanevent.setBirthDate(person.getBirthDate());
            enrichloanevent.setGender(person.getGender());
            enrichloanevent.setFirstName(person.getFirstName());
            enrichloanevent.setLastName(person.getLastName());
            enrichloanevent.setNationality(person.getNationality());
        } else {
            // No person found
        }

        return enrichloanevent;
    }
}
