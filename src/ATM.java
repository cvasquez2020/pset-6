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
    	System.out.println("Welcome to the AIT ATM!\n");
    	startup();
    }
    public void startup() {

        while (true) {
            System.out.print("Account No.: ");
            String accountStr = in.next();
            if (accountStr.charAt(0) == '+' || accountStr.length() == 0) {
            	signUp();
            }
            System.out.print("PIN        : ");
            int pin = in.nextInt();
            Long accountNo = Long.parseLong(accountStr);
            if (isValidLogin(accountNo, pin)) {
                System.out.println("\nHello, again, " + activeAccount.getAccountHolder().getFirstName() + "!\n");

                boolean validLogin = true;
                while (validLogin) {
                    switch (getSelection()) {
                        case VIEW: showBalance(); break;
                        case DEPOSIT: deposit(); break;
                        case WITHDRAW: withdraw(); break;
                        case TRANSFER: transfer(); break;
                        case LOGOUT: validLogin = false; break;
                        default: System.out.println("\nInvalid selection.\n"); break;
                    }
                }
            } else {
                if (Long.toString(accountNo).contentEquals("-1") && pin == -1) {
                    shutdown();
                } else {
                    System.out.println("\nInvalid account number and/or PIN.\n");
                }
            }
        }
    }

    public boolean isValidLogin(long accountNo, int pin) {
    	activeAccount = bank.login(accountNo, pin);
    	
    	if (activeAccount != null) {
    		return true;
    	} else {
    		return false;
    	}
    }

    public int getSelection() {
        System.out.println("[1] View balance");
        System.out.println("[2] Deposit money");
        System.out.println("[3] Withdraw money");
        System.out.println("[4] Transfer money");
        System.out.println("[5] Logout");
        return in.nextInt();
    }

    public void showBalance() {
        System.out.println("\nCurrent balance: " + activeAccount.getBalance());
        System.out.println();
    }

    public void signUp () {
    	String firstName = "";
    	String lastName = "";
    	int pin;
    	
    	do {
    		System.out.print("\nFirst name: ");
    		firstName = in.next();
    		System.out.println(firstName == null);
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
    	bank.update(activeAccount);
    	bank.save();
    	this.startup();
    }

    public void deposit() {
        System.out.print("\nEnter amount: ");
        double amount = in.nextDouble();

        activeAccount.deposit(amount);
        System.out.println();
        bank.update(activeAccount);
    	bank.save();
    }
    
    

    public void withdraw() {
        System.out.print("\nEnter amount: ");
        double amount = in.nextDouble();

        activeAccount.withdraw(amount);
        System.out.println();
        bank.update(activeAccount);
    	bank.save();
    }
    
    public void transfer() {
    	System.out.print("\nEnter account: ");
    	Long otherAccountNo = in.nextLong();
    	if (bank.getAccount(otherAccountNo) != null) {
    		
    		System.out.print("\nEnter amount: ");
            double transferAmount = in.nextDouble();
            
            if (transferAmount == 0) {
            	System.out.println("Deposit rejected. Amount must be greater than $0.00.");
            	
            }
            
            activeAccount.withdraw(transferAmount);
            bank.getAccount(otherAccountNo).deposit(transferAmount);
            
            System.out.println("\nTransfer accepted!"); 
            System.out.println();
            bank.update(activeAccount);
        	bank.save();
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
