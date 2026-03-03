package quiz.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class QuizSession {
    private final LocalDateTime startedAt;
    private final long durationMs;
    private final int total;
    private final int correct;

    public QuizSession(LocalDateTime startedAt, long durationMs, int total, int correct) {
        this.startedAt = Objects.requireNonNull(startedAt);
        this.durationMs = durationMs;
        this.total = total;
        this.correct = correct;
    }

    public LocalDateTime getStartedAt() { return startedAt; }
    public long getDurationMs() { return durationMs; }
    public int getTotal() { return total; }
    public int getCorrect() { return correct; }

    public double getPercent() {
        if (total <= 0) return 0.0;
        return (correct * 100.0) / total;
    }
}