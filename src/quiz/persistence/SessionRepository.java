package quiz.persistence;

import quiz.model.QuizSession;
import java.util.List;

public interface SessionRepository {
    List<QuizSession> loadAll();
    void append(QuizSession session);
}