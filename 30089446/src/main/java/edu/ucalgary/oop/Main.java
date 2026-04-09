package edu.ucalgary.oop;

import java.util.Scanner;

public class Main {

  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);

    VictimController victimController = new VictimController();
    SupplyController supplyController = new SupplyController();
    SkillController skillController = new SkillController();
    CulturalRequirementController culturalController =
      new CulturalRequirementController();
    InquiryController inquiryController = new InquiryController();
    FamilyRelationController familyRelationController =
      new FamilyRelationController();

    boolean running = true;

    while (running) {
      System.out.println();
      System.out.println(
        "----------- Disaster Relief Management System -----------"
      );
      System.out.println("1. Victim Management");
      System.out.println("2. Supply Management");
      System.out.println("3. Skill Registry");
      System.out.println("4. Cultural or Religious Requirements");
      System.out.println("5. Inquiry Management");
      System.out.println("6. Family Relationship Management");
      System.out.println("7. Exit");

      int choice = readInt(scanner, "Please choose an option (1-7): ");

      switch (choice) {
        case 1:
          victimController.menu();
          break;
        case 2:
          supplyController.menu();
          break;
        case 3:
          skillController.menu();
          break;
        case 4:
          culturalController.menu();
          break;
        case 5:
          inquiryController.menu();
          break;
        case 6:
          familyRelationController.menu();
          break;
        case 7:
          running = false;
          break;
        default:
          System.out.println("Invalid option. Please choose 1-7.");
      }
    }

    DatabaseManager.getInstance().close();
    System.out.println("Goodbye.");
  }

  private static int readInt(Scanner scanner, String prompt) {
    while (true) {
      System.out.print(prompt);
      String input = scanner.nextLine().trim();

      try {
        return Integer.parseInt(input);
      } catch (RuntimeException e) {
        System.out.println("Please enter a valid number.");
      }
    }
  }
}
