package clock;

import java.time.Instant;

public class SettableClock implements Clock{

    private Instant instant;

    public SettableClock(Instant instant) {
        this.instant = instant;
    }

    public void setNow(Instant now) {
        instant = now;
    }

    @Override
    public Instant now() {
        return instant;
    }
}
