package quiz.persistence;

import quiz.model.QuizSession;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TxtSessionRepository implements SessionRepository {
    private final Path file;

    public TxtSessionRepository(Path file) {
        this.file = file;
    }

    @Override
    public List<QuizSession> loadAll() {
        try {
            if (!Files.exists(file)) return new ArrayList<>();
            List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
            List<QuizSession> sessions = new ArrayList<>();
            for (String raw : lines) {
                String line = raw.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] parts = line.split("\\|", -1);
                if (parts.length < 4) continue;

                LocalDateTime ts = LocalDateTime.parse(parts[0].trim());
                long dur = Long.parseLong(parts[1].trim());
                int total = Integer.parseInt(parts[2].trim());
                int correct = Integer.parseInt(parts[3].trim());
                sessions.add(new QuizSession(ts, dur, total, correct));
            }
            return sessions;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load sessions from " + file, e);
        }
    }

    @Override
    public void append(QuizSession session) {
        try {
            Files.createDirectories(file.getParent());
            String line = session.getStartedAt() + "|" +
                    session.getDurationMs() + "|" +
                    session.getTotal() + "|" +
                    session.getCorrect() + "|" +
                    String.format(java.util.Locale.US, "%.2f", session.getPercent());

            boolean exists = Files.exists(file);
            List<String> out = new ArrayList<>();
            if (!exists) out.add("# timestamp|durationMs|total|correct|percent");
            out.add(line);

            Files.write(file, out, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException("Failed to append session to " + file, e);
        }
    }
}
