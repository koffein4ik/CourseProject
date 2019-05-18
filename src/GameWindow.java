import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.Transition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;


public class GameWindow extends Application {

    public static int gl_height;
    public static int gl_width;
    public static GameField gl_mainfield;
    public static ArrayList<ObjToTransfer> gl_objectsOnField = new ArrayList<>();
    public static ArrayList<javafx.scene.Node> animations = new ArrayList<>();
    public static ArrayList<Animation> anim = new ArrayList<>();
    public static int animationOffset = 0;
    public static Player pl1;

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
        File f2 = new File("src/Pictures/grass_32px.png");
        Image grass = new Image(f2.toURI().toString());
        File f3 = new File("src/Pictures/pers1_32px.png");
        Image pers1 = new Image(f3.toURI().toString());
        File f4 = new File("src/Pictures/Parenki1.png");
        Image img = new Image(f4.toURI().toString());
        final ImageView parenki = new ImageView(img);
        File f5 = new File("src/Pictures/running_guy.png");
        File f6 = new File("src/Pictures/monster.png");
        Image monster = new Image(f6.toURI().toString());
        Image run = new Image(f5.toURI().toString());
        //Player pl1 = new Player("John", 1, (byte)1);
        ArrayList<javafx.scene.Node> addedObjects = new ArrayList<>();
        ArrayList<javafx.scene.Node> fieldMovingObjects = new ArrayList<>();
        String levelPath = "";
        try
        {
            Socket clientSocket = new Socket("127.0.0.1", 11000);
            //DataInputStream in = new DataInputStream(clientSocket.getInputStream());
            //DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
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
//        Timer timer = new Timer();
//        timer.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                System.out.print("I would be called every 2 seconds");
//                root.getChildren().remove(fieldMovingObjects);
//                fieldMovingObjects.clear();
//                for (int i = 0; i < gl_objectsOnField.size(); i++)
//                {
//                    System.out.println("Object id " + gl_objectsOnField.get(i).id);
//                    System.out.println("Object x "+ gl_objectsOnField.get(i).x);
//                    System.out.println("Object y " + gl_objectsOnField.get(i).y);
//                    if (gl_objectsOnField.get(i).id == 1)
//                    {
//                        ImageView person1 = new ImageView(pers1);
//                        person1.setLayoutY(gl_objectsOnField.get(i).x /* offset*/);
//                        person1.setLayoutX(gl_objectsOnField.get(i).y /* offset*/);
//                        fieldMovingObjects.add(person1);
//                        //root.getChildren().add(person1);
//                        //Отдельный список, в котором будут объекты на поле
//                    }
//                }
//                //root.getChildren().addAll(fieldMovingObjects);
//            }
//        }, 0, 2000);
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run()
            {
                //int animationOffset = 0;
                while(true)
                {
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
//                                System.out.println("Object id " + gl_objectsOnField.get(i).id);
//                                System.out.println("Object x "+ gl_objectsOnField.get(i).x);
//                                System.out.println("Object y " + gl_objectsOnField.get(i).y);
                                if (gl_objectsOnField.get(i).id == 101)
                                {
                                    if (gl_objectsOnField.get(i).lastmove.equals("RIGHT"))
                                    {
                                        ImageView person1 = new ImageView(img);
                                        person1.setViewport(new Rectangle2D(animationOffset, 0, 32, 32));
                                        person1.setLayoutX(gl_objectsOnField.get(i).x /* offset*/);
                                        person1.setLayoutY(gl_objectsOnField.get(i).y /* offset*/);
                                        fieldMovingObjects.add(person1);
                                    }
                                    else
                                    {
                                        ImageView person1 = new ImageView(pers1);
                                        person1.setLayoutX(gl_objectsOnField.get(i).x /* offset*/);
                                        person1.setLayoutY(gl_objectsOnField.get(i).y /* offset*/);
                                        fieldMovingObjects.add(person1);
                                    }
                                }
                                if (gl_objectsOnField.get(i).id == 102)
                                {
                                    ImageView person1 = new ImageView(pers1);
                                    person1.setLayoutX(gl_objectsOnField.get(i).x /* offset*/);
                                    person1.setLayoutY(gl_objectsOnField.get(i).y /* offset*/);
                                    fieldMovingObjects.add(person1);
                                }
                                if (gl_objectsOnField.get(i).id > 111)
                                {
                                    ImageView monst1 = new ImageView(monster);
                                    monst1.setLayoutX(gl_objectsOnField.get(i).x /* offset*/);
                                    monst1.setLayoutY(gl_objectsOnField.get(i).y /* offset*/);
                                    fieldMovingObjects.add(monst1);
                                }
                            }
                            root.getChildren().addAll(fieldMovingObjects);
                            root.getChildren().addAll(animations);
                        }
                    });
                    try {
                        Thread.sleep(20);
                    }
                    catch (Exception ex)
                    {
                        System.out.println(ex.toString());
                    }
                    animationOffset += offset;
                    if (animationOffset == (offset * 4)) animationOffset = 0;
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
        //root.getChildren().addAll(fieldObjects);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    void updateAddedObjects(GameField mainfield)
    {
        for (int i = 0; i < gl_height; i++)
        {
            for (int j = 0; j < gl_width; j++)
            {

            }
        }
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
        File f4 = new File("src/Pictures/Parenki1.png");
        Image img = new Image(f4.toURI().toString());
        final ImageView parenki = new ImageView(img);
        try {
            while(true) {
                Socket clientSocket = new Socket("127.0.0.1", 11000);
                ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream objInput = new ObjectInputStream(clientSocket.getInputStream());
                //DataInputStream in = new DataInputStream(clientSocket.getInputStream());
                //DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
                //out.writeUTF("1"); // Изменить на playername
                out.writeByte(1);
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
                //System.out.println(size);
                GameWindow.gl_objectsOnField.clear();
                for(int i = 0; i < size; i++)
                {
                    ObjToTransfer obj1 = (ObjToTransfer)objInput.readObject();
//                     System.out.println("Object id " + obj1.id);
//                     System.out.println("Object x "+ obj1.x);
//                     System.out.println("Object y " + obj1.y);
                     this.objectsOnField.add(obj1);
                     GameWindow.gl_objectsOnField.add(obj1);
                }
                //globalObjectsOnField.clear();
                //globalObjectsOnField = objectsOnField;
                //GameWindow.gl_objectsOnField = objectsOnField;
                /*imgToPaintOnField.clear();
                for (int i = 0; i < objectsOnField.size(); i++)
                {
                    if (objectsOnField.get(i).id == 1)
                    {
                        ImageView person1 = new ImageView(pers1);
                        person1.setLayoutX(objectsOnField.get(i).x);
                        person1.setLayoutY(objectsOnField.get(i).y);
                        imgToPaintOnField.add(person1);
                    }
                }*/
                Thread.sleep(80);
            }
        }
        catch (Exception ex)
        {
            System.out.println(ex.toString());
        }
    }
}

class SpriteAnimation extends Transition
{
    private final ImageView imgView;
    private final int count;
    private final int columns;
    private final int offsetX;
    private final int offsetY;
    private final int witdth;
    private final int height;

    public SpriteAnimation(ImageView imgView, int count, int columns, int offsetX, int offsetY, int witdth, int height, Duration duration) {
        this.imgView = imgView;
        this.count = count;
        this.columns = columns;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.witdth = witdth;
        this.height = height;
        setCycleDuration(duration);
    }

    protected void interpolate(double k)
    {
        final int index = Math.min((int) Math.floor(k * count), count - 1);
        final int x = (index % columns) * witdth + offsetX;
        final int y = (index / columns) * witdth + offsetY;
        imgView.setViewport(new Rectangle2D(x, y, witdth, height));
    }
}
