package com.company.ubank;

import com.company.ubank.dtos.Account;
import com.company.ubank.dtos.Transaction;
import com.company.ubank.exceptions.AccountAlreadyRegisteredException;
import com.company.ubank.exceptions.AccountNotFoundException;
import com.company.ubank.exceptions.IncorrectPasswordException;
import com.company.ubank.exceptions.InsufficientBalanceException;
import com.company.ubank.services.*;


import java.util.Scanner;

public class Application {
    private Scanner scan;

    private AccountService accountService;
    private TransactionService transactionService;

    //a flag used to check whether a user is logged in or not
    private boolean isLoggedIn;

    //an attribute to store account no of the logged in user
    private int loggedInAccountNo;

    public Application (AccountService accountService, TransactionService transactionService) {
        scan = new Scanner(System.in);
        this.accountService = accountService;
        this.transactionService = transactionService;
        isLoggedIn = false;
        loggedInAccountNo = 0;
    }

    private void start () {
        boolean flag = true;

        System.out.println("*********************");
        System.out.println("********U-Bank*******");
        System.out.println("*********************");

        do {
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Account");
            System.out.println("4. Deposit");
            System.out.println("5. Withdraw");
            System.out.println("6. Account Statement");
            System.out.println("7. Logout");
            System.out.println("8. Exit");

            System.out.print("\nPlease select an option: ");
            String choice = scan.nextLine();

            switch (choice) {
                case "1": login(); break;
                case "2": register(); break;
                case "3": getAccount(); break;
                case "4": deposit(); break;
                case "5": withdraw(); break;
                case "6": getAccountStatement(); break;
                case "7": logout(); break;
                case "8": flag=false; break;
                default:  System.out.println("Error"); break;
            }
        } while (flag);
    }

    //This method is used to perform login function for the user.
    //If the user is already logged in, then he won't be able to login again.
    //Also a user can only login, if the account no and password provided by
    //the user are present in the accounts array.
    private void login () {
        if (isLoggedIn) {
            System.out.println("You are already logged in.");
            return;
        }

        System.out.println("*********************");
        System.out.println("********Login********");
        System.out.println("*********************");

        Account account = getAccountFromUser();

        try {
            if (accountService.login(account)) {
                System.out.println("You are logged in.");
                isLoggedIn = true;
                loggedInAccountNo = account.getAccountNo();
            }
        } catch (NullPointerException e) {
            //code to execute when account object was null
            System.out.println(e.getMessage());
        } catch (AccountNotFoundException e) {
            //code to execute when account no was not found
            System.out.println(e.getMessage());
        } catch (IncorrectPasswordException e) {
            //code to execute when password is incorrect.
            System.out.println(e.getMessage());
        }
    }

    //This method is used to perform register function for the user.
    //If the user is already logged in, then he won't be able to register.
    //Also a user can only register, if the account no and password provided by
    //the user are not present in the accounts array.
    private void register () {
        if (isLoggedIn) {
            System.out.println("You are already logged in.");
            return;
        }

        System.out.println("*********************");
        System.out.println("******Register*******");
        System.out.println("*********************");

        Account account = getAccountFromUser();

        try {
            if (accountService.register(account)) {
                System.out.println("You are logged in.");
                isLoggedIn = true;
                loggedInAccountNo = account.getAccountNo();
            }
        } catch (NullPointerException e) {
            //code to execute when account object was null
            System.out.println(e.getMessage());
        } catch (AccountAlreadyRegisteredException e) {
            //code to execute when account already registered
            System.out.println(e.getMessage());
        }
    }

    private Account getAccountFromUser() {
        System.out.print("Account No.:");
        int accountNo = 0;

        try {
            accountNo = Integer.parseInt(scan.nextLine());
            System.out.println("You entered: " + accountNo);
        } catch (NumberFormatException e) {
            System.out.println("Account number should be in numeric form.");
            return null;
        } finally {
            System.out.println("Current account no: " + accountNo);
        }

        System.out.print("Password:");
        String password = scan.nextLine();

        Account account = new Account();
        account.setAccountNo(accountNo);
        account.setPassword(password);
        return account;
    }

    private void getAccount () {
        if (!isLoggedIn) {
            System.out.println("You are not logged in.");
            return;
        }

        System.out.println("*********************");
        System.out.println("*******Account*******");
        System.out.println("*********************");

        try {
            System.out.println(accountService.getAccount(loggedInAccountNo));
        } catch (AccountNotFoundException e) {
            System.out.println("Account not found");
        }
    }

    private void deposit () {
        if (!isLoggedIn) {
            System.out.println("You are not logged in.");
            return;
        }

        System.out.println("*********************");
        System.out.println("*******Deposit*******");
        System.out.println("*********************");

        System.out.print("Amount: ");
        int amount = 0;

        try {
            amount = Integer.parseInt(scan.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Amount should be in numeric form");
            return;
        }
        try {
            Account account = accountService.deposit(loggedInAccountNo, amount);
            System.out.println("Money successfully deposited into account.");

        }catch ( AccountNotFoundException e) {
            System.out.println("Could not deposit into account.");
        }
    }

    private void withdraw () {
        if (!isLoggedIn) {
            System.out.println("You are not logged in.");
            return;
        }

        System.out.println("*********************");
        System.out.println("******Withdraw*******");
        System.out.println("*********************");

        System.out.print("Amount: ");
        int amount = 0;

        try {
            amount = Integer.parseInt(scan.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Amount should be in numeric form");
            return;
        }
        try {
            Account account = accountService.withdraw(loggedInAccountNo, amount);
            System.out.println("Money successfully withdrawn from account.");

        }catch (AccountNotFoundException e) {
            System.out.println("Could not withdraw from account.");

        }
        catch (InsufficientBalanceException e){
            System.out.println("Could not withdraw from account.");

        }

    }

    private void getAccountStatement() {
        if (!isLoggedIn) {
            System.out.println("You are not logged in.");
            return;
        }

        System.out.println("*********************");
        System.out.println("**Account Statement**");
        System.out.println("*********************");

        Transaction[] transactions = transactionService.getTransactions(loggedInAccountNo);
        if (transactions == null) {
            System.out.println("This feature is not available for mobile");
            return;
        } else if (transactions[0] == null) {
            System.out.println("No transaction exists for you.");
            return;
        }
        for (Transaction transaction: transactions) {
            if (transaction == null) {
                break;
            }
            System.out.println(transaction);
        }
    }

    private void logout () {
        if (!isLoggedIn) {
            System.out.println("You are not logged in.");
            return;
        }
        System.out.println("Logged out successfully");
        isLoggedIn = false;
        loggedInAccountNo = 0;
    }

    public static void main(String[] args) {
        TransactionService transactionService = new TransactionServiceImpl();
        AccountService accountService = new AccountServiceImpl(transactionService);
        Application application = new Application(accountService, transactionService);
        application.start();
    }
}
