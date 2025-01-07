import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BoyerMoore {

    //Initial data
    static String[] states = {"Alabama", "Alaska", "Arizona", "Arkansas", "California", "Colorado", "Connecticut", "Delaware", "Florida", "Georgia", "Hawaii"
        , "Idaho", "Illinois", "Indiana", "Iowa", "Kansas", "Kentucky", "Louisiana", "Maine", "Maryland", "Massachusetts", "Michigan", "Minnesota"
        , "Mississippi", "Missouri", "Montana", "Nebraska", "Nevada", "New Hampshire", "New Jersey", "New Mexico", "New York", "North Carolina"
        , "North Dakota", "Ohio", "Oklahoma", "Oregon", "Pennsylvania", "Rhode Island", "South Carolina", "South Dakota", "Tennessee", "Texas"
        , "Utah", "Vermont", "Virginia", "Washington", "West Virginia", "Wisconsin", "Wyoming"};
    static Scanner scanner;

    public static void main(String[] args) {
        boolean running = true;
        scanner = new Scanner(System.in);
        String input;

        //Keep running until user selected exit option
        try {
            while(running) {
                System.out.println("Please select an option by typing the number and hitting enter");
                System.out.println();
                System.out.println("1. Display the text");
                System.out.println("2. Search");
                System.out.println("3. Exit program");
                System.out.println();

                //Get input from user
                input = scanner.nextLine().toLowerCase();

                System.out.println();

                if (input.equals("1")) {
                    //Cycle through all of the states and print them out
                    for (int i = 0; i < states.length; i++) {
                        System.out.print(states[i]);
                        if (i < states.length - 1) System.out.print(", ");
                        else {
                            System.out.println();
                            System.out.println();
                        }
                    }
                } else if (input.equals("2")) {
                    //Get input of what to search for
                    System.out.print("Please enter some text to search for: ");
                    String pattern = scanner.nextLine().toLowerCase();

                    //Cycle through all of the states and look for matches
                    for (String state : states) {
                        List<Integer> positions = boyerMoore(state.toLowerCase(), pattern);
                        if (!positions.isEmpty()) System.out.println(pattern + " found in " + state + " at position(s) " + positions);
                    }
                //Exit if 3 is entered
                } else if (input.equals("3")) running = false;
            }
        } finally {
            scanner.close();
        }
    }

    private static List<Integer> boyerMoore(String text, String pattern) {
        List<Integer> result = new ArrayList<>();
        int[] badCharTable = buildBadCharTable(pattern);

        int patternLength = pattern.length();
        int textLength = text.length();
        int shift = 0;

        //While the shift is smaller than the difference between the lengths of our strings
        while (shift <= (textLength - patternLength)) {
            int j = patternLength - 1;
            //Cycle backwards through the pattern and text
            while (j >= 0 && pattern.charAt(j) == text.charAt(shift + j)) {
                j--;
            }

            //If J is less than 0 it means we found our pattern in the string
            if (j < 0) {
                result.add(shift);
                if (shift + patternLength < textLength) shift += patternLength - badCharTable[text.charAt(shift + patternLength)];
                else shift += 1;
            } else {
                shift += Math.max(1, j - badCharTable[text.charAt(shift + j)]);
            }
        }

        return result;
    }

    //Cycle through every possible character and record the last recorded position of each
    private static int[] buildBadCharTable(String pattern) {
        int[] table = new int[256];
        int patternLength = pattern.length();
        
        for (int i = 0; i < 256; i++) {
            table[i] = -1;
        }
        for (int i = 0; i < patternLength; i++) {
            table[pattern.charAt(i)] = i;
        }

        return table;
    }
}
