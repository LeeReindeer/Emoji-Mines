package moe.leer;

import java.util.HashMap;

/**
 * @author leer
 * Created at 6/11/18 4:47 PM
 */
public class Emoji {
  public static final String MINE = "\uD83D\uDCA3";
  public static final String FLAG = "\uD83C\uDF88";
  public static final String TIME = "\uD83D\uDD50";

  public static final String ONE = "1️⃣";
  public static final String TWO = "2️⃣";
  public static final String THREE = "3️⃣";
  public static final String FOUR = "4️⃣";
  public static final String FIVE = "5️⃣";
  public static final String SIX = "6️⃣";
  public static final String SEVEN = "7️⃣";
  public static final String EIGHT = "8️⃣";

  public static final String WIN = "\uD83C\uDF1D";
  public static final String LOSE = "\uD83C\uDF1A";

  public static HashMap<Integer, String> numberEmojiMap;

  static {
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
}
