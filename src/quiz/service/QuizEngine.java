
package quiz.service;

import quiz.model.Question;
import quiz.model.QuizSession;
import quiz.persistence.SessionRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class QuizEngine {
    private final QuestionBankService bank;
    private final SessionRepository sessions;
    private final Random rnd = new Random();

    public QuizEngine(QuestionBankService bank, SessionRepository sessions) {
        this.bank = bank;
        this.sessions = sessions;
        // beolvasható, ha kell később listázni:
        sessions.loadAll();
    }

    public List<Question> pickQuestions(int count) {
        List<Question> all = new ArrayList<>(bank.getAll()); // másolat, hogy shuffle-elhessük
        List<Question> picked = new ArrayList<>(Math.max(0, count));
        if (all.isEmpty() || count <= 0) return picked;

        int size = all.size();

        if (count <= size) {
            java.util.Collections.shuffle(all, rnd);
            picked.addAll(all.subList(0, count));
            return picked;
        }

        while (picked.size() < count) {
            java.util.Collections.shuffle(all, rnd);
            for (Question q : all) {
                picked.add(q);
                if (picked.size() == count) break;
            }
        }
        return picked;
    }

    public void saveSession(QuizSession session) {
        sessions.append(session);
    }

    public void flushSessions() {
        // most nincs buffer
    }
}
