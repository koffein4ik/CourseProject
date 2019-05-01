import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.File;

public class GameWindow extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Pane root = new Pane();
        Scene scene = new Scene(root, 320, 160);
        File f1 = new File("src/Pictures/wall.png");
        Image wall = new Image(f1.toURI().toString());
        ImageView iv1 = new ImageView(wall);
        Player pl1 = new Player("John", 1);
        GameField mainField = new GameField(10, 20);
        PlayerCoord pl1Coord = new PlayerCoord();
        pl1Coord.getPlayerCoord(mainField, 1);
        scene.setOnKeyTyped(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent ke) {
                switch (ke.getCharacter())
                {
                    case "w": pl1.sendCommand("UP"); break;
                    case "a": pl1.sendCommand("LEFT"); break;
                    case "s": pl1.sendCommand("DOWN"); break;
                    case "d": pl1.sendCommand("RIGHT"); break;
                }
                //pl1Coord = pl1Coord.getPlayerCoord(mainField, 1);
                System.out.println(pl1Coord.x);
                System.out.println(pl1Coord.y);
            }
        });
        //iv1.setTranslateX(pl1Coord.x);
        //iv1.setTranslateY(pl1Coord.y);
        root.getChildren().add(iv1);
        root.getChildren().get(0).setTranslateX(100);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void updateField(GameField oldField, GameField newField, PlayerCoord pl1Coord)
    {
        oldField = newField;
        pl1Coord = pl1Coord.getPlayerCoord(oldField, 1);
    }
}
