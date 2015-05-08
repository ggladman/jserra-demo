# Server and client for the J.Serra school visit

## Overview

This is a simple client-server combination which will serve as a demo for the visit by 5th grade students from Junipero Serra Elementary School to Xoom on May 14th, 2015.

The goal of the applications is to allow each student to run a client application, and connect them to a central server, allowing them to send money to each other. The central server application will display a running ledger of all transactions. The idea is to have the students make a simple change, such as adding a "message" property to each transfer, and see the effects on the big screen after compiling and running. The estimated time for each group of ~13 students is only 15-20 minutes, so the scope of the "assignment" is very limited.

## Technologies

* Both the server and the client are written using Spring Boot.
* The web applications are implemented using jQuery (Javascript).
* Both the server and the client support REST APIs which are called by their respective web front-ends.
* The server also supports a REST API which is intended for use by the clients.
* Communication from the Spring controller to the web app is done via STOMP over websockets, using Spring support for Java and the STOMP JS library for the webapps.
* Notifications are broadcast from the server to all clients via a RabbitMQ topic exchange.

## Useful information

* both projects are now configured to build with Java 1.6.

* rabbitmq is required to run on the server machine (although this could be changed to be any external rabbit instance).

* to start rabbitmq, if you have it installed (i.e., not via vagrant):

    `/sbin/rabbitmq_server-3.1.5/sbin/rabbitmq-server start`
* to launch the rabbitmq console:

    `http://localhost:15672/#/`

## to-do items (MANDATORY)
* SERVER: increment the recipient's balance on the server on receipt of xfers
* SERVER: keep an in-memory list of the xfers performed while the server has been running, and send it to the server webapp when the page is reloaded.
* SERVER: error handling: return failure if sending to a nonexistent recipient
* CLIENT/SERVER: error handling: don't allow sending of money to self

## to-do items (OPTIONAL)
* CLIENT: display a pop-up notification when a new user logs in, or when a xfer is received
* SERVER: implement a graphical view of nodes with running balances, and animate xfers
