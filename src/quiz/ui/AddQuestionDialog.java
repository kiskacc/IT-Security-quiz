package quiz.ui;

import quiz.model.Option;
import quiz.model.TwoOptionQuestion;
import quiz.service.QuestionBankService;

import javax.swing.*;
import javax.swing.border.Border;

import java.awt.*;

public class AddQuestionDialog extends JDialog {

    public AddQuestionDialog(Frame owner, QuestionBankService bank) {
        super(owner, "Új kérdés felvitele", true);

        setSize(640, 480);
        setMinimumSize(new Dimension(600, 440));
        setLocationRelativeTo(owner);

        // --- Multi-line inputs ---
        JTextArea qField = new JTextArea(4, 40);
        JTextArea aField = new JTextArea(3, 40);
        JTextArea bField = new JTextArea(3, 40);

        configureTextArea(qField);
        configureTextArea(aField);
        configureTextArea(bField);

        JScrollPane qScroll = new JScrollPane(qField);
        JScrollPane aScroll = new JScrollPane(aField);
        JScrollPane bScroll = new JScrollPane(bField);


        // --- Correct option selection (ALULRA kerül) ---
        JRadioButton aCorrect = new JRadioButton("A a helyes");
        JRadioButton bCorrect = new JRadioButton("B a helyes");
        ButtonGroup group = new ButtonGroup();
        group.add(aCorrect);
        group.add(bCorrect);
        aCorrect.setSelected(true);

        JPanel correctPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        correctPanel.add(new JLabel("Helyes válasz:"));
        correctPanel.add(aCorrect);
        correctPanel.add(bCorrect);
        correctPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // --- Form layout ---
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        int INDENT = 0;
        form.add(leftLabel("Kérdés:"));
        form.add(indented(qScroll, INDENT));
        form.add(Box.createVerticalStrut(12));

        form.add(leftLabel("A válasz:"));
        form.add(indented(aScroll, INDENT));
        form.add(Box.createVerticalStrut(12));

        form.add(leftLabel("B válasz:"));
        form.add(indented(bScroll, INDENT));
        form.add(Box.createVerticalStrut(12));

        form.add(correctPanel);

        // --- Buttons ---
        JButton addBtn = new JButton("Hozzáadás");
        addBtn.addActionListener(e -> {
            try {
                String question = qField.getText().trim();
                String a = aField.getText().trim();
                String b = bField.getText().trim();

                if (question.isEmpty() || a.isEmpty() || b.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "A kérdés és mindkét válasz mező kitöltése kötelező.",
                            "Hiányzó adat",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                Option correct = aCorrect.isSelected() ? Option.A : Option.B;
                bank.addAndSave(new TwoOptionQuestion(question, a, b, correct));

                JOptionPane.showMessageDialog(this, "Kérdés hozzáadva.");

                qField.setText("");
                aField.setText("");
                bField.setText("");
                aCorrect.setSelected(true);
                qField.requestFocus();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Hiba: " + ex.getMessage(),
                        "Hiba",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton closeBtn = new JButton("Vissza a menübe");
        closeBtn.addActionListener(e -> dispose());

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(addBtn);
        bottom.add(closeBtn);

        // --- Root ---
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        root.add(form, BorderLayout.CENTER);
        root.add(bottom, BorderLayout.SOUTH);

        setContentPane(root);
    }

    // --- Helpers ---

    private static void configureTextArea(JTextArea ta) {
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        ta.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    }

    private static JLabel leftLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private static JComponent indented(Component c, int leftPx) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(0, leftPx, 0, 0));
        p.add(c, BorderLayout.CENTER);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        return p;
    }

}