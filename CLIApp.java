import java.util.Scanner;

public class CLIApp {
    public static void main(String[] args) {
        // Create model
        INumberleModel model = new NumberleModel();

        // Initialize the game
        model.startNewGame();

        // Game main loop
        Scanner scanner = new Scanner(System.in);
        while (!model.isGameOver()) {
            // Display current guesses and remaining attempts
            System.out.println("Remaining times：" + model.getRemainingAttempts());

            // Get user input
            System.out.print("Please enter your guess:");
            String input = scanner.nextLine();

            // Process user input
            model.compareExpressions(model.getTargetNumber(), input);
        }

        // Display results after the game ends
        if (model.isGameWon()) {
            System.out.println("Congratulations, you guessed it right! The target number is：" + model.getTargetNumber());
        } else {
            System.out.println("Unfortunately, the game failed! The target number is:" + model.getTargetNumber());
        }
    }
}
