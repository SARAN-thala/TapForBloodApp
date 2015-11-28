package tw.tapforblood.helpers;

public class Environment {
    public static final String HOST_URI = "https://pure-garden-9285.herokuapp.com";

    public static String createUser()
    {
        return HOST_URI+ "/users";
    }
    public static String getAllRequestsForUser(String userId)
    {
        return HOST_URI+ "/blood_requests?user_id=" + userId;
    }

    public static String myRequestsForUser(String id) {
        return HOST_URI + "/users/" + id + "/requests";
    }
    public static String createBloodRequest(){
        return HOST_URI + "/blood_requests";
    }
    public static String acceptRequestByUser(String requestId, String userId)
    {
        return HOST_URI+ "/users/" + userId + "/requests/"+ requestId +"/create";
    }

    public static String getResponsesUrl(String userId, String bloodReqId) {
        return HOST_URI +"/users/" + userId + "/responses/" + bloodReqId;
    }
}
