package nl.frankkie.thingiverse.lib;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

/**
 *
 * @author frankkie
 */
public class ThingiverseClient {

  private String clientId = "";
  private String clientSecret = "";
  private String clientCallback = "";
  private String accesTokenString = "";
  OAuthService service;

  public ThingiverseClient(String clientId, String clientSecret, String clientCallback) {
    this.clientId = clientId;
    this.clientSecret = clientSecret;
    this.clientCallback = clientCallback;

    //init
    service = new ServiceBuilder().
            provider(ThingiverseAPI.class)
            .apiKey(clientId)
            .apiSecret(clientSecret)
            .callback(clientCallback)
            .build();
  }

  /**
   * Use this when the user has logged in already, and you have the accestoken.
   * The accesToken is used to use further API-calls.
   *
   * @param token
   */
  public void loginWithAccesToken(String token) {
    this.accesTokenString = token;
  }

  /**
   * Use this when the user logs in for the first time, and you do not have a
   * accestoken. The user needs to login at the given URL. Get the code from the
   * browser.
   *
   * @return url where the user must login.
   */
  public String loginFirstTime() {
    String authUrl = service.getAuthorizationUrl(null);
    return authUrl;
  }

  /**
   * Use this when the user has a code from the browser. This browser-code can
   * be exchanged for a accesToken. The accesToken is used to use further
   * API-calls.
   *
   * @param code the code from the browser
   * @return accesToken
   */
  public String loginWithBrowserCode(String code) {
    Verifier v = new Verifier(code);
    Token accessToken = service.getAccessToken(null, v);
    accesTokenString = accessToken.getToken();
    return accesTokenString;
  }
  public static final String GET = "GET", POST = "POST", PUT = "PUT", DELETE = "DELETE";

  private String call(String method, String url) {
    Verb verb = Verb.GET;
    if (POST.equals(method)) {
      verb = Verb.POST;
    }
    if (PUT.equals(method)) {
      verb = Verb.PUT;
    }
    if (DELETE.equals(method)) {
      verb = Verb.DELETE;
    }
    String urlEnd = url;
    if (!url.startsWith("/")) {
      urlEnd = "/" + url;
    }
    OAuthRequest request = new OAuthRequest(verb, "http://api.thingiverse.com" + urlEnd);
    request.addHeader("Authorization", "Bearer " + accesTokenString);
    Response response = request.send();
    return response.getBody();
  }

  //USER//
  public String user(String username) {
    return call(GET, "/users/" + username + "/");
  }

  public String thingsByUser(String username) {
    return call(GET, "/users/" + username + "/things");
  }

  public String likesByUser(String username) {
    return call(GET, "/users/" + username + "/likes");
  }

  public String copiesByUser(String username) {
    return call(GET, "/users/" + username + "/copies");
  }

  //THINGS//
  public String thing(String id) {
    return call(GET, "/things/" + id + "/");
  }

  public String imagesBything(String id) {
    return call(GET, "/things/" + id + "/images/");
  }

  public String imageBything(String id, String imageId) {
    return call(GET, "/things/" + id + "/images/" + imageId);
  }

  public String filesByThing(String id) {
    return call(GET, "/things/" + id + "/files/");
  }

  public String fileByThing(String id, String fileId) {
    return call(GET, "/things/" + id + "/files/" + fileId);
  }

  public String likesByThing(String id) {
    return call(GET, "/things/" + id + "/likes/");
  }

  public String ancestorsByThing(String id) {
    return call(GET, "/things/" + id + "/ancestors/");
  }

  public String derivaticesByThing(String id) {
    return call(GET, "/things/" + id + "/derivatices/");
  }

  public String tagsByThing(String id) {
    return call(GET, "/things/" + id + "/tags/");
  }

  public String categoryByThing(String id) {
    return call(GET, "/things/" + id + "/categories/");
  }

  public String copiesByThing(String id) {
    return call(GET, "/things/" + id + "/copies/");
  }

  public String likeThing(String id) {
    return call(POST, "/things/" + id + "/likes");
  }

  public String unlikeThing(String id) {
    return call(DELETE, "/things/" + id + "/likes");
  }

  //COPIES//
  public String copy(String id) {
    return call(GET, "/copies/" + id + "/");
  }

  public String imagesByCopy(String id) {
    return call(GET, "/copies/" + id + "/images");
  }

  public String deleteCopy(String id) {
    return call(DELETE, "/copies/" + id + "/");
  }

  //COLLECTIONS//
  public String collection(String id) {
    return call(GET, "/collections/" + id + "/");
  }

  public String thingsByCollection(String id) {
    return call(GET, "/collections/" + id + "/");
  }

  public String newCollection(String name, String description) {
    if (description == null) {
      description = "";
    }
    if (name == null || "".equals(name)) {
      throw new RuntimeException("name is not optional, http://www.thingiverse.com/developers/rest-api-reference");
    }
    //return call(POST, "/collections/");
    OAuthRequest request = new OAuthRequest(Verb.POST, "http://api.thingiverse.com/collections/");
    request.addHeader("Authorization", "Bearer " + accesTokenString);
    request.addBodyParameter("description", description);
    Response response = request.send();
    return response.getBody();
  }

  /**
   * Add thing to collection.
   * http://www.thingiverse.com/developers/rest-api-reference
   *
   * @param collectionId
   * @param thingId
   * @param description Optional. Reason for ading the Thing
   * @return response
   */
  public String addThingToCollection(String collectionId, String thingId, String description) {
    if (description == null) {
      description = "";
    }
    OAuthRequest request = new OAuthRequest(Verb.POST, "http://api.thingiverse.com/collections/" + collectionId + "/things/" + thingId);
    request.addHeader("Authorization", "Bearer " + accesTokenString);
    request.addBodyParameter("description", description);
    Response response = request.send();
    return response.getBody();
  }

  public String removeThingFromCollection(String collectionId, String thingId) {
    return call(DELETE, "/collections/" + collectionId + "/things/" + thingId);
  }

  public String removeCollection(String id) {
    return call(DELETE, "/collections/" + id);
  }
  //OTHER//
  
  public String newest(){
    return call(GET, "/newest/");
  }
  
  public String popular(){
    return call(GET, "/popular/");
  }
  
  public String featured(){
    return call(GET, "/featured/");
  }
  
  public String search(String term){
    term = term.replaceAll(" ", "+"); //basic url-encode // TODO fix this
    return call(GET, "/search/" + term + "/");
  }
  
  //CATEGORIES//
  /**
   * List all categories
   * @return list of all categories
   */
  public String categories(){
    return call(GET, "/categories/");
  }
  
  /**
   * Get details about category.
   * Category ids are normalized "slugs". For example, the id for the "Automotive" category's id would be "automotive". The "Replacement Parts" category would have an id of "replacement-parts", etc.
   * @param id
   * @return details
   */
  public String category(String id){
    return call(GET, "/categories/" + id + "/");
  }
  
  public String latestThingsByCategory(String id){
    return call(GET, "/categories/" + id + "/things");
  }
  
  public String latestThingsByTag(String id){
    return call(GET, "/tags/" + id + "/things");
  }
  
  /**
   * Get all tags
   * @return list of tags
   */
  public String tags(){
    return call(GET, "/tags/");
  }
  
  /**
   * details about a tag
   * @return details
   */
  public String tag(String id){
    return call(GET, "/tags/" + id + "/");
  }
  
}
