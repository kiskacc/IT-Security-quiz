package quiz.persistence;

import quiz.model.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class TxtQuestionRepository implements QuestionRepository {
    private final Path file;

    public TxtQuestionRepository(Path file) {
        this.file = file;
    }

    @Override
    public List<Question> loadAll() {
        try {
            if (!Files.exists(file)) return new ArrayList<>();
            List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
            List<Question> result = new ArrayList<>();
            for (String raw : lines) {
                String line = raw.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                // question|A|B|a/b
                String[] parts = line.split("\\|", -1);
                if (parts.length != 4) continue; // robusztus: rossz sor átugrás
                String q = parts[0].trim();
                String a = parts[1].trim();
                String b = parts[2].trim();
                Option correct = Option.fromCode(parts[3].trim());
                result.add(new TwoOptionQuestion(q, a, b, correct));
            }
            return result;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load questions from " + file, e);
        }
    }

    @Override
    public void saveAll(List<Question> questions) {
        try {
            Files.createDirectories(file.getParent());
            List<String> lines = new ArrayList<>();
            lines.add("# format: question|A option|B option|a/b");
            for (Question q : questions) {
                if (q instanceof TwoOptionQuestion tq) {
                    lines.add(escape(tq.getPrompt()) + "|" +
                              escape(tq.getOptionA()) + "|" +
                              escape(tq.getOptionB()) + "|" +
                              tq.getCorrectOption().toCode());
                } else {
                    // ha később lesz más típus, itt lehet type prefixet adni
                }
            }
            Files.write(file, lines, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save questions to " + file, e);
        }
    }

    private String escape(String s) {
        // Minimál: ne tartalmazzon '|'-t, vagy cseréljük
        return s.replace("|", "/");
    }
}
