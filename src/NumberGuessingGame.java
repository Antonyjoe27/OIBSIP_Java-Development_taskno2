import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

/**
 * TASK 2 — Number Guessing Game (Swing GUI)
 * Features: difficulty levels, attempt counter, score tracking, play again
 */
public class NumberGuessingGame extends JFrame {

    // ── Game state ──────────────────────────────────────────────────────────
    private int secretNumber;
    private int maxAttempts;
    private int attemptsLeft;
    private int rangeMax;
    private int roundNumber  = 0;
    private int totalRounds  = 0;
    private int totalWins    = 0;
    private boolean gameOver = false;

    // ── UI components ────────────────────────────────────────────────────────
    private JLabel  lblTitle, lblHint, lblAttempts, lblScore, lblRange;
    private JTextField tfGuess;
    private JButton btnGuess, btnNewGame;
    private JComboBox<String> cbDifficulty;
    private JTextArea taHistory;

    public NumberGuessingGame() {
        setTitle("Number Guessing Game");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(480, 560);
        setLocationRelativeTo(null);
        setResizable(false);
        buildUI();
        startNewGame();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        root.setBackground(new Color(30, 30, 46));

        // ── Top panel ────────────────────────────────────────────────────────
        JPanel top = new JPanel(new GridLayout(5, 1, 4, 4));
        top.setOpaque(false);

        lblTitle = styled(new JLabel("🎯 Guess the Number!", SwingConstants.CENTER), 22, Font.BOLD, Color.WHITE);
        lblRange  = styled(new JLabel("", SwingConstants.CENTER), 13, Font.PLAIN, new Color(180, 180, 200));
        lblHint   = styled(new JLabel("Enter your guess below", SwingConstants.CENTER), 15, Font.ITALIC, new Color(137, 220, 235));
        lblAttempts = styled(new JLabel("", SwingConstants.CENTER), 13, Font.PLAIN, new Color(250, 179, 135));
        lblScore  = styled(new JLabel("", SwingConstants.CENTER), 13, Font.PLAIN, new Color(166, 227, 161));

        top.add(lblTitle);
        top.add(lblRange);
        top.add(lblHint);
        top.add(lblAttempts);
        top.add(lblScore);
        root.add(top, BorderLayout.NORTH);

        // ── Centre input panel ───────────────────────────────────────────────
        JPanel centre = new JPanel(new GridBagLayout());
        centre.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);

        // Difficulty selector
        c.gridx = 0; c.gridy = 0; c.gridwidth = 1;
        centre.add(styled(new JLabel("Difficulty:"), 13, Font.BOLD, Color.WHITE), c);
        cbDifficulty = new JComboBox<>(new String[]{"Easy (1–50, 10 tries)", "Medium (1–100, 7 tries)", "Hard (1–200, 5 tries)"});
        cbDifficulty.setSelectedIndex(1);
        cbDifficulty.setFont(new Font("SansSerif", Font.PLAIN, 13));
        c.gridx = 1; c.gridwidth = 2;
        centre.add(cbDifficulty, c);

        // Guess input
        c.gridx = 0; c.gridy = 1; c.gridwidth = 1;
        centre.add(styled(new JLabel("Your Guess:"), 13, Font.BOLD, Color.WHITE), c);
        tfGuess = new JTextField(8);
        tfGuess.setFont(new Font("Monospaced", Font.BOLD, 18));
        tfGuess.setHorizontalAlignment(JTextField.CENTER);
        c.gridx = 1;
        centre.add(tfGuess, c);

        btnGuess = new JButton("Guess!");
        styleBtn(btnGuess, new Color(137, 180, 250));
        c.gridx = 2;
        centre.add(btnGuess, c);

        // New game button
        btnNewGame = new JButton("New Game");
        styleBtn(btnNewGame, new Color(166, 227, 161));
        c.gridx = 1; c.gridy = 2; c.gridwidth = 2;
        centre.add(btnNewGame, c);

        root.add(centre, BorderLayout.CENTER);

