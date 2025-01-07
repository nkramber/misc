import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;

public class PoliticalPartyGuesser {
    private Scanner scanner;
    private List<LinkedHashMap<String, Integer>> surveyQuestions;
    private int[][] responses;
    //Green = 0, Libertarian = 1, Republican = 2, Democrat = 3

    public static void main(String[] args) {
        PoliticalPartyGuesser ppg = new PoliticalPartyGuesser();
        ppg.run();
    }

    public PoliticalPartyGuesser() {
        scanner = new Scanner(System.in);
        surveyQuestions = new ArrayList<>();
        responses = new int[10][4];
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 4; y++) {
                responses[x][y] = 0;
            }
        }

        try (BufferedReader br = new BufferedReader(new FileReader("questions.txt"))) {
            String line;
            int i = 0;
            HashMap<String, Integer> surveyQuestion = new LinkedHashMap<>();
            while ((line = br.readLine()) != null) {
                i++;
                if (i % 6 == 0) {
                    surveyQuestions.add(new LinkedHashMap<String, Integer>(surveyQuestion));
                    surveyQuestion = new LinkedHashMap<String, Integer>();
                    continue;
                }
                surveyQuestion.put(line, i % 6 - 2);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        boolean running = true;

        System.out.println("You are taking a survey that will attempt to guess what political party you support. Please answer the questions as accurately"
        + " as possible by typing the number of the option you wish to select and pressing enter. Type 'exit' at any time to quit.");
                    System.out.println();

        int i = 0;
        for (LinkedHashMap<String, Integer> question : surveyQuestions) {
            for (Entry<String, Integer> entry : question.entrySet()) {
                System.out.println(entry.getKey());
            }

            String input = scanner.nextLine();
            while (!validInput(input)) {
                System.out.println("Please enter a valid option");
                input = scanner.nextLine();
            }

            if (input.equals("exit")) {
                running = false;
                break;
            }

            responses[i][Integer.parseInt(input) - 1] = 1;
            System.out.println();
            i++;
        }

        int[][] partySums = new int[4][1];
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 4; y++) {
                partySums[y][0] += responses[x][y];
            }
        }

        for (int j = 0; j < partySums.length; j++) {
            //Calculate party based on highest of 4 scores
        }

        System.out.println();
        System.out.println("Press enter to exit");
        scanner.nextLine();
        stop();
    }

    private void stop() {
        scanner.close();
    }

    private boolean validInput(String s) {
        return (s.equals("exit") || s.equals("1") || s.equals("2") || s.equals("3") || s.equals("4"));
    }
}
