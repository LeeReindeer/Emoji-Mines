package moe.leer;

import javax.swing.*;
import java.net.URL;
import java.util.HashMap;

/**
 * @author leer
 * Created at 6/11/18 4:47 PM
 */
public class Emoji {

  public final ImageIcon MINE_ICON;

  public final ImageIcon MINE;
  public final ImageIcon FLAG;
  public final ImageIcon TIME;

  public final ImageIcon ONE;
  public final ImageIcon TWO;
  public final ImageIcon THREE;
  public final ImageIcon FOUR;
  public final ImageIcon FIVE;
  public final ImageIcon SIX;
  public final ImageIcon SEVEN;
  public final ImageIcon EIGHT;

  public final ImageIcon WIN;
  public final ImageIcon LOSE;


  public HashMap<Integer, ImageIcon> numberEmojiMap;

  {
    MINE_ICON = new ImageIcon(getResource("resource/emoji-mine.png"));

    MINE = new ImageIcon(getResource("resource/emoji-mine-24.png"));
    FLAG = new ImageIcon(getResource("resource/emoji-flag-24.png"));
    TIME = new ImageIcon(getResource("resource/emoji-clock.png"));
    WIN = new ImageIcon(getResource("resource/emoji-full-moon.png"));
    LOSE = new ImageIcon(getResource("resource/emoji-new-moon.png"));

    ONE = new ImageIcon(getResource("resource/emoji-one.png"));
    TWO = new ImageIcon(getResource("resource/emoji-two.png"));
    THREE = new ImageIcon(getResource("resource/emoji-three.png"));
    FOUR = new ImageIcon(getResource("resource/emoji-four.png"));
    FIVE = new ImageIcon(getResource("resource/emoji-five.png"));
    SIX = new ImageIcon(getResource("resource/emoji-six.png"));
    SEVEN = new ImageIcon(getResource("resource/emoji-seven.png"));
    EIGHT = new ImageIcon(getResource("resource/emoji-eight.png"));
    numberEmojiMap = new HashMap<>();
    numberEmojiMap.put(1, ONE);
    numberEmojiMap.put(2, TWO);
    numberEmojiMap.put(3, THREE);
    numberEmojiMap.put(4, FOUR);
    numberEmojiMap.put(5, FIVE);
    numberEmojiMap.put(6, SIX);
    numberEmojiMap.put(7, SEVEN);
    numberEmojiMap.put(8, EIGHT);
  }

  private URL getResource(String path) {
    return this.getClass().getClassLoader().getResource(path);
  }
}
