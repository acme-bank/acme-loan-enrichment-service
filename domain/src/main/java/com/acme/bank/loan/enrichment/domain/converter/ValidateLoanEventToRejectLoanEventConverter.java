package com.acme.bank.loan.enrichment.domain.converter;

import com.acme.bank.loan.enrichment.domain.event.RejectLoanEvent;
import com.acme.bank.loan.enrichment.domain.event.ValidateLoanEvent;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

@Component
public class ValidateLoanEventToRejectLoanEventConverter extends AbstractConverter<ValidateLoanEvent, RejectLoanEvent> {

    @Override
    public RejectLoanEvent convert(ValidateLoanEvent validateLoanEvent) {
        RejectLoanEvent rejectLoanEvent = new RejectLoanEvent();
        rejectLoanEvent.setUuid(validateLoanEvent.getUuid());
        rejectLoanEvent.setRejectedTimestamp(ZonedDateTime.now());
        return rejectLoanEvent;
    }
}
