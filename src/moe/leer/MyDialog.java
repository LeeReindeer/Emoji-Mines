package moe.leer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MyDialog extends JDialog {
  private JPanel contentPane;
  private JButton buttonAgain;
  private JButton buttonChange;
  private JLabel msgLabel;

  private ActionListener againListener;
  private ActionListener changeListener;

  public MyDialog(Frame owner, String msg) {
    super(owner, msg);
    setContentPane(contentPane);
    setModal(true);
    getRootPane().setDefaultButton(buttonAgain);

    setMessage(msg);
    buttonAgain.setBackground(MineBoard.UNOPEN_COLOR);
    buttonChange.setBackground(MineBoard.UNOPEN_COLOR);

    buttonAgain.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (againListener != null) {
          againListener.actionPerformed(e);
        }
        onClose();
      }
    });

    buttonChange.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (changeListener != null) {
          changeListener.actionPerformed(e);
        }
        onClose();
      }
    });

    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        // can't close
      }
    });

    // call onCancel() on ESCAPE
    contentPane.registerKeyboardAction(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (againListener != null) {
          againListener.actionPerformed(e);
        }
        onClose();
      }
    }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
  }

  public void setMessage(String msg) {
    msgLabel.setText(msg);
    msgLabel.setHorizontalAlignment(SwingConstants.CENTER);
  }

  public void setAgainListener(ActionListener againListener) {
    this.againListener = againListener;
  }

  public void setChangeListener(ActionListener changeListener) {
    this.changeListener = changeListener;
  }

  private void onClose() {
    dispose();
  }

//  public static void main(String[] args) {
//    MyDialog dialog = new MyDialog("You lose!");
//    dialog.setAgainListener(new ActionListener() {
//      @Override
//      public void actionPerformed(ActionEvent e) {
//        System.out.println("again");
//      }
//    });
//    dialog.pack();
//    dialog.setVisible(true);
//    System.exit(0);
//  }
}
