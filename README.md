# Messaging Notification #

Steps to run this project:

1. In terminal window of your IDE, run following commands:
    gradle build
    gradle run

Note: You should have gradle package installed in your system in order to import this project.

### Project Summary ###

This is an example implementation of RestFUL web services using Spring Boot framework and Kafka messaging with email.

* There are two Model classes : FileMetadata and Owner. The FileMetadata class holds the data structure of all the expired files for owners.
* The Owner class holds the user data structure of the file creator, using RestFUL web services resources to call File Share app.

### Notes ###
* Kafka Email Notification
* Spring Scheduler for scheduling email notification.
* MongoDb to persist data of File Data and Owners.
* Header Authorization.
