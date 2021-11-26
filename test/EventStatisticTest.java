import clock.SettableClock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import statistic.EventsStatistic;
import statistic.EventsStatisticImpl;

import java.time.Instant;

public class EventStatisticTest {

    private final SettableClock clock = new SettableClock(Instant.now());
    private final EventsStatistic stat = new EventsStatisticImpl(clock);

    private final int MORE_THAN_HOUR_SECONDS = 3601;

    @Before
    public void before() {
        clock.setNow(Instant.now());
    }

    @Test
    public void emptyTest() {
        Assert.assertEquals(0.0, stat.getEventStatisticByName("1"), 0);
        Assert.assertEquals(0.0, stat.getAllEventStatistic(), 0);
    }

    @Test
    public void simpleByNameTest() {
        stat.incEvent("1");
        stat.incEvent("1");
        stat.incEvent("1");

        double rpm = stat.getEventStatisticByName("1");
        Assert.assertEquals(3 / 60.0, rpm, 0);
    }

    @Test
    public void simpleAllEventsTest() {
        stat.incEvent("1");
        stat.incEvent("2");
        stat.incEvent("3");
        stat.incEvent("45");

        double rpm = stat.getAllEventStatistic();
        Assert.assertEquals(4 / 60.0, rpm, 0);
    }

    @Test
    public void simpleByNameExpiresTest() {
        stat.incEvent("1");
        addTime(MORE_THAN_HOUR_SECONDS);
        stat.incEvent("1");
        stat.incEvent("1");

        double rpm = stat.getEventStatisticByName("1");
        Assert.assertEquals(2 / 60.0, rpm, 0);
    }

    @Test
    public void simpleAllExpiresTest() {
        stat.incEvent("1");
        stat.incEvent("2");
        stat.incEvent("2");
        addTime(MORE_THAN_HOUR_SECONDS);
        stat.incEvent("1");
        stat.incEvent("3");
        stat.incEvent("40");
        stat.incEvent("hello");

        double rpm = stat.getAllEventStatistic();
        Assert.assertEquals(4 / 60.0, rpm, 0);
    }

    @Test
    public void doesNotRemoveUntilHourPassed() {
        stat.incEvent("1");
        stat.incEvent("2");
        stat.incEvent("2");
        addTime(30 * 60);
        double halfRpm1 = stat.getEventStatisticByName("1");
        double halfRpm2 = stat.getEventStatisticByName("2");
        Assert.assertEquals(1 / 60.0, halfRpm1, 0);
        Assert.assertEquals(2 / 60.0, halfRpm2, 0);
        stat.incEvent("2");
        stat.incEvent("3");
        stat.incEvent("3");
        stat.incEvent("4");
        addTime(30 * 60 + 1);
        double rpm1 = stat.getEventStatisticByName("1");
        double rpm2 = stat.getEventStatisticByName("2");
        double rpm3 = stat.getEventStatisticByName("3");
        double rpm4 = stat.getEventStatisticByName("4");
        Assert.assertEquals(0, rpm1, 0);
        Assert.assertEquals(1 / 60.0, rpm2, 0);
        Assert.assertEquals(2 / 60.0, rpm3, 0);
        Assert.assertEquals(1 / 60.0, rpm4, 0);
    }

    private void addTime(int seconds) {
        Instant now = clock.now();
        clock.setNow(now.plusSeconds(seconds));
    }
}
