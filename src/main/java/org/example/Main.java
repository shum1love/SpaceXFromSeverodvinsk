package org.example;

import java.util.Scanner;

//TIP <b>SPACE COMPANY</b>
public class Main {

    public static void main(String[] args) {
        Main app = new Main();
        app.sayWelcome();
    }

    private void sayWelcome() {
        String companyName = "SpaceX";
        System.out.printf("Hello, we are %s company.%n", companyName);
        System.out.println("How much money do you have?");

        Scanner scanner = new Scanner(System.in);
        int personCash = scanner.nextInt();

        for (Rockets rocket : Rockets.values()) {
            int rocketCanBuy = personCash / rocket.getRocketPrice();
            System.out.printf("You can buy %d rocket(s) %s%n", rocketCanBuy, rocket.getRocketName());
        }

        System.out.println("Какую рокету вы хотите купить?");
    }
}
