package com.acme.bank.loan.domain.event;

import java.time.ZonedDateTime;
import java.util.UUID;

@SuppressWarnings({"unused"})
public class ValidateLoanEvent {

    private UUID eventId;
    private UUID personId;
    private ZonedDateTime validatedTimestamp;

    public UUID getEventId() {
        return eventId;
    }

    public void setEventId(UUID eventId) {
        this.eventId = eventId;
    }

    public UUID getPersonId() {
        return personId;
    }

    public void setPersonId(UUID personId) {
        this.personId = personId;
    }

    public ZonedDateTime getValidatedTimestamp() {
        return validatedTimestamp;
    }

    public void setValidatedTimestamp(ZonedDateTime validatedTimestamp) {
        this.validatedTimestamp = validatedTimestamp;
    }
}
