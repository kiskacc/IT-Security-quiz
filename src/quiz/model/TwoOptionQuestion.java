package quiz.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class TwoOptionQuestion implements Question {
    private final String prompt;
    private String optionA;
    private String optionB;
    private Option correct;

    public TwoOptionQuestion(String prompt, String optionA, String optionB, Option correct) {
        this.prompt = Objects.requireNonNull(prompt).trim();
        this.optionA = Objects.requireNonNull(optionA).trim();
        this.optionB = Objects.requireNonNull(optionB).trim();
        this.correct = Objects.requireNonNull(correct);
        if (this.prompt.isEmpty()) throw new IllegalArgumentException("Question cannot be empty");
        if (this.optionA.isEmpty() || this.optionB.isEmpty()) throw new IllegalArgumentException("Options cannot be empty");
    }

    @Override public String getPrompt() { return prompt; }

    @Override
    public List<String> getOptions() {
        List<String> opts = new ArrayList<>(2);
        opts.add(optionA);
        opts.add(optionB);
        return opts;
    }

    @Override public Option getCorrectOption() { return correct; }

    @Override
    public void shuffleOptions(Random rnd) {
        // 50% swap A/B
        if (rnd.nextBoolean()) {
            String tmp = optionA;
            optionA = optionB;
            optionB = tmp;
            correct = correct.flipped();
        }
    }

    public String getOptionA() { return optionA; }
    public String getOptionB() { return optionB; }
}