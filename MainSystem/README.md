# Server for messaging system
---
A java server, written using RMI technology, able to deal with clients which want to send messages over the network. 
# Project Structure
---
MainServerMessaggistica is the parent project.

  - messaging_system: Server class and a small GUI.

  - messaging_interfaces: Interfaces for Clients and MonitoringServices.

  - db_messaging: to deal with a PostgresDB.

  - client_messaging: a small client for testing.

# Main topics
---

  - Registration of clients.

  - Sending messages to one client.

  - Sending broadcast messages.

  - Delete client subscription.

  - Service for monitoring the system.

  - Messages are bounded and delivered when the receiver
  sign-In.
  
# Workflow
---
Before starting is needed to create a Postgres database named "dbMessaging" in localhost(db is automatically initialized as Server is started, you haven't to write any SQL code).

  - To compile the entire project, run "mvn install" in the parent project.

  - Run ServerUI class in messaging-system module.

  - Run Client in client_messaging module for testing. 

# Look for details on GitHub
  ---
  [https://github.com/Ale-98/MessagingServer](http://)