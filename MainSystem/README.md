# Server for messaging system
---
A java server, written using RMI technology, able to deal with clients which want to send messages over the network. 

Import project in your IDE as an existing maven project. All modules will automatically be visible.

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
  - Messages are delivered to clients even if receivers are not signed 
  when the messages are sended.
  - When a client unsubscribed, messages directed to him and not delivered yet are deleted.
  
# Workflow
---
Before executing the project is needed to create a Postgres database named "dbMessaging" in localhost(db is automatically initialized as Server is started, you haven't to write any SQL code).

  - To compile the entire project, run "mvn install" in the parent project.
  - Run ServerUI class in messaging-system module.
  - Insert credentials for accessing your DB.
  - Hit start button.
  - Run Client in client_messaging module for testing. 

# How to start client
---
- Start Client class in client_messaging module.
- Insert nickname.
- Insert a password. 
- Insert nickname of the receiver of the messages.
- Start messaging with him. 
- Add as many Clients as you want by starting Client class.

# Look for details on GitHub
---
  [https://github.com/Ale-98/MessagingServer](http://)