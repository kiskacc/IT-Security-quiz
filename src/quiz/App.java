
package quiz;

import quiz.persistence.*;
import quiz.service.*;
import quiz.ui.MainFrame;

import javax.swing.*;
import java.nio.file.Path;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Path dataDir = Path.of("data");
            Path questionsFile = dataDir.resolve("questions.txt");
            Path sessionsFile  = dataDir.resolve("sessions.txt");

            QuestionRepository qRepo = new TxtQuestionRepository(questionsFile);
            SessionRepository sRepo = new TxtSessionRepository(sessionsFile);

            QuestionBankService bank = new QuestionBankService(qRepo);
            bank.load(); // program indulásakor beolvasás

            QuizEngine engine = new QuizEngine(bank, sRepo);

            MainFrame frame = new MainFrame(bank, engine);
            frame.setVisible(true);

            // biztos ami biztos: ablak bezárásakor mentés
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    bank.save();           // kérdések mentése
                    engine.flushSessions(); // ha bármi bufferelés lenne (most nincs, de bővíthető)
                } catch (Exception ignored) {}
            }));
        });
    }
}
