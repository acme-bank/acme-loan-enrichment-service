package com.acme.bank.loan.enrichment.service.stream;

import com.acme.bank.loan.enrichment.domain.config.AcmeProperties;
import com.acme.bank.loan.enrichment.domain.event.EnrichLoanEvent;
import com.acme.bank.loan.enrichment.domain.event.RejectLoanEvent;
import com.acme.bank.loan.enrichment.domain.event.ValidateLoanEvent;
import com.acme.bank.loan.enrichment.service.helper.KafkaHelper;
import com.acme.bank.loan.enrichment.service.repository.PersonRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Properties;

@SuppressWarnings({"Duplicates", "unchecked", "unused"})
@Component
public class ValidateLoanKafkaStream {

    private static final Logger LOGGER = LoggerFactory.getLogger(ValidateLoanKafkaStream.class);

    private String applicationName;
    private final AcmeProperties acmeProperties;
    private KafkaStreams streams;
    private final KafkaHelper kafkaHelper;
    private final PersonRepository personRepository;
    private final ConversionService conversionService;

    @Autowired
    public ValidateLoanKafkaStream(@Value("${spring.application.name}") String applicationName,
                                   final AcmeProperties acmeProperties,
                                   @Qualifier("kafkaObjectMapper") final ObjectMapper objectMapper,
                                   final KafkaHelper kafkaHelper,
                                   final PersonRepository personRepository,
                                   final ConversionService conversionService) {
        this.applicationName = applicationName;
        this.acmeProperties = acmeProperties;
        this.kafkaHelper = kafkaHelper;
        this.personRepository = personRepository;
        this.conversionService = conversionService;
    }

    @PostConstruct
    public void startStream() {
        AcmeProperties.Kafka.Topics topics = acmeProperties.getKafka().getTopics();

        StreamsBuilder streamBuilder = new StreamsBuilder();
        KStream<String, ValidateLoanEvent>[] streamBranches = streamBuilder.stream(topics.getValidateLoan(), kafkaHelper.cosumedWith(ValidateLoanEvent.class))
                .branch(this::enrichLoan, this::rejectLoan);

        send(streamBranches[0], EnrichLoanEvent.class, topics.getEnrichLoan());
        send(streamBranches[1], RejectLoanEvent.class, topics.getRejectLoan());

        streams = new KafkaStreams(streamBuilder.build(), properties());
        streams.start();
    }

    private boolean enrichLoan(String key, ValidateLoanEvent event) {
        AcmeProperties.Kafka.Topics topics = acmeProperties.getKafka().getTopics();

        LOGGER.info("Received event with key {} on topic {}", key, topics.getValidateLoan());

        return personRepository.findByPersonalId(event.getPersonalId()) != null;
    }

    private boolean rejectLoan(String key, ValidateLoanEvent value) { // NOSONAR
        return Boolean.TRUE; // Always reject if validation fails
    }

    private <T> void send(KStream<String, ValidateLoanEvent> stream, Class<T> clazz, String targetTopic) {
        stream.map((key, event) -> convert(key, event, clazz, targetTopic))
                .to(targetTopic, kafkaHelper.producedWith(clazz));
    }

    private <T> KeyValue<String, T> convert(String key, ValidateLoanEvent event, Class<T> clazz, String targetTopic) {
        LOGGER.info("Sending event with key {} to topic {}", key, targetTopic);

        return new KeyValue<>(key, conversionService.convert(event, clazz));
    }

    private Properties properties() {
        AcmeProperties.Kafka.Topics topics = acmeProperties.getKafka().getTopics();
        return kafkaHelper.properties(applicationName.concat("-").concat(topics.getEnrichLoan()));
    }

    @PreDestroy
    public void closeStream() {
        if (streams != null) {
            streams.close();
        }
    }
}
