/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;
import java.text.SimpleDateFormat;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */
public class Messenger {

   // reference to physical database connection.
   private Connection _connection = null;

   // handling the keyboard inputs through a BufferedReader
   // This variable can be global for convenience.
   static BufferedReader in = new BufferedReader(
                                new InputStreamReader(System.in));

   /**
    * Creates a new instance of Messenger
    *
    * @param hostname the MySQL or PostgreSQL server hostname
    * @param database the name of the database
    * @param username the user name used to login to the database
    * @param password the user login password
    * @throws java.sql.SQLException when failed to make a connection.
    */
   public Messenger (String dbname, String dbport, String user, String passwd) throws SQLException {

      System.out.print("Connecting to database...");
      try{
         // constructs the connection URL
         String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
         System.out.println ("Connection URL: " + url + "\n");

         // obtain a physical connection
         this._connection = DriverManager.getConnection(url, user, passwd);
         System.out.println("Done");
      }catch (Exception e){
         System.err.println("Error - Unable to Connect to Database: " + e.getMessage() );
         System.out.println("Make sure you started postgres on this machine");
         System.exit(-1);
      }//end catch
   }//end Messenger

   /**
    * Method to execute an update SQL statement.  Update SQL instructions
    * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
    *
    * @param sql the input SQL string
    * @throws java.sql.SQLException when update failed
    */
   public void executeUpdate (String sql) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the update instruction
      stmt.executeUpdate (sql);

