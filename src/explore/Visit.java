package explore;

import lombok.Getter;
import org.graphstream.graph.Edge;
import java.time.Instant;

@Getter
public class Visit {
    public final Robot robot;
    public final Edge from;
    public final boolean original;
    public final Edge to;
    private Instant when;

    public Visit(Robot robot, Edge from, Edge to, boolean original){
        this.robot = robot;
        this.from = from;
        this.to = to;
        this.original = original;
    }

    public Instant getWhen(){
        return when;
    }

    public boolean isNotFinished(){
        return when == null;
    }

    public void finish() {
        this.when = Instant.now();
    }

}
