package explore;

public class Main {

    public static void main(String[] args) {

        Controller controller = new Controller(2);
        Gui frame = new Gui(controller);
        frame.setVisible(true);
        Thread t = new Thread(controller);
        t.run();
    }

}
