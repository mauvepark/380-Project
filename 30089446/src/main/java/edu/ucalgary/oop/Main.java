package edu.ucalgary.oop;

public class Main {
    public static void main(String[] args) {
        DatabaseManager db = DatabaseManager.getInstance();
        System.out.println("Connected.");
        db.close();
    }
}
