package hx.springclouds.streams.interceptors;

public class UserContextHolder {

    private static final ThreadLocal<UserContext> userContextLocal =new ThreadLocal<UserContext>();

    public static final UserContext getUserContext(){
        UserContext userContext=userContextLocal.get();
        if(userContext==null){
            userContext=createEmptyUserContext();
            userContextLocal.set(userContext);
        }
        return userContextLocal.get();

    }

    public static void setUserContext(UserContext userContext) {
        userContextLocal.set(userContext);
    }

    private static final UserContext createEmptyUserContext() {
        return new UserContext();
    }
}
