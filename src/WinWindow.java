import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class WinWindow extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Pane root = new Pane();
        Label lbWin = new Label("Congratulations! You won!");
        root.getChildren().add(lbWin);
        lbWin.setLayoutX(25);
        lbWin.setLayoutY(25);
        Scene scene = new Scene(root, 200, 100);
        primaryStage.initModality(Modality.APPLICATION_MODAL);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void showWindow(int status)
    {
        Stage primaryStage = new Stage();
        Pane root = new Pane();
        Label lb1 = new Label();
        if (status == -1)
        {
            lb1 = new Label("Congratulations! You won!");
        }
        if (status == -2)
        {
            lb1 = new Label("You lost!");
        }
        root.getChildren().add(lb1);
        lb1.setLayoutX(25);
        lb1.setLayoutY(25);
        Scene scene = new Scene(root, 200, 100);
        primaryStage.initModality(Modality.APPLICATION_MODAL);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
