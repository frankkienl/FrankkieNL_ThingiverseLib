package nl.frankkie.thingiverse.demo;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.Properties;
import nl.frankkie.thingiverse.lib.ThingiverseClient;

/**
 * Thingiverse API Demo in Java (on the computer, not for Android) You need to
 * login, the first time you run this application.
 *
 * The next time it will use the accesToken from the previous login. remove the
 * file System.getProperty("user.home") + System.getProperty("file.separator") +
 * "ThingiverseRipper.properties"; to login again.
 *
 * @author frankkie
 */
public class ApiDemo {

  public static String clientId = "1e0845ef6c97aed09214";
  public static String clientSecret = "d7b6920da341cdeb063262c09ee402e3";
  public static String clientCallback = "http://frankkie.nl/thingiverse/api.php";
  public ThingiverseClient client;

  /**
   * Start Application
   * @param args 
   */
  public static void main(String[] args) {
    new ApiDemo();
  }

  /**
   * Do stuff
   */
  public ApiDemo() {
    client = new ThingiverseClient(clientId, clientSecret, clientCallback);
    String accessTokenString;
    accessTokenString = getSavedAccesToken();
    if (accessTokenString == null || accessTokenString.equalsIgnoreCase("")) {
      String authUrl = client.loginFirstTime();
      try {
        //start the browser
        Desktop.getDesktop().browse(URI.create(authUrl));
      } catch (IOException ex) {
        System.err.println("Browser does not work.");
        ex.printStackTrace();
      }
      String browserCode = javax.swing.JOptionPane.showInputDialog("Log in with your Thingiverse-account, click allow, paste code here:");
      accessTokenString = client.loginWithBrowserCode(browserCode);
      saveAccesToken(accessTokenString); // save for next time :P
    }
    
    //Do some API-calls
    //these are only visible in the commandline
    System.out.println("Featured:");
    String featured = client.featured();
    System.out.println(featured);
    System.out.println("Newest:");
    String newest = client.newest();
    System.out.println(newest);
    System.out.println("Me:");
    String me = client.user("me");
    System.out.println(me);
    //show gui version
    //JOptionPane.showMessageDialog(null, newest);//nvm, does not look good anyway
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
      System.out.println(file.getAbsolutePath());
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
    } catch (IOException ioe) {
      //file cannot be read, act like the file does not exist.
      System.err.println("Properties-File does not exist, or cannot be read.");
    }
    return answer;
  }
}
