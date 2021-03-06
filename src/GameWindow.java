import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;


public class GameWindow extends Application {

    public static int gl_height;
    public static int gl_width;
    public static GameField gl_mainfield;
    public static ArrayList<ObjToTransfer> gl_objectsOnField = new ArrayList<>();
    public static ArrayList<javafx.scene.Node> animations = new ArrayList<>();
    public static ArrayList<Animation> anim = new ArrayList<>();
    public static int animationOffset = 0;
    public static Player pl1;
    static AtomicInteger lastRecievedCommand = new AtomicInteger(0);
    static Boolean messageDisplayed;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        int offset = 32;
        int height = 1;
        int width = 1;
        byte playerID;
        ArrayList<ObjToTransfer> objectsOnFiled = new ArrayList<>();
        GameField mainField = new GameField(1, 1);
        Pane root = new Pane();
        Scene scene = new Scene(root, 320, 160);
        File f1 = new File("src/Pictures/wall_32px.png");
        Image wall = new Image(f1.toURI().toString());
        File f2 = new File("src/Pictures/plitka.png");
        Image grass = new Image(f2.toURI().toString());
        File f3 = new File("src/Pictures/pers1_32px.png");
        Image pers1 = new Image(f3.toURI().toString());
        File f6 = new File("src/Pictures/monster.png");
        File f7 = new File("src/Pictures/chelbosy1.png");
        File f10 = new File("src/Pictures/chelbosy2.png");
        File f11 = new File("src/Pictures/chelbosy3.png");
        File f8 = new File("src/Pictures/slime.png");
        Image slime = new Image(f8.toURI().toString());
        Image chelbosy = new Image(f7.toURI().toString());
        Image chelbosy2 = new Image(f10.toURI().toString());
        Image chelbosy3 = new Image(f11.toURI().toString());
        Image monster = new Image(f6.toURI().toString());
        ArrayList<javafx.scene.Node> addedObjects = new ArrayList<>();
        ArrayList<javafx.scene.Node> fieldMovingObjects = new ArrayList<>();
        String levelPath = "";
        try
        {
            Socket clientSocket = new Socket("127.0.0.1", 11000);
            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
            //out.writeUTF("1");
            out.writeByte(1);
            out.writeUTF("GetLevelPath");
            out.flush();
            levelPath = in.readUTF();
            playerID = in.readByte();
            System.out.println(playerID);
            pl1 = new Player("Player", 1, playerID);
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
            gl_height = height;
            gl_width = width;
        }
        catch (Exception ex)
        {
            System.out.println(ex.toString());
        }
        gl_mainfield = mainField;
        // Отрисовка поля из файла
        for (int i = 0; i < gl_height; i++)
        {
            for (int j = 0; j < gl_width; j++)
            {
                switch (gl_mainfield.getField()[i][j]) {
                    case 10:
                    {
                        ImageView blockIv = new ImageView(wall);
                        blockIv.setLayoutY(i /* offset*/);
                        blockIv.setLayoutX(j /* offset*/);
                        addedObjects.add(blockIv);
                        break;
                    }
                    case 41:
                    {
                        ImageView grassIv = new ImageView(grass);
                        grassIv.setLayoutY(i /* offset*/);
                        grassIv.setLayoutX(j /* offset*/);
                        addedObjects.add(grassIv);
                        break;
                    }
                    case 1:
                    {
//                        ImageView grassIv = new ImageView(grass);
//                        grassIv.setLayoutY(i /* offset*/);
//                        grassIv.setLayoutX(j /* offset*/);
//                        addedObjects.add(grassIv);
//                        ImageView person1 = new ImageView(pers1);
//                        person1.setLayoutY(i /* offset*/);
//                        person1.setLayoutX(j /* offset*/);
//                        addedObjects.add(person1);
                    }
                }
            }
        }

        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run()
            {
                while(true)
                {
                    messageDisplayed = false;
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run()
                        {
                            root.getChildren().clear();
                            addedObjects.clear();
                            for (int i = 0; i < gl_height; i++)
                            {
                                for (int j = 0; j < gl_width; j++)
                                {
                                    switch (gl_mainfield.getField()[i][j]) {
                                        case 10:
                                        {
                                            ImageView blockIv = new ImageView(wall);
                                            blockIv.setLayoutY(i /* offset*/);
                                            blockIv.setLayoutX(j /* offset*/);
                                            addedObjects.add(blockIv);
                                            break;
                                        }
                                        case 41:
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
                //                        ImageView person1 = new ImageView(pers1);
                //                        person1.setLayoutY(i /* offset*/);
                //                        person1.setLayoutX(j /* offset*/);
                //                        addedObjects.add(person1);
                                        }
                                    }
                                }
                            }
                            //root.getChildren().remove(fieldMovingObjects);
                            root.getChildren().addAll(addedObjects);
                            fieldMovingObjects.clear();
                            for (int i = 0; i < gl_objectsOnField.size(); i++)
                            {
                                if (gl_objectsOnField.get(i).id == 101)
                                {
                                    switch (gl_objectsOnField.get(i).lastmove)
                                    {
                                        case "RIGHT" :
                                        {
                                            ImageView person1 = new ImageView(chelbosy);
                                            person1.setViewport(new Rectangle2D(animationOffset, 0, 32, 32));
                                            person1.setLayoutX(gl_objectsOnField.get(i).x /* offset*/);
                                            person1.setLayoutY(gl_objectsOnField.get(i).y /* offset*/);
                                            fieldMovingObjects.add(person1);
                                            break;
                                        }
                                        case "LEFT" :
                                        {
                                            ImageView person1 = new ImageView(chelbosy);
                                            person1.setViewport(new Rectangle2D(animationOffset, 32, 32, 32));
                                            person1.setLayoutX(gl_objectsOnField.get(i).x /* offset*/);
                                            person1.setLayoutY(gl_objectsOnField.get(i).y /* offset*/);
                                            fieldMovingObjects.add(person1);
                                            break;

                                        }
                                        case "DOWN" :
                                        {
                                            ImageView person1 = new ImageView(chelbosy);
                                            person1.setViewport(new Rectangle2D(animationOffset, 64, 32, 32));
                                            person1.setLayoutX(gl_objectsOnField.get(i).x /* offset*/);
                                            person1.setLayoutY(gl_objectsOnField.get(i).y /* offset*/);
                                            fieldMovingObjects.add(person1);
                                            break;

                                        }
                                        case "UP" :
                                        {
                                            ImageView person1 = new ImageView(chelbosy);
                                            person1.setViewport(new Rectangle2D(animationOffset, 96, 32, 32));
                                            person1.setLayoutX(gl_objectsOnField.get(i).x /* offset*/);
                                            person1.setLayoutY(gl_objectsOnField.get(i).y /* offset*/);
                                            fieldMovingObjects.add(person1);
                                            break;
                                        }
                                        case "SHOOT" :
                                        {
                                            System.out.println("SHOOT " + gl_objectsOnField.get(i).viewDirection);
                                            switch (gl_objectsOnField.get(i).viewDirection)
                                            {
                                                case "RIGHT" :
                                                {
                                                    ImageView person1 = new ImageView(chelbosy);
                                                    person1.setViewport(new Rectangle2D(animationOffset, 128, 32, 32));
                                                    person1.setLayoutX(gl_objectsOnField.get(i).x /* offset*/);
                                                    person1.setLayoutY(gl_objectsOnField.get(i).y /* offset*/);
                                                    fieldMovingObjects.add(person1);
                                                    break;
                                                }
                                                case "LEFT" :
                                                {
                                                    ImageView person1 = new ImageView(chelbosy);
                                                    person1.setViewport(new Rectangle2D(animationOffset, 160, 32, 32));
                                                    person1.setLayoutX(gl_objectsOnField.get(i).x /* offset*/);
                                                    person1.setLayoutY(gl_objectsOnField.get(i).y /* offset*/);
                                                    fieldMovingObjects.add(person1);
                                                    break;
                                                }
                                                case "DOWN" :
                                                {
                                                    ImageView person1 = new ImageView(chelbosy);
                                                    person1.setViewport(new Rectangle2D(animationOffset, 192, 32, 32));
                                                    person1.setLayoutX(gl_objectsOnField.get(i).x /* offset*/);
                                                    person1.setLayoutY(gl_objectsOnField.get(i).y /* offset*/);
                                                    fieldMovingObjects.add(person1);
                                                    break;
                                                }
                                                case "UP" :
                                                {
                                                    ImageView person1 = new ImageView(chelbosy);
                                                    person1.setViewport(new Rectangle2D(animationOffset, 224, 32, 32));
                                                    person1.setLayoutX(gl_objectsOnField.get(i).x /* offset*/);
                                                    person1.setLayoutY(gl_objectsOnField.get(i).y /* offset*/);
                                                    fieldMovingObjects.add(person1);
                                                    break;
                                                }
                                            }
                                            break;
                                        }
                                        default:
                                        {
                                            ImageView person1 = new ImageView(chelbosy);
                                            String viewDirection = gl_objectsOnField.get(i).viewDirection;
                                            int viewDirect = 0;
                                            switch (viewDirection)
                                            {
                                                case "RIGHT" : viewDirect = 0; break;
                                                case "LEFT" : viewDirect = 32; break;
                                                case "DOWN" : viewDirect = 64; break;
                                                case "UP" : viewDirect = 96; break;
                                            }
                                            person1.setViewport(new Rectangle2D(0, viewDirect, 32, 32));
                                            person1.setLayoutX(gl_objectsOnField.get(i).x /* offset*/);
                                            person1.setLayoutY(gl_objectsOnField.get(i).y /* offset*/);
                                            fieldMovingObjects.add(person1);
                                        }

                                    }
                                }
                                if (gl_objectsOnField.get(i).id == 102)
                                {
                                    fieldMovingObjects.add(getImage(gl_objectsOnField.get(i).lastmove, gl_objectsOnField.get(i).viewDirection, chelbosy2, gl_objectsOnField.get(i).x, gl_objectsOnField.get(i).y));
                                }
                                if (gl_objectsOnField.get(i).id == 103)
                                {
                                    fieldMovingObjects.add(getImage(gl_objectsOnField.get(i).lastmove, gl_objectsOnField.get(i).viewDirection, chelbosy3, gl_objectsOnField.get(i).x, gl_objectsOnField.get(i).y));
                                }
                                if (gl_objectsOnField.get(i).id > 111)
                                {
                                        switch (gl_objectsOnField.get(i).lastmove) {
                                            case "RIGHT": {
                                                ImageView person1 = new ImageView(slime);
                                                person1.setViewport(new Rectangle2D(animationOffset, 0, 32, 32));
                                                person1.setLayoutX(gl_objectsOnField.get(i).x /* offset*/);
                                                person1.setLayoutY(gl_objectsOnField.get(i).y /* offset*/);
                                                fieldMovingObjects.add(person1);
                                                break;
                                            }
                                            case "LEFT": {
                                                ImageView person1 = new ImageView(slime);
                                                person1.setViewport(new Rectangle2D(animationOffset, 32, 32, 32));
                                                person1.setLayoutX(gl_objectsOnField.get(i).x /* offset*/);
                                                person1.setLayoutY(gl_objectsOnField.get(i).y /* offset*/);
                                                fieldMovingObjects.add(person1);
                                                break;

                                            }
                                            case "DOWN": {
                                                ImageView person1 = new ImageView(slime);
                                                person1.setViewport(new Rectangle2D(animationOffset, 64, 32, 32));
                                                person1.setLayoutX(gl_objectsOnField.get(i).x /* offset*/);
                                                person1.setLayoutY(gl_objectsOnField.get(i).y /* offset*/);
                                                fieldMovingObjects.add(person1);
                                                break;

                                            }
                                            case "UP": {
                                                ImageView person1 = new ImageView(slime);
                                                person1.setViewport(new Rectangle2D(animationOffset, 96, 32, 32));
                                                person1.setLayoutX(gl_objectsOnField.get(i).x /* offset*/);
                                                person1.setLayoutY(gl_objectsOnField.get(i).y /* offset*/);
                                                fieldMovingObjects.add(person1);
                                                break;
                                            }
                                        }
                                }
                            }
                            root.getChildren().addAll(fieldMovingObjects);
                            root.getChildren().addAll(animations);
                            if ((lastRecievedCommand.get() == -1) || (lastRecievedCommand.get() == -2))
                            {
                                WinWindow.showWindow(lastRecievedCommand.get());
                                messageDisplayed = true;
                            }
                        }
                    });
                    try {
                        Thread.sleep(50);
                    }
                    catch (Exception ex)
                    {
                        System.out.println(ex.toString());
                    }
                    animationOffset += offset;
                    if (animationOffset == (offset * 4)) animationOffset = 0;
                    if (((lastRecievedCommand.get() == -1) || (lastRecievedCommand.get() == -2)) && (messageDisplayed))
                    {
                        try
                        {
                            Thread.sleep(2500);
                        }
                        catch (Exception ex)
                        {
                            System.out.println(ex.toString());
                        }
                        Platform.exit();
                        break;
                    }
                }
            }
        });
        thread1.start();
        myThread Jthread = new myThread("Jthread", gl_objectsOnField, anim);
        Jthread.start();

        scene.setOnKeyTyped(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent ke) {
                switch (ke.getCharacter())
                {
                    case "w": pl1.sendCommand("UP"); break;
                    case "a": pl1.sendCommand("LEFT"); break;
                    case "s": pl1.sendCommand("DOWN"); break;
                    case "d": pl1.sendCommand("RIGHT"); break;
                    case " ": pl1.sendCommand("SHOOT"); break;
                }
            }
        });
        root.getChildren().addAll(addedObjects);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public ImageView getImage(String lastmove, String viewDirection, Image img1, int x, int y)
    {
        ImageView result = new ImageView();
        switch (lastmove)
        {
            case "RIGHT" :
            {
                ImageView person1 = new ImageView(img1);
                person1.setViewport(new Rectangle2D(animationOffset, 0, 32, 32));
                person1.setLayoutX(x);
                person1.setLayoutY(y);
                return person1;
            }
            case "LEFT" :
            {
                ImageView person1 = new ImageView(img1);
                person1.setViewport(new Rectangle2D(animationOffset, 32, 32, 32));
                person1.setLayoutX(x);
                person1.setLayoutY(y);
                return person1;

            }
            case "DOWN" :
            {
                ImageView person1 = new ImageView(img1);
                person1.setViewport(new Rectangle2D(animationOffset, 64, 32, 32));
                person1.setLayoutX(x);
                person1.setLayoutY(y);
                return person1;

            }
            case "UP" :
            {
                ImageView person1 = new ImageView(img1);
                person1.setViewport(new Rectangle2D(animationOffset, 96, 32, 32));
                person1.setLayoutX(x);
                person1.setLayoutY(y);
                return person1;
            }
            case "SHOOT" :
            {
                switch (viewDirection)
                {
                    case "RIGHT" :
                    {
                        ImageView person1 = new ImageView(img1);
                        person1.setViewport(new Rectangle2D(animationOffset, 128, 32, 32));
                        person1.setLayoutX(x);
                        person1.setLayoutY(y);
                        return person1;
                    }
                    case "LEFT" :
                    {
                        ImageView person1 = new ImageView(img1);
                        person1.setViewport(new Rectangle2D(animationOffset, 160, 32, 32));
                        person1.setLayoutX(x);
                        person1.setLayoutY(y);
                        return person1;
                    }
                    case "DOWN" :
                    {
                        ImageView person1 = new ImageView(img1);
                        person1.setViewport(new Rectangle2D(animationOffset, 192, 32, 32));
                        person1.setLayoutX(x);
                        person1.setLayoutY(y);
                        return person1;
                    }
                    case "UP" :
                    {
                        ImageView person1 = new ImageView(img1);
                        person1.setViewport(new Rectangle2D(animationOffset, 224, 32, 32));
                        person1.setLayoutX(x);
                        person1.setLayoutY(y);
                        return person1;
                    }
                }
                break;
            }
            default:
            {
                ImageView person1 = new ImageView(img1);
                int viewDirect = 0;
                switch (viewDirection)
                {
                    case "RIGHT" : viewDirect = 0; break;
                    case "LEFT" : viewDirect = 32; break;
                    case "DOWN" : viewDirect = 64; break;
                    case "UP" : viewDirect = 96; break;
                }
                person1.setViewport(new Rectangle2D(0, viewDirect, 32, 32));
                person1.setLayoutX(x);
                person1.setLayoutY(y);
                return person1;
            }

        }
        return  result;
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
    ArrayList<ObjToTransfer> objectsOnField = new ArrayList<>();
    ArrayList<ObjToTransfer> globalObjectsOnField;
    ArrayList<Animation> animations = new ArrayList<>();
    public myThread(String name, ArrayList<ObjToTransfer> gl_ObjectsOnField, ArrayList<Animation> animations1)
    {
        super(name);
        this.globalObjectsOnField = gl_ObjectsOnField;
        this.animations = animations1;
    }

    public void run()
    {
        try {
            while(true) {
                Socket clientSocket = new Socket("127.0.0.1", 11000);
                ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream objInput = new ObjectInputStream(clientSocket.getInputStream());
                out.writeByte(GameWindow.pl1.playerId);
                out.flush();
                out.writeUTF("GetField");
                out.flush();
                GameWindow.gl_objectsOnField.clear();
                this.objectsOnField.clear();
                int size = objInput.readInt();
                if (size < 0)
                {
                    GameWindow.lastRecievedCommand.set(size);
                    return;
                }
                for(int i = 0; i < size; i++)
                {
                    ObjToTransfer obj1 = (ObjToTransfer)objInput.readObject();
                    this.objectsOnField.add(obj1);
                    GameWindow.gl_objectsOnField.add(obj1);
                }
                Thread.sleep(200);
            }
        }
        catch (Exception ex)
        {
            System.out.println(ex.toString());
        }
    }
}
