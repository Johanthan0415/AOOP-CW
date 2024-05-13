import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Observable;

public class NumberleModel extends Observable implements INumberleModel {
    private String targetNumber;
    private StringBuilder currentGuess;
    private int remainingAttempts;
    private boolean gameWon;
    private List<String> equationsList;
    private List<String> guessHistory;

    @Override
    public void initialize() {
        loadEquations();
        Random rand = new Random();
        int randomIndex = rand.nextInt(equationsList.size());
        targetNumber = equationsList.get(randomIndex);
        currentGuess = new StringBuilder("       ");
        remainingAttempts = MAX_ATTEMPTS;
        gameWon = false;
        guessHistory = new ArrayList<>();
        setChanged();
        notifyObservers();
    }

    @Override
    public boolean processInput(String input) {
        // Check if the input is valid
        if (!isValidInput(input)) {
            return false;
        }

        // Check if the guess is correct
        boolean guessCorrect = isGuessCorrect(input);

        // Record guessing history
        guessHistory.add(input);

        // Update current guessing status
        updateCurrentGuess(input, guessCorrect);

        // Check if you have won the game
        if (guessCorrect) {
            gameWon = true;
        }
        // Update remaining attempts
        remainingAttempts--;
        // Notify observers of updates
        setChanged();
        notifyObservers();

        return true;
    }

    @Override
    public List<String> getGuessHistory() {
        return new ArrayList<>(guessHistory);
    }

    @Override
    public boolean isGameOver() {
        return remainingAttempts <= 0 || gameWon;
    }

    @Override
    public boolean isGameWon() {
        return gameWon;
    }

    @Override
    public String getTargetNumber() {
        return targetNumber;
    }

    @Override
    public StringBuilder getCurrentGuess() {
        return currentGuess;
    }

    @Override
    public int getRemainingAttempts() {
        return remainingAttempts;
    }

    @Override
    public void startNewGame() {
        initialize();
    }

    @Override
    public boolean isCharacterCorrect(String guess, int i) {
        // Get target number
        String targetNumber = getTargetNumber();
        // Check if the current character matches the corresponding position character in the target number
        char currentChar = guess.charAt(i);
        return currentChar == targetNumber.charAt(i);
    }


    @Override
    public boolean isCharacterInTarget(String guess, int i) {
        String targetNumber = getTargetNumber();
        char currentChar = guess.charAt(i);
        // Check if the current character is in another position within the target number and not in the current position
        return targetNumber.indexOf(currentChar) != -1 && currentChar != targetNumber.charAt(i);
    }

    @Override
    public boolean compareExpressions(String target, String guess) {
        // Check if the input is valid
        if (!isValidInput(guess)) {
            return false;
        }

        // Check if the guess is correct
        boolean guessCorrect = isGuessCorrect(guess);

        // Record guessing history
        guessHistory.add(guess);

        // Update current guessing status
        updateCurrentGuess(guess, guessCorrect);

        // Check if you have won the game
        if (guessCorrect) {
            gameWon = true;
        }
        // Update remaining attempts
        remainingAttempts--;
        // Initialize a tag array to record whether each number is correctly matched
        boolean[] matched = new boolean[target.length()];

        // Initialize a tag array to record the matching status of each character
        boolean[] correctPosition = new boolean[target.length()];

        // Check the matching of each character
        for (int i = 0; i < target.length(); i++) {
            char targetChar = target.charAt(i);
            char guessChar = guess.charAt(i);

            if (targetChar == guessChar) {
                // If the characters are the same, set it to match successfully and mark it as the correct position
                matched[i] = true;
                correctPosition[i] = true;
            }
        }

        // Check for characters that are not in the correct position but are in the target equation
        for (int i = 0; i < target.length(); i++) {
            char guessChar = guess.charAt(i);

            if (!correctPosition[i] && target.contains(String.valueOf(guessChar))) {
                // If the character is in the target equation but not in the correct position, set it to match successfully
                matched[target.indexOf(guessChar)] = true;
            }
        }

        // Output results and set background color
        boolean allMatched = true;
        for (int i = 0; i < target.length(); i++) {
            char targetChar = target.charAt(i);
            char guessChar = guess.charAt(i);

            if (matched[i]) {
                // If character matching is successful, set the background color to green
                System.out.print(ANSI_GREEN_BACKGROUND + targetChar + ANSI_RESET);
            } else if (target.contains(String.valueOf(guessChar))) {
                // If the character is in the target equation but not in the correct position, set the background color to orange
                System.out.print(ANSI_ORANGE_BACKGROUND + guessChar + ANSI_RESET);
                allMatched = false;
            } else {
                // If the characters do not match successfully, set the background color to gray
                System.out.print(ANSI_GREY_BACKGROUND + guessChar + ANSI_RESET);
                allMatched = false;
            }
        }
        System.out.println(); //

        // Determine if the guess was successful
        if (allMatched) {
        } else {
            System.out.println("Continuing to speculate");
        }

        return guessCorrect;
    }

    // Read the formulas from the equations.txt file and store them in the equationsList
    private void loadEquations() {
        equationsList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("equations.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                equationsList.add(line.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Check if the input is valid
    private boolean isValidInput(String input) {
        // Check if the input length is 7 characters
        if (input.length() != 7) {
            return false;
        }

        // Check if the input characters are legal
        for (char ch : input.toCharArray()) {
            if (!isValidCharacter(ch)) {
                return false;
            }
        }

        return true;
    }

    // Check if a single character is legal
    private boolean isValidCharacter(char ch) {
        // Check if the character is a number or one of the following arithmetic symbols:+- */=
        return Character.isDigit(ch) || "+-*/=".indexOf(ch) != -1;
    }

    // Check if the guess is correct
    private boolean isGuessCorrect(String input) {
        // Check if the user's input guess is exactly the same as the target number
        return input.equals(targetNumber);
    }

    // Update current guessing status
    private void updateCurrentGuess(String input, boolean guessCorrect) {
        currentGuess.replace(0, input.length(), input);
    }

    //ANSI Console Color Code
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
    public static final String ANSI_ORANGE_BACKGROUND = "\u001B[43m";
    public static final String ANSI_GREY_BACKGROUND = "\u001B[47m";
}
