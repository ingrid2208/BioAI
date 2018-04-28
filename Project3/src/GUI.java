import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

/**
 * This class is dedicated to all GUI related tasks for the JSSP
 */
class GUI extends BorderPane {

    private final Stage primaryStage;

    private final Color[] colors = new Color[]{Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.PURPLE, Color.ORANGE, Color.DARKBLUE, Color.PINK, Color.LIGHTGRAY, Color.DARKCYAN};

    GUI(Stage primaryStage, int jobCount) {
        super();
        this.primaryStage = primaryStage;
        final Scene scene = new Scene(this, 840, 100 + jobCount * 50);
        primaryStage.setScene(scene);
        primaryStage.setTitle("JSSP - Job Shop Scheduling Problem");
        primaryStage.show();
    }

    void createGantt(Solution solution, int optimal) {

        final int makespan = solution.getMakespan();
        final int[][][] schedule = solution.getSchedule();
        final int width = 800;
        final double widthTranslate = (double) width / makespan;
        final int height = schedule.length * 50;

//        final Stage stage = new Stage();
        final Pane pane = new Pane();
//        final ScrollPane scrollPane = new ScrollPane(pane);
//        final Scene scene = new Scene(scrollPane, width + 15, height + 15);
//        stage.setScene(scene);
//        stage.setTitle(title);

        pane.setMaxSize(width, height);
        pane.setMinSize(width, height);

        for (int i = 0; i < schedule.length; i ++) {
            final int y = i * 50;
            for (int j = 0; j < schedule[0].length; j ++) {
                final String name = String.valueOf(j);
                final Task task = new Task(name, schedule[i][j][1] * widthTranslate, colors[j % colors.length]);
                task.setTranslateX(schedule[i][j][0] * widthTranslate);
                task.setTranslateY(y);
                pane.getChildren().add(task);
            }
        }

        setCenter(pane);
        pane.setStyle("-fx-border-color: gray");
        BorderPane.setAlignment(pane, Pos.CENTER);
        String rating = String.format("%.2f%%", (double) optimal / solution.getMakespan() * 100);
        final Text text = new Text("Current makespan: " + solution.getMakespan() + ", optimal: " + optimal + ", rating: " + rating);
        setBottom(text);
        BorderPane.setAlignment(text, Pos.CENTER);
//        stage.show();
    }

    private class Task extends StackPane {

        private Task(String text, double width, Color color) {
            super();
            final Rectangle rectangle = new Rectangle(width, 50);
            rectangle.setFill(color);
            getChildren().addAll(rectangle, new Text(text));
        }

    }

}