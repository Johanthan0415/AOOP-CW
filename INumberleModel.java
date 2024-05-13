import java.util.List;

public interface INumberleModel {
    int MAX_ATTEMPTS = 6;

    void initialize();

    boolean processInput(String input);

    List<String> getGuessHistory();

    boolean isGameOver();

    boolean isGameWon();

    String getTargetNumber();

    StringBuilder getCurrentGuess();

    int getRemainingAttempts();

    void startNewGame();

    boolean isCharacterCorrect(String guess, int i);

    boolean isCharacterInTarget(String guess, int i);

    boolean compareExpressions(String target, String guess);
}