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
    private Optional<Visit> lastVisit;
    private Visit currentVisit;

    public Robot(Node startNode){
        id = ++idc;
        this.currentNode = startNode;
        evaluate();
    }

    public void move(){
        currentNode = currentVisit.getTo().getOpposite(currentNode);
        fromEdge = currentVisit.getTo();
    }

    /**
     * First part of algorithm, should run right after moving
     */
    public void evaluate() {

        ArrayList<Visit> chalks = currentNode.getAttribute("chalks");

        lastVisit = chalks.stream()
                .filter(v -> v.robot == this)
                .sorted(Comparator.comparing(Visit::getWhen).reversed())
                .findFirst();

        //if the robot has been here before
        if (lastVisit.isPresent()) {
            fromEdge.addAttribute("finished", true);
            fromEdge.addAttribute("ui.style", "fill-color: rgb(205,92,92);");
        }

        currentVisit = new Visit (this, fromEdge, !lastVisit.isPresent());
        chalks.add(currentVisit);
    }

    /**
     * Second part of algorithm, should run after the pause, before moving
     */
    public void chooseDirection(){

        ArrayList<Visit> chalks = currentNode.getAttribute("chalks");

        //if the robot has been here before
        //but now it came on a different edge then the one it used to left v the last time, it should go back
        if (lastVisit.isPresent() && (lastVisit.get().getTo() != fromEdge)) {
            currentVisit.setTo(fromEdge);
            return;
        }

        //original entry edges for all robots
        Set<Edge> originalEdges = chalks.stream()
                .filter(v -> v.isOriginal() && v.from != null)
                .map(v -> v.from).collect(toSet());

        //original entry edge for this robot
        Optional<Edge> originalEdge = chalks.stream()
                .filter(v -> v.robot == this && v.isOriginal() && v.from != null)
                .map(v -> v.from)
                .findFirst();

        //if there's an edge that is not finished and not original entry to any robots
        //prefer the least used edge (by all robots)
        Optional<Edge> to = currentNode.getEdgeSet().stream()
                .filter(e -> !e.hasAttribute("finished") && !originalEdges.contains(e))
                .sorted(Comparator.comparing(e -> chalks.stream().filter(v -> v.getTo() != null && v.getTo().equals(e)).count()))
                .findFirst();

        if (to.isPresent()){
            currentVisit.setTo(to.get());
            return;
        }

        //otherwise - no unused edge, go back, using the original entry if exists
        if (originalEdge.isPresent()){
            currentVisit.setTo(originalEdge.get());
        } else {
            //btw this can only happen at the very first step at startnode
            currentVisit.setTo(fromEdge);
        }

    }

    @Override
    public String toString() {
        return "Robot "+id;
    }
}
