import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application{

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        // Creating a new GUI
        GUI gui = new GUI();
        Scene scene = new Scene(gui, 1920, 600);
        primaryStage.setTitle("IT3708 - Assignment 2");
        primaryStage.setScene(scene);
        primaryStage.show();

        MOOA mooa = new MOOA(
                gui,
                "./Test Images Project 2/86016/Test image.jpg",
                50,
                20,
                0.2,
                0.7,
                100,
                1,
                50,
                0.5,
                0.5,
                false);

        Thread mooaThread = new Thread(() -> {
            Solution[] solutions = mooa.iterate();
        });
        mooaThread.start();
    }
}
