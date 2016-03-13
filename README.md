# Documentation for CS166 Database Messenger App

Table of Contents
* [Introduction](#intro)
  * [Indexes](#index)
* [Menu](#menu)
* [Functions](#func)
  * [User Account](#accnt)
  * [Contact and Block Lists](#lists)
  * [Chats](#chat)
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
**Entry Menu**
1. [Create user](#userCreate)
2. [Log in](#userLogin)
3. Exit

**Main Menu**
1. [Contact List](#contactMenu)
2. [Block List](#blockMenu)
3. [Chat List](#chatMenu)
4. [Delete account](#userDelete)
5. Log out

<a name="contactMenu">**Contact List Sub Menu**</a>
1. [Browse contact list](#contactBrowse)
2. [Add to contact list](#contactAdd)
3. [Delete from contact list](#contactDelete)
4. Return to main menu

<a name="blockMenu">**Block List Sub Menu**</a>
1. [Browse block list](#blockBrowse)
2. [Add to block list](#blockAdd)
3. [Delete from block list](#blockDelete)
4. Return to main menu

<a name="chatMenu">**Chat Sub Menu**</a>
1. [Browse chat list](#chatBrowse)
2. [Add a chat](#chatAdd)
3. [Delete a chat](#chatDelete)
4. Return to main menu

<a name="chatMenu2">**Chat Sub Sub Menu**</a>
1. [View messages](#msgBrowse)
2. [Add chat member](#chatMemAdd)
3. [Remove chat member](#chatMemDelete)
4. Return to chat list

<a name="msgMenu">**Message Sub Sub Sub Menu**</a>
1. 


##<a name="func">Functions</a>
###<a name="accnt">User Account</a>
* <a name="userCreate">*void CreateUser (Messenger)*</a> - A new user is asked to enter a Login ID and password to create a new account.
* <a name="userLogin">*String LogIn (Messenger)*</a> - A user is asked to enter their Login ID and password to access further functions of message app.
* <a name="userDelete">*boolean DeleteAccount (Messenger, String)*</a> - A logged-in user can choose to delete their own account (all the information associated with the user's Login ID will be deleted), and be logged out of the application.

###<a name="lists">Contact and Block Lists</a>
* <a name="contactAdd">*void AddToContact (Messenger, String)*</a> -A user is able to add a new contact member (who is not already in the contact list) by entering the person's Login ID into the console.
* <a name="contactDelete">*void DeleteFromContact (Messenger, String)*</a> - A user is able to delete a contact member by entering the person's Login ID into the console.
* <a name="contactBrowse">*void ListContacts (Messenger, String)*</a> - A user is able to view a list of all their contact members.
* <a name="blockAdd">*void AddToBlock (Messenger, String)*</a> - A user is able to add block member (who is not already in the block list) by entering the person's Login ID into the console.
* <a name="blockDelete">*void DeleteFromBlock (Messenger, String)*</a> - A user is able to delete a block member by entering the person's Login ID into the console.
* <a name="blockBrowse">*ListBlocks (Messenger, String)*</a> - A user is able to view a list of all their block members.

###<a name="chat">Chats</a>
* <a name="chatAdd">*void CreateChat (Messenger, String)*</a> - A user sets initial receivers and an initial message to create a new chat.
* <a name="chatDelete">*DeleteChat (Messenger, String)*</a> - A user is able to delete an existing chat by entering the chat ID into the terminal, if the user is the initial sender. 
* <a name="chatBrowse">*void ListChat(Messenger, String)*</a> - A user can view all chats that they are part of and choose to look at a [specific chat](#chatMenu2) more closely (viewing messages or changing the number of people in the group).
* <a name="chatMemAdd">*void AddToChat (Messenger, String, String)*</a> - If a user is the initial sender of the chat, they are able to add a member to chat through the console.
* <a name="ChatMemDelete">*RemoveFromChat (Messenger, String, String)*</a> - If a user is the initial sender of the chat, they are able to remove a member from the chat through the console.

###<a name="msgs">Messages</a>
* <a name="msgBrowse">*void ChatViewer (Messenger, String, String)*</a> - A user is able to view all of the messages for a chat as well as the [Message menu](#msgMenu). By default, the most recent 10 messages are displayed first.
* <a name="msgMore">*void DisplayMessages (Messenger, List<List<String>>, int){*</a> - This displays the messages that the user is able to look at. By default the 10 most recent messages are displayed and any previous messages are shown in batches of 10.
* <a name="msgAdd">*void NewMessage (Messenger, String, String)*</a> - A user can type a new message in the console to add to a chat that they are a member of.
* <a name="msgEdit">*void EditMessage (Messenger, String)*</a> - A user can edit a message that they sent by entering its message ID as well as the new message content.
* <a name="msgDelete">*void DeleteMessage (Messenger, String)*</a> - A user can delete a message that they sent by entering the message ID.

###<a name="misc">Miscellaneous</a>
In addition to the functions used for actual Database Messenger, we have also included some small helper functions. These include getting answers for questions or validating users before adding or deleting users.

####User Prompts
* *int readChoice ()* - gets a user's choice from a range of numeric options
* *bool readYN (String)* - gets a yes or no response from the user for a question

####Validation
* *boolean verifyUser (Messenger, String)* - verifies that a user exists in the table of Users
* *boolean isInit (Messenger, String, String)* - verifies that a user is the initial sender of the Chat that they are in
* *boolean isMember (Messenger, String, String)* - verifies that the user is a member of the Chat that they are browsing
* *boolean isSender (Messenger, String, String)* - verifies that the user is the sender of the selected Message

####Other
* *public static String quote (String text)* - replaces all single quotes with "\'"
