import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;

public class PoliticalPartyGuesser {
    private Scanner scanner;
    //List of hashmaps to hold the survey questions
    private List<LinkedHashMap<String, Integer>> surveyQuestions;
    private int[][] responses;
    //Green = 0, Libertarian = 1, Republican = 2, Democrat = 3

    public static void main(String[] args) {
        //Create instance of our program and start it
        PoliticalPartyGuesser ppg = new PoliticalPartyGuesser();
        ppg.run();
    }

    public PoliticalPartyGuesser() {
        //Constructor to initialize our user inputs, create our arrays and lists, and initialize them
        scanner = new Scanner(System.in);
        surveyQuestions = new ArrayList<>();
        responses = new int[7][4];
        for (int x = 0; x < responses.length; x++) {
            for (int y = 0; y < responses[x].length; y++) {
                responses[x][y] = 0;
            }
        }

        //Read the questions from the file provided and store them in our list
        try (BufferedReader br = new BufferedReader(new FileReader("questions.txt"))) {
            String line;
            int i = 0;
            HashMap<String, Integer> surveyQuestion = new LinkedHashMap<>();
            while ((line = br.readLine()) != null) {
                i++;
                //Each question has 6 lines - question line, 4 responses, and a blank line. Seperate the questions by knowing that their are 6 lines each
                if (i % 6 == 0) {
                    surveyQuestions.add(new LinkedHashMap<String, Integer>(surveyQuestion));
                    surveyQuestion = new LinkedHashMap<String, Integer>();
                    continue;
                }
                //Use the remainder of the line number divided by 6 - 2 as the value for the question key
                surveyQuestion.put(line, i % 6 - 2);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        System.out.println("You are taking a survey that will attempt to guess what political party you support. Please answer the questions as accurately"
        + " as possible by typing the number of the option you wish to select and pressing enter. Type 'exit' at any time to quit.");
                    System.out.println();

        //Cycle through each question
        int i = 0;
        for (LinkedHashMap<String, Integer> question : surveyQuestions) {
            for (Entry<String, Integer> entry : question.entrySet()) {
                System.out.println(entry.getKey());
            }

            System.out.println();
            String input = scanner.nextLine();

            //Only accept what we consider to be valid inputs
            List<String> validInputs = new ArrayList<>();
            validInputs.add("1");
            validInputs.add("2");
            validInputs.add("3");
            validInputs.add("4");
            validInputs.add("exit");
            while (!validInput(input, validInputs)) {
                System.out.println();
                System.out.println("Please enter a valid option");
                System.out.println();
                input = scanner.nextLine();
            }

            if (input.equals("exit")) stop();

            //Record the user response
            responses[i][Integer.parseInt(input) - 1] = 1;
            System.out.println();
            i++;
        }

        //Calculate the total guesses by cycling through the responses
        int[][] partySums = new int[4][1];
        for (int x = 0; x < responses.length; x++) {
            for (int y = 0; y < responses[x].length; y++) {
                partySums[y][0] += responses[x][y];
            }
        }

        //Calculate the result
        int result = 0;
        int resultCount = 0;
        for (int j = 0; j < partySums.length; j++) {
            if (partySums[j][0] > resultCount) {
                result = j;
                resultCount = partySums[j][0];
            }
        }

        String guessedParty = "";
        if (result == 0) guessedParty = "green"; 
        if (result == 1) guessedParty = "libertarian"; 
        if (result == 2) guessedParty = "republican"; 
        if (result == 3) guessedParty = "democrat"; 

        System.out.println("Based on your responses, we guess that you identify most closely with the " + guessedParty + " political party.\n");
        System.out.println("Please type the name of the political party you identify most closely with so we can get better at guessing! (please enter "
                    + "green, libertarian, republican, or democrat)");
        System.out.println();
        String actualParty = scanner.nextLine().toLowerCase();

        List<String> validInputs = new ArrayList<>();
        validInputs.add("green");
        validInputs.add("libertarian");
        validInputs.add("republican");
        validInputs.add("democrat");
        while (!validInput(actualParty, validInputs)) {
            System.out.println();
            System.out.println("Please enter a valid option");
            System.out.println();
            actualParty = scanner.nextLine().toLowerCase();
        }

        //Record out results
        writeResultsFile(responses, result, guessedParty, actualParty);

        System.out.println();
        System.out.println("Press enter to exit");
        scanner.nextLine();
        stop();
    }

    private void stop() {
        scanner.close();
        System.exit(0);
    }

    private boolean validInput(String s, List<String> validInputs) {
        for (String validInput : validInputs) {
            if (s.equals(validInput)) return true;
        }
        return false;
    }

    private void writeResultsFile(int[][] responses, int result, String guessedParty, String actualParty) {
        //Create a list to hold our responses
        List<String> responseList = new ArrayList<>();

        //Cycle though the responses and build the list to be recorded
        for (int i = 0; i < responses.length; i++) {
            StringBuilder response = new StringBuilder();
            for (int j = 0; j < responses[i].length; j++) {
                if (responses[i][j] == 1) {
                    response.append("Question " + (i + 1) + " answered " + (j + 1));
                }
            }
            if (!response.isEmpty()) responseList.add(response.toString());
        }

        String fileName = actualParty + ".txt";

        //Write our responses to a file named whatever party they input
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
            if (new File(fileName).length() > 0) writer.newLine();
            writer.write("We guessed the party was: " + guessedParty);
            writer.newLine();
            writer.write("Actual party: " + actualParty);
            writer.newLine();
            for (String string : responseList) {
                writer.write(string);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
