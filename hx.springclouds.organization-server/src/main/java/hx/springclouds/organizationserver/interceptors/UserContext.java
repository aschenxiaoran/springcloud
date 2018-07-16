package hx.springclouds.organizationserver.interceptors;

import org.springframework.stereotype.Component;

@Component
public class UserContext {


    //region private thread locals

    private static final ThreadLocal<String> correlationIdLocal =new ThreadLocal<String>();
    private static final ThreadLocal<String> authTokenLocal =new ThreadLocal<String>();
    private static final ThreadLocal<String> userIdLocal =new ThreadLocal<String>();
    private static final ThreadLocal<String> orgIdLocal =new ThreadLocal<String>();

    //endregion

    //region public static variables

    public static final String CORRELATION_ID="tmx-correlation-id";
    public static final String AUTH_TOKEN="Authorization";
    public static final String USER_ID="tmx-user-id";
    public static final String ORG_ID="tmx-org-id";

    //endregion

    //region public methods

    public static String getCorrelationId(){
        return correlationIdLocal.get();
    }

    public static void setCorrelationId(String correlationId){
        correlationIdLocal.set(correlationId);
    }

    public static String getAuthToken(){
        return authTokenLocal.get();
    }

    public static void setAuthToken(String authToken){
        authTokenLocal.set(authToken);
    }

    public static String getUserId(){
        return userIdLocal.get();
    }

    public static void setUserId(String userId){
        userIdLocal.set(userId);
    }

    public static String getOrgId(){
        return orgIdLocal.get();
    }

    public static void setOrgId(String orgId){
        orgIdLocal.set(orgId);
    }

    //endregion
}
