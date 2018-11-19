package explore;

import org.graphstream.algorithm.generator.Generator;
import org.graphstream.algorithm.generator.RandomGenerator;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Controller implements Runnable {
    private Graph graph;
    private Set<Robot> robots;
    private Node startNode;

    public Controller(int r){

        //graph setup. should contain at leaast one node
        setGraph2();
        startNode = graph.getNode(0);
        startNode.addAttribute("ui.style", "size: 20;");

        //robot setup
        this.robots = new HashSet<>();
        for (int i =0; i < r; i++) {
            if (graph.getNodeCount()>0)
                robots.add(new Robot(startNode));
        }
    }

    @Override
    public void run(){

        startNode.setAttribute("ui.label", getNodeLabel(startNode));
        graph.display();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException ex) {
        }

        while (!isFinished()){

            tick();


            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
            }
        }
    }

    private void tick () {
        //nodes with robots
        Set<Node> currentNodes = robots.stream()
                .map(Robot::getCurrentNode)
                .collect(Collectors.toSet());
        currentNodes.forEach(n -> n.removeAttribute("ui.label"));

        //robots choose new edge to move on
        boolean graphdone=edgesFinished();
        robots.stream()
                .filter(r -> !graphdone || (r.getCurrentNode() != startNode))
                .forEach(Robot::chooseDirection);

        //moves
        currentNodes.forEach(n -> {
            ArrayList<Visit> chalks = (ArrayList<Visit>) n.getAttribute("chalks");
            chalks.stream()
                    .filter(Visit::isNotFinished)
                    .forEach(v -> {
                        v.robot.move(v.to);
                        v.finish();
                    });
        });

        //labels
        robots.stream()
                .map(Robot::getCurrentNode)
                .distinct()
                .forEach(n -> n.setAttribute("ui.label", getNodeLabel(n)));
    };

    private boolean isFinished(){
        boolean robotsFinished = robots.stream().allMatch(r -> r.getCurrentNode() == startNode);
        return  robotsFinished && edgesFinished();
    }

    private boolean edgesFinished () {
        return graph.getEdgeSet().stream().allMatch(e -> e.hasAttribute("finished"));
    }

    private String getNodeLabel(Node node){
        return robots.stream()
                .filter(r -> r.getCurrentNode().equals(node))
                .map(Robot::toString)
                .collect(Collectors.joining(","));
    }

    private void setGraph2 (){
        graph = new SingleGraph("Random");
        Generator gen = new RandomGenerator(4, false, false);
        gen.addSink(graph);
        gen.begin();
        for(int i=0; i<6; i++)
            gen.nextEvents();
        gen.end();

        //add chalks
        graph.getNodeSet().stream().forEach(n -> n.addAttribute("chalks", new ArrayList<Visit>()));
        //set edges to gray
        graph.getEdgeSet().forEach(e->e.addAttribute("ui.style", "fill-color: rgb(220,220,220);"));

    }

    private void setGraph1 (){
        graph = new SingleGraph("Tutorial");

        graph.addNode("A" );
        graph.addNode("B" );
        graph.addNode("C" );
        graph.addNode("D" );
        graph.addNode("E" );
        graph.addNode("F" );
        graph.addEdge("AB", "A", "B" ,false);
        graph.addEdge("BC", "B", "C" ,false);
        graph.addEdge("CA", "C", "A" ,false);
        graph.addEdge("CD", "C", "D" ,false);
        graph.addEdge("DE", "D", "E" ,false);
        graph.addEdge("DF", "D", "F" ,false);

        //add chalks
        graph.getNodeSet().stream().forEach(n -> n.addAttribute("chalks", new ArrayList<Visit>()));
        //set edges to gray
        graph.getEdgeSet().forEach(e->e.addAttribute("ui.style", "fill-color: rgb(220,220,220);"));


    }

}
