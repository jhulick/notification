package gov.max.microservice.message.tasks;

import gov.max.microservice.message.api.FileOwnerDataController;
import gov.max.microservice.message.service.EmailService;
import gov.max.microservice.message.service.MessageProducer;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class - Checks expired file data in every 5 minutes and email results of expired files to the owner
 *
 * getExpiredFiles - Method returns expired files only after checking the Database.
 * If there are no files expired then it will return null/empty HashMap.
 *
 * reportExpiredFiles - Method send email every 5 minutes if expired file data found in database.
 */
@Component
public class ScheduledFileExpirationTasks {

    private static final Logger logger = Logger.getLogger(ScheduledFileExpirationTasks.class);

    @Value("${kafka.topic}")
    private String topic;

    @Value("${kafka.url}")
    private String kafkaBrokerUrl;

    @Autowired
    FileOwnerDataController fileDataController;

    @Autowired
    private EmailService emailService;

    private ArrayList<String> getExpiredFiles() {

        ConcurrentHashMap<String, ArrayList<String>> expiredFileData = fileDataController.getExpiredFileMetadata();
        ArrayList<String> fileDataMessageList = new ArrayList<>();
        try {
            if (expiredFileData.isEmpty()) {
                logger.info("There is no file data expired recently!!");
            } else {
                for (ArrayList<String> value : expiredFileData.values()) {
                    for (String msg : value) {
                        fileDataMessageList.add(msg);
                    }
                }
            }
        } catch (NullPointerException e) {
            logger.info("There is no expired file data found, NullPointerException generated");
        }

        return fileDataMessageList;
    }

    @Scheduled(fixedRate = 300000)
    public void reportExpiredFiles() {
        try {
            ArrayList<String> fileDataMessageList = getExpiredFiles();
            for (String msg : fileDataMessageList) {
                logger.info(msg);

                MessageProducer producer = new MessageProducer(kafkaBrokerUrl);
                producer.send(topic, msg);
            }
        } catch (NullPointerException e) {
            logger.info("NullPointException generated while sending mail to Kafka server");
        }
    }
}
