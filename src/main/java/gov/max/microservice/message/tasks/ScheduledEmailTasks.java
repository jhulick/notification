package gov.max.microservice.message.tasks;

import gov.max.microservice.message.service.EmailService;
import gov.max.microservice.message.service.MessageConsumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledEmailTasks {

    @Autowired
    private EmailService emailService;

    @Value("${kafka.topic}")
    private String topic;

    @Value("${kafka.url}")
    private String kafkaBrokerUrl;

    @Scheduled(fixedRate = 5000)
    public void consumeMessages() {
        MessageConsumer kafkaConsumer = new MessageConsumer(kafkaBrokerUrl, "file-share-group", topic, emailService);
        kafkaConsumer.consume();
    }
}