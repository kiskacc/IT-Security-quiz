package quiz.persistence;

import quiz.model.Question;
import java.util.List;

public interface QuestionRepository {
    List<Question> loadAll();
    void saveAll(List<Question> questions);
}