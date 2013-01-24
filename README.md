FrankkieNL_ThingiverseLib
=========================

Java Library for connecting to the Thingiverse API (Android and 'Desktop' Java)

Getting Started:

1) Download ThingiverseLib.jar

Tip: This file can be found in /ThingiverseLib/dist/


2) Add it to your Java-project

Android-Tip: Paste it into /project/libs/

Desktop-Tip: (NetBeans) RightClick project name, Properties, Libraries, Add JAR/Folder


3) Get clientId, clientSecret and clientCallback from:

http://www.thingiverse.com/developers

(click the 'Create an App!'-button)


4) Use the following code:
```java
ThingiverseClient client = new ThingiverseClient("clientId","clientSecret","clientCallback");
String authUrl = client.loginFirstTime(); //get url where the user must login
//IF DESKTOP
Desktop.getDesktop().browse(URI.create(authUrl)); //better put try-catch around it
//IF ANDROID
Intent i = new Intent(Intent.ACTION_VIEW);
i.setData(Uri.parse(authUrl));
startActivity(i);
//ENDIF
String accessTokenString = client.loginWithBrowserCode(codeTheUserGotFromBrowser);
//Optional: save the accessTokenString, else the user must login with browser next time
//Now you can use the client to access the API
String featured = cleint.featured(); //get featured things, you will get JSON in a String
String thisUser = cleint.user("me"); //get info about logged-in user, you will get JSON in a String
```
