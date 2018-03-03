package com.acme.bank.loan.domain.config;

public enum KafkaTopic {

    VALIDATED_LOANS("validated-loans"),
    ENRICHED_LOANS("enriched-loans"),
    REJECTED_LOANS("rejected-loans");

    private String topicName;

    KafkaTopic(String topicName) {
        this.topicName = topicName;
    }

    public String getTopicName() {
        return topicName;
    }
}
