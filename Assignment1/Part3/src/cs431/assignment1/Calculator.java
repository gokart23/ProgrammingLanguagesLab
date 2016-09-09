package cs431.assignment1;

import java.awt.Color;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Scanner;
import java.util.Stack;
import java.util.TimerTask;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 *
 * @author Karthik Duddu
 */
public class Calculator extends javax.swing.JFrame implements KeyListener {

    private static final int CHANGE_TIMEOUT = 400;
    private static final int NUM_BUTTONS = 12;
    private static final int FUNC_BUTTONS = 6;
    private static final int NUM_HIGHLIGHT = 0, FUNC_HIGHLIGHT = 1;

    private static Color bgColor;
    private String internalRepresentation = "";

    private JButton[] numButtons;
    private JButton[] funcButtons;

    private ButtonHighlighter numHighlighter, funcHighlighter;
    private volatile Integer currChange = 0, funcChange = 0;

    /**
     * Creates new form Calculator
     */
    public Calculator() {
        initComponents();
        // <editor-fold defaultstate="collapsed" desc="Reference array creation">                          
        numButtons = new JButton[NUM_BUTTONS];
        funcButtons = new JButton[FUNC_BUTTONS];
        numButtons[10] = button0;
        numButtons[0] = button1;
        numButtons[1] = button2;
        numButtons[2] = button3;
        numButtons[3] = button4;
        numButtons[4] = button5;
        numButtons[5] = button6;
        numButtons[6] = button7;
        numButtons[7] = button8;
        numButtons[8] = button9;
        numButtons[11] = buttonRPar;
        numButtons[9] = buttonLPar;

        funcButtons[3] = buttonDivide;
        funcButtons[1] = buttonMinus;
        funcButtons[2] = buttonMultiply;
        funcButtons[0] = buttonPlus;
        funcButtons[5] = buttonRes;
        funcButtons[4] = buttonClear;
        // </editor-fold>

        bgColor = button0.getBackground();
        numHighlighter = new ButtonHighlighter(NUM_HIGHLIGHT);
        funcHighlighter = new ButtonHighlighter(FUNC_HIGHLIGHT);
        displayArea.addKeyListener(this);

        (new Thread(numHighlighter)).start();
        (new Thread(funcHighlighter)).start();
    }

    private void clearCalc() {
        internalRepresentation = "";
        displayArea.setText("");
    }

    private void getResult() {
        try {
            String res = postfixEvaluate(toPostfix(internalRepresentation)).toString();
            internalRepresentation = res;
            displayArea.setText(res);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Invalid expression detected! Clearing calculator");
            clearCalc();
        }

    }

    private static String toPostfix(String infix) //converts an infix expression to postfix
    {
        Stack<String> operators = new Stack<String>();
        Scanner tokens = new Scanner(infix);
//        tokens.useDelimiter("\\s*");
        String postfix = "", symbol = "";

        while (tokens.hasNext()) {
            if (tokens.hasNextInt()) {
                postfix += " " + tokens.nextInt() + " ";
                System.out.println("Added int:" + postfix);
            } else {
                symbol = tokens.next().trim();
                System.out.println("Symbol is: " + symbol);
                switch (symbol) {
                    case "(":
                        operators.push(symbol);
                        System.out.println("Inside so far:" + postfix);
                        break;
                    case ")":
                        System.out.println("Inside )");
                        while (!"(".equals(operators.peek())) {
                            postfix = postfix + " " + operators.pop() + " ";
                        }
                        operators.pop();
                        System.out.println("Postfix so far:" + postfix);
                        break;
                    default:
                        while (!operators.isEmpty() && !("(".equals(operators.peek())) && prec(symbol) <= prec(operators.peek())) {
                            System.out.println(prec(symbol) + " " + prec(operators.peek()));
                            System.out.println("Inside so far:" + operators.peek() + " " + ("(".equals(operators.peek())) + " " + (prec(symbol) <= prec(operators.peek())));
                            postfix = postfix + " " + operators.pop() + " ";
                        }
                        operators.push(symbol);
                        System.out.println("Postfix so far:" + postfix);
                        break;
                }
            }
        }
        while (!operators.isEmpty()) {
            postfix = postfix + " " + operators.pop() + " ";
        }
        postfix = postfix.trim();
        System.out.println("Postfix expression: " + postfix);
        return postfix;
    }

