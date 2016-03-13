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
	
	ResultSet rs = stmt.executeQuery (String.format("SELECT currval('%s')", sequence));
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
                           System.out.println("---------------");
                           System.out.println("1. Browse chat list");
                           System.out.println("2. Add a new chat");
                           System.out.println("3. Delete a chat");
                           System.out.println(".........................");
                           System.out.println("9. Return to main menu");
                           switch (readChoice()){
                              case 1: ListChat(esql, authorisedUser); break;
                              case 2: CreateChat(esql, authorisedUser); break;
                              case 3: DeleteChat(esql, authorisedUser);
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
            // Print out a break before moving on
            System.out.println("\n");
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
         System.out.println(prompt + " (Y/N): ");
         try { // reads the input
            yn = in.readLine();
            System.out.println("\n");
            if ((yn.equals("Y")) || (yn.equals("y")))
               return true;
            else if ((yn.equals("N")) || (yn.equals("n")))
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
         // Makes sure that user does not have any quotes in name
         user = quote(user);
         // Makes sure that the login exists
         String query = String.format("SELECT * FROM USR WHERE login='%s'", user); 
         int userNum = esql.executeQuery(query);
         if (userNum > 0)
           return true;
         else {
            System.out.println("'" + user + "' does not exist!");
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
   public static boolean isInit(Messenger esql, String author, String chat){
      try{
         // Makes sure that the user is the initial sender
         String query = String.format("SELECT * FROM CHAT WHERE init_sender='%s' AND chat_id=%s", author, chat);
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
   public static boolean isMember(Messenger esql, String user, String chat){
      try{
         // Makes sure that the user is a member of the chat
         String query = String.format("SELECT * FROM CHAT_LIST WHERE chat_id=%s AND member='%s'", chat, user);
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
    * Replaces any "'" with "\'"
    * @returns a corrected string
    **/
   public static String quote(String text) {
      // Replaces single quotes with escape single quotes
      if(text.contains("'")){
         System.out.println(text);
         return text.replace("'", "\\'");
      }
      // Returns text
      return text;
    }
   /*
    * Checks if the user is author of a message
    * @returns if the user is the author of the message
    **/
   public static boolean isSender(Messenger esql, String author, String msg){
      try{
         // Makes sure that the user sent the message
         String query = String.format("SELECT * FROM MESSAGE WHERE msg_id=%s AND sender_login='%s'", msg, author);
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
         login = quote(login);
         
         System.out.print("\tEnter user password: ");
         String password = in.readLine();
         password = quote(login);
         
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
    * Deletes a logged in user
    * Makes sure that the user has deleted all of their owned chats and messages
    **/
   public static boolean DeleteAccount(Messenger esql, String author){
      try{
         String query = String.format("SELECT * FROM CHAT Where init_sender='%s'", author);
         int chatNum = esql.executeQuery(query);
         
         // Returns an error message since not all chats have been deleted
         if(chatNum > 0){
            System.out.println("Please delete all chats you owned and any messages that you wrote.");
            return true;
         }
         
         // Gets all lists that the user owns
         query = String.format("SELECT block_list FROM USR WHERE login='%s'", author);
		 String block_id = esql.executeQueryAndReturnResult(query).get(0).get(0);
		 query = String.format("SELECT contact_list FROM USR WHERE login='%s'", author);
		 String contact_id = esql.executeQueryAndReturnResult(query).get(0).get(0);
		 
		 // Deletes all members of the user's contacts and blocked users
		 query = String.format("DELETE FROM USER_LIST_CONTAINS WHERE list_id=%s", block_id);
     	 esql.executeUpdate(query);		 
		 query = String.format("DELETE FROM USER_LIST_CONTAINS WHERE list_id=%s", contact_id);
     	 esql.executeUpdate(query);
     	 
     	 // Deletes the user from everyone else's contact and blocked users
     	 query = String.format("DELETE FROM USER_LIST_CONTAINS WHERE list_member='%s'", author);
     	 esql.executeUpdate(query);
     	 
     	 // Deletes the user's actual lists		 
		 query = String.format("DELETE FROM USER_LIST Where list_id=%s", block_id);
     	 esql.executeUpdate(query);
		 query = String.format("DELETE FROM USER_LIST Where list_id=%s", contact_id);
     	 esql.executeUpdate(query);
     	 
     	 // Deletes the uesr's account
     	 query = String.format("DELETE FROM USR Where login='%s'", author);
     	 esql.executeUpdate(query);
		 System.out.println("Your account has been deleted!");
		 System.out.println("You will now be logged out.");
		 return false;      
      }catch(Exception e){
         System.err.println (e.getMessage ());
		 return true;
      }
   }
   
   /*
    * Check log in credentials for an existing user
    * @return User login or null is the user does not exist
    **/
   public static String LogIn(Messenger esql){
      try{
         System.out.print("\tEnter user login: ");
         String login = in.readLine();
         login = quote(login);
         System.out.print("\tEnter user password: ");
         String password = in.readLine();
         password = quote(password);

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

   /*
    * Adds a user to the contact list
    **/
   public static void AddToContact(Messenger esql, String author){
      // Your code goes here.
      try{
         System.out.print("\tEnter new Contact login: ");
         String contact = in.readLine();
         contact = quote(contact);
         
         // Checks new contact exists
         if(verifyUser(esql, contact)){
            // Gets the contact list.
            String query = String.format("SELECT contact_list FROM USR WHERE login = '%s'", author);
            String contact_id = esql.executeQueryAndReturnResult(query).get(0).get(0);
            
            // Makes sure that the user is not on the block list.
            query = String.format("SELECT block_list FROM USR WHERE login = '%s'", author);
            String block_id = esql.executeQueryAndReturnResult(query).get(0).get(0);
            query = String.format("SELECT * FROM USER_LIST_CONTAINS WHERE list_id=%s AND list_member='%s'", block_id, contact);
            int userNum = esql.executeQuery(query);
            if(userNum > 0){
                String prompt = contact + " is in Block list. Would you like to move it to Contacts List?";
                if(readYN(prompt)){
                    // Remove from Blocked list
                    query = String.format("DELETE FROM USER_LIST_CONTAINS WHERE list_id='%s' and list_member='%s'", block_id, contact);
         	        esql.executeUpdate(query);
                }
                else {
                    // Exit
                    System.out.println("Adding "+ contact +" to Contacts is cancelled!");
                    System.out.println(contact + " will remain blocked.");
                    return;
                }
            }
            else {
                //Check whether user is already in Contact list
                query = String.format("SELECT * FROM USER_LIST_CONTAINS WHERE list_id=%s AND list_member='%s'", contact_id, contact);
                userNum = esql.executeQuery(query);
                if(userNum > 0){
                    System.out.println(contact + " is already in Contact list!");
                    return;
                }
            }
            // Adds user into Contact list
            query = String.format("INSERT INTO USER_LIST_CONTAINS VALUES (%s, '%s')", contact_id, contact);
            esql.executeUpdate(query);
            System.out.println (contact + " has been successfully added to the Contact list!");
         }
      }catch(Exception e){
       System.err.println (e.getMessage ());
      }
   }//end AddToContact
   
   /*
    * Deletes a user from the contact list
    **/
   public static void DeleteFromContact(Messenger esql, String author){
      // Your code goes here.
      try{
         // Gets the author's contact list
         String query = String.format("SELECT contact_list FROM USR WHERE login='%s'", author);
         String contact_id = esql.executeQueryAndReturnResult(query).get(0).get(0);
         
         // Gets a contact to delete
         System.out.print("\tEnter Contact to delete: ");
         String contact = in.readLine();
         contact = quote(contact);
         
         // Checks if contact exists
         if(verifyUser(esql, contact)){
            // Makes sure that the contact is in the Contact list
            query = String.format("SELECT * FROM USER_LIST_CONTAINS WHERE list_id=%s AND list_member='%s'", contact_id, contact);
            int userNum = esql.executeQuery(query);
            if(userNum > 0){
                // Removes contact from the Contact list
                query = String.format("DELETE FROM USER_LIST_CONTAINS WHERE list_id=%s AND list_member='%s'", contact_id, contact);
                esql.executeUpdate(query);
                System.out.println(contact + " has been deleted from Contacts!");
            }
            else{
                System.out.println(contact + " is not in Contact list and cannot be deleted!");
            }
         }
      }catch(Exception e){
        System.err.println (e.getMessage ());
      }
   }//end DeletefromContact
  
   /*
    * Displays all contacts for a user
    **/
   public static void ListContacts(Messenger esql, String author){
      // Your code goes here.
      try{
         // Get the contact list id 
		 String query =  String.format("SELECT contact_list FROM USR WHERE login = '%s'",author); 
		 String contact_id = esql.executeQueryAndReturnResult(query).get(0).get(0); 
         
         // Retrieves and displays the contact_list
         query = String.format("SELECT list_member as Contacts FROM USER_LIST_CONTAINS WHERE list_id = %s", contact_id);
	     int contacts = esql.executeQueryAndPrintResult(query);
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end ListContacts
   
   /*
    * Adds a user to the block list
    **/
   public static void AddToBlock(Messenger esql, String author){
      // Your code goes here.
      try{
         // Makes sure that the blocked user is already not in block list
         String query = String.format("SELECT block_list FROM USR WHERE login='%s'", author);
         String block_id = esql.executeQueryAndReturnResult(query).get(0).get(0);
         
         // Gets the new blocked user
         System.out.print("\tEnter new Block login: ");
         String block = in.readLine();
         block = quote(block);
         
         // Checks new blocked user exists
         if(verifyUser(esql, block)){
            // Makes sure that the user is not on the Contact list.
            query =  String.format("SELECT contact_list FROM USR WHERE login = '%s'", author); 
            String contact_id = esql.executeQueryAndReturnResult(query).get(0).get(0);
            query = String.format("SELECT * FROM USER_LIST_CONTAINS WHERE list_id=%s AND list_member='%s'", contact_id, block);
            int userNum = esql.executeQuery(query);
            
            if(userNum > 0){
               // Potential blocked user is already in contact list
               String prompt = block + " is in Contact list. Would you like to move it to Block list?";
               if(readYN(prompt)){
                  // Remove from Contact list
                  query = String.format("DELETE FROM USER_LIST_CONTAINS WHERE list_id=%s AND list_member='%s'", contact_id, block);
                  esql.executeUpdate(query);
               }
               else{
                  System.out.println(block + " will remain in Contact list.");
                  return;
               }
            }
            else{
               query = String.format("SELECT * FROM USER_LIST_CONTAINS WHERE list_id=%s AND list_member='%s'", block_id, block);
               userNum = esql.executeQuery(query);
               
               if(userNum > 0){
                  // Blocked user is already in block list
                  System.out.println(block + " is already in Block list!");
                  return;
               }
            }
	        // Adds the user to the block list
	        query = String.format("INSERT INTO USER_LIST_CONTAINS VALUES(%s, '%s')", block_id, block);
	        esql.executeUpdate(query);
	        System.out.println(block + " has been successfully added to Block list!");
	     }
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end AddToBlock
   
   /*
    * Deletes a blocked user from the block list
    **/
   public static void DeleteFromBlock(Messenger esql, String author){
      // Your code goes here.
      try{
         // Gets the block list id
         String query = String.format("SELECT block_list FROM USR WHERE login='%s'", author);
         String block_id = esql.executeQueryAndReturnResult(query).get(0).get(0);
         
         // Gets the blocked user
         System.out.print("\tEnter Block login to Delete: ");
         String block = in.readLine();
         block = quote(block);
         
         // Checks that the blocked user exists
         if(verifyUser(esql, block)){
            // Makes sure that the blocked user is in the user's block list
            query = String.format("SELECT * FROM USER_LIST_CONTAINS WHERE list_id=%s AND list_member='%s'", block_id, block);
            int userNum = esql.executeQuery(query);
            if(userNum > 0){
               query = String.format("DELETE FROM USER_LIST_CONTAINS WHERE list_id=%s AND list_member='%s'", block_id, block);
               esql.executeQuery(query);
            }
            else{
               System.out.println(block + " is not in Block list and cannot be deleted!");
            }    
         }
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end DeletefromBlock
      
   /*
    * Displays all contacts for a user
    **/
   public static void ListBlocks(Messenger esql, String author){
      // Your code goes here.
      try{
         // Get the block list id 
		 String query =  String.format("SELECT block_list FROM USR WHERE login = '%s'", author); 
		 String block_id = esql.executeQueryAndReturnResult(query).get(0).get(0); 
         
         // Retrieves and displays the block_list
         query = String.format("SELECT list_member as Contacts FROM USER_LIST_CONTAINS WHERE list_id = %s", block_id);
         int blocks = esql.executeQueryAndPrintResult(query);
	     // Put in rest of display
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end ListBlocks
   
   /*
    * Lists user's chats
    **/
   public static void ListChat(Messenger esql, String author){
      try{
         String query = String.format("SELECT L.chat_id, MAX(M.msg_timestamp) AS Received FROM CHAT_LIST L, MESSAGE M WHERE L.member = '%s' AND M.chat_id=L.chat_id GROUP BY L.chat_id ORDER BY MAX(M.msg_timestamp) DESC", author);
         List<List<String>> chatList = esql.executeQueryAndReturnResult(query);
         // Display the chat list and be able to access the messages inside.
         for(int i = 0; i < chatList.size(); ++i){
            // Get the chat id and last time updated
            String cid = chatList.get(i).get(0);
            String time = chatList.get(i).get(1);
            System.out.println("Chat #: " + cid + " \n\tLast updated: " + time + "\n\tMembers:");
            
            // Gets and formats the chat members
            query = String.format("SELECT member FROM CHAT_LIST WHERE chat_id=%s", cid);
            List<List<String>> memberList = esql.executeQueryAndReturnResult(query);
            for(int j = 0; j < memberList.size(); ++j){
               System.out.println("\t" + memberList.get(j).get(0));
            }
         }
            
         String prompt = "Would you like to look at a chat?";
         if(readYN(prompt)){
            System.out.print("Chat to look at: ");
            String chat = in.readLine();
            boolean msgmenu = isMember(esql, author, chat);
            // Message menu
            while(msgmenu){
                System.out.println("Chat #" + chat + " Menu");
                System.out.println("-------------------");
                System.out.println("1. View messages");
                System.out.println("2. Add chat member");
                System.out.println("3. Remove chat member");
                System.out.println(".........................");
                System.out.println("9. Back to chat list");
                switch(readChoice()){
                    case 1: ChatViewer(esql, author, chat); break;
                    case 2: AddToChat(esql, author, chat); break;
                    case 3: RemoveFromChat(esql, author, chat); break;
                    case 9: msgmenu = false; break;
                    default: System.out.println("Unrecognized choice!"); break;
                }
            }
         }
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end ListChat
   
   /*
    * Allows the author of the chat to add more members
    * A new member will be added to the chat
    **/
   public static void AddToChat(Messenger esql, String author, String chat){
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
               String query = String.format("INSERT INTO CHAT_LIST (chat_id, member) VALUES (%s,'%s')", chat, member);
               esql.executeUpdate(query);
               System.out.println(member + " has been successfully added to the chat!");
               
               // Changes the chat type to group if the people in the chat is more than 2
               query = String.format("SELECT * FROM CHAT_LIST WHERE chat_id=%s", chat);
               int userNum = esql.executeQuery(query);
               if(userNum == 3){
                  query = String.format("UPDATE CHAT SET chat_type='group' WHERE chat_id=%s", chat);
                  esql.executeUpdate(query);
               }
            }
            else{
               System.out.println(member + " is already a member of this chat!");
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
    public static void RemoveFromChat(Messenger esql, String author, String chat){
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
         String query = String.format("INSERT INTO CHAT (chat_type, init_sender) VALUES ('private', '%s')", author);
         esql.executeUpdate(query);
         int chat_id = esql.getCurrSeqVal("chat_chat_id_seq");
         String chat = Integer.toString(chat_id);
         query = String.format("INSERT INTO CHAT_LIST (chat_id, member) VALUES (%s, '%s')", chat, author);
         esql.executeUpdate(query);
         
         // Asks chat creator who to send initial message to
         boolean done = false;
         System.out.print("Please list which users to chat with.\n");
         while(!done){
            AddToChat(esql, author, chat);
            String prompt = "Are these all the user(s) you want to add to the chat?";
            done = readYN(prompt);
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
   public static void DeleteChat(Messenger esql, String author){
      try{
         System.out.print("Chat to delete: ");
         String chat = in.readLine();
         // Verify if user is chat creator
         if(isInit(esql, author, chat)){
            // Confirm deletion
            String prompt = "Are you sure you want to delete this chat?";
            if(readYN(prompt)){
                // Deletes all messages in chat
                String query = String.format("DELETE FROM MESSAGE WHERE chat_id=%s", chat);
                esql.executeUpdate(query);
                // Deletes all users in chat
                query = String.format("DELETE FROM CHAT_LIST WHERE chat_id=%s", chat);
                esql.executeUpdate(query);
                // Deletes chat
                query = String.format("DELETE FROM CHAT WHERE chat_id=%s", chat);
                esql.executeUpdate(query); 
            }
         }
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end DeleteChat

   /*
    * Allows a user to write a message
    **/
   public static void NewMessage(Messenger esql, String author, String chat){
      // Your code goes here.
      try{
         // Checks that sender is a member of the chat
         if(isMember(esql, author, chat)){
            boolean done = false;
            String message = null;
            System.out.println("Please type your message:");
            while(!done){
               // Gets the message from the user
               message = in.readLine();
               message = quote(message);
               String prompt = "Is this the message you want to send?";
               done = readYN(prompt);
            }
            // Get message timestamp
            Calendar calendar = Calendar.getInstance();
		    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss Z");
		    java.sql.Timestamp MessageTime = new java.sql.Timestamp(calendar.getTime().getTime());
		    String msgTime=dateFormat.format(MessageTime);
		    
		    // Sends the message
            String query = String.format("INSERT INTO MESSAGE (msg_text, msg_timestamp, sender_login, chat_id) VALUES ('%s', '%s', '%s', %s)", message, msgTime, author, chat);
            esql.executeUpdate(query);
            System.out.println("Message has been sent successfully!\n");
         }
         else
            System.out.println(author + " is not a member of this chat!\n");
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end NewMessage
   
   /*
    * Allows the author of a message to edit a message
    **/
   public static void EditMessage(Messenger esql, String author){
      try{
         // Asks for a message to edit
         System.out.print("Message to update: ");
         String msg = in.readLine();
         
         // Confirm user is the sender of the message
         if(isSender(esql, author, msg)){
            boolean done = false;
            String prompt = null;
            String message = null;
            // Get the edited message
            while(!done){
               message = in.readLine();
               message = quote(message);
               prompt = "Are you done editing the message?";
               done = readYN(prompt);
            }
            // Edit the message
            String query = String.format("UPDATE MESSAGE SET msg_text='%s' WHERE msg_id=%s", message, msg);
            esql.executeUpdate(query);
            System.out.println("Message has been edited!\n");
         }
         else
            System.out.println(author + " cannot edit this message!\n");
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end EditMessage
   
   /*
    * Allows the author of a message to delete a message
    **/
   public static void DeleteMessage(Messenger esql, String author){
      try{
         // Asks for a message to delete
         System.out.print("Message to delete: ");
         String msg = in.readLine();
         
         // Confirm user is sender of the message
         if(isSender(esql, author, msg)){
            String prompt = "Are you sure you want to delete this message?";
            boolean confirm = readYN(prompt);
            if(confirm){
               String query = String.format("DELETE FROM MESSAGE WHERE msg_id=%s", msg);
               esql.executeUpdate(query);
               System.out.println("Message has been deleted!");
            }
         }
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end DeleteMessage
   
   /*
    * Allows user to view messages in a chat
    **/
   public static void ChatViewer(Messenger esql, String author, String cid){
      try{
         // Gets the chat_id as a list
         String query = String.format("SELECT msg_id, msg_timestamp, msg_text, sender_login AS Received FROM  MESSAGE WHERE chat_id=%s ORDER BY msg_timestamp DESC", cid);
         List<List<String>> msgList = esql.executeQueryAndReturnResult(query);
         int cnt = 0;
         DisplayMessages(esql, msgList, cnt);
           
         // Message submenu
         boolean minimenu = true;
         while(minimenu){
             System.out.println("...............");
             System.out.println("1. Add new message");
             System.out.println("2. Edit a message");
             System.out.println("3. Delete a message");
             // Asks to display more if there are unseen messages
             if((msgList.size() - (cnt + 10)) > 0){
                System.out.println("4. Display more messages");
             }
             System.out.println("..................");
             System.out.println("9. Return to chat menu");
             switch(readChoice()){
                case 1: NewMessage(esql, author, cid); break;
                case 2: EditMessage(esql, author); break;
                case 3: DeleteMessage(esql, author); break;
                case 4: cnt += 10; DisplayMessages(esql, msgList, cnt); break;
                case 9: minimenu = false; break;
                default: System.out.println("Unrecognized choice!\n"); break;
             }
         }
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end ChatViewer


   public static void DisplayMessages(Messenger esql, List<List<String>> msgList, int cnt){
      // Your code goes here.
      // Display all messages if size is 10 or less
      int view = cnt;
      if((msgList.size() - cnt) > 10)
         view = cnt + 10;
      else
         view = msgList.size();
         
      // Display the messages
         for(int i = cnt; i < view; ++i){
            String msgId = msgList.get(i).get(0);
            String msgTime = msgList.get(i).get(1);
            String msgText = msgList.get(i).get(2);
            String msgSender = msgList.get(i).get(3);
            
            int num = msgList.size() - i;
            System.out.println("(" + num + ") " + "Message #: " + msgId);
            System.out.println("\tSent at: " + msgTime);
            System.out.println("\tFrom: " + msgSender);
            System.out.println("\t" + msgText);
         }
   }//end DisplayMessages

}//end Messenger
