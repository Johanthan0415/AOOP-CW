import static org.junit.Assert.*;
import org.junit.*;

public class NumberleModelTest {
    private NumberleModel model;

    @Before
    public void setUp() {
        model = new NumberleModel();
        model.initialize();
    }

    @Test
    public void testProcessInput() {
        // Test valid inputs
        assertTrue(model.processInput("1234567"));
        // Test invalid input
        assertFalse(model.processInput("12345"));
    }

    @Test
    public void testIsGameOver() {
        // After initialization, the game should not end
        assertFalse(model.isGameOver());
        // Simulate the situation of game failure
        for (int i = 0; i < NumberleModel.MAX_ATTEMPTS; i++) {
            model.processInput("1111111");
        }
        assertTrue(model.isGameOver());
    }

    @Test
    public void testIsGameWon() {
        // You shouldn't win at the beginning of the game
        assertFalse(model.isGameWon());
        // Set the target number for the model and simulate the situation of winning the game
        model.processInput(model.getTargetNumber());
        assertTrue(model.isGameWon());
    }

    @Test
    public void testGetRemainingAttempts() {
        // After initialization, the remaining attempts should be the maximum number of attempts
        assertEquals(NumberleModel.MAX_ATTEMPTS, model.getRemainingAttempts());
        // Simulate a few wrong guesses
        for (int i = 0; i < 3; i++) {
            model.processInput("1111111");
        }
        assertEquals(NumberleModel.MAX_ATTEMPTS - 3, model.getRemainingAttempts());
    }

}
