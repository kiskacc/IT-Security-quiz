
package quiz.model;

import java.util.List;
import java.util.Random;

public interface Question {
    String getPrompt();
    List<String> getOptions();      // index 0 => A, index 1 => B (később bővíthető)
    Option getCorrectOption();
    void shuffleOptions(Random rnd);
}