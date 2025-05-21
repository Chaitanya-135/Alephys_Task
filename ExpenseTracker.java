package com.index;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

class Transaction {
    String type;
    String category;
    double amount;
    LocalDate date;

    public Transaction(String type, String category, double amount, LocalDate date) {
        this.type = type;
        this.category = category;
        this.amount = amount;
        this.date = date;
    }

    @Override
    public String toString() {
        return String.join(",", type, category, String.valueOf(amount), date.toString());
    }
}

public class ExpenseTracker {
    private static final List<Transaction> transactions = new ArrayList<>();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("======= Expense Tracker =======");

        while (true) {
            System.out.println("\n--- Menu ---");
            System.out.println("1. Add Income");
            System.out.println("2. Add Expense");
            System.out.println("3. View Monthly Summary");
            System.out.println("4. Load Transactions from File");
            System.out.println("5. Save Transactions to File");
            System.out.println("6. Exit");
            System.out.print("Choose option (1 to 6): ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1" -> addTransaction("Income");
                case "2" -> addTransaction("Expense");
                case "3" -> viewSummaryByMonth();
                case "4" -> loadFromFile();
                case "5" -> saveToFile();
                case "6" -> {
                    System.out.println("Exiting. Thank you!");
                    return;
                }
                default -> System.out.println("Invalid choice. Try 1 to 6.");
            }
        }
    }

    private static void addTransaction(String type) {
        System.out.printf("\n--- Add %s ---\n", type);
        System.out.print("Enter category: ");
        String category = scanner.nextLine();

        System.out.print("Enter amount: ");
        double amount;
        try {
            amount = Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount. Try again.");
            return;
        }

        LocalDate date = LocalDate.now();
        transactions.add(new Transaction(type, category, amount, date));
        System.out.println(type + " recorded.");
    }

    private static void viewSummaryByMonth() {
        System.out.print("\nEnter month to view (YYYY-MM): ");
        try {
            String[] input = scanner.nextLine().split("-");
            int year = Integer.parseInt(input[0]);
            int month = Integer.parseInt(input[1]);

            Map<String, Double> income = new HashMap<>();
            Map<String, Double> expense = new HashMap<>();
            double totalIncome = 0, totalExpense = 0;

            for (Transaction t : transactions) {
                if (t.date.getYear() == year && t.date.getMonthValue() == month) {
                    if (t.type.equals("Income")) {
                        totalIncome += t.amount;
                        income.merge(t.category, t.amount, Double::sum);
                    } else {
                        totalExpense += t.amount;
                        expense.merge(t.category, t.amount, Double::sum);
                    }
                }
            }

            System.out.printf("\n--- Summary for %04d-%02d ---\n", year, month);
            System.out.println("Income: " + totalIncome);
            income.forEach((cat, amt) -> System.out.println(" - " + cat + ": " + amt));
            System.out.println("Expense: " + totalExpense);
            expense.forEach((cat, amt) -> System.out.println(" - " + cat + ": " + amt));
            System.out.println("Net Savings: " + (totalIncome - totalExpense));

        } catch (Exception e) {
            System.out.println("Invalid format. Use YYYY-MM.");
        }
    }

    private static void loadFromFile() {
        System.out.print("Enter file name to load: ");
        String fileName = scanner.nextLine();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            int count = 0;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                if (isFirstLine && line.toLowerCase().contains("Type,Category,Amount,Date")) {
                    isFirstLine = false;
                    continue;
                }

                String[] parts = line.split(",");
                if (parts.length != 4) {
                    System.out.println("Skipping invalid line: " + line);
                    continue;
                }

                try {
                    String type = parts[0].trim();
                    String category = parts[1].trim();
                    double amount = Double.parseDouble(parts[2].trim());
                    LocalDate date = LocalDate.parse(parts[3].trim(), formatter);

                    transactions.add(new Transaction(type, category, amount, date));
                    count++;
                } catch (Exception e) {
                    System.out.println("Error parsing line: " + line);
                    System.out.println(" Reason: " + e.getMessage());
                }
            }

            System.out.println(count + "-Transactions Are Loaded From File.");
        } catch (IOException e) {
            System.out.println("Error loading file: " + e.getMessage());
        }
    }


    private static void saveToFile() {
        System.out.print("Enter file name to save: ");
        String fileName = scanner.nextLine();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (Transaction t : transactions) {
                writer.write(t.toString());
                writer.newLine();
            }
            System.out.println(transactions.size() + "-Transactions Are Saved.");
        } catch (IOException e) {
            System.out.println("Error writing file: " + e.getMessage());
        }
    }
}