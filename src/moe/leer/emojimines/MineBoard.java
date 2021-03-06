package moe.leer.emojimines;

import edu.princeton.cs.algs4.WeightedQuickUnionUF;
import xyz.leezoom.java.util.Log;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;

/**
 * @author leer
 * Created at 6/11/18 2:20 PM
 * <p>
 * Left click block, if there is a mine you lose, else it print out
 * the mines around it(in adj 8 blocks).
 * <p>
 * Right click to flag mine.
 * <p>
 * Double click, if you flag all mines or adj blocks is no mines, the others in adj 8 blocks show,
 * else you lose
 */
public class MineBoard extends JPanel {

  private static final String TAG = "MineBoard";


  private Emoji emoji;

  // default size 8 * 8 -> 10 mines
  // 16 * 16 -> 40 mines , 30 * 30->99mines
  private int size = 8;
  private boolean[][] board;
  private boolean[][] open;
  private JButton[][] boxes;

  public static final Color BACKGROUD_COLOR = Color.WHITE;
  public static final Color UNOPEN_COLOR = new Color(0x7a9eb1);
  public static final Color MINE_COLOR = new Color(0xe16b8c);
  public static final Color BUTTON_COLOR = new Color(0xfffffb);

  private final HashMap<Integer, Integer> mineRateMap;

  private ArrayList<Point> mines;
  private ArrayList<Point> flags;
  private boolean isFirstTimeBlankClick = true;
  private boolean isFirstTimeClick = true;
  private boolean isDoubleClick = false;
  private WeightedQuickUnionUF unionUF;

  private StatusListener statusListener;

  // init for every instance
  {
    mineRateMap = new HashMap<>();
    mineRateMap.put(8, 10);
    mineRateMap.put(16, 40);
    mineRateMap.put(30, 99);
//    mineIcon = new ImageIcon(getClass().getClassLoader().getResource("resource/emoji-mine-24.png"));
//    flagIcon = new ImageIcon(getClass().getClassLoader().getResource("resource/emoji-flag-24.png"));
  }

  public static class Point {
    int x;
    int y;

    Point() {
    }

    Point(int x, int y) {
      this.x = x;
      this.y = y;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof Point)) return false;
      Point point = (Point) o;
      return x == point.x &&
          y == point.y;
    }

    @Override
    public int hashCode() {
      return Objects.hash(x, y);
    }
  }

  public interface StatusListener {
    void onStart();

    void onWin();

    void onLose();

    void onFlagChange(int sizeOfFlags);
  }

  public MineBoard(int size) throws HeadlessException {
    initData(size);
    initView();
  }

  public MineBoard(int size, int mines) throws HeadlessException {
    initData(size);
    mineRateMap.put(size, mines);
    initView();
  }

  public void setStatusListener(StatusListener statusListener) {
    this.statusListener = statusListener;
  }

  public void reset() {
    reset(size, mineRateMap.get(size));
  }

  public void reset(int size, int mineSize) {
    this.removeAll();
    initData(size);
    mines.clear();
    flags.clear();
    mineRateMap.put(size, mineSize);
    initView();
    statusListener.onFlagChange(0);
  }

  public ArrayList<Point> getMines() {
    return mines;
  }

  private void initData(int size) {
    this.size = size;
    this.unionUF = new WeightedQuickUnionUF(size * size);
    emoji = new Emoji();
    board = new boolean[size][size];
    open = new boolean[size][size];
    boxes = new JButton[size][size];
    isFirstTimeClick = true;
    isFirstTimeBlankClick = true;
    isDoubleClick = false;
  }

  private void initView() {
    this.setSize(300 * size / 8, 300 * size / 8);
    this.setBackground(Color.WHITE);
    GridLayout gridLayout = new GridLayout(size, size);
    gridLayout.setHgap(4);
    gridLayout.setVgap(4);
    this.setLayout(gridLayout);

    initBoard(this);
    initMines();
  }

  private void initMines() {
    Integer totalMines = mineRateMap.get(size);
    Log.d(TAG, "mines: " + totalMines);
    if (totalMines == null) {
      totalMines = 8;
    }
    mines = new ArrayList<>(totalMines);
    flags = new ArrayList<>(totalMines);
  }

  /**
   * generate mines that ensures excludePoint won't be mine
   * Called when first left click
   */
  private void genRandomMinesEnsured(int mineSize, Point excludePoint) {
    // fill mines
    int minesCnt = 0;
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        if (i == excludePoint.x && j == excludePoint.y) continue;
        board[i][j] = true;
        minesCnt++;
        if (minesCnt == mineSize) break;
      }
      if (minesCnt == mineSize) break;
    }
    // shuffle array
    Random random = new Random(System.currentTimeMillis());
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        int randomPoint = random.nextInt(size * size);
        Point point = oneD2point(randomPoint);
        if (i == excludePoint.x && j == excludePoint.y || point.equals(excludePoint)) continue;
        boolean temp = board[point.x][point.y];
        board[point.x][point.y] = board[i][j];
        board[i][j] = temp;
      }
    }
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        if (board[i][j]) mines.add(new Point(i, j));
      }
    }
    Log.d(TAG, Arrays.deepToString(board));
  }

  private void initBoard(JPanel panel) {
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        boxes[i][j] = new JButton();
        final Point point = new Point(i, j);
        JButton box = boxes[i][j];
        initBox(box);
        box.addMouseListener(new MouseAdapter() {

          @Override
          public void mousePressed(MouseEvent e) {
            if (e.getClickCount() > 2) {
              return;
            }
            if (SwingUtilities.isLeftMouseButton(e)) {
              if (e.getClickCount() == 2) {
                handleDoublyClick(point);
                isDoubleClick = true;
              } else {
                Integer timerinterval = (Integer) Toolkit.getDefaultToolkit().getDesktopProperty(
                    "awt.multiClickInterval");
                Timer timer = new Timer(timerinterval, evt -> {
                  if (isDoubleClick) {
                    isDoubleClick = false; // reset flag
                  } else {
                    handleLeftClick(point, box);
                  }
                });
                timer.setRepeats(false);
                timer.start();
              }
            } else if (SwingUtilities.isRightMouseButton(e)) {
              handleRightClick(point, box);
            }
          }

          @Override
          public void mouseClicked(MouseEvent e) {

          }
        });
        panel.add(box);
      }
    }
  }

  private void handleRightClick(Point point, JButton box) {
    Log.d(TAG, "right click");
    if (!box.isEnabled()
        || isFirstTimeClick || isOpen(point)) {
      Log.d(TAG, "can't flag any more");
      return;
    }
    if (flags.contains(point)) { // already flagged
      flags.remove(point);
      setUnOpen(box);
    } else {
      flags.add(point);
      // assume it is right before game ending
      setFlagBox(point, true);
      // win when all mines are flagged, but not all box is clicked.
      if (flags.containsAll(mines) && flags.size() == mines.size()) {
        processWin();
      }
    }
    statusListener.onFlagChange(flags.size());
  }

  /**
   * generate mines when first left click
   *
   * @param point clicked point
   * @param box   clicked button
   */
  private void handleLeftClick(Point point, JButton box) {
    if (isFirstTimeClick) {
      genRandomMinesEnsured(mineRateMap.get(size), point);
      statusListener.onStart();
      isFirstTimeClick = false;
    }
    if (isFlag(point)) {
      Log.d(TAG, "can't click flagged box");
      return;
    }
    Log.d(TAG, "left click");
    if (isMine(point)) {
      setMineBox(point);
      processLose();
    } else {
      open[point.x][point.y] = true;
      int mines = countMines(point);
      if (mines == 0) {
        setBlankBox(point);
        showUnionBlankBlock(point);
      } else {
        setNumberBox(point, mines);
      }
    }
  }

  private void handleDoublyClick(Point point) {
    if (isUnOpen(point)) {
      Log.d(TAG, "can't double click");
      return;
    }
    Log.d(TAG, "double click");
    if (!showNumberAroundPoint(point.x, point.y)) {
      processLose();
    }
  }

  private void processWin() {
    showAllMines();
    showAllFlags();
    statusListener.onWin();
  }

  private void processLose() {
    showAllMines();
    showAllFlags();
    Log.d(TAG, "You lose.");
    statusListener.onLose();
  }

  private void initBox(JButton box) {
    box.setBackground(UNOPEN_COLOR);
    SwingUtil.setDefaultFont(box);
//    box.setFont(new Font("Arial", Font.PLAIN, 20));
    box.setIcon(null);
    box.setSelected(false);
    box.setEnabled(true);
  }

  /**
   * All array index start from 0.
   * convert 2D [row, col] to 1D array index
   *
   * @return index in 1D array
   */
  private int xyTo1D(int row, int col) {
    return (row) * size + (col);
  }

  private int[] oneD2xy(int one) {
    int[] xy = new int[2];
    xy[0] = one / (size);
    xy[1] = one % (size);
    return xy;
  }

  private Point oneD2point(int one) {
    Point point = new Point();
    point.x = one / (size);
    point.y = one % (size);
    return point;
  }

  private void setUnOpen(JButton box) {
    initBox(box);
  }

  private void setBlankBox(Point point) {
    JButton box = boxes[point.x][point.y];
    open[point.x][point.y] = true;
    box.setText("");
    box.setIcon(null);
    box.setBackground(BACKGROUD_COLOR);
    box.setEnabled(false);
  }

  private void setNumberBox(Point point, int mines) {
    JButton box = boxes[point.x][point.y];
    open[point.x][point.y] = true;
    box.setBackground(BACKGROUD_COLOR);
    box.setIcon(emoji.numberEmojiMap.get(mines));

  }

  private void setMineBox(Point point) {
    JButton box = boxes[point.x][point.y];
    open[point.x][point.y] = true;
    box.setBackground(MINE_COLOR);
    box.setIcon(emoji.MINE);
  }

  private void setFlagBox(Point point, boolean correct) {
    JButton box = boxes[point.x][point.y];
    box.setIcon(emoji.FLAG);
    if (correct) {
      box.setBackground(Color.CYAN);
    } else {
      box.setBackground(Color.RED);
    }
  }

  private int countMines(Point point) {
    int count = 0;
    for (int k = -1; k <= 1; k++) {
      for (int l = -1; l <= 1; l++) {
        // check range
        if (!validRange(point.x + k, point.y + l)) {
          continue;
        }
        // counting
        if (isMine(point.x + k, point.y + l)) {
          count++;
        }
      }
    }
    return count;
  }

  private boolean isBlank(Point point) {
    if (!validRange(point.x, point.y)) {
      return false;
    }
    return countMines(point) == 0;
  }

  private boolean isBlank(int i, int j) {
    if (!validRange(i, j)) {
      return false;
    }
    return countMines(new Point(i, j)) == 0;
  }

  private boolean isMine(Point point) {
    if (!validRange(point.x, point.y)) {
      return false;
    }
    return board[point.x][point.y];
  }

  private boolean isMine(int i, int j) {
    if (!validRange(i, j)) {
      return false;
    }
    return board[i][j];
  }

  private boolean isFlag(Point point) {
    if (!validRange(point.x, point.y)) {
      return false;
    }
    return flags.contains(point);
  }

  private boolean isUnOpen(Point point) {
    if (!validRange(point.x, point.y)) {
      return false;
    }
    return !open[point.x][point.y];
  }

  private boolean isOpen(Point point) {
    if (!validRange(point.x, point.y)) {
      return false;
    }
    return open[point.x][point.y];
  }

  /**
   * Union all connected blank(it's adj blocks have no mine),
   * So when you clicked on a blank block, the all connected block will show.
   */
  private void unionAllBlankBlock() {
    //as known, [i, j] is already a blank.
    for (int row = 0; row < size; row++) {
      for (int col = 0; col < size; col++) {
        if (isBlank(row, col)) {
          int thisPoint = xyTo1D(row, col);
          // top
          if (isBlank(row - 1, col)) {
            unionUF.union(thisPoint, xyTo1D(row, col));
          }
          // left
          if (isBlank(row - 1, col)) {
            unionUF.union(thisPoint, xyTo1D(row - 1, col));
          }
          // bottom
          if (isBlank(row, col + 1)) {
            unionUF.union(thisPoint, xyTo1D(row, col + 1));
          }
          // right
          if (isBlank(row + 1, col)) {
            unionUF.union(thisPoint, xyTo1D(row + 1, col));
          }
        }
      }
    }
  }

  /**
   * show blank block union to this point
   *
   * @param point
   */
  private void showUnionBlankBlock(Point point) {
    if (isFirstTimeBlankClick) {
      unionAllBlankBlock();
      isFirstTimeBlankClick = false;
    }
    ArrayList<Point> points = new ArrayList<>(size * size - mineRateMap.get(size));
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        if (unionUF.connected(xyTo1D(point.x, point.y), xyTo1D(i, j))) {
          Point p = new Point(i, j);
          points.add(p);
          setBlankBox(p);
          showNumberAroundBlank(i, j);
          clearFlag(p);
        }
      }
    }

  }

  /**
   * show number box around(top, left, bottom, top these for point) the blank union block
   * point(i, j) shall be a blank box
   */
  private void showNumberAroundBlank(int row, int col) {
    // loop at most 4 times(top, left, bottom, right)
    for (int i = -1; i <= 1; i++) {
      for (int j = -1; j <= 1; j++) {
        Point adjPoint = new Point(row + i, col + j);
        if (adjPoint.x != row && adjPoint.y != col) {
          continue;
        }
        showAdjBox(adjPoint);
        clearFlag(adjPoint);
      }
    }
  }

  private void showAdjBox(Point adjPoint) {
    if (validRange(adjPoint.x, adjPoint.y)
        && !isMine(adjPoint)) {
      Point p = new Point(adjPoint.x, adjPoint.y);
      int count = countMines(p);
      if (count == 0) {
        setBlankBox(p);
      } else {
        setNumberBox(p, count);
      }
    }
  }

  /**
   * check and clear this flag if is flagged, when user clicked blank blocks
   * and it contains flag blocks
   */
  private boolean clearFlag(Point point) {
    if (flags.contains(point)) {
      flags.remove(point);
      statusListener.onFlagChange(flags.size());
      return true;
    }
    return false;
  }

  /**
   * adj 8 eight points
   *
   * @return true if there is no mines or all mine is flagged
   */
  private boolean showNumberAroundPoint(int row, int col) {
    // loop at most 4 times(top, left, bottom, right)
    boolean isAllFlaged = true;
    for (int i = -1; i <= 1; i++) {
      for (int j = -1; j <= 1; j++) {
        Point adjPoint = new Point(row + i, col + j);
        if (isMine(adjPoint) && !isFlag(adjPoint)) { // unflagged mine here
          isAllFlaged = false;
        }
        showAdjBox(adjPoint);
        if (!isMine(adjPoint)) {
          clearFlag(adjPoint);
        }
      }
    }
    return isAllFlaged;
  }

  private void showAllMines() {
    for (Point m : mines) {
      setMineBox(m);
    }
  }

  private void showAllFlags() {
    for (Point f : flags) {
      setFlagBox(f, mines.contains(f));
    }
  }

  private boolean validRange(int i, int j) {
    return i <= size - 1 && j <= size - 1 && i >= 0 && j >= 0;
  }
}