    private static int prec(String x) {
        if ("+".equals(x) || "-".equals(x)) {
            return 1;
        }
        if ("*".equals(x) || "/".equals(x)) {
            return 2;
        }
        return 0;
    }

    private Integer postfixEvaluate(String exp) {
        Stack<Integer> operands = new Stack<Integer>();
        Scanner tokens = new Scanner(exp);
        while (tokens.hasNext()) {
            if (tokens.hasNextInt()) {
                operands.push(tokens.nextInt());
            } else {
                int operand_2 = operands.pop();
                int operand_1 = operands.pop();
                String op = tokens.next();

                if (op.equals("+")) {
                    operands.push(operand_1 + operand_2);
                } else if (op.equals("-")) {
                    operands.push(operand_1 - operand_2);
                } else if (op.equals("*")) {
                    operands.push(operand_1 * operand_2);
                } else {
                    operands.push(operand_1 / operand_2);
                }
            }
        }
        return operands.pop();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        System.out.println("Pressed " + e.getKeyChar());
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            if (numButtons[currChange] == buttonLPar || numButtons[currChange] == buttonRPar) {
                internalRepresentation += " " + numButtons[currChange].getText() + " ";
            } else {
                internalRepresentation += numButtons[currChange].getText();
            }
            displayArea.setText(displayArea.getText() + numButtons[currChange].getText());
        } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (funcButtons[funcChange] == buttonRes) {
                getResult();
            } else if (funcButtons[funcChange] == buttonClear) {
                clearCalc();
            } else {
                internalRepresentation += " " + funcButtons[funcChange].getText() + " ";
                displayArea.setText(displayArea.getText() + funcButtons[funcChange].getText());
            }
        }
        System.out.println("Internal:" + internalRepresentation);
    }

    @Override
    public void keyTyped(KeyEvent e
    ) {
    }

    @Override
    public void keyReleased(KeyEvent e
    ) {
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jFrame1 = new javax.swing.JFrame();
        jPanel1 = new javax.swing.JPanel();
        displayArea = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        button1 = new javax.swing.JButton();
        button2 = new javax.swing.JButton();
        button3 = new javax.swing.JButton();
        button4 = new javax.swing.JButton();
        button5 = new javax.swing.JButton();
        button6 = new javax.swing.JButton();
        button7 = new javax.swing.JButton();
        button8 = new javax.swing.JButton();
        button9 = new javax.swing.JButton();
        buttonLPar = new javax.swing.JButton();
        button0 = new javax.swing.JButton();
        buttonRPar = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        buttonPlus = new javax.swing.JButton();
        buttonMinus = new javax.swing.JButton();
        buttonDivide = new javax.swing.JButton();
        buttonMultiply = new javax.swing.JButton();
        buttonRes = new javax.swing.JButton();
        buttonClear = new javax.swing.JButton();

        javax.swing.GroupLayout jFrame1Layout = new javax.swing.GroupLayout(jFrame1.getContentPane());
        jFrame1.getContentPane().setLayout(jFrame1Layout);
        jFrame1Layout.setHorizontalGroup(
            jFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        jFrame1Layout.setVerticalGroup(
            jFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setToolTipText("displayArea");

        displayArea.setEditable(false);
        displayArea.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(displayArea)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(displayArea, javax.swing.GroupLayout.DEFAULT_SIZE, 56, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setFocusable(false);

        button1.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        button1.setText("1");
        button1.setFocusable(false);

        button2.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        button2.setText("2");
        button2.setFocusable(false);

        button3.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        button3.setText("3");
        button3.setFocusable(false);

        button4.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        button4.setText("4");
        button4.setFocusable(false);

        button5.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        button5.setText("5");
        button5.setFocusable(false);

        button6.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        button6.setText("6");
        button6.setFocusable(false);

        button7.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        button7.setText("7");
        button7.setFocusable(false);

        button8.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        button8.setText("8");
        button8.setToolTipText("");
        button8.setFocusable(false);

        button9.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        button9.setText("9");
        button9.setFocusable(false);

        buttonLPar.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        buttonLPar.setText("(");
        buttonLPar.setFocusable(false);

        button0.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        button0.setText("0");
        button0.setFocusable(false);

        buttonRPar.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        buttonRPar.setText(")");
        buttonRPar.setFocusable(false);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(button7, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(button8, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(button9, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(button1, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(button2, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(button3, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(buttonLPar, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(button0, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(buttonRPar, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(button4, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(button5, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(button6, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button1, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button2, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button3, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button4, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button5, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button6, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button7, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button8, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button9, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 11, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonLPar, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button0, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonRPar, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel3.setFocusable(false);

        buttonPlus.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        buttonPlus.setText("+");
        buttonPlus.setFocusable(false);

        buttonMinus.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        buttonMinus.setText("-");
        buttonMinus.setFocusable(false);

        buttonDivide.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        buttonDivide.setText("/");
        buttonDivide.setFocusable(false);

        buttonMultiply.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        buttonMultiply.setText("*");
        buttonMultiply.setFocusable(false);

        buttonRes.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        buttonRes.setText("RES");
        buttonRes.setFocusable(false);

        buttonClear.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        buttonClear.setText("CLEAR");
        buttonClear.setFocusable(false);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(buttonPlus, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(buttonMinus, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(buttonClear, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(buttonRes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(buttonMultiply, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(buttonDivide, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonDivide, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonPlus, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonMinus, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonMultiply, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonRes, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonClear, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Calculator.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Calculator.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Calculator.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Calculator.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                Calculator newCalculator = new Calculator();
                newCalculator.setVisible(true);
            }
        });
    }

    private class ButtonHighlighter implements Runnable {

        private final int MODE;
        private java.util.Timer changeTimer;

        private final Runnable changeColourNum = new Runnable() {
            @Override
            public void run() {
//                        System.out.println ("Responding to number colour change in " + Thread.currentThread().getName());
//                System.out.println(KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner());
                numButtons[currChange].setBackground(bgColor);
                currChange = (++currChange) % NUM_BUTTONS;
                numButtons[currChange].setBackground(Color.red);
            }
        };

        private final Runnable changeColourFunc = new Runnable() {
            @Override
            public void run() {
//                        System.out.println ("Responding to function colour change in " + Thread.currentThread().getName());
                funcButtons[funcChange].setBackground(bgColor);
                funcChange = (++funcChange) % FUNC_BUTTONS;
                funcButtons[funcChange].setBackground(Color.blue);
            }
        };

        @Override
        public void run() {
            changeTimer = new java.util.Timer();

            if (this.MODE == 0) {
                changeTimer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            SwingUtilities.invokeAndWait(changeColourNum);
//                        System.out.println ("Invoked number colour change from " + Thread.currentThread().getName());
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }, 100, CHANGE_TIMEOUT);
            } else {
                changeTimer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            SwingUtilities.invokeAndWait(changeColourFunc);
//                        System.out.println ("Invoked function colour change from " + Thread.currentThread().getName());
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }, 100, CHANGE_TIMEOUT);
            }
        }

        public ButtonHighlighter(int MODE) {
            this.MODE = MODE;
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Variables declaration - do not modify ">
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton button0;
    private javax.swing.JButton button1;
    private javax.swing.JButton button2;
    private javax.swing.JButton button3;
    private javax.swing.JButton button4;
    private javax.swing.JButton button5;
    private javax.swing.JButton button6;
    private javax.swing.JButton button7;
    private javax.swing.JButton button8;
    private javax.swing.JButton button9;
    private javax.swing.JButton buttonClear;
    private javax.swing.JButton buttonDivide;
    private javax.swing.JButton buttonLPar;
    private javax.swing.JButton buttonMinus;
    private javax.swing.JButton buttonMultiply;
    private javax.swing.JButton buttonPlus;
    private javax.swing.JButton buttonRPar;
    private javax.swing.JButton buttonRes;
    private javax.swing.JTextField displayArea;
    private javax.swing.JFrame jFrame1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    // End of variables declaration//GEN-END:variables
    // </editor-fold>
}
