package com.acme.bank.loan.domain.event;

import java.time.ZonedDateTime;
import java.util.UUID;

@SuppressWarnings({"unused"})
public class ValidateLoanEvent {

    private UUID uuid;
    private ZonedDateTime validatedTimestamp;
    private String personalId;

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public ZonedDateTime getValidatedTimestamp() {
        return validatedTimestamp;
    }

    public void setValidatedTimestamp(ZonedDateTime validatedTimestamp) {
        this.validatedTimestamp = validatedTimestamp;
    }

    public String getPersonalId() {
        return personalId;
    }

    public void setPersonalId(String personalId) {
        this.personalId = personalId;
    }
}
