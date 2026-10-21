import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class BankQueueManagement {

    private static final Queue<String> bankQueue =
            new LinkedList<>();

    private static final Scanner scanner =
            new Scanner(System.in);

  
    public static void addCustomer() {
        System.out.print("Enter customer name: ");
        String name = scanner.nextLine().trim();

        if (name.isEmpty()) {
            System.out.println("Customer name cannot be empty.");
            return;
        }

        bankQueue.offer(name);
        System.out.println(name + " has been added to the queue.");
    }

  
    public static void serveCustomer() {
        String customer = bankQueue.poll();

        if (customer == null) {
            System.out.println("No customers are waiting.");
        } else {
            System.out.println("Now serving: " + customer);
        }
    }

  
    public static void displayQueue() {
        if (bankQueue.isEmpty()) {
            System.out.println("The queue is empty.");
            return;
        }

        System.out.println("\nCustomers waiting (Front to Rear):");

        int position = 1;
        for (String customer : bankQueue) {
            System.out.println(position + ". " + customer);
            position++;
        }

        System.out.println("Total waiting: " + bankQueue.size());
    }

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n=== Bank Queue Management System ===");
            System.out.println("1. Add customer");
            System.out.println("2. Serve next customer");
            System.out.println("3. Display waiting queue");
            System.out.println("4. View number of waiting customers");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    addCustomer();
                    break;

                case "2":
                    serveCustomer();
                    break;

                case "3":
                    displayQueue();
                    break;

                case "4":
                    System.out.println(
                        "Customers waiting: " + bankQueue.size()
                    );
                    break;

                case "5":
                    System.out.println("System closed.");
                    scanner.close();
                    return;

                default:
                    System.out.println(
                        "Invalid option. Please try again."
                    );
            }
        }
    }
}
