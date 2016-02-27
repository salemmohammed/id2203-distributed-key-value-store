package system.coordination.meld.event;

import se.sics.kompics.timer.SchedulePeriodicTimeout;
import se.sics.kompics.timer.ScheduleTimeout;
import se.sics.kompics.timer.Timeout;

/**
 * Created by Robin on 2016-02-27.
 */
public class MELDTimeout extends Timeout{
    public MELDTimeout(SchedulePeriodicTimeout request) {
        super(request);
    }
}
