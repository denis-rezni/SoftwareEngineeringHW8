package statistic;

import clock.Clock;

import java.io.PrintStream;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public class EventsStatisticImpl implements EventsStatistic {

    private final Clock clock;
    private final PrintStream out;

    private final Map<String, Queue<Instant>> map = new HashMap<>();


    public EventsStatisticImpl(Clock clock, PrintStream out) {
        this.clock = clock;
        this.out = out;
    }

    public EventsStatisticImpl(Clock clock) {
        this(clock, System.out);
    }

    @Override
    public void incEvent(String name) {
        map.putIfAbsent(name, new ArrayDeque<>());
        map.get(name).add(clock.now());
    }

    @Override
    public double getEventStatisticByName(String name) {
        Instant now = clock.now();
        Queue<Instant> queue = map.get(name);
        if (queue == null) return 0;
        removeOldEvents(queue, now);
        return queue.size() / 60.0;
    }

    private void removeOldEvents(Queue<Instant> queue, Instant now) {
        while (!queue.isEmpty() && queue.peek().plus(1, ChronoUnit.HOURS).isBefore(now)) {
            queue.poll();
        }
    }

    @Override
    public double getAllEventStatistic() {
        int count = 0;
        Instant now = clock.now();
        for (Queue<Instant> queue : map.values()) {
            removeOldEvents(queue, now);
            count += queue.size();
        }
        return count / 60.0;
    }

    @Override
    public void printStatistic() {
        for (String eventName : map.keySet()) {
            double stat = getEventStatisticByName(eventName);
            out.println(eventName + " : " + stat);
        }
    }
}
