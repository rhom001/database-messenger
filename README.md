# Documentation for CS166 Database Messenger App

Table of Contents
* [Introduction](#intro)
  * [Indexes](#index)
* [Menu](#menu)
* [Functions](#func)
  * [User Account](#accnt)
  * [Contact and Block Lists](#lists)
  * [Chats and Chat Viewer](#chat)
  * [Messages](#msgs)
  * [Miscellaneous](#misc)

##<a name="intro">Introduction</a>
To demonstrate what we have learned in CS 166 - Database Management Systems, we have programmed a basic messaging app using Java. The app allows users to communicate with other users in a chat with the use of messages. Although the app front end is Java, the back end of the app uses SQL in the form of PostgreSQL.

Due to the console-based nature of the application, we have included some additional functionality. For all functions used in the application, whether it affects Users, Chats, or Messages, there should be a response from the application if the function used was a success. In addition, there are indexes that are used to speed up the query efficiency for all queries that are run during the application.

###<a name="index">Indexes</a>
**Users**
Since we are constantly looking up Users, it is important that there is an index for validating the login (**USR.login**) that the user is looking for exists. Considering that there are Contact (**USR.contact_id**) and Block (**USR.block_id**) lists (**USER_LIST_CONTAINS.list_id**), there is a need to find users (**USER_LIST_CONTAINS.list_member**) from those lists..

**Chat**
In addition we are also looking for the initial sender (**CHAT.init_sender**) of a Chat (**CHAT.chat_id**) as well as the members (**CHAT_LIST.member**) of the Chat (**CHAT_LIST.chat_id**).

**Message**
When the user delves into a Chat (**MESSAGE.chat_id**), they are able to look through the Messages (**MESSAGE.msg_id**) as well as the sender (**MESSAGE.sender_login**) and time sent (**MESSAGE.msg_timestamp**) of each Message.

##<a name="menu">Menu</a>


##<a name="func">Functions</a>
###<a name="accnt">User Account</a>

###<a name="lists">Contact and Block Lists</a>

###<a name="chat">Chats and Chat Viewer</a>

###<a name="msgs">Messages</a>

###<a name="misc">Miscellaneous</a>
In addition to the functions used for actual Database Messenger, we have also included some small helper functions. These include getting answers for questions or validating users before adding or deleting users.

####User Prompts
* *public static int readChoice()* - gets a user's choice from a range of numeric options
* *public static bool readYN(String prompt)* - gets a yes or no response from the user for a question

####Validation
* *public static boolean verifyUser(Messenger esql, String user)* - verifies that a user exists in the table of Users
* *public static boolean isInit(Messenger esql, String author, String chat)* - verifies that a user is the initial sender of the Chat that they are in
* *public static boolean isMember(Messenger esql, String user, String chat)* - verifies that the user is a member of the Chat that they are browsing
* *public static boolean isSender(Messenger esql, String author, String msg)* - verifies that the user is the sender of the selected Message

####Other
* *public static String quote(String text)* - replaces all single quotes with "\'"
