import java.io.IOException;
import java.util.Scanner;

public class ATM {
	private Bank bank;
    private Scanner in;
    private BankAccount activeAccount;

    public static final int VIEW = 1;
    public static final int DEPOSIT = 2;
    public static final int WITHDRAW = 3;
    public static final int TRANSFER = 4;
    public static final int LOGOUT = 5;
    ////////////////////////////////////////////////////////////////////////////
    //                                                                        //
    // Refer to the Simple ATM tutorial to fill in the details of this class. //
    // You'll need to implement the new features yourself.                    //
    //                                                                        //
    ////////////////////////////////////////////////////////////////////////////
	public static final String FIRST_NAME_WIDTH = "20";
	public static final String LAST_NAME_WIDTH = "30";

    /**
     * Constructs a new instance of the ATM class.
     */

    public ATM() {
        this.in = new Scanner(System.in);
        
        //sactiveAccount = new BankAccount(1234, 123456789, 0, new User("Ryan", "Wilson"));
        try {
			this.bank = new Bank();
		} catch (IOException e) {
			// cleanup any resources (i.e., the Scanner) and exit
		}
    }


    /*
     * Application execution begins here.
     */
    public void hello() {
    	System.out.println("Welcome to the AIT ATM!");
    	startup();
    }
    
    public void commit() {
    	bank.update(activeAccount);
    	bank.save();
    }
    
    public void startup() {

        while (true) {
            System.out.print("\nAccount No.: ");
            String accountString = in.next();
            
            if (accountString.trim().equals("+")) {
            	signUp();
            }
            
            System.out.print("PIN        : ");
            int pin = in.nextInt();
            Long accountNo = Long.parseLong(accountString);
            
            if (isValidLogin(accountNo, pin)) {
                System.out.println("\nHello, again, " + activeAccount.getAccountHolder().getFirstName() + "!");

                boolean validLogin = true;
                while (validLogin) {
                    switch (getSelection()) {
                        case VIEW: showBalance(); break;
                        case DEPOSIT: deposit(); break;
                        case WITHDRAW: withdraw(); break;
                        case TRANSFER: transfer(); break;
                        case LOGOUT: validLogin = false; break;
                        default: System.out.println("\nInvalid selection."); break;
                    }
                }
            } else {            	
                if (Long.toString(accountNo).equals("-1") && pin == -1) {
                    shutdown();
                } else {
                    System.out.println("\nInvalid account number and/or PIN.");
                }
            }
        }
    }

    public boolean isValidLogin(long accountNo, int pin) {
    	activeAccount = bank.login(accountNo, pin);
    	return activeAccount != null;
    }

    public int getSelection() {
        System.out.println("\n[1] View balance");
        System.out.println("[2] Deposit money");
        System.out.println("[3] Withdraw money");
        System.out.println("[4] Transfer money");
        System.out.println("[5] Logout");
        return in.nextInt();
    }

    public void showBalance() {
        System.out.println("\nCurrent balance: " + activeAccount.getBalance());
    }

    public void signUp () {
    	String firstName = "";
    	String lastName = "";
    	int pin;
    	
    	do {
    		System.out.print("\nFirst name: ");
    		firstName = in.next();
    	} while ((firstName.length() > 20 || firstName.length() < 1) || firstName == null);

    	do{
    		System.out.print("\nLast name: ");
    		lastName = in.next();
    	} while ((lastName.length() > 20 || lastName.length() < 1) || lastName == null);
    	
    	do {
    		System.out.print("\nPIN: ");
    		pin = in.nextInt();
    	} while (pin < 1000 || pin > 9999);
    	
    	long accountNo = bank.generateAccountNo();
    	activeAccount = new BankAccount(pin, accountNo, 0, new User(firstName, lastName));
    	bank.createAccount(pin, new User(firstName, lastName));
    	System.out.println("\nThank you. Your account number is " + activeAccount.getAccountNo() + ".\nPlease login to access your newly created account.");
    	commit();
    	this.startup();
    }

    public void deposit() {
        System.out.print("\nEnter amount: ");
        double amount = in.nextDouble();

        if (amount <= 0) {
        	System.out.println("\nDeposit rejected. Amount must be greater than $0.00.");
        } else if ((amount + activeAccount.balance) > 999999999999.99) {
        	System.out.println("\nDeposit rejected. Amount would cause balance to exceed $999,999,999,999.99.");
        } else {
        	activeAccount.deposit(amount);
            commit();
        }
    }

    public void withdraw() {
        System.out.println("\nEnter amount: ");
        double amount = in.nextDouble();
        
        if (amount <= 0) {
        	System.out.println("\nWithdrawal rejected. Amount must be greater than $0.00.");
        } else if ((activeAccount.balance - amount) < 0) {
        	System.out.println("\nWithdrawal rejected. Insufficient funds.");
        } else {
        	activeAccount.withdraw(amount);
        	commit();
        }
    }
    
    public void transfer() {
    	System.out.print("\nEnter account: ");
    	Long otherAccountNo = in.nextLong();
    	
    	if (bank.getAccount(otherAccountNo) != null) {
    		System.out.print("\nEnter amount: ");
            double transferAmount = in.nextDouble();
            
            if (transferAmount <= 0) {
            	System.out.println("\nDeposit rejected. Amount must be greater than $0.00.");	
            } else if (activeAccount.balance - transferAmount < 0) {
            	System.out.println("\nTransfer rejected. Insufficient funds.");
            } else if ((transferAmount + bank.getAccount(otherAccountNo).balance) > 999999999999.99) {
            	System.out.println("\nTransfer rejected. Amount would cause destination balance to exceed $999,999,999,999.99.");         
            } else {
            	activeAccount.withdraw(transferAmount);
            	bank.getAccount(otherAccountNo).deposit(transferAmount);
            	System.out.println("\nTransfer accepted!"); 
                commit();
            }
    	} else {
    		System.out.println("\nTransfer rejected. Destination account not found.");
    	}
    }

    public void shutdown() {
        if (in != null) {
            in.close();
        }

        System.out.println("\nGoodbye!");
        System.exit(0);
    }

    public static void main(String[] args) {
        ATM atm = new ATM();

        atm.hello();
    }
}
