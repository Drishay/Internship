import java.util.Scanner; 
import java.sql.*; // Import all SQL libraries
import java.util.HashMap;

public class SecurityCheck {

    // This method performs a security check (login validation) and returns the login status.
    public String[] securityCheck(Connection connection, Scanner scanner, SqlDatabase sqlDatabase, String tableName) throws SQLException {
        ChooseLogin chooseLogin = new ChooseLogin(); // Initialize the ChooseLogin class
        String[] loggedIn = chooseLogin.chooseLogin(scanner, connection, sqlDatabase, tableName); // Attempt to login

        if (Boolean.parseBoolean(loggedIn[1])) { // If login is successful (LoggedIn = true)
            System.out.println("\nLogin Successful");
        } else {
            System.out.println("\nLogin Unsuccessful");
        }
        return loggedIn; // Return user login status and user ID
    }
}

class ChooseLogin {
    
    boolean pass; // Default value of pass is false

    // This method allows the user to either login or exit the system.
    public String[] chooseLogin(Scanner scanner, Connection connection, SqlDatabase sqlDatabase, String tableName) throws SQLException {
        while (true) {
            System.out.print("\nLogin (L) or Exit (E) : "); // Ask user to login or exit
            String choice = scanner.nextLine(); // Take user input

            switch (choice) {
                case "L":
                    Login login = new Login(); // Initialize Login class
                    return login.login(scanner, connection, sqlDatabase, tableName); // Attempt to login

                case "E":
                    System.exit(0); // Exit the program if user selects "E"

                default:
                    System.out.println("Invalid choice"); // Invalid input handling
            }

        }
    }
}

class Login {
    
    // This method handles the login process
    public String[] login(Scanner scanner, Connection connection, SqlDatabase sqlDatabase, String tableName) throws SQLException {
        String userID = null; // Initialize userID to null
        String userPassword;
        boolean pass = false; // Default login pass state is false

        int password_attempts = 3; // Max number of password attempts = 3
        for (int i = 1; i <= password_attempts; i++) {

            try {
                System.out.print("\nEnter your USER ID: ");
                userID = scanner.nextLine(); // Get user ID from input

                // Query to get user password from database
                String query = "Select User_Password from " + tableName + " where User_ID = \"" + userID + "\" ;";
                ResultSet database_Password = sqlDatabase.read(query, connection); // Execute query to retrieve password

                if (database_Password.next()) { // If user ID is found
                   String databasePassword = database_Password.getString("User_Password"); // Get password from result

                    System.out.print("Enter your Password: ");
                    userPassword = scanner.nextLine(); // Take user password input

                    Hashing_Password hashing_Password = new Hashing_Password(); // Initialize password hashing class
                    String user_hashed_Password = hashing_Password.hashPassword(userPassword); // Hash user password

                    // Check if hashed password matches the one in the database
                    if (user_hashed_Password.equals(databasePassword)){
                        pass = true; // Login is successful
                        break; // Exit the loop after successful login
                    } 
                    else {
                        System.out.println("Invalid Password"); // Incorrect password handling
                    }

                    // If max attempts reached, prompt for password recovery
                    if (i == password_attempts) {
                        System.out.print("\nForgot password? yes or no: ");
                        String choice = scanner.nextLine(); // Get user input for forgot password
                        if (choice.equalsIgnoreCase("yes") || choice.equalsIgnoreCase("y")){
                            Forgot_Password forgot_Password = new Forgot_Password(); // Initialize Forgot_Password class
                            forgot_Password.forgot_password(scanner, connection, sqlDatabase, userID, tableName); // Handle forgot password
                            pass = false; // Set pass to false after password recovery
                        }
                        else{
                            break;
                        }
                    }

                } else {
                    System.out.println("user ID not found"); // If no user ID found
                }

            } catch (SQLException e) {
                System.out.println(e.getMessage()); // Handle SQL exception
            }
        }
        return new String[]{userID, String.valueOf(pass)}; // Return user ID and login status
    }
}

class Forgot_Password {
    // This method handles the forgot password functionality
    public void forgot_password(Scanner scanner, Connection connection, SqlDatabase sqlDatabase, String userID, String tableName) throws SQLException {
        int forgot_answer_attempts = 3; // Max number of attempts to answer the forgot question = 3
        for (int i = 1; i <= forgot_answer_attempts; i++) {

            try {
                // Query to retrieve user's security question
                String query = "Select User_Forgot_Question from " + tableName + " where User_ID = \"" + userID + "\" ;";
                ResultSet forgot_question = sqlDatabase.read(query, connection); 

                // Query to retrieve user's security answer
                String query1 = "Select User_Forgot_Answer from " + tableName + " where User_ID = \"" + userID + "\" ;";
                ResultSet forgot_answer = sqlDatabase.read(query1, connection); 

                if (forgot_question.next()) { // If security question found
                    String forgot_Question = forgot_question.getString("User_Forgot_Question"); // Retrieve question
                    System.out.println("\nForgot question: " + forgot_Question); // Display the security question

                    if (forgot_answer.next()) { // If security answer found
                        String forgot_Answer = forgot_answer.getString("User_Forgot_Answer"); // Retrieve the answer
                        System.out.print("Enter your answer: ");
                        String entered_forgot_Answer = scanner.nextLine(); // Take user input for security answer
                        Hashing_Password hashing_Password = new Hashing_Password(); // Initialize Hashing_Password class
                        entered_forgot_Answer = hashing_Password.hashPassword(entered_forgot_Answer); // Hash the entered answer

                        // If hashed answer matches the one in the database
                        if (entered_forgot_Answer.equals(forgot_Answer)) {
                            // Allow user to set a new password
                            for (int j = 1; j <= 3; j++ ){
                                System.out.print("\nnew password (50 character limit): ");
                                String new_password = scanner.nextLine(); // Take new password input
                                System.out.print("confirm password: ");
                                String confirm_password = scanner.nextLine(); // Confirm new password
                                if (new_password.equals(confirm_password)){
                                    String new_password_hash = hashing_Password.hashPassword(new_password); // Hash new password
                                    String query2 = "UPDATE " + tableName + " SET User_Password = \"" + new_password_hash + "\" WHERE User_ID = \"" + userID + "\";";
                                    sqlDatabase.write(query2, connection); // Update the database with new password
                                    System.out.println("Password changed\n"); // Inform user of password change
                                    break; // Exit loop after successful password change
                                }
                                else{
                                    System.out.println("Passwords do not match"); // Handle mismatched passwords
                                }
                            }
                            break; // Exit loop after correct answer
                        }
                        else {
                            System.out.println("Invalid Answer\n"); // Incorrect answer handling
                        }
                    }
                    else {
                        System.out.println("Forgot Answer not found\n"); // If security answer not found
                    }

                } else {
                    System.out.println("Forgot Question not found\n"); // If security question not found
                }

            } catch (SQLException e) {
                System.out.println(e.getMessage()); // Handle SQL exception
            }
        }
    }
}

