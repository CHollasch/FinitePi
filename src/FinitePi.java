import javax.swing.*;
import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

/**
 * Created by Connor on 3/10/16.
 */
public class FinitePi {

      private static int DECIMAL_PLACES = 50_000;
      private static final BigDecimal FOUR = new BigDecimal("4");

      private static long time;

      private static BigDecimal atan239;
      private static BigDecimal atan1023;
      private static BigDecimal atan5832;
      private static BigDecimal atan110443;
      private static BigDecimal atan4841182;
      private static BigDecimal atan6826318;

      private static JProgressBar progressBar;
      private static TextAreaOutput output;

      public static void main(String[] args)
              throws FileNotFoundException {
            JFrame frame = new JFrame("Pi Finder");
            frame.setSize(800, 700);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());
            frame.setFont(new Font("Serif", Font.PLAIN, 24));

            JTextField numDecimal = new JTextField("50000");
            numDecimal.setColumns(9);
            numDecimal.setFont(frame.getFont());

            JButton find = new JButton("Find Pi");
            find.setFont(frame.getFont());

            JTextArea pi = new JTextArea("3.14159");
            pi.setFont(frame.getFont().deriveFont(14f));
            pi.setEditable(false);

            find.addActionListener(actionEvent -> {
                  try {
                        FinitePi.DECIMAL_PLACES = Integer.parseInt(numDecimal.getText());
                  } catch (NumberFormatException e) {
                        return;
                  }

                  progressBar.setValue(0);
                  output.clear();

                  atan239 = null;
                  atan1023 = null;
                  atan5832 = null;
                  atan110443 = null;
                  atan4841182 = null;
                  atan6826318 = null;

                  time = System.currentTimeMillis();
                  System.out.println("Starting arc tangent workers.");

                  System.out.println("  >> Atan(239) starting <<");
                  ATanWorker atan239 = new ATanWorker(239, DECIMAL_PLACES, bigDecimal -> {
                        System.out.println("  Found Atan(239)");
                        FinitePi.atan239 = bigDecimal;
                        check(pi);
                  });

                  System.out.println("  >> Atan(1023) starting <<");
                  ATanWorker atan1023 = new ATanWorker(1023, DECIMAL_PLACES, bigDecimal -> {
                        System.out.println("  Found Atan(1023)");
                        FinitePi.atan1023 = bigDecimal;
                        check(pi);
                  });

                  System.out.println("  >> Atan(5832) starting <<");
                  ATanWorker atan5832 = new ATanWorker(5832, DECIMAL_PLACES, bigDecimal -> {
                        System.out.println("  Found Atan(5832)");
                        FinitePi.atan5832 = bigDecimal;
                        check(pi);
                  });

                  System.out.println("  >> Atan(110443) starting <<");
                  ATanWorker atan110443 = new ATanWorker(110443, DECIMAL_PLACES, bigDecimal -> {
                        System.out.println("  Found Atan(110443)");
                        FinitePi.atan110443 = bigDecimal;
                        check(pi);
                  });

                  System.out.println("  >> Atan(4841182) starting <<");
                  ATanWorker atan4841182 = new ATanWorker(4841182, DECIMAL_PLACES, bigDecimal -> {
                        System.out.println("  Found Atan(4841182)");
                        FinitePi.atan4841182 = bigDecimal;
                        check(pi);
                  });

                  System.out.println("  >> Atan(6826318) starting <<");
                  ATanWorker atan6826318 = new ATanWorker(6826318, DECIMAL_PLACES, bigDecimal -> {
                        System.out.println("  Found Atan(6826318)");
                        FinitePi.atan6826318 = bigDecimal;
                        check(pi);
                  });

                  final double iterationsAtan239 = (657l * DECIMAL_PLACES) / 3125;

                  if (!ATanWorker.executor.isShutdown()) {
                        try {
                              ATanWorker.executor.submit((Runnable) () -> {
                                    while (true) {
                                          if (FinitePi.atan239 != null) {
                                                return;
                                          }

                                          // Takes the longest time no matter what, we only care about this.
                                          int atan239Percent = (int) ((atan239.getCurrentSpot() / iterationsAtan239) * 100);

                                          progressBar.setValue(Math.min(100, atan239Percent));

                                          try {
                                                Thread.sleep(10l);
                                          } catch (InterruptedException e) {
                                                return;
                                          }
                                    }
                              });
                        } catch (RejectedExecutionException e) {
                        }
                  }
            });

            progressBar = new JProgressBar(0, 100);
            progressBar.setValue(0);
            progressBar.setStringPainted(true);

            JPanel panel = new JPanel();
            panel.setLayout(new FlowLayout());

            JPanel progressBarPanel = new JPanel();
            progressBarPanel.setLayout(new GridLayout(2, 0));
            progressBarPanel.add(new JLabel("Progress", SwingConstants.CENTER));
            progressBarPanel.add(progressBar);

            panel.add(progressBarPanel);

            JPanel numDecimalPlacePanel = new JPanel();
            numDecimalPlacePanel.setLayout(new GridLayout());
            numDecimalPlacePanel.add(new JLabel("Number of Decimal Places", SwingConstants.CENTER));
            numDecimalPlacePanel.add(numDecimal);

            panel.add(numDecimalPlacePanel);
            panel.add(find);

            pi.setLineWrap(true);

            JScrollPane scrollPane = new JScrollPane(pi);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

            frame.add(panel, BorderLayout.PAGE_START);
            frame.add(scrollPane, BorderLayout.CENTER);

            JTextArea log = new JTextArea("Press \"Find Pi\" to find pi.");
            log.setRows(10);
            log.setEditable(false);

            log.setForeground(Color.white);
            log.setBackground(Color.black);

            JScrollPane scrollLog = new JScrollPane(log);
            scrollLog.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

            System.setOut(output = new TextAreaOutput(log));

            frame.add(scrollLog, BorderLayout.PAGE_END);
            frame.setVisible(true);
      }

      private static void check(JTextArea field) {
            if (atan239 != null && atan1023 != null && atan5832 != null && atan110443 != null && atan4841182 != null && atan6826318 != null) {
                  progressBar.setValue(100);

                  System.out.println("Atan values computed (took " + (System.currentTimeMillis() - time) / 1000 + " seconds), computing PI");

                  BigDecimal $183 = new BigDecimal("183");
                  BigDecimal $32 = new BigDecimal("32");
                  BigDecimal $68 = new BigDecimal("68");
                  BigDecimal $12 = new BigDecimal("12");
                  BigDecimal $100 = new BigDecimal("100");

                  BigDecimal result = $183.multiply(atan239)
                          .add($32.multiply(atan1023))
                          .subtract($68.multiply(atan5832))
                          .add($12.multiply(atan110443))
                          .subtract($12.multiply(atan4841182))
                          .subtract($100.multiply(atan6826318));

                  result = result.multiply(FOUR).setScale(DECIMAL_PLACES, BigDecimal.ROUND_FLOOR);

                  field.setText(result.toString());

                  System.out.println("Found PI to " + DECIMAL_PLACES + " decimal places.");
                  System.out.println("Done (took " + (System.currentTimeMillis() - time) / 1000 + " seconds)");

                  ATanWorker.executor.shutdownNow();
                  ATanWorker.executor = Executors.newCachedThreadPool();
            }
      }

      private static class TextAreaOutput extends PrintStream {

            private JTextArea area;

            public TextAreaOutput(JTextArea area)
                    throws FileNotFoundException {
                  super(new OutputStream() {
                        @Override
                        public void write(int i)
                                throws IOException {
                        }
                  });

                  this.area = area;
            }

            @Override
            public void println(String s) {
                  area.setText(area.getText() + "\n" + s);
                  area.setCaretPosition(area.getDocument().getLength());
            }

            public void clear() {
                  area.setText("");
            }
      }
}
