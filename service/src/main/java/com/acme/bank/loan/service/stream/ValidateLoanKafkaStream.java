package com.acme.bank.loan.service.stream;

import com.acme.bank.loan.domain.config.AcmeProperties;
import com.acme.bank.loan.domain.config.KafkaTopic;
import com.acme.bank.loan.domain.event.EnrichLoanEvent;
import com.acme.bank.loan.domain.event.RejectLoanEvent;
import com.acme.bank.loan.domain.event.ValidateLoanEvent;
import com.acme.bank.loan.service.helper.KafkaHelper;
import com.acme.bank.loan.service.repository.PersonRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@SuppressWarnings({"Duplicates", "unchecked", "unused"})
@Component
public class ValidateLoanKafkaStream {

    private static final Logger LOGGER = LoggerFactory.getLogger(ValidateLoanKafkaStream.class);

    private final AcmeProperties acmeProperties;
    private KafkaStreams streams;
    private final KafkaHelper kafkaHelper;
    private final PersonRepository personRepository;
    private final ConversionService conversionService;

    @Autowired
    public ValidateLoanKafkaStream(final AcmeProperties acmeProperties,
                                   @Qualifier("kafkaObjectMapper") final ObjectMapper objectMapper,
                                   final KafkaHelper kafkaHelper,
                                   final PersonRepository personRepository,
                                   final ConversionService conversionService) {
        this.acmeProperties = acmeProperties;
        this.kafkaHelper = kafkaHelper;
        this.personRepository = personRepository;
        this.conversionService = conversionService;
    }

    @PostConstruct
    public void startStream() {
        StreamsBuilder streamBuilder = new StreamsBuilder();
        KStream<String, ValidateLoanEvent>[] streamBranches = streamBuilder.
                stream(KafkaTopic.VALIDATED_LOANS.getTopicName(), kafkaHelper.consumedWith(ValidateLoanEvent.class))
                .branch(this::enrichLoan, this::rejectLoan);

        send(streamBranches[0], EnrichLoanEvent.class, KafkaTopic.ENRICHED_LOANS);
        send(streamBranches[1], RejectLoanEvent.class, KafkaTopic.REJECTED_LOANS);

        streams = new KafkaStreams(streamBuilder.build(), acmeProperties.kafkaProperties(KafkaTopic.VALIDATED_LOANS));
        streams.start();
    }

    private boolean enrichLoan(String key, ValidateLoanEvent event) {
        LOGGER.info("Received event with key {} on topic {}", key, KafkaTopic.VALIDATED_LOANS.getTopicName());

        return personRepository.findByPersonId(event.getPersonId()) != null;
    }

    private boolean rejectLoan(String key, ValidateLoanEvent value) { // NOSONAR
        return Boolean.TRUE; // Always reject if validation fails
    }

    private <T> void send(KStream<String, ValidateLoanEvent> stream, Class<T> clazz, KafkaTopic targetTopic) {
        stream.map((key, event) -> convert(key, event, clazz, targetTopic))
                .to(targetTopic.getTopicName(), kafkaHelper.producedWith(clazz));
    }

    private <T> KeyValue<String, T> convert(String key, ValidateLoanEvent event, Class<T> clazz, KafkaTopic targetTopic) {
        LOGGER.info("Sending event with key {} to topic {}", key, targetTopic.getTopicName());

        return new KeyValue<>(key, conversionService.convert(event, clazz));
    }

    @PreDestroy
    public void closeStream() {
        if (streams != null) {
            streams.close();
        }
    }
}
