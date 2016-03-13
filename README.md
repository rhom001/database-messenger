# Documentation for CS166 Database Messenger App

Table of Contents
* [Introduction](#intro)
* [User Account](#accnt)
* [Contact and Block Lists](#lists)
* [Chats and Chat Viewer](#chat)
* [Messages](#msgs)
* [Miscellaneous](#misc)

##[Introduction](intro)
To demonstrate what we have learned in CS 166 - Database Management Systems, we have programmed a basic messaging app using Java. The app allows users to communicate with other users in a chat with the use of messages. Although the app front end is Java, the back end of the app uses SQL in the form of PostgreSQL.

##[User Account](accnt)

##[Contact and Block Lists](lists)

##[Chats and Chat Viewer](chat)

##Messages(msgs)

##[Miscellaneous](misc)
In addition to the functions used for actual Database Messenger, we have also included some small helper functions. These include getting answers for questions or validating users before adding or deleting users.

###User Prompts
* *public static int readChoice()* - gets a user's choice from a range of numeric options
* *<li><em>public static bool readYN(String prompt)* - gets a yes or no response from the user for a question

###Validation
* *public static boolean verifyUser(Messenger esql, String user)* - verifies that a user exists in the table of Users
* *public static boolean isInit(Messenger esql, String author, String chat)* - verifies that a user is the initial sender of the Chat that they are in
* *public static boolean isMember(Messenger esql, String user, String chat)* - verifies that the user is a member of the Chat that they are browsing
* *public static boolean isSender(Messenger esql, String author, String msg)* - verifies that the user is the sender of the selected Message

###Other
* *public static String quote(String text)* - replaces all single quotes with "\'"
