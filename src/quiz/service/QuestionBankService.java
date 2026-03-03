package quiz.service;

import quiz.model.Question;
import quiz.persistence.QuestionRepository;

import java.util.*;

public class QuestionBankService {
    private final QuestionRepository repo;
    private final List<Question> questions = new ArrayList<>();
    private final Random rnd = new Random();

    public QuestionBankService(QuestionRepository repo) {
        this.repo = repo;
    }

    public void load() {
        questions.clear();
        questions.addAll(repo.loadAll());
    }

    public void save() {
        repo.saveAll(questions);
    }

    public List<Question> getAll() {
        return Collections.unmodifiableList(questions);
    }

    public int size() { return questions.size(); }

    public void addAndSave(Question q) {
        questions.add(q);
        repo.saveAll(questions);
    }

    public void shuffleAll() {
        // kérdések sorrend
        Collections.shuffle(questions, rnd);
        // opciók sorrend
        for (Question q : questions) q.shuffleOptions(rnd);
    }
}