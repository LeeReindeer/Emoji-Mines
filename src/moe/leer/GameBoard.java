package moe.leer;

import xyz.leezoom.java.util.Log;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author leer
 * Created at 6/11/18 7:44 PM
 */
public class GameBoard extends JFrame {

  public static final String TAG = "GameBoard";
  private MineBoard mineBoard;

  private TimerPanel timerPanel;
  private JLabel flagLabel;
  private JLabel statusLabel;

//  private MyDialog dialog;

  private transient long time;
  private final Emoji emoji = new Emoji();

  private int size = 8;
  private int mine = 10;

  private boolean isGameStart = false;

  private MineBoard.StatusListener statusListener = new MineBoard.StatusListener() {
    @Override
    public void onStart() {
      isGameStart = true;
      timerPanel.start();
    }

    @Override
    public void onWin() {
      timerPanel.stop();
      isGameStart = false;
      statusLabel.setIcon(emoji.WIN);
    }

    @Override
    public void onLose() {
      timerPanel.stop();
      isGameStart = false;
      statusLabel.setIcon(emoji.LOSE);
    }

    @Override
    public void onFlagChange(int sizeOfFlags) {
      updateFlag(sizeOfFlags);
    }
  };

  public static final Color BACKGROUD_COLOR = Color.WHITE;
  public static final Color UNOPEN_COLOR = new Color(0x7a9eb1);
  public static final Color BUTTON_COLOR = new Color(0xfffffb);


  public GameBoard() throws HeadlessException {
    initFrame();
    timerPanel = new TimerPanel();
//    JButton startButton = new JButton("Start");
//    JButton stopButton = new JButton("stop");
//    stopButton.setBackground(BUTTON_COLOR);
//    startButton.setBackground(BUTTON_COLOR);
//
//    startButton.addActionListener(e -> {
//      timerPanel.start();
//    });
//    stopButton.addActionListener(e -> {
//      timerPanel.stop();
//    });

    JPanel controlPanel = new JPanel();
    mineBoard = new MineBoard(size, mine);
    mineBoard.setStatusListener(statusListener);
    flagLabel = new JLabel();
    statusLabel = new JLabel();
    statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
    statusLabel.setIcon(emoji.MINE);

    flagLabel.setHorizontalAlignment(SwingConstants.CENTER);
    SwingUtil.setDefaultFont(flagLabel);
    updateFlag(0);

    GridLayout gridLayout = new GridLayout(1, 3);
    gridLayout.setVgap(4);
    gridLayout.setHgap(4);
    controlPanel.setLayout(gridLayout);
    controlPanel.add(timerPanel);
    controlPanel.add(statusLabel);
    controlPanel.add(flagLabel);
//    controlPanel.add(startButton);
//    controlPanel.add(stopButton);
    BorderLayout borderLayout = new BorderLayout();
    borderLayout.setHgap(8);
    borderLayout.setVgap(8);
    this.getContentPane().setLayout(borderLayout);
    this.getContentPane().add(controlPanel, BorderLayout.NORTH);
    this.getContentPane().add(mineBoard, BorderLayout.CENTER);

    initStatusButton();
//    this.addFocusListener(this);
    WindowAdapter windowAdapter = new WindowAdapter() {
      @Override
      public void windowGainedFocus(WindowEvent e) {
        super.windowGainedFocus(e);
        Log.i(TAG, "enter...");
        if (timerPanel.isStop() && isGameStart) {
          mineBoard.setEnabled(true);
          statusListener.onStart();
        }
      }

      @Override
      public void windowLostFocus(WindowEvent e) {
        super.windowLostFocus(e);
        Log.i(TAG, "exit...");
        if (!timerPanel.isStop() && isGameStart) {
          timerPanel.stop();
          mineBoard.setEnabled(false);
        }
      }
    };
    this.addWindowFocusListener(windowAdapter);
    this.addWindowListener(windowAdapter);
    this.setVisible(true);
  }

  private void initFrame() {
    this.setSize(500 * size / 8, 500 * size / 8 + 100);
    this.setTitle("Emoji Mines");
    this.setIconImage(emoji.MINE_ICON.getImage());
    this.setResizable(false);
    this.setLayout(new BorderLayout(8, 8));
    this.setBackground(BACKGROUD_COLOR);
    this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
  }

  private void initStatusButton() {
    statusLabel.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 1) {
          isGameStart = false;
          resetMineBoard();
          timerPanel.reset();
          statusLabel.setIcon(emoji.MINE);
          repaint();
        }
      }
    });
  }

  private void resetMineBoard() {
    this.setVisible(false);
    mineBoard.reset(size, mine);
    mineBoard.setStatusListener(statusListener);
//    this.getContentPane().add(mineBoard, BorderLayout.CENTER);
    this.setVisible(true);
    helper();
  }

  private void updateFlag(int flagSize) {
    flagLabel.setIcon(emoji.FLAG);
    flagLabel.setText(String.format("%d/%d", flagSize, mine));
  }

  private void helper() {
    for (MineBoard.Point point : mineBoard.getMines()) {
      System.out.printf("[%d. %d] ", point.x, point.y);
    }
    System.out.println();
  }

  private class TimerPanel extends JPanel {

    private double centerX;
    private Timer timer;
    private double centerY;
    private boolean isStop = true;

    @Override
    protected synchronized void paintComponent(Graphics g) {
      super.paintComponent(g);
      centerX = this.getWidth() / 2;
      centerY = this.getHeight() / 2;
      g.setFont(new Font("Arial", Font.PLAIN, 20));
      g.drawImage(emoji.TIME.getImage(), 0, 0, 24, 24, (img, infoflags, x, y, width, height) -> false);
      g.drawString(getTimeFormatString(time), (int) centerX - 50, (int) centerY + 5);
    }

    public void start() {
      isStop = false;
      timer = new Timer();
      timer.schedule(new TimerTask() {
        @Override
        public void run() {
          time++;
          redraw();
        }
      }, 0, 1000);
    }

    private synchronized void redraw() {
      repaint();
    }

    public boolean isStop() {
      return isStop;
    }

    public void stop() {
      timer.cancel();
      timer.purge();
      isStop = true;
    }

    public void reset() {
      stop();
      time = 0;
      repaint();
    }

    public void restart() {
      stop();
      time = 0;
      start();
    }

  }

  private static String getTimeFormatString(long time) {
    int hour = 0;
    int minute = 0;
    int second = 0;
    hour = (int) (time / 3600);
    time %= 3600;
    minute = (int) (time / 60);
    second = (int) (time %= 60);
    StringBuilder sb = new StringBuilder();
    sb.append(String.format("%02d", hour)).append(":").append(String.format("%02d", minute)).append(":").append(String.format("%02d", second));
    return sb.toString();
  }

  public static void main(String[] args) {
    GameBoard game = new GameBoard();
    game.helper();
  }
}
