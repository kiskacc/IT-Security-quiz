package quiz.ui;

import quiz.service.QuestionBankService;
import quiz.service.QuizEngine;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private final QuestionBankService bank;
    private final QuizEngine engine;
    private final JLabel status;

    public MainFrame(QuestionBankService bank, QuizEngine engine) {
        super("Mini Quiz");
        this.bank = bank;
        this.engine = engine;

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(520, 260);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));

        JLabel title = new JLabel("Mini Quiz – menü");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));

        status = new JLabel(statusText());

        JPanel center = new JPanel(new GridLayout(0, 1, 10, 10));

        JButton addBtn = new JButton("Új kérdések felvitele");
        addBtn.addActionListener(e -> {
            AddQuestionDialog dlg = new AddQuestionDialog(this, bank);
            dlg.setVisible(true);
            refreshStatus();
        });

        JButton shuffleBtn = new JButton("Kérdések megkeverése");
        shuffleBtn.addActionListener(e -> {
            bank.shuffleAll();
            JOptionPane.showMessageDialog(this, "Kérdések + válaszok megkeverve.");
        });

        JButton quizBtn = new JButton("Új kikérdező indítása");
        quizBtn.addActionListener(e -> {
            String in = JOptionPane.showInputDialog(this, "Hány kérdés legyen?", "10");
            if (in == null) return;
            int n;
            try {
                n = Integer.parseInt(in.trim());
                if (n <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Adj meg egy pozitív egész számot.");
                return;
            }
            QuizDialog dlg = new QuizDialog(this, bank, engine, n);
            dlg.setVisible(true);
        });

        JButton saveExitBtn = new JButton("Mentés és kilépés");
        saveExitBtn.addActionListener(e -> {
            bank.save();
            dispose();
            System.exit(0);
        });

        center.add(addBtn);
        center.add(shuffleBtn);
        center.add(quizBtn);
        center.add(saveExitBtn);

        root.add(title, BorderLayout.NORTH);
        root.add(center, BorderLayout.CENTER);
        root.add(status, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private String statusText() {
        return "Betöltött kérdések száma: " + bank.size();
    }

    private void refreshStatus() {
        status.setText(statusText());
    }
}
