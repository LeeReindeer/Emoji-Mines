package moe.leer.emojimines;

import javax.swing.*;
import java.awt.*;

/**
 * @author leer
 * Created at 6/11/18 11:09 PM
 */
public class SwingUtil {

  public static void setDefaultFont(JComponent component) {
    component.setFont(new Font("Arial", Font.PLAIN, 20));
  }

  public static void setDefaultFont(JComponent component, int size) {
    component.setFont(new Font("Arial", Font.PLAIN, size));
  }
}
