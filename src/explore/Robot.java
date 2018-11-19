package explore;

import lombok.Getter;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;

import java.awt.*;
import java.util.*;

import static java.util.stream.Collectors.toSet;

@Getter
public class Robot {
    protected static int idc;
    private final int id;
    private Node currentNode;
    private Edge fromEdge;

    public Robot(Node startNode){
        id = ++idc;
        this.currentNode = startNode;
    }

    public void move(Edge on){
        currentNode = on.getOpposite(currentNode);
        fromEdge = on;
    }

    public void chooseDirection(){
        //chalks should be initialized for all nodes
        ArrayList<Visit> chalks = currentNode.getAttribute("chalks");
        
        Optional<Edge> originalEdge = chalks.stream()
                .filter(v -> v.robot == this && v.isOriginal() && v.from != null)
                .map(v -> v.from)
                .findFirst();
        
        final Optional<Visit> lastVisit = chalks.stream()
                .filter(v -> v.robot == this)
                .sorted(Comparator.comparing(Visit::getWhen).reversed())
                .findFirst();

        //if the robot has been here before
        // but now it came on a different edge then the one it used to left v the last time
        // then the edge it comes from should be marked finished, and it should go back
        if (lastVisit.isPresent() && (lastVisit.get().to != fromEdge)) {
            fromEdge.addAttribute("finished", true);
            fromEdge.addAttribute("ui.style", "fill-color: rgb(205,92,92);");
            chalks.add(new Visit(this, fromEdge, fromEdge, false));
            return;
        }
        
        //if this robot has been here before, comes back from a component on the same edge
        if (lastVisit.isPresent()) {
            fromEdge.addAttribute("finished", true);
            fromEdge.addAttribute("ui.style", "fill-color: rgb(205,92,92);");
        }
        // else: fromEdge is the original entry for this robot at this node
        else {
            if (fromEdge != null) originalEdge = Optional.of(fromEdge);
        }

        //if there's an edge that is not finished and not original entry to any robots
        //(fromEdge is also an original entry if this robot never visited this vertex before)
        //prefer the least used edge (by all robots)
        Set<Edge> originalEdges = chalks.stream()
                .filter(v -> v.isOriginal() && v.from != null)
                .map(v -> v.from).collect(toSet());
        if (!lastVisit.isPresent() && fromEdge != null) { originalEdges.add(fromEdge); }

        Optional<Edge> to = currentNode.getEdgeSet().stream()
                .filter(e -> !e.hasAttribute("finished") && !originalEdges.contains(e))
                .sorted(Comparator.comparing(e -> chalks.stream().map(v -> v.to).filter(ed -> ed.equals(e)).count()))
                .findFirst();

        if (to.isPresent()){
            chalks.add(new Visit(this, fromEdge, to.get(), !lastVisit.isPresent()));
            return;
        }

        //otherwise - no unused edge, go back, using the original entry if exists
        if (originalEdge.isPresent()){
            chalks.add(new Visit(this, fromEdge, originalEdge.get(), !lastVisit.isPresent()));
        } else {
            chalks.add(new Visit(this, fromEdge, fromEdge, !lastVisit.isPresent()));
        }

    }

    @Override
    public String toString() {
        return "Robot "+id;
    }
}
