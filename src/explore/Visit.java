package explore;

import lombok.Getter;
import lombok.Setter;
import org.graphstream.graph.Edge;
import java.time.Instant;

@Getter
@Setter
public class Visit {
    public final Robot robot;
    public final Edge from;
    public final boolean original;
    private Edge to;
    private Instant when;

    public Visit(Robot robot, Edge from, boolean original){
        this.robot = robot;
        this.from = from;
        this.original = original;
    }

    public boolean isNotFinished(){
        return when == null;
    }

    public void finish() {
        this.when = Instant.now();
    }

}
