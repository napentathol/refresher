package us.sodiumlabs.refresher;

import org.junit.Test;

import static org.junit.Assert.*;

public class RefresherTest {

    private static final class RefresherTestClass {
        boolean isClosed = true;
        int instantiationCount = 0;

        RefresherTestClass restart() {
            isClosed = false;
            instantiationCount++;
            return this;
        }

        void close() {
            isClosed = true;
        }
    }

    @Test
    public void testGetDoesNotIncreaseInstantiationCount() {
        final RefresherTestClass test = new RefresherTestClass();
        final Refresher<RefresherTestClass> testClassRefresher = new Refresher<>(test::restart, RefresherTestClass::close);

        verifyClosed(0, test);
        verifyResult(1, test, testClassRefresher.get());
        verifyResult(1, test, testClassRefresher.get());
        verifyResult(1, test, testClassRefresher.get());
        verifyResult(1, test, testClassRefresher.get());
        verifyResult(1, test, testClassRefresher.get());
    }

    @Test
    public void testRefreshDoesIncreaseInstantiationCount() {
        final RefresherTestClass test = new RefresherTestClass();
        final Refresher<RefresherTestClass> testClassRefresher = new Refresher<>(test::restart, RefresherTestClass::close);

        verifyClosed(0, test);
        verifyResult(1, test, testClassRefresher.refresh());
        verifyResult(2, test, testClassRefresher.refresh());
        verifyResult(3, test, testClassRefresher.refresh());
        verifyResult(4, test, testClassRefresher.refresh());
        verifyResult(5, test, testClassRefresher.refresh());
    }

    @Test
    public void testClosedWithGet()
    {
        final RefresherTestClass test = new RefresherTestClass();
        final Refresher<RefresherTestClass> testClassRefresher = new Refresher<>(test::restart, RefresherTestClass::close);

        verifyClosed(0, test);
        verifyResult(1, test, testClassRefresher.get());
        verifyResult(1, test, testClassRefresher.get());
        testClassRefresher.close();
        verifyClosed(1, test);
        verifyResult(2, test, testClassRefresher.get());
        testClassRefresher.close();
        verifyClosed(2, test);
        verifyResult(3, test, testClassRefresher.get());
        verifyResult(3, test, testClassRefresher.get());
    }

    @Test
    public void testClosedWithRefresh()
    {
        final RefresherTestClass test = new RefresherTestClass();
        final Refresher<RefresherTestClass> testClassRefresher = new Refresher<>(test::restart, RefresherTestClass::close);

        verifyClosed(0, test);
        verifyResult(1, test, testClassRefresher.refresh());
        verifyResult(2, test, testClassRefresher.refresh());
        testClassRefresher.close();
        verifyClosed(2, test);
        verifyResult(3, test, testClassRefresher.refresh());
        testClassRefresher.close();
        verifyClosed(3, test);
        verifyResult(4, test, testClassRefresher.refresh());
        verifyResult(5, test, testClassRefresher.refresh());

    }

    @Test
    public void testCloseBeforeOpen()
    {
        final RefresherTestClass test = new RefresherTestClass();
        final Refresher<RefresherTestClass> testClassRefresher = new Refresher<>(test::restart, RefresherTestClass::close);

        verifyClosed(0, test);
        testClassRefresher.close();
        verifyClosed(0, test);

        verifyResult(1, test, testClassRefresher.get());
        verifyResult(1, test, testClassRefresher.get());
        verifyResult(1, test, testClassRefresher.get());
        verifyResult(1, test, testClassRefresher.get());
        verifyResult(1, test, testClassRefresher.get());
    }

    @Test
    public void testTheRinger()
    {
        final RefresherTestClass test = new RefresherTestClass();
        final Refresher<RefresherTestClass> testClassRefresher = new Refresher<>(test::restart, RefresherTestClass::close);

        verifyClosed(0, test);

        // 001
        verifyClosed(0, test);
        verifyResult(1, test, testClassRefresher.refresh());
        verifyResult(2, test, testClassRefresher.refresh());
        testClassRefresher.close();
        verifyClosed(2, test);
        verifyResult(3, test, testClassRefresher.get());
        testClassRefresher.close();
        verifyClosed(3, test);
        verifyResult(4, test, testClassRefresher.get());
        verifyResult(4, test, testClassRefresher.get());

        // 010
        verifyResult(4, test, testClassRefresher.get());
        verifyResult(4, test, testClassRefresher.get());
        testClassRefresher.close();
        verifyClosed(4, test);
        verifyResult(5, test, testClassRefresher.refresh());
        testClassRefresher.close();
        verifyClosed(5, test);
        verifyResult(6, test, testClassRefresher.get());
        verifyResult(6, test, testClassRefresher.get());

        // 011
        verifyResult(7, test, testClassRefresher.refresh());
        verifyResult(8, test, testClassRefresher.refresh());
        testClassRefresher.close();
        verifyClosed(8, test);
        verifyResult(9, test, testClassRefresher.refresh());
        testClassRefresher.close();
        verifyClosed(9, test);
        verifyResult(10, test, testClassRefresher.get());
        verifyResult(10, test, testClassRefresher.get());

        // 100
        verifyResult(10, test, testClassRefresher.get());
        verifyResult(10, test, testClassRefresher.get());
        testClassRefresher.close();
        verifyClosed(10, test);
        verifyResult(11, test, testClassRefresher.get());
        testClassRefresher.close();
        verifyClosed(11, test);
        verifyResult(12, test, testClassRefresher.refresh());
        verifyResult(13, test, testClassRefresher.refresh());

        // 101
        verifyResult(14, test, testClassRefresher.refresh());
        verifyResult(15, test, testClassRefresher.refresh());
        testClassRefresher.close();
        verifyClosed(15, test);
        verifyResult(16, test, testClassRefresher.get());
        testClassRefresher.close();
        verifyClosed(16, test);
        verifyResult(17, test, testClassRefresher.refresh());
        verifyResult(18, test, testClassRefresher.refresh());

        // 110
        verifyResult(18, test, testClassRefresher.get());
        verifyResult(18, test, testClassRefresher.get());
        testClassRefresher.close();
        verifyClosed(18, test);
        verifyResult(19, test, testClassRefresher.refresh());
        testClassRefresher.close();
        verifyClosed(19, test);
        verifyResult(20, test, testClassRefresher.refresh());
        verifyResult(21, test, testClassRefresher.refresh());
    }

    private void verifyResult(final int expectedCount, final RefresherTestClass expected, final RefresherTestClass result) {
        assertSame(expected, result);
        assertEquals(expectedCount, result.instantiationCount);
        assertFalse(result.isClosed);
    }

    private void verifyClosed(final int expectedCount, final RefresherTestClass testClass) {
        assertEquals(expectedCount, testClass.instantiationCount);
        assertTrue(testClass.isClosed);
    }

}