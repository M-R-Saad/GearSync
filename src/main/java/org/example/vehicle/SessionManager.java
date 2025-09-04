package org.example.vehicle;

public class SessionManager {

    private static Integer currentUserId;

    private static String currentUserType;

    public static String getCurrentUserType() {
        return currentUserType;
    }

    public static void setCurrentUserType(String userType) {
        currentUserType = userType;
    }

    public static void setCurrentUserId(int userID) {
        currentUserId = userID;
    }

    public static int getCurrentUserId() {
        return currentUserId;
    }

    public static void clearSession() {
        currentUserId = null;
        currentUserType = null;
    }
}
