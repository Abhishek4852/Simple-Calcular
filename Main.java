import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Stack;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Main extends JFrame {
    private static void createAndShowGUI() {
        JFrame jFrame = new JFrame("Calculator");
        jFrame.setSize(300, 400);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setLayout(null);
        jFrame.setVisible(true);

        JTextArea t1 = new JTextArea(2, 20);
        t1.setBounds(20, 20, 250, 50);
        t1.setFont(new Font("Arial", Font.BOLD, 24));
        t1.setEditable(false);
        t1.setBackground(Color.WHITE);
        jFrame.add(t1);

        String[] buttonLabels = {
            "7", "8", "9", "/",
            "4", "5", "6", "*",
            "1", "2", "3", "-",
            "0", ".", "=", "+"
        };

        JPanel jp = new JPanel();
        jp.setLayout(new GridLayout(4, 4, 10, 10));
        jp.setBounds(20, 90, 250, 250);

        for (String label : buttonLabels) {
            JButton button = new JButton(label);
            button.setFont(new Font("Arial", Font.BOLD, 24));
            jp.add(button);
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String command = e.getActionCommand();
                    if (command.equals("=")) {
                        try {
                            String s = t1.getText();
                            double result = evaluateString(s);
                            t1.setText(String.valueOf(result));
                        } catch (Exception ex) {
                            t1.setText("Error");
                        }
                    } else {
                        t1.append(command);
                    }
                }
            });
        }

        jFrame.add(jp);

        JButton clearButton = new JButton("C");
        clearButton.setFont(new Font("Arial", Font.BOLD, 24));
        clearButton.setBounds(20, 350, 250, 50);
        clearButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                t1.setText("");
            }
        });
        jFrame.add(clearButton);

        // Add key listener for keyboard input
        t1.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_1:
                    case KeyEvent.VK_2:
                    case KeyEvent.VK_3:
                    case KeyEvent.VK_4:
                    case KeyEvent.VK_5:
                    case KeyEvent.VK_6:
                    case KeyEvent.VK_7:
                    case KeyEvent.VK_8:
                    case KeyEvent.VK_9:
                    case KeyEvent.VK_0:
                        t1.append(String.valueOf(e.getKeyChar()));
                        break;
                    case KeyEvent.VK_PLUS:
                        t1.append("+");
                        break;
                    case KeyEvent.VK_MINUS:
                        t1.append("-");
                        break;
                    case KeyEvent.VK_MULTIPLY:
                        t1.append("*");
                        break;
                    case KeyEvent.VK_DIVIDE:
                        t1.append("/");
                        break;
                    case KeyEvent.VK_PERIOD:
                        t1.append(".");
                        break;
                    case KeyEvent.VK_ENTER:
                    case KeyEvent.VK_EQUALS:
                        try {
                            String s = t1.getText();
                            double result = evaluateString(s);
                            t1.setText(String.valueOf(result));
                        } catch (Exception ex) {
                            t1.setText("Error");
                        }
                        break;
                    case KeyEvent.VK_BACK_SPACE:
                        String currentText = t1.getText();
                        if (currentText.length() > 0) {
                            t1.setText(currentText.substring(0, currentText.length() - 1));
                        }
                        break;
                    case KeyEvent.VK_C:
                        t1.setText("");
                        break;
                }
            }
        });

        t1.setFocusable(true);
        t1.requestFocusInWindow();
    }

    public static double evaluateString(String expression) {
        expression = expression.replaceAll("\\s", "");

        if (expression.isEmpty()) {
            return 0;
        }

        Stack<Double> operandStack = new Stack<>();
        Stack<Character> operatorStack = new Stack<>();
        char lastChar = ' ';

        for (int i = 0; i < expression.length(); i++) {
            char ch = expression.charAt(i);

            if (Character.isDigit(ch) || ch == '.') {
                StringBuilder numBuilder = new StringBuilder();
                while (i < expression.length() && (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) {
                    numBuilder.append(expression.charAt(i));
                    i++;
                }
                i--;
                operandStack.push(Double.parseDouble(numBuilder.toString()));
                lastChar = ch;
            } else if (ch == '(') {
                operatorStack.push(ch);
                lastChar = ch;
            } else if (ch == ')') {
                while (!operatorStack.isEmpty() && operatorStack.peek() != '(') {
                    double result = applyOperator(operatorStack.pop(), operandStack.pop(), operandStack.pop());
                    operandStack.push(result);
                }
                operatorStack.pop();
                lastChar = ch;
            } else if (isOperator(ch)) {
                if (ch == '-' && (lastChar == ' ' || isOperator(lastChar) || lastChar == '(')) {
                    StringBuilder numBuilder = new StringBuilder();
                    numBuilder.append(ch);
                    i++;
                    while (i < expression.length() && (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) {
                        numBuilder.append(expression.charAt(i));
                        i++;
                    }
                    i--;
                    operandStack.push(Double.parseDouble(numBuilder.toString()));
                } else {
                    while (!operatorStack.isEmpty() && precedence(ch) <= precedence(operatorStack.peek())) {
                        double result = applyOperator(operatorStack.pop(), operandStack.pop(), operandStack.pop());
                        operandStack.push(result);
                    }
                    operatorStack.push(ch);
                }
                lastChar = ch;
            }
        }

        while (!operatorStack.isEmpty()) {
            double result = applyOperator(operatorStack.pop(), operandStack.pop(), operandStack.pop());
            operandStack.push(result);
        }

        return operandStack.pop();
    }

    private static boolean isOperator(char ch) {
        return ch == '+' || ch == '-' || ch == '*' || ch == '/';
    }

    private static int precedence(char op) {
        if (op == '+' || op == '-') {
            return 1;
        } else if (op == '*' || op == '/') {
            return 2;
        }
        return 0;
    }

    private static double applyOperator(char operator, double b, double a) {
        switch (operator) {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
            case '/':
                if (b == 0) throw new ArithmeticException("Division by zero");
                return a / b;
        }
        return 0;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}
