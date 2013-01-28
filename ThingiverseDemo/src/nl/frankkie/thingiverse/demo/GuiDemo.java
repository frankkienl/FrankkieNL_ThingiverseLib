package nl.frankkie.thingiverse.demo;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Properties;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import nl.frankkie.thingiverse.lib.ThingMethod;
import nl.frankkie.thingiverse.lib.ThingiverseClient;

/**
 *
 * @author frankkie
 */
public class GuiDemo {

  //http://www.thingiverse.com/developers/rest-api-reference#things
  boolean noLogHorizontalScroll = true;
  public static String clientId = "1e0845ef6c97aed09214";
  public static String clientSecret = "d7b6920da341cdeb063262c09ee402e3";
  public static String clientCallback = "http://frankkie.nl/thingiverse/api.php";
  public ThingiverseClient client;
  public JTextArea logTextArea = new JTextArea("LOG:\n");

  public static void main(String[] args) {
    new GuiDemo();
  }

  public GuiDemo() {
    client = new ThingiverseClient(clientId, clientSecret, clientCallback);
    //String accessTokenString;
    //accessTokenString = getSavedAccesToken();
    JFrame frame = new JFrame("Thingiverse API Demo");
    frame.setSize(400, 600);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setLayout(new BorderLayout());
    JPanel callsPanel = new JPanel();
    callsPanel.setLayout(new GridLayout(0, 1));
    JScrollPane scrollPaneCenter = new JScrollPane(callsPanel);
    //frame.add(scrollPaneCenter, BorderLayout.CENTER);
    //Get Calls
    doCalls(callsPanel);
    //
    JPanel logPanel = new JPanel(new GridLayout());
    //logTextArea = new JTextArea("LOG:\n");
    logPanel.add(logTextArea);
    JScrollPane scrollPaneSouth = new JScrollPane(logPanel);
    if (noLogHorizontalScroll) {
      scrollPaneCenter.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    }
    //frame.add(scrollPaneSouth, BorderLayout.SOUTH);
    //
    JSplitPane splot = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollPaneCenter, scrollPaneSouth);
    splot.setOneTouchExpandable(true);
    splot.setDividerLocation(150);

//Provide minimum sizes for the two components in the split pane
    Dimension minimumSize = new Dimension(100, 100);
    scrollPaneCenter.setMinimumSize(minimumSize);
    scrollPaneSouth.setMinimumSize(minimumSize);
    frame.add(splot, BorderLayout.CENTER);
    splot.setDividerLocation(500);
    frame.setVisible(true);
  }

  void doCalls(JPanel callsPanel) {
    Method[] declaredMethods = client.getClass().getDeclaredMethods();
    for (final Method method : declaredMethods) {
      if (true) {
        JButton b = new JButton(method.getName());
        b.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent ae) {
            clickedMethod(method);
          }
        });
        callsPanel.add(b);
        System.out.println("Added: " + method.getName());
      }
    }
  }

  public void clickedMethod(final Method m) {
    JFrame frame = new JFrame("Method call: " + m.getName());
    frame.setSize(300, 200);
    frame.setLayout(new BorderLayout());
    JPanel argsPanel = new JPanel(new GridLayout(0, 2));
    JLabel callName = new JLabel(m.getName());
    frame.add(callName, BorderLayout.NORTH);
    Class<?>[] parameterTypes = m.getParameterTypes();
    final ArrayList<JTextField> tfs = new ArrayList<JTextField>();
    ThingMethod annotation = m.getAnnotation(ThingMethod.class);
    for (int i=0; i < parameterTypes.length; i++) { //just to get the number of args xD
      JLabel lbl = new JLabel(annotation.params()[i]);
      JTextField tf = new JTextField("");
      argsPanel.add(lbl);
      argsPanel.add(tf);
      tfs.add(tf);
    }
    JScrollPane scrollPaneCenter = new JScrollPane(argsPanel);
    frame.add(scrollPaneCenter, BorderLayout.CENTER);
    //
    JButton callBtn = new JButton("Call method");
    callBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent ae) {
        try {
          ArrayList<String> str = new ArrayList<String>();
          for (JTextField tf : tfs) {
            str.add(tf.getText());
          }
          Object invoke = m.invoke(client, str.toArray());
          log("" + invoke);
        } catch (Exception ex) {
          javax.swing.JOptionPane.showMessageDialog(null, ex);
          log(ex.toString());
          ex.printStackTrace();
        }
      }
    });
    frame.add(callBtn, BorderLayout.SOUTH);
    frame.setVisible(true);
  }

  public void log(String s) {
    logTextArea.setText(logTextArea.getText() + s + "\n");
    logTextArea.invalidate();
  }

  /**
   * Save accesToken to file
   *
   * @param accesToken
   */
  public void saveAccesToken(String accesToken) {
    String filename = "ThingiverseRipper.properties";
    Properties props;
    File file;
    try {
      file = new File(System.getProperty("user.home") + System.getProperty("file.separator") + filename);
      if (!file.exists()) {
        file.createNewFile();
      }
      //System.out.println(file.getAbsolutePath());
      log(file.getAbsolutePath());
      props = new Properties();
      props.load(new FileInputStream(file));
      props.setProperty("access_token", accesToken);
      props.store(new FileWriter(file), "ThingiverseRipper");
    } catch (FileNotFoundException fnfe) {
      //file does not exist, no problem
      System.err.println("Properties-File does not exist.");
      fnfe.printStackTrace();
    } catch (IOException ioe) {
      //file cannot be read, act like the file does not exist.
      System.err.println("Properties-File does not exist, or cannot be read/written.");
    }
  }

  /**
   * Get AccesToken from file
   *
   * @return accesToken from file
   */
  public String getSavedAccesToken() {
    String answer = null;
    String filename = "ThingiverseRipper.properties";
    Properties props;
    File file;
    try {
      file = new File(System.getProperty("user.home") + System.getProperty("file.separator") + filename);
      props = new Properties();
      props.load(new FileInputStream(file));
      answer = props.getProperty("access_token");
    } catch (FileNotFoundException fnfe) {
      //file does not exist, no problem
      System.err.println("Properties-File does not exist.");
      log("Properties-File does not exist.");
    } catch (IOException ioe) {
      //file cannot be read, act like the file does not exist.
      System.err.println("Properties-File does not exist, or cannot be read.");
      log("Properties-File does not exist, or cannot be read.");
    }
    return answer;
  }
}