class Hashing_Password {
    // This method hashes the password based on a predefined mapping
    public String hashPassword(String password) {
        // HashMap to store the characters and their hashed values
        HashMap<Character, String> hashMap = new HashMap<>();
        
        // Small alphabets and their corresponding hash values
        hashMap.put('a', "a1A"); hashMap.put('b', "b2B"); hashMap.put('c', "c3C"); hashMap.put('d', "d4D");
        hashMap.put('e', "e5E"); hashMap.put('f', "f6F"); hashMap.put('g', "g7G"); hashMap.put('h', "h8H");
        hashMap.put('i', "i9I"); hashMap.put('j', "j10J"); hashMap.put('k', "k11K"); hashMap.put('l', "l12L");
        hashMap.put('m', "m13M"); hashMap.put('n', "n14N"); hashMap.put('o', "o15O"); hashMap.put('p', "p16P");
        hashMap.put('q', "q17Q"); hashMap.put('r', "r18R"); hashMap.put('s', "s19S"); hashMap.put('t', "t20T");
        hashMap.put('u', "u21U"); hashMap.put('v', "v22V"); hashMap.put('w', "w23W"); hashMap.put('x', "x24X");
        hashMap.put('y', "y25Y"); hashMap.put('z', "z26Z");

        // Capital alphabets and their corresponding hash values
        hashMap.put('A', "A1a"); hashMap.put('B', "B2b"); hashMap.put('C', "C3c"); hashMap.put('D', "D4d");
        hashMap.put('E', "E5e"); hashMap.put('F', "F6f"); hashMap.put('G', "G7g"); hashMap.put('H', "H8h");
        hashMap.put('I', "I9i"); hashMap.put('J', "J10j"); hashMap.put('K', "K11k"); hashMap.put('L', "L12l");
        hashMap.put('M', "M13m"); hashMap.put('N', "N14n"); hashMap.put('O', "O15o"); hashMap.put('P', "P16p");
        hashMap.put('Q', "Q17q"); hashMap.put('R', "R18r"); hashMap.put('S', "S19s"); hashMap.put('T', "T20t");
        hashMap.put('U', "U21u"); hashMap.put('V', "V22v"); hashMap.put('W', "W23w"); hashMap.put('X', "X24x");
        hashMap.put('Y', "Y25y"); hashMap.put('Z', "Z26z");

        // Numbers and their corresponding hash values
        hashMap.put('0', "019"); hashMap.put('1', "128"); hashMap.put('2', "237"); hashMap.put('3', "346");
        hashMap.put('4', "455"); hashMap.put('5', "564"); hashMap.put('6', "673"); hashMap.put('7', "782");
        hashMap.put('8', "891"); hashMap.put('9', "9100");

        // Special characters and their corresponding hash values
        hashMap.put('!', "!1!"); hashMap.put('@', "@2@"); hashMap.put('#', "#3#"); hashMap.put('$', "$4$");
        hashMap.put('%', "%5%"); hashMap.put('^', "^6^"); hashMap.put('&', "&7&"); hashMap.put('*', "*8*");
        hashMap.put('(', "(9("); hashMap.put(')', ")10)"); hashMap.put('-', "-11-"); hashMap.put('_', "_12_");
        hashMap.put('=', "=13="); hashMap.put('+', "+14+"); hashMap.put('{', "{15{"); hashMap.put('}', "}16}");
        hashMap.put('[', "[17["); hashMap.put(']', "]18]"); hashMap.put('\\', "\\19\\"); hashMap.put('|', "|20|");
        hashMap.put(';', ";21;"); hashMap.put(':', ":22:"); hashMap.put('\'', "'23'"); hashMap.put('"', "\"24\"");
        hashMap.put(',', ",25,"); hashMap.put('.', ".26."); hashMap.put('/', "/27/"); hashMap.put('?', "?28?");
        hashMap.put('<', "<29<"); hashMap.put('>', ">30>"); hashMap.put('~', "~31~"); hashMap.put('`', "`32`");

        // Space character
        hashMap.put(' ', " 50 ");

        // StringBuilder to construct the hashed password
        StringBuilder hashedPassword = new StringBuilder();

        // Loop through each character in the password and append its hash value
        for (char ch : password.toCharArray()) {
            if (hashMap.containsKey(ch)) {
                hashedPassword.append(hashMap.get(ch)); // Add hashed value for the character
            } else {
                hashedPassword.append(ch); // If not found in hash map, append the character itself
            }
        }

        return hashedPassword.toString(); // Return the hashed password
    }
}