      // close the instruction
      stmt.close ();
   }//end executeUpdate

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and outputs the results to
    * standard out.
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQueryAndPrintResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and output them to standard out.
      boolean outputHeader = true;
      while (rs.next()){
	 if(outputHeader){
	    for(int i = 1; i <= numCol; i++){
		System.out.print(rsmd.getColumnName(i) + "\t");
	    }
	    System.out.println();
	    outputHeader = false;
	 }
         for (int i=1; i<=numCol; ++i)
            System.out.print (rs.getString (i) + "\t");
         System.out.println ();
         ++rowCount;
      }//end while
      stmt.close ();
      return rowCount;
   }//end executeQuery

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the results as
    * a list of records. Each record in turn is a list of attribute values
    *
    * @param query the input query string
    * @return the query result as a list of records
    * @throws java.sql.SQLException when failed to execute the query
    */
   public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException { 
      // creates a statement object 
      Statement stmt = this._connection.createStatement (); 
 
      // issues the query instruction 
      ResultSet rs = stmt.executeQuery (query); 
 
      /* 
       ** obtains the metadata object for the returned result set.  The metadata 
       ** contains row and column info. 
       */ 
      ResultSetMetaData rsmd = rs.getMetaData (); 
      int numCol = rsmd.getColumnCount (); 
      int rowCount = 0; 
 
      // iterates through the result set and saves the data returned by the query. 
      boolean outputHeader = false;
      List<List<String>> result  = new ArrayList<List<String>>(); 
      while (rs.next()){
          List<String> record = new ArrayList<String>(); 
         for (int i=1; i<=numCol; ++i) 
            record.add(rs.getString (i)); 
         result.add(record); 
      }//end while 
      stmt.close (); 
      return result; 
   }//end executeQueryAndReturnResult

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the number of results
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQuery (String query) throws SQLException {
       // creates a statement object
       Statement stmt = this._connection.createStatement ();

       // issues the query instruction
       ResultSet rs = stmt.executeQuery (query);

       int rowCount = 0;

       // iterates through the result set and count nuber of results.
       if(rs.next()){
          rowCount++;
       }//end while
       stmt.close ();
       return rowCount;
   }

   /**
    * Method to fetch the last value from sequence. This
    * method issues the query to the DBMS and returns the current 
    * value of sequence used for autogenerated keys
    *
    * @param sequence name of the DB sequence
    * @return current value of a sequence
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int getCurrSeqVal(String sequence) throws SQLException {
	Statement stmt = this._connection.createStatement ();
	
	ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
	if (rs.next())
		return rs.getInt(1);
	return -1;
   }

   /**
    * Method to close the physical connection if it is open.
    */
   public void cleanup(){
      try{
         if (this._connection != null){
            this._connection.close ();
         }//end if
      }catch (SQLException e){
         // ignored.
      }//end try
   }//end cleanup

   /**
    * The main execution method
    *
    * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
    */
   public static void main (String[] args) {
      if (args.length != 3) {
         System.err.println (
            "Usage: " +
            "java [-classpath <classpath>] " +
            Messenger.class.getName () +
            " <dbname> <port> <user>");
         return;
      }//end if
      
      Greeting();
      Messenger esql = null;
      try{
         // use postgres JDBC driver.
         Class.forName ("org.postgresql.Driver").newInstance ();
         // instantiate the Messenger object and creates a physical
         // connection.
         String dbname = args[0];
         String dbport = args[1];
         String user = args[2];
         esql = new Messenger (dbname, dbport, user, "");

         boolean keepon = true;
         while(keepon) {
            // These are sample SQL statements
            System.out.println("MAIN MENU");
            System.out.println("---------");
            System.out.println("1. Create user");
            System.out.println("2. Log in");
            System.out.println("9. < EXIT");
            String authorisedUser = null;
            switch (readChoice()){
               case 1: CreateUser(esql); break;
               case 2: authorisedUser = LogIn(esql); break;
               case 9: keepon = false; break;
               default : System.out.println("Unrecognized choice!"); break;
            }//end switch
            if (authorisedUser != null) {
              boolean usermenu = true;
              boolean contactmenu = false;
              boolean blockmenu = false;
              boolean chatmenu = false;
              int cid = 0;
              boolean flag = false;
              while(usermenu) {
                System.out.println("MAIN MENU");
                System.out.println("---------");
                System.out.println("1. Contact List");
                System.out.println("2. Block List");
                System.out.println("3. Chat List");
                System.out.println("4. Delete account");
                System.out.println(".........................");
                System.out.println("9. Log out");
                switch (readChoice()){
                    case 1: contactmenu = true;
                        while(contactmenu){
                           System.out.println("Contact List Menu");
                           System.out.println("-----------");
                           System.out.println("1. Browse contact list");
                           System.out.println("2. Add to contact list");
                           System.out.println("3. Delete from contact list");
                           System.out.println(".........................");
                           System.out.println("9. Return to main menu");
                           switch (readChoice()){
                              case 1: ListContacts(esql, authorisedUser); break;
                              case 2: AddToContact(esql, authorisedUser); break;
                              case 3: DeleteFromContact(esql, authorisedUser); break;
                              case 9: contactmenu = false; break;
                              default : System.out.println("Unrecognized choice!"); break;
                           }
                        }// end contact menu
                           break;
                    case 2: blockmenu = true;
                        while(blockmenu){
                           System.out.println("Block List Menu");
                           System.out.println("-----------");
                           System.out.println("1. Browse block list");
                           System.out.println("2. Add to block list");
                           System.out.println("3. Delete from block list");
                           System.out.println(".........................");
                           System.out.println("9. Return to main menu");
                           switch (readChoice()){
                              case 1: ListBlocks(esql, authorisedUser); break;
                              case 2: AddToBlock(esql, authorisedUser); break;
                              case 3: DeleteFromBlock(esql, authorisedUser); break;
                              case 9: blockmenu = false; break;
                              default : System.out.println("Unrecognized choice!"); break;
                           }
                        }// end block menu
                           break;
                    case 3: chatmenu = true;
                        while(chatmenu){
                           System.out.println("Chat List Menu");
                           System.out.println("-----------");
                           System.out.println("1. Browse chat list");
                           System.out.println("2. Add a new chat");
                           System.out.println("3. Add a new chat member");
                           System.out.println("4. Delete a chat member");
                           System.out.println("5. Delete a chat");
                           System.out.println(".........................");
                           System.out.println("9. Return to main menu");
                           switch (readChoice()){
                              case 1: ListChat(esql, authorisedUser); break;
                              case 2: CreateChat(esql, authorisedUser); break;
                              case 3: AddToChat(esql, authorisedUser, cid, flag); break;
                              case 4: RemoveFromChat(esql, authorisedUser, cid); break;
                              case 5: DeleteChat(esql, authorisedUser, cid);
                              case 9: chatmenu = false; break;
                              default : System.out.println("Unrecognized choice!"); break;
                           }
                        }// end chat menu
                           break;
                     case 4: usermenu = DeleteAccount(esql, authorisedUser); break;
                     case 9: usermenu = false; break;
                     default : System.out.println("Unrecognized choice!"); break;
                  }
              }
            }
         }//end while
      }catch(Exception e) {
         System.err.println (e.getMessage ());
      }finally{
         // make sure to cleanup the created table and close the connection.
         try{
            if(esql != null) {
               System.out.print("Disconnecting from database...");
               esql.cleanup ();
               System.out.println("Done\n\nBye !");
            }//end if
         }catch (Exception e) {
            // ignored.
         }//end try
      }//end try
   }//end main
  
   public static void Greeting(){
      System.out.println(
         "\n\n*******************************************************\n" +
         "              User Interface      	               \n" +
         "*******************************************************\n");
   }//end Greeting

   /*
    * Reads the user's choice given from the keyboard
    * @int
    **/
   public static int readChoice() {
      int input;
      // returns only if a correct value is given.
      do {
         System.out.print("Please make your choice: ");
         try { // read the integer, parse it and break.
            input = Integer.parseInt(in.readLine());
            break;
         }catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         }//end try
      }while (true);
      return input;
   }//end readChoice

   /*
    * Reads the user's Yes/No choice
    * @returns if a user answers 'yes' or 'no'
    */
   public static boolean readYN(String prompt) {
      String yn;
      // returns only if 'Y', 'y', 'N', or 'n' is given.
      do {
         System.out.print(prompt + "(Y/N): ");
         try { // reads the input
            yn = in.readLine();
            if ((yn == "Y") || (yn =="y"))
               return true;
            else if ((yn == "N") || (yn == "n"))
               return false;
            else {
              System.out.println("Please put in either 'Y' or 'N'!");
              continue;
            }
         }catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         }//end try
      }while (true);
   }//end readYN

   /* 
    * Checks if a user is valid.
    * @returns if the user exists
    **/
   public static boolean verifyUser(Messenger esql, String user){
      try{
         // Makes sure that the login exists
         String query = String.format("SELECT * FROM USR WHERE login='%s'", user); 
         int userNum = esql.executeQuery(query);
         if (userNum > 0)
           return true;
         else {
            System.out.print("'" + user + "' does not exist!");
            return false;
         }
      }catch(Exception e){
         System.err.println (e.getMessage ());
         return false;
     }
   }//end verifyUser
   
   /*
    * Checks if the user is the initial sender of the chat
    * @returns if the user is the initial sender
    **/
   public static boolean isInit(Messenger esql, String author, int chat){
      try{
         // Makes sure that the user is the initial sender
         String query = String.format("SELECT * FROM CHAT WHERE init_sender='%s' AND chat_id=%d", author, chat);
         int userNum = esql.executeQuery(query);
         if(userNum == 0){
            System.out.print(author + " is not the initial sender of this chat!");
            return false;
         }
         return true;
     }catch(Exception e){
         System.err.println (e.getMessage ());
         return false;
     }
   }//end isInit
   
   /* 
    * Checks if the user is member of a chat
    * @returns if a user is a member of a chat
    **/
   public static boolean isMember(Messenger esql, String user, int chat){
      try{
         // Makes sure that the user is a member of the chat
         String query = String.format("SELECT * FROM CHAT_LIST WHERE chat_id=%d AND member='%s'", chat, user);
         int userNum = esql.executeQuery(query);
         if(userNum == 0)
            return false;
         return true;
      }catch(Exception e){
         System.err.println (e.getMessage ());
         return false;
     }
   }//end isMember
   
   /*
    * Checks if the user is author of a message
    * @returns if the user is the author of the message
    **/
   public static boolean isSender(Messenger esql, String author, int msg){
      try{
         // Makes sure that the user sent the message
         String query = String.format("SELECT * FROM MESSAGE WHERE msg_id=%d AND sender_login='%s'", msg, author);
         int userNum = esql.executeQuery(query);
         if(userNum == 0)
            return false;
         return true;
      }catch(Exception e){
         System.err.println (e.getMessage ());
         return false;
     }
   }//end isSender
 
   /*
    * Creates a new user with provided login, password and phoneNum
    * An empty block and contact list would be generated and associated with a user
    **/
   public static void CreateUser(Messenger esql){
      try{
         System.out.print("\tEnter user login: ");
         String login = in.readLine();
         System.out.print("\tEnter user password: ");
         String password = in.readLine();
         System.out.print("\tEnter user phone: ");
         String phone = in.readLine();

	 //Creating empty contact\block lists for a user
	 esql.executeUpdate("INSERT INTO USER_LIST(list_type) VALUES ('block')");
	 int block_id = esql.getCurrSeqVal("user_list_list_id_seq");
         esql.executeUpdate("INSERT INTO USER_LIST(list_type) VALUES ('contact')");
	 int contact_id = esql.getCurrSeqVal("user_list_list_id_seq");
         
	 String query = String.format("INSERT INTO USR (phoneNum, login, password, block_list, contact_list) VALUES ('%s','%s','%s',%s,%s)", phone, login, password, block_id, contact_id);

         esql.executeUpdate(query);
         System.out.println ("User successfully created!");
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end CreateUser
   
   /*
    * Check log in credentials for an existing user
    * @return User login or null is the user does not exist
    **/
   public static String LogIn(Messenger esql){
      try{
         System.out.print("\tEnter user login: ");
         String login = in.readLine();
         System.out.print("\tEnter user password: ");
         String password = in.readLine();

         String query = String.format("SELECT * FROM Usr WHERE login = '%s' AND password = '%s'", login, password);
         int userNum = esql.executeQuery(query);
	 if (userNum > 0)
		return login;
         return null;
      }catch(Exception e){
         System.err.println (e.getMessage ());
         return null;
      }
   }//end LogIn

   public static void AddToContact(Messenger esql, String author){
      // Your code goes here.
      try{
         System.out.print("\tEnter new Contact login: ");
         String contact = in.readLine();
         // Checks new contact exists
         if(verifyUser(esql, contact)){
            // Makes sure that the user is not on the block list.
            String query = String.format("SELECT * " + 
                                         "FROM USER_LIST_CONTAINS ulc, USR u " +
                                         "WHERE u.login='%s' AND ulc.list_id=u.block_list AND ulc.list_member='%s'", author, contact);
            int userNum = esql.executeQuery(query);
            if(userNum > 0){
                String prompt = contact + " is in Blocked list. Would you like to move it to Contacts List?";
                if(readYN(prompt)){
                    // Remove from Blocked list
                }
                else {
                    // Exit
                }
            }
            else {
                //query = "INSERT INTO USER_LIST_CONTAINS VALUES ('
            }
        }
        }catch(Exception e){
         System.err.println (e.getMessage ());
        }
   }//end AddToContact

   public static void ListContacts(Messenger esql, String author){
      // Your code goes here.
      // ...
      // ...
   }//end ListContacts
   
   /*
    * Allows the author of the chat to add more members
    * A new member will be added to the chat
    **/
   public static void AddToChat(Messenger esql, String author, int chat, boolean flag){
      try{
         // Verifies that the user is the initial sender of the chat
         if(isInit(esql, author, chat)){
            boolean valid = false;
            String member = null;
            while(!valid){
               System.out.print("\tEnter user to add: ");
               member = in.readLine();
               valid = verifyUser(esql, member);
            }
            // If user exists and is not a member of the chat, add them to the chat
            if(!isMember(esql, member, chat))
            {
               String query = String.format("INSERT INTO CHAT_LIST (chat_id, member) VALUES (%d,'%s')", chat, member);
               esql.executeUpdate(query);
               flag = true;
               System.out.print(member + " has been successfully added to the chat!");
            }
            else{
               flag = false;
               System.out.print(member + " is already a member of this chat!");
            }
         }
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end AddToChat
   
   /*
    * Removes a member from the chat
    * An existing member in the chat is removed
    **/
    public static void RemoveFromChat(Messenger esql, String author, int chat){
      try{
         // Verifies that the user is the initial sender of the chat
         if(isInit(esql, author, chat)){
            boolean valid = false;
            String member = null;
            while(!valid){
               System.out.print("\tEnter user to remove: ");
               member = in.readLine();
               valid = verifyUser(esql, member);
            }
            // If user exists and is a member of the chat, remove them from the chat
            if(isMember(esql, member, chat))
            {
               String query = String.format("DELETE FROM CHAT_LIST WHERE member='%s' AND chat_id=%s", member, chat);
               esql.executeUpdate(query);
               System.out.print(member + " has been successfully removed from the chat!");
            }
            else
               System.out.print(member + " is not a member of this chat and cannot be removed!");
             }
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end RemoveFromChat
   
   /*
    * Adds a chat to the user's chat list
    * Creates a new chat by the user
    **/
   public static void CreateChat(Messenger esql, String author){
      try{
         // Trigger generates chat number once new chat is created
         int members = 0;
         int chat = esql.getCurrSeqVal("chat_chat_id_seq");
         String query = String.format("INSERT INTO CHAT (chat_id, chat_type, init_sender) VALUES (%d, 'private', '%s')", chat, author);
         esql.executeUpdate(query);
         query = String.format("INSERT INTO CHAT_LIST (chat_id, member) VALUES (%d, '%s')", chat, author);
         esql.executeUpdate(query);
         
         // Asks chat creator who to send initial message to
         boolean done = false;
         System.out.print("Please list which users to chat with.\n");
         while(!done){
            AddToChat(esql, author, chat, done);
            if(done){
                members++;
                if(members == 2){
                  query = String.format("UPDATE CHAT SET chat_type='group' WHERE chat_id=%d", chat);
                  esql.executeUpdate(query);
                }
                String prompt = "Are these all the user(s) you want to add to the chat?";
                done = readYN(prompt);
            }
        }
        // Write an initial message to all members of the chat
        NewMessage(esql, author, chat);
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end CreateChat
   
   /*
    * Deletes a chat
    **/
   public static void DeleteChat(Messenger esql, String author, int chat){
      try{
         // Verify if user is chat creator
         if(isInit(esql, author, chat)){
            // Confirm deletion
            String prompt = "Are you sure you want to delete this chat?";
            if(readYN(prompt)){
                // Deletes all messages in chat
                String query = String.format("DELETE FROM MESSAGE WHERE chat_id=%d", chat);
                esql.executeUpdate(query);
                // Deletes all users in chat
                query = String.format("DELETE FROM USER_LIST WHERE chat_id=%d", chat);
                esql.executeUpdate(query);
                // Deletes chat
                query = String.format("DELETE FROM CHAT WHERE chat_id=%d", chat);
                esql.executeUpdate(query); 
            }
         }
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end DeleteChat

   public static void NewMessage(Messenger esql, String author, int chat){
      // Your code goes here.
      try{
         // Checks that sender is a member of the chat
         if(isMember(esql, author, chat)){
            boolean done = false;
            String prompt = null;
            String message = null;
            while(!done){
               // Gets the message from the user
               message = in.readLine();
               prompt = "Is this the message you want to send?";
               done = readYN(prompt);
            }
            // Get message id
            int msgId = esql.getCurrSeqVal("message_msg_id_seq");
            // Get message timestamp
            Calendar calendar = Calendar.getInstance();
		    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss Z");
		    java.sql.Timestamp MessageTime = new java.sql.Timestamp(calendar.getTime().getTime());
		    String msgTime=dateFormat.format(MessageTime);
		    
		    // Sends the message
            String query = String.format("INSERT INTO (msg_id, msg_text, msg_timestamp, sender_login, chat_id) VALUES (%d, '%s', '%s', '%s', %d)", msgId, message, msgTime, author, chat);
            esql.executeUpdate(query);
         }
         else
            System.out.print(author + " is not a member of this chat!");
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end NewMessage
   
   /*
    * Allows the author of a message to edit a message
    **/
   public static void EditMessage(Messenger esql, String author, int msg){
      try{
         // Confirm user is the sender of the message
         if(isSender(esql, author, msg)){
            boolean done = false;
            String prompt = null;
            String message = null;
            // Get the edited message
            while(!done){
               message = in.readLine();
               prompt = "Are you done editing the message?";
               done = readYN(prompt);
            }
            // Edit the message
            String query = String.format("UPDATE MESSAGE SET msg_text='%s' WHERE msg_id=%d", message, msg);
            esql.executeUpdate(query);
         }
         else
            System.out.print(author + " cannot edit this message!");
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end EditMessage
   
   /*
    * Allows the author of a message to delete a message
    **/
   public static void DeleteMessage(Messenger esql, String author, int msg){
      try{
         // Confirm user is sender of the message
         if(isSender(esql, author, msg)){
            String prompt = "Are you sure you want to delete this message?";
            boolean confirm = readYN(prompt);
            if(confirm){
               String query = String.format("DELETE FROM MESSAGE WHERE msg_id=$d", msg);
            }
         }
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end DeleteMessage
   
   /*
    * Allows user to view messages in a chat
    **/
   public static void ChatViewer(Messenger esql, int cid){
      try{
      
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end ChatViewer


   public static void Query6(Messenger esql){
      // Your code goes here.
      // ...
      // ...
   }//end Query6

}//end Messenger
