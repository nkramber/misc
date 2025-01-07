import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

public class Lottery {
    private Scanner input = new Scanner(System.in);

    public static void main(String[] args) {
        // Create a new instance of the lottery class
        Lottery lottery = new Lottery();

        // Execute the run method of the lottery object
        lottery.run();
    }

    private void run() {
        // Get input from the player to determine the variables for the lottery
        int odds = printAndGetPositiveInt("What are the odds of winning the lottery?: 1/");
        int playsPerDraw = printAndGetPositiveInt("How many tickets per lottery draw will you buy?: " );
        int drawsPerWeek = printAndGetPositiveInt("How many times will the lottery draw per week?: ");
        int costPerTicket = printAndGetPositiveInt("How much does one ticket cost?: $");

        int tries = 1;
        boolean hasWon = false;

        // Loop until the player wins
        while (!hasWon) {
            // Check if a random integer between 0 and the maximum odds set by the player is equal to 1
            if (getRandomInt(odds) == 0) {
                hasWon = true;
                // Calculate how much money was spent on tickets
                int money_spent = costPerTicket * tries;
                String timeUnit = "weeks";
                // Calculate how many weeks it took to win
                int timeToWin = tries / playsPerDraw / drawsPerWeek;

                // If it took more than 52 weeks, change the unit to years
                if (timeToWin > 52) {
                    timeUnit = "years";
                    timeToWin = timeToWin / 52;
                }

                // Check if the player won before hitting the expected odds
                String result = "You beat the odds!";
                if (tries > odds) {
                    result = "You didn't beat the odds...";
                }

                // Output the results
                System.out.println("You win! It took you " + timeToWin + " " + timeUnit + " to win. You spent $" + money_spent + ". You bought " + tries + " tickets. " + result);
            } else {
                // If the player didn't win, add 1 to the tries and try again
                tries += 1;
            }
        }
    }

    private int printAndGetPositiveInt(String s) {
        int i = 0;
        // Until the user inputs an integer above 0, keep asking the player a specified question or string
        while (i <= 0) {
            try {
                System.out.print(s);
                i = input.nextInt();
            // Listen for an exception that means the player input a letter or letters instead of a number
            } catch (InputMismatchException e) {
                input.next();
            }
        }

        return i;
    }

    private int getRandomInt(int max) {
        // Return a random integer between 0 and the max odds
        return ThreadLocalRandom.current().nextInt(max);
    }
}