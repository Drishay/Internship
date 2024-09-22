import java.util.Scanner;
import java.sql.*; // * represents that every library is imported from SQL

public class BankService {
    
    // Displays the menu for the user and processes the selected option
    public boolean menu(Scanner scanner, Connection connection, SqlDatabase sqlDatabase, String User_ID) throws SQLException {
        boolean pass = true;
        
        // Displaying menu options
        System.out.println("\nSelect a method");
        System.out.println("0. Log Out");
        System.out.println("1. Account Details");
        System.out.println("2. View Balance");
        System.out.println("3. Deposit Amount");
        System.out.println("4. Withdraw Amount");
        System.out.println("5. Transfer Amount");
        System.out.print("Enter your choice: ");
        
        try {
            String choice = scanner.nextLine(); // Read user's choice

            switch (choice) {
                case "1": // View account details
                    Account_details account_details = new Account_details();
                    account_details.display_details(scanner, connection, sqlDatabase, User_ID);
                    break;

                case "2": // Check account balance
                    Check_Balance check_Balance = new Check_Balance();
                    check_Balance.view_Balance(scanner, connection, sqlDatabase, User_ID);
                    break;

                case "3": // Deposit money into the account
                    Deposit_Balance deposit_Balance = new Deposit_Balance();
                    deposit_Balance.deposit_Balance(scanner, connection, sqlDatabase, User_ID);
                    break;

                case "4": // Withdraw money from the account
                    Withdraw_Balance withdraw_Balance = new Withdraw_Balance();
                    withdraw_Balance.withdraw_Balance(scanner, connection, sqlDatabase, User_ID);
                    break;

                case "5": // Transfer money to another account
                    Transfer transfer = new Transfer();
                    transfer.transfer(scanner, connection, sqlDatabase, User_ID);
                    break;

                case "0": // Log out of the system
                    System.out.println("Log Out");
                    pass = false; // Log out
                    break;

                default: // Handle invalid choice
                    System.out.println("Invalid choice");
                    break;
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage()); // Handle any exceptions
        }

        return pass; // Return whether the user stays logged in
    }
}

class Account_details {
    
    // Displays specific account details based on user's selection
    public void display_details(Scanner scanner, Connection connection, SqlDatabase sqlDatabase, String User_ID) throws SQLException {
        while (true) {
            // Display options for details to view
            System.out.println("\nSelect a detail to view");
            System.out.println("0. GO back");
            System.out.println("1. Account Holder's Name");
            System.out.println("2. Account Holder's Parent Name");
            System.out.println("3. Account Holder's Date Of Birth");
            System.out.println("4. Account Holder's PAN Number");
            System.out.println("5. Account Holder's Aadhar Number");
            System.out.println("6. Account Holder's Address");
            System.out.println("7. Account Holder's Phone Number");
            System.out.println("8. Account Holder's Email");
            System.out.println("9. Account Holder's Account Number");
            System.out.println("10. Account Holder's Account Type");
            System.out.println("11. Account Holder's Account Status");
            System.out.println("12. Account Holder's Account Balance");

            String detail_to_get = null; // Default value
            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine(); // Read user's choice

            // Map user's choice to the corresponding detail
            switch (choice) {
                case "0": break; // Go back to the main menu
                case "1": detail_to_get = "User_Name"; break;
                case "2": detail_to_get = "User_Parent_Name"; break;
                case "3": detail_to_get = "User_DOB"; break;
                case "4": detail_to_get = "User_Pan_Number"; break;
                case "5": detail_to_get = "User_Aadhar_Number"; break;
                case "6": detail_to_get = "User_Address"; break;
                case "7": detail_to_get = "User_Phone_Number"; break;
                case "8": detail_to_get = "User_Email"; break;
                case "9": detail_to_get = "User_Account_Number"; break;
                case "10": detail_to_get = "User_Account_Type"; break;
                case "11": detail_to_get = "User_Account_Status"; break;
                case "12": detail_to_get = "User_Account_Balance"; break;
                default: System.out.println("Invalid choice"); continue;
            }

            if (choice.equals("0")) {
                break; // Exit the loop and return to the main menu
            }

            // Query and display the selected detail
            try {
                String result = null;
                String query = "select " + detail_to_get + " from customer_details where User_ID = \"" + User_ID + "\";";
                ResultSet resultSet = sqlDatabase.read(query, connection);

                if (resultSet.next()) {
                    result = resultSet.getString(detail_to_get); // Fetch the requested detail
                } else {
                    System.out.println("\n" + detail_to_get + ": not found");
                }

                System.out.println("\n" + detail_to_get + ": " + result); // Print the result

            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage()); // Handle any exceptions
            }
        }
    }
}

class Check_Balance {
    
    // Views the account balance of the user
    public void view_Balance(Scanner scanner, Connection connection, SqlDatabase sqlDatabase, String User_ID) throws SQLException {
        try {
            String result = null;
            String query = "select User_Account_Balance from customer_details where User_ID = \"" + User_ID + "\";";
            ResultSet resultSet = sqlDatabase.read(query, connection); // Query to get the account balance

            if (resultSet.next()) {
                result = resultSet.getString("User_Account_Balance"); // Fetch the balance
            } else {
                System.out.println("\nAccount details not available");
            }

            System.out.println("\nUser_Account_Balance: " + result); // Display the balance

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage()); // Handle any exceptions
        }
    }
}

class Deposit_Balance {

