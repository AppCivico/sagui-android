package com.eokoe.sagui;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;

/**
 * @author Pedro Silva
 * @since 25/07/17
 */

public final class TestUtils {
    public static void assertOk(Observable observable) throws Exception {
        TestObserver testObserver = observable.test();
        testObserver.assertSubscribed();
        testObserver.assertNoErrors();
        testObserver.assertComplete();
        testObserver.awaitTerminalEvent();
    }

    public static void assertError(Observable observable) {
        TestObserver testObserver = observable.test();
        testObserver.assertSubscribed();
        testObserver.assertNotComplete();
        testObserver.awaitTerminalEvent();
    }

    public static void assertError(Observable observable, Throwable exception) {
        TestObserver testObserver = observable.test();
        testObserver.assertSubscribed();
        testObserver.assertNotComplete();
        testObserver.assertError(exception);
        testObserver.awaitTerminalEvent();
    }
}
