package com.acme.bank.loan.service.converter;

import com.acme.bank.loan.domain.converter.AbstractConverter;
import com.acme.bank.loan.domain.entity.PersonEntity;
import com.acme.bank.loan.domain.event.EnrichLoanEvent;
import com.acme.bank.loan.domain.event.ValidateLoanEvent;
import com.acme.bank.loan.service.repository.PersonRepository;
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
        enrichloanevent.setEventId(validateLoanEvent.getEventId());
        enrichloanevent.setPersonId(validateLoanEvent.getPersonId());
        enrichloanevent.setEnrichedTimestamp(ZonedDateTime.now());

        PersonEntity person = personRepository.findByPersonId(validateLoanEvent.getPersonId());
        if (person != null) {
            enrichloanevent.setSsn(person.getSsn());
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
