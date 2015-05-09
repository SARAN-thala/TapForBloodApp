package tw.tapforblood.helpers;

/**
 * Created by rajeswari on 09/05/15.
 */
public class Environment {
    public static final String HOST_URI = "https://pure-garden-9285.herokuapp.com";

    public static String createUser()
    {
        return HOST_URI+ "/users";
    }

    public String getFindStudentByIdURL(Integer id)
    {
        return HOST_URI+ "/student/"+id;
    }

}
