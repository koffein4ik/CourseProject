import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;

public class GameWindow extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        int offset = 16;
        int height = 1;
        int width = 1;
        ArrayList<ObjToTransfer> objectsOnFiled = new ArrayList<>();
        GameField mainField = new GameField(1, 1);
        Pane root = new Pane();
        Scene scene = new Scene(root, 320, 160);
        File f1 = new File("src/Pictures/wall.png");
        Image wall = new Image(f1.toURI().toString());
        File f2 = new File("src/Pictures/grass.png");
        Image grass = new Image(f2.toURI().toString());
        File f3 = new File("src/Pictures/pers1.png");
        Image pers1 = new Image(f3.toURI().toString());
        Player pl1 = new Player("John", 1);
        ArrayList<javafx.scene.Node> addedObjects = new ArrayList<>();
        String levelPath = "";
        try
        {
            Socket clientSocket = new Socket("127.0.0.1", 11000);
            //DataInputStream in = new DataInputStream(clientSocket.getInputStream());
            //DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
            out.writeUTF("1");
            out.writeUTF("GetLevelPath");
            out.flush();
            levelPath = in.readUTF();
            FileInputStream fin = new FileInputStream(levelPath);
            byte[] buffer = new byte[fin.available()];
            fin.read(buffer, 0, fin.available());
            int i = 0;
            String val1 = "";
            while (buffer[i] != 32)
            {
                val1 += (char)buffer[i];
                i++;
            }
            i++;
            height = Integer.parseInt(val1);
            val1 = "";
            while (buffer[i] != 13)
            {
                val1 += (char)buffer[i];
                i++;
            }
            width = Integer.parseInt(val1);
            fin.close();
            height *= offset;
            width *= offset;
            mainField = new GameField(height, width);
            mainField.fromFile(levelPath, offset);
        }
        catch (Exception ex)
        {
            System.out.println(ex.toString());
        }
        for (int i = 0; i < height; i++)
        {
            for (int j = 0; j < width; j++)
            {
                switch (mainField.getField()[i][j]) {
                    case 10:
                    {
                        ImageView blockIv = new ImageView(wall);
                        blockIv.setLayoutY(i /* offset*/);
                        blockIv.setLayoutX(j /* offset*/);
                        addedObjects.add(blockIv);
                        break;
                    }
                    case 101:
                    {
                        ImageView grassIv = new ImageView(grass);
                        grassIv.setLayoutY(i /* offset*/);
                        grassIv.setLayoutX(j /* offset*/);
                        addedObjects.add(grassIv);
                        break;
                    }
                    case 1:
                    {
                        ImageView grassIv = new ImageView(grass);
                        grassIv.setLayoutY(i /* offset*/);
                        grassIv.setLayoutX(j /* offset*/);
                        addedObjects.add(grassIv);
                        ImageView person1 = new ImageView(pers1);
                        person1.setLayoutY(i /* offset*/);
                        person1.setLayoutX(j /* offset*/);
                        addedObjects.add(person1);
                    }
                }
            }
        }
        myThread Jthread = new myThread("Jthread", mainField, objectsOnFiled);
        Jthread.start();
        scene.setOnKeyTyped(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent ke) {
                switch (ke.getCharacter())
                {
                    case "w": pl1.sendCommand("UP"); break;
                    case "a": pl1.sendCommand("LEFT"); break;
                    case "s": pl1.sendCommand("DOWN"); break;
                    case "d": pl1.sendCommand("RIGHT"); break;
                }
            }
        });
        root.getChildren().addAll(addedObjects);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}

class mapThread extends Thread
{
    ArrayList<javafx.scene.Node> addedObjects;
    public mapThread(String name, ArrayList<javafx.scene.Node> mapObjects)
    {
        super(name);
        this.addedObjects = mapObjects;
    }
}

class myThread extends Thread
{
    GameField mainfield;
    ArrayList<ObjToTransfer> objectsOnField = new ArrayList<>();
    public myThread(String name, GameField field, ArrayList<ObjToTransfer> objOnField)
    {
        super(name);
        this.mainfield = field;
        this.objectsOnField = objOnField;
    }

    public void run()
    {
        try {
            while(true) {
                Socket clientSocket = new Socket("127.0.0.1", 11000);
                ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream objInput = new ObjectInputStream(clientSocket.getInputStream());
                //DataInputStream in = new DataInputStream(clientSocket.getInputStream());
                //DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
                out.writeUTF("1"); // Изменить на playername
                out.flush();
                out.writeUTF("GetField");
                out.flush();
                /*byte gameField[] = new byte[10 * 10];
                in.read(gameField);
                int counter = 0;
                for (int i = 0; i < 10; i++)
                {
                    for (int j = 0; j < 10; j++)
                    {
                        System.out.print(gameField[counter] + " ");
                        mainfield.setFieldDot(i, j, gameField[counter]);
                        counter++;
                    }
                    System.out.println();
                }
                System.out.println("----------------------------");*/
                this.objectsOnField.clear();
                int size = objInput.readInt();
                System.out.println(size);
                for(int i = 0; i < size; i++)
                {
                     ObjToTransfer obj1 = (ObjToTransfer)objInput.readObject();
                     System.out.println(obj1.id);
                     System.out.println(obj1.x);
                     System.out.println(obj1.y);
                     this.objectsOnField.add(obj1);
                }
                Thread.sleep(1000);
            }
        }
        catch (Exception ex)
        {
            System.out.println(ex.toString());
        }
    }
}
