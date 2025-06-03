package server;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Keeps the random 16-byte challenge for every user that is in the middle
 * of the login flow.  Key = handleLower (always lower-case).
 */
public final class SessionManager {
    private static final ConcurrentHashMap<String, byte[]> pendingChallenges =
            new ConcurrentHashMap<>();

    private SessionManager() {} // static-only

    public static void storePendingChallenge(String handleLower, byte[] challenge) {
        pendingChallenges.put(handleLower, challenge);
    }

    public static byte[] getPendingChallenge(String handleLower) {
        return pendingChallenges.get(handleLower);
    }

    public static void removePendingChallenge(String handleLower) {
        pendingChallenges.remove(handleLower);
    }
}

