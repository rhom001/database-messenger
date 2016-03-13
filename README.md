# Documentation for CS166 Database Messenger App

-<a href="#intro">Introduction</a>
-<a href="#accnt">User Account</a>
-<a href="#lists">Contact and Block Lists</a>
-<a href="#chat">Chats and Chat Viewer</a>
-<a href="#msgs">Messages</a>
-<a href="#misc">Miscellaneous</a>

##[Introduction](intro)
<p>To demonstrate what we have learned in CS 166 - Database Management Systems, we have programmed a basic messaging app using Java. The app allows users to communicate with other users in a chat with the use of messages. Although the app front end is Java, the back end of the app uses SQL in the form of PostgreSQL.</p>

<h2 id="accnt">User Account</h2>

<h2 id="lists">Contact and Block Lists</h2>

<h2 id="chat">Chats and Chat Viewer</h2>

<h2 id="msgs">Messages</h2>

<h2 id="misc">Miscellaneous</h2>
<p>In addition to the functions used for actual Database Messenger, we have also included some small helper functions. These include getting answers for questions or validating users before adding or deleting users.</p>

<h3>User Prompts</h3>
<ul>
    <li><em>public static int readChoice()</em> - gets a user's choice from a range of numeric options</li>
    <li><em>public static bool readYN(String prompt)</em> - gets a yes or no response from the user for a question</li>
</ul>

<h3>Validation</h3>
<ul>
    <li><em>public static boolean verifyUser(Messenger esql, String user)</em> - verifies that a user exists in the table of Users</li>
    <li><em>public static boolean isInit(Messenger esql, String author, String chat) - verifies that a user is the initial sender of the Chat that they are in</li>
    <li><em>public static boolean isMember(Messenger esql, String user, String chat)</em> - verifies that the user is a member of the Chat that they are browsing</li>
    <li><em>public static boolean isSender(Messenger esql, String author, String msg)</em> - verifies that the user is the sender of the selected Message</li>
</ul>

<h3>Other</h3>
<ul>
    <li><em>public static String quote(String text)</em> - replaces all single quotes with "\'"</li>
</ul>
