import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Observer;
import java.util.Map;
import java.util.HashMap;

public class NumberleView implements Observer {
    private final INumberleModel model;
    private final NumberleController controller;
    private final JFrame frame = new JFrame("Numberle");
    private final JTextField inputTextField = new JTextField(3);
    ;
    private final JLabel attemptsLabel = new JLabel("Attempts remaining: ");

    private final JPanel guessPanel = new JPanel();
    private final List<JLabel[]> guessLabelsList = new ArrayList<>();

    public NumberleView(INumberleModel model, NumberleController controller) {
        this.controller = controller;
        this.model = model;
        this.controller.startNewGame();
        ((NumberleModel) this.model).addObserver(this);
        initializeFrame();
        this.controller.setView(this);
        update((NumberleModel) this.model, null);
    }

    public void initializeFrame() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 200);
        frame.setLayout(new BorderLayout());

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.X_AXIS));
        center.add(new JPanel());

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(3, 1));

        inputPanel.add(inputTextField);

        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(e -> {
            controller.processInput(inputTextField.getText());
            inputTextField.setText("");
        });
        inputPanel.add(submitButton);
        //Restart
        JButton restartButton = new JButton("Restart");
        restartButton.addActionListener(e -> {
            controller.startNewGame();
        });

        JButton testButton = new JButton("Target Equation");
        testButton.addActionListener(e -> {
            // Obtain the target number and display it directly in the historical guessing record area
            String targetNumber = model.getTargetNumber();
            List<String> testHistory = new ArrayList<>();
            testHistory.add(targetNumber);
            updateGuessHistory(testHistory);
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(restartButton);
        buttonPanel.add(testButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        attemptsLabel.setText("Attempts remaining: " + controller.getRemainingAttempts());
        inputPanel.add(attemptsLabel);
        center.add(inputPanel);
        center.add(new JPanel());
        frame.add(center, BorderLayout.NORTH);

        frame.add(guessPanel, BorderLayout.CENTER); // Add an empty guessing panel

        frame.setVisible(true);

        JPanel keyboardPanel = new JPanel();
        keyboardPanel.setLayout(new BoxLayout(keyboardPanel, BoxLayout.X_AXIS));
        keyboardPanel.add(new JPanel());
        JPanel numberPanel = new JPanel();
        numberPanel.setLayout(new GridLayout(2, 5));
        keyboardPanel.add(numberPanel);

        for (int i = 0; i < 10; i++) {
            JButton button = new JButton(Integer.toString(i));
            button.setEnabled(true);
            button.addActionListener(e -> {
                inputTextField.setText(inputTextField.getText() + button.getText());
            });
            button.setPreferredSize(new Dimension(50, 50));
            numberPanel.add(button);
        }

        // Add symbol button
        String[] symbols = {"+", "-", "*", "/", "="};
        for (String symbol : symbols) {
            JButton button = new JButton(symbol);
            button.addActionListener(e -> {
                inputTextField.setText(inputTextField.getText() + symbol);
            });
            button.setPreferredSize(new Dimension(50, 50));
            numberPanel.add(button);
        }

        // Add Backspace Button
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            String text = inputTextField.getText();
            if (!text.isEmpty()) {
                inputTextField.setText(text.substring(0, text.length() - 1));
            }
        });
        backButton.setPreferredSize(new Dimension(50, 50));
        numberPanel.add(backButton);

        keyboardPanel.add(new JPanel());

        frame.add(keyboardPanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private void updateGuessHistory(List<String> guessHistory) {
        // Clear History Panel
        guessPanel.removeAll();

        // Record the number of occurrences of each character in the target number
        Map<Character, Integer> targetCharCount = new HashMap<>();
        for (char c : model.getTargetNumber().toCharArray()) {
            targetCharCount.put(c, targetCharCount.getOrDefault(c, 0) + 1);
        }

        // Process historical guessing records line by line
        for (String guess : guessHistory) {
            JPanel guessRowPanel = new JPanel(new GridLayout(1, guess.length()));
            guessRowPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);

            // Record the number of occurrences of each character in the guess
            Map<Character, Integer> guessCharCount = new HashMap<>();
            for (char c : guess.toCharArray()) {
                guessCharCount.put(c, guessCharCount.getOrDefault(c, 0) + 1);
            }

            // Set the display and background color for each character
            for (int i = 0; i < guess.length(); i++) {
                char currentChar = guess.charAt(i);
                Color backgroundColor;

                // Get the number of times this character appears in the target number
                int targetCharOccurrences = targetCharCount.getOrDefault(currentChar, 0);
                // Obtain the number of occurrences of this character in the guess
                int guessCharOccurrences = guessCharCount.getOrDefault(currentChar, 0);

                // If the character only appears once in the target number and its position in the guess is incorrect, it is marked in orange
                if (targetCharOccurrences == 1 && !controller.isCharacterCorrect(guess, i)) {
                    //Only symbols/numbers corresponding to the number of times are marked with an orange background
                    backgroundColor = guessCharOccurrences > targetCharOccurrences ? Color.WHITE : Color.ORANGE;
                }
                // Handle other situations according to the original logic
                else if (controller.isCharacterCorrect(guess, i)) {
                    backgroundColor = Color.GREEN;
                } else if (controller.isCharacterInTarget(guess, i)) {
                    backgroundColor = Color.ORANGE;
                } else {
                    backgroundColor = Color.GRAY;
                }

                JLabel label = new JLabel(String.valueOf(currentChar));
                label.setOpaque(true);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setBackground(backgroundColor);

                guessRowPanel.add(label);
            }

            guessPanel.add(guessRowPanel); // Add a row to the history panel
        }

        // Redraw Panel
        guessPanel.revalidate();
        guessPanel.repaint();
    }


    @Override
    public void update(java.util.Observable o, Object arg) {
        attemptsLabel.setText("Attempts remaining: " + controller.getRemainingAttempts());
        StringBuilder currentGuess = ((NumberleModel) model).getCurrentGuess();
        String targetNumber = model.getTargetNumber();
        List<String> guessHistory = ((NumberleModel) model).getGuessHistory(); // Obtain guessing history

        if (guessLabelsList.isEmpty()) {
            // Initialize Guess Panel
            initializeGuessPanel(targetNumber.length(), guessHistory.size() + 1);
        }

        // Update current guess
        updateCurrentGuess(currentGuess, targetNumber);

        // Update historical guessing records
        updateGuessHistory(guessHistory);


        // Check if the game has ended
        if (controller.isGameOver()) {
            // Display game result message box
            if (controller.isGameWon()) {
                JOptionPane.showMessageDialog(frame, "Congratulations! You won the game!");
            } else {
                JOptionPane.showMessageDialog(frame, "Sorry! You lost the game. Please try again.");
            }
        }
    }

    private void initializeGuessPanel(int length, int rows) {
        guessPanel.setLayout(new GridLayout(rows, length));
        guessLabelsList.clear();
        for (int i = 0; i < rows; i++) {
            JLabel[] guessLabels = new JLabel[length];
            for (int j = 0; j < length; j++) {
                guessLabels[j] = new JLabel(" ");
                guessLabels[j].setHorizontalAlignment(SwingConstants.CENTER);
                guessPanel.add(guessLabels[j]);
            }
            guessLabelsList.add(guessLabels);
        }
    }

    private void updateCurrentGuess(StringBuilder currentGuess, String targetNumber) {
        JLabel[] currentGuessLabels = guessLabelsList.get(guessLabelsList.size() - 1);
        for (int i = 0; i < Math.min(currentGuess.length(), targetNumber.length()); i++) {
            char currentChar = currentGuess.charAt(i);
            char targetChar = targetNumber.charAt(i);
            Color backgroundColor;
            if (Character.isDigit(currentChar) || "+-*/=".indexOf(currentChar) != -1) {
                backgroundColor = Color.WHITE;
            } else {
                backgroundColor = Color.GRAY;
            }
            currentGuessLabels[i].setText(String.valueOf(currentChar));
            currentGuessLabels[i].setOpaque(true);
            currentGuessLabels[i].setBackground(backgroundColor);
            if (currentChar == targetChar) {
                currentGuessLabels[i].setForeground(Color.GREEN);
            } else if (targetNumber.indexOf(currentChar) != -1) {
                currentGuessLabels[i].setForeground(Color.ORANGE);
            } else {
                currentGuessLabels[i].setForeground(Color.GRAY);
            }
        }
    }
}
