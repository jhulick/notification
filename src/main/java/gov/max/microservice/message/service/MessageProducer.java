package gov.max.microservice.message.service;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import org.apache.log4j.Logger;

import java.util.Properties;

public class MessageProducer {
    private static final Logger logger = Logger.getLogger(MessageProducer.class);

    private static Producer<String, String> producer;
    private final Properties properties = new Properties();

    public MessageProducer(String kafkaURL) {
        properties.put("metadata.broker.list", kafkaURL);
        properties.put("serializer.class", "kafka.serializer.StringEncoder");
        properties.put("request.required.acks", "1");
        producer = new Producer<>(new ProducerConfig(properties));
    }

    public void send(String topic, String msg) {
        KeyedMessage<String, String> data = new KeyedMessage<>(topic, msg);
        producer.send(data);

        logger.info("Sending message: " + msg);

        producer.close();
    }
}