    // Deposits a specific amount into the user's account
    public void deposit_Balance(Scanner scanner, Connection connection, SqlDatabase sqlDatabase, String User_ID) throws SQLException {
        try {
            Double account_balance = null;
            Double newBalance = null;
            String query1 = "select User_Account_Balance from customer_details where User_ID = \"" + User_ID + "\";";
            ResultSet resultSet = sqlDatabase.read(query1, connection); // Query to get the current account balance

            if (resultSet.next()) {
                account_balance = resultSet.getDouble("User_Account_Balance"); // Fetch the balance
                System.out.print("\nEnter amount to deposit: ");
                Double amount = scanner.nextDouble(); // Get the amount to deposit
                scanner.nextLine(); // Add this line to consume the newline character

                if (amount >= 0) {
                    newBalance = account_balance + amount; // Calculate the new balance
                    String query2 = "UPDATE customer_details SET User_Account_Balance = " + newBalance + ";";
                    sqlDatabase.write(query2, connection); // Update the balance in the database

                    System.out.println("\nCustomer Account Balance updated, Please check it!");
                } else {
                    System.out.println("\nInvalid amount");
                }
            } else {
                System.out.println("\nAccount details not available");
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage()); // Handle any exceptions
        }
    }
}

class Withdraw_Balance {

    // Withdraws a specific amount from the user's account
    public void withdraw_Balance(Scanner scanner, Connection connection, SqlDatabase sqlDatabase, String User_ID) throws SQLException {
        try {
            Double account_balance = null;
            Double newBalance = null;
            String query1 = "select User_Account_Balance from customer_details where User_ID = \"" + User_ID + "\";";
            ResultSet resultSet = sqlDatabase.read(query1, connection); // Query to get the current account balance

            if (resultSet.next()) {
                account_balance = resultSet.getDouble("User_Account_Balance"); // Fetch the balance
                System.out.print("\nEnter amount to withdraw: ");
                Double amount = scanner.nextDouble(); // Get the amount to withdraw
                scanner.nextLine(); // Add this line to consume the newline character

                if (account_balance >= 0) {
                    if (amount <= account_balance && amount >= 0) {
                        newBalance = account_balance - amount; // Calculate the new balance
                        String query2 = "UPDATE customer_details SET User_Account_Balance = " + newBalance + ";";
                        sqlDatabase.write(query2, connection); // Update the balance in the database

                        System.out.println("\nCustomer Account Balance updated, Please check it!");
                    } else if (amount < 0) {
                        System.out.println("\nInvalid amount");
                    } else {
                        System.out.println("\nInsufficient balance");
                    }
                } else {
                    System.out.println("\nInsufficient balance");
                }
            } else {
                System.out.println("\nAccount details not available");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage()); // Handle any exceptions
        }
    }
}

class Transfer {

    // Transfers a specific amount from the user's account to another account
    public void transfer(Scanner scanner, Connection connection, SqlDatabase sqlDatabase, String User_ID) throws SQLException {
        try {
            Double Payee_account_balance = null;
            Double account_balance = null;
            Double newBalance = null;
            Double amount = 0.0;

            // Get the Payee's account number
            System.out.print("\nEnter your Payee's Account Number: ");
            String Payee_Account_Number = scanner.nextLine();
            String query = "Select User_Account_Balance from customer_details where User_Account_Number = \"" + Payee_Account_Number + "\" ;";
            ResultSet resultSet = sqlDatabase.read(query, connection); // Query to get the Payee's balance

            if (resultSet.next()) {
                Payee_account_balance = resultSet.getDouble("User_Account_Balance"); // Fetch the Payee's balance

                String query1 = "select User_Account_Balance from customer_details where User_ID = \"" + User_ID + "\"; ";
                ResultSet resultSet1 = sqlDatabase.read(query1, connection); // Query to get the user's balance

                if (resultSet1.next()) {
                    account_balance = resultSet1.getDouble("User_Account_Balance"); // Fetch the user's balance
                    System.out.print("Enter amount to transfer: ");
                    amount = scanner.nextDouble(); // Get the amount to transfer
                    scanner.nextLine(); // Add this line to consume the newline character

                    if (account_balance >= 0) {
                        if (amount <= account_balance && amount >= 0) {
                            newBalance = account_balance - amount; // Calculate the new balance for the user
                            String query2 = "UPDATE customer_details SET User_Account_Balance = " + newBalance + " WHERE User_ID = \"" + User_ID + "\";";
                            sqlDatabase.write(query2, connection); // Update the user's balance in the database

                            System.out.println("\nCustomer Account Balance updated, Please check it!");

                            newBalance = Payee_account_balance + amount; // Calculate the new balance for the Payee
                            String query3 = "UPDATE customer_details SET User_Account_Balance = " + newBalance + " WHERE User_Account_Number = \"" + Payee_Account_Number + "\";";
                            sqlDatabase.write(query3, connection); // Update the Payee's balance in the database

                            System.out.println("Payee Account Balance updated, Please check it!");
                        } else if (amount < 0) {
                            System.out.println("\nInvalid amount");
                        } else {
                            System.out.println("\nInsufficient balance");
                        }
                    } else {
                        System.out.println("\nInsufficient balance");
                    }
                } else {
                    System.out.println("\nAccount details not available");
                }
            } else {
                System.out.println("\nAccount details not available");
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage()); // Handle any exceptions
        }
    }
}