        // ── History area ─────────────────────────────────────────────────────
        taHistory = new JTextArea(7, 30);
        taHistory.setEditable(false);
        taHistory.setFont(new Font("Monospaced", Font.PLAIN, 12));
        taHistory.setBackground(new Color(17, 17, 27));
        taHistory.setForeground(new Color(205, 214, 244));
        taHistory.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
        JScrollPane scroll = new JScrollPane(taHistory);
        scroll.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(88, 91, 112)),
            "Round History", 0, 0,
            new Font("SansSerif", Font.BOLD, 12), new Color(180, 180, 200)));
        root.add(scroll, BorderLayout.SOUTH);

        // ── Listeners ────────────────────────────────────────────────────────
        btnGuess.addActionListener(e -> handleGuess());
        tfGuess.addActionListener(e -> handleGuess());
        btnNewGame.addActionListener(e -> startNewGame());

        add(root);
    }

    private void startNewGame() {
        int idx = cbDifficulty.getSelectedIndex();
        switch (idx) {
            case 0 -> { rangeMax = 50;  maxAttempts = 10; }
            case 1 -> { rangeMax = 100; maxAttempts = 7;  }
            case 2 -> { rangeMax = 200; maxAttempts = 5;  }
        }
        secretNumber = new Random().nextInt(rangeMax) + 1;
        attemptsLeft = maxAttempts;
        gameOver     = false;
        roundNumber++;

        tfGuess.setText("");
        tfGuess.setEditable(true);
        btnGuess.setEnabled(true);
        cbDifficulty.setEnabled(true);

        lblRange.setText("Guess a number between 1 and " + rangeMax);
        lblHint.setText("Enter your guess below");
        lblHint.setForeground(new Color(137, 220, 235));
        lblAttempts.setText("Attempts remaining: " + attemptsLeft);
        updateScore();
        appendHistory("── Round " + roundNumber + " started (1–" + rangeMax + ", " + maxAttempts + " tries) ──");
    }

    private void handleGuess() {
        if (gameOver) { startNewGame(); return; }

        String input = tfGuess.getText().trim();
        int guess;
        try {
            guess = Integer.parseInt(input);
        } catch (NumberFormatException ex) {
            lblHint.setText("⚠ Please enter a valid number!");
            lblHint.setForeground(new Color(243, 139, 168));
            return;
        }

        if (guess < 1 || guess > rangeMax) {
            lblHint.setText("⚠ Number must be between 1 and " + rangeMax);
            lblHint.setForeground(new Color(243, 139, 168));
            return;
        }

        attemptsLeft--;
        totalRounds++;

        if (guess == secretNumber) {
            lblHint.setText("🎉 Correct! You got it in " + (maxAttempts - attemptsLeft) + " attempt(s)!");
            lblHint.setForeground(new Color(166, 227, 161));
            appendHistory("Round " + roundNumber + " — Correct! (" + (maxAttempts - attemptsLeft) + " attempts used)");
            totalWins++;
            gameOver = true;
            endRound();
        } else if (attemptsLeft == 0) {
            lblHint.setText("💀 Out of attempts! The number was " + secretNumber);
            lblHint.setForeground(new Color(243, 139, 168));
            appendHistory("Round " + roundNumber + " — Lost! Answer was " + secretNumber);
            gameOver = true;
            endRound();
        } else if (guess < secretNumber) {
            lblHint.setText("📈 Too Low! Try higher.");
            lblHint.setForeground(new Color(250, 179, 135));
            appendHistory("  Guess: " + guess + " → Too Low");
        } else {
            lblHint.setText("📉 Too High! Try lower.");
            lblHint.setForeground(new Color(250, 179, 135));
            appendHistory("  Guess: " + guess + " → Too High");
        }

        lblAttempts.setText("Attempts remaining: " + attemptsLeft);
        updateScore();
        tfGuess.setText("");
        tfGuess.requestFocus();
    }

    private void endRound() {
        tfGuess.setEditable(false);
        btnGuess.setEnabled(false);
        cbDifficulty.setEnabled(true);
        updateScore();

        int choice = JOptionPane.showConfirmDialog(this,
            "Play again?", "Round Over", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) startNewGame();
    }

    private void updateScore() {
        lblScore.setText("Score: " + totalWins + " win(s) in " + roundNumber + " round(s)");
    }

    private void appendHistory(String line) {
        taHistory.append(line + "\n");
        taHistory.setCaretPosition(taHistory.getDocument().getLength());
    }

    // ── Helpers ──────────────────────────────────────────────────────────────
    private <T extends JComponent> T styled(T c, int size, int style, Color fg) {
        c.setFont(new Font("SansSerif", style, size));
        c.setForeground(fg);
        return c;
    }
    private void styleBtn(JButton b, Color bg) {
        b.setBackground(bg);
        b.setForeground(new Color(30, 30, 46));
        b.setFont(new Font("SansSerif", Font.BOLD, 13));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new NumberGuessingGame().setVisible(true));
    }
}
