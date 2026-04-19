package quiz.ui;

import quiz.model.Option;
import quiz.model.Question;
import quiz.model.QuizSession;
import quiz.service.QuestionBankService;
import quiz.service.QuizEngine;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.List;

public class QuizDialog extends JDialog {
    private final List<Question> picked;
    private int index = 0;
    private int correct = 0;

    private static final int WRAP_WIDTH_PX = 500;     // válaszokhoz
    private static final int Q_WRAP_WIDTH_PX = 640;   // kérdéshez

    private final JLabel qLabel = new JLabel();
    private final JRadioButton aBtn = new JRadioButton();
    private final JRadioButton bBtn = new JRadioButton();
    private final ButtonGroup group = new ButtonGroup();

    private final JButton checkBtn = new JButton("Válasz ellenőrzése");
    private final JButton nextBtn = new JButton("Következő");

    private boolean answered = false;

    private final long startMs;
    private final LocalDateTime startedAt;

    public QuizDialog(Frame owner, QuestionBankService bank, QuizEngine engine, int n) {
        super(owner, "Kikérdező", true);

        this.startedAt = LocalDateTime.now();
        this.startMs = System.currentTimeMillis();

        setSize(700, 420);
        setMinimumSize(new Dimension(620, 360));
        setLocationRelativeTo(owner);

        if (bank.size() == 0) {
            JOptionPane.showMessageDialog(owner,
                    "Nincs kérdés a bankban. Vegyél fel előbb kérdéseket.");
            this.picked = List.of();
            dispose();
            return;
        }

        this.picked = engine.pickQuestions(n);

        // --- Question label setup ---
        qLabel.setVerticalAlignment(SwingConstants.TOP);
        qLabel.setFont(qLabel.getFont().deriveFont(Font.BOLD, 15f));

        // --- Radio buttons setup ---
        group.add(aBtn);
        group.add(bBtn);

        aBtn.setVerticalAlignment(SwingConstants.TOP);
        bBtn.setVerticalAlignment(SwingConstants.TOP);
        aBtn.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        bBtn.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

        
        checkBtn.setEnabled(true);
        nextBtn.setEnabled(false);

        aBtn.addActionListener(e -> {
            if (answered) {
                aBtn.setSelected(false);
            }
        });

        bBtn.addActionListener(e -> {
            if (answered) {
                bBtn.setSelected(false);
            }
        });

        checkBtn.addActionListener(e -> {
            if (!aBtn.isSelected() && !bBtn.isSelected()) {
                JOptionPane.showMessageDialog(this, "Válassz A-t vagy B-t!");
                return;
            }

            Question q = picked.get(index);
            Option chosen = aBtn.isSelected() ? Option.A : Option.B;

            // lock
            lockAnswers();

            answered = true;

            if (chosen == q.getCorrectOption()) {
                correct++;
                markCorrect(chosen);
            } else {
                markWrong(chosen, q.getCorrectOption());
            }

            checkBtn.setEnabled(false);
            nextBtn.setEnabled(true);
        });
        
        nextBtn.addActionListener(e -> {
            if (!aBtn.isSelected() && !bBtn.isSelected()) {
                JOptionPane.showMessageDialog(this, "Válassz A-t vagy B-t!");
                return;
            }

            Question q = picked.get(index);
            Option chosen = aBtn.isSelected() ? Option.A : Option.B;
            if (!answered)
                if (chosen == q.getCorrectOption()) correct++;

            index++;
            if (index >= picked.size()) {
                long dur = System.currentTimeMillis() - startMs;
                QuizSession session = new QuizSession(startedAt, dur, picked.size(), correct);
                engine.saveSession(session);

                JOptionPane.showMessageDialog(this,
                        "Kész!\n" +
                        "Eredmény: " + correct + "/" + picked.size() +
                        String.format(java.util.Locale.US, " (%.2f%%)\n", session.getPercent()) +
                        "Idő: " + (dur / 1000.0) + " s"
                );
                dispose();
                return;
            }

            loadQuestion();
        });

        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        // Top: question (stabil HTML wrap + scroll)
        JPanel qPanel = new JPanel(new BorderLayout());
        qPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        qPanel.add(qLabel, BorderLayout.CENTER);

        JScrollPane qScroll = new JScrollPane(qPanel);
        qScroll.setBorder(BorderFactory.createEmptyBorder());
        qScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        qScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        qScroll.setPreferredSize(new Dimension(0, 140));

        root.add(qScroll, BorderLayout.NORTH);

        // Center: answers
        JPanel answers = new JPanel();
        answers.setLayout(new BoxLayout(answers, BoxLayout.Y_AXIS));
        answers.add(aBtn);
        answers.add(Box.createVerticalStrut(10));
        answers.add(bBtn);

        JScrollPane scroll = new JScrollPane(answers);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        root.add(scroll, BorderLayout.CENTER);

        // Bottom: next button
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(checkBtn);
        bottom.add(nextBtn);
        root.add(bottom, BorderLayout.SOUTH);

        setContentPane(root);

        loadQuestion();
    }

    private void loadQuestion() {
        Question q = picked.get(index);

        qLabel.setText(wrapHtmlQuestion((index + 1) + "/" + picked.size() + " – " + q.getPrompt()));

        aBtn.setText(wrapHtml("A) " + q.getOptions().get(0)));
        bBtn.setText(wrapHtml("B) " + q.getOptions().get(1)));

        group.clearSelection();
        
        answered = false;

        aBtn.setEnabled(true);
        bBtn.setEnabled(true);

        checkBtn.setEnabled(true);
        nextBtn.setEnabled(true);
    }
    
    private void lockAnswers() {
        answered = true;
    }

    private String wrapHtmlQuestion(String text) {
        String esc = text
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");

        return "<html><table width='" + Q_WRAP_WIDTH_PX + "'><tr><td>" + esc + "</td></tr></table></html>";
    }

    private void markCorrect(Option chosen) {
        if (chosen == Option.A) {
            aBtn.setText(wrapHtml("<span style='color:green'>✔</span> A) " + getRawOptionText(aBtn)));
        } else {
            bBtn.setText(wrapHtml("<span style='color:green'>✔</span> B) " + getRawOptionText(bBtn)));
        }
    }

    private void markWrong(Option chosen, Option correctOpt) {
        if (chosen == Option.A) {
            aBtn.setText(wrapHtml("<span style='color:red'>✘</span> A) " + getRawOptionText(aBtn)));
        } else {
            bBtn.setText(wrapHtml("<span style='color:red'>✘</span> B) " + getRawOptionText(bBtn)));
        }

        // jelöljük a helyeset is
        if (correctOpt == Option.A && chosen != Option.A) {
            aBtn.setText(wrapHtml("<span style='color:green'>✔</span> A) " + getRawOptionText(aBtn)));
        } else {
            bBtn.setText(wrapHtml("<span style='color:green'>✔</span> B) " + getRawOptionText(bBtn)));
        }
    }

    private String getRawOptionText(JRadioButton btn) {
        String text = btn.getText();

        // remove HTML + prefixek
        return text.replaceAll("<.*?>", "")
                .replace("✔ ", "")
                .replace("✘ ", "")
                .replace("A) ", "")
                .replace("B) ", "")
                .trim();
    }

    private String wrapHtml(String text) {
        return "<html><body style='width:" + WRAP_WIDTH_PX + "px'>" + text + "</body></html>";
    }

    
    
}