import javax.swing.text.StyledEditorKit;
import java.io.*;
import java.net.Socket;
import java.net.ServerSocket;
import java.rmi.MarshalException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameServer
{
    private static ServerSocket server;

    public static void main(String args[])
    {
        int offset = 32;
        int shift = 1;
        String path = "E:\\CourseProject\\src\\Levels\\level1.txt";
        final int port = 11000;
        int width = 0;
        int height = 0;
        ArrayList<ObjToTransfer> objectsOnFiled = new ArrayList<>();
        ArrayList<PlayerCoord> emptyCoordinates = new ArrayList<>();
        ArrayList<Mob> monsters = new ArrayList<>();
        byte startMonsterId = 112;
        monsters = generateMonsters(2, startMonsterId);
        Map<Byte, String> objLastMoves = new HashMap<>();
        try (FileInputStream fin = new FileInputStream(path)) {
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
        } catch (IOException ex) {

            System.out.println(ex.getMessage());
        }
        height *= offset;
        width *= offset;
        GameField mainField = new GameField(height, width);
        emptyCoordinates = mainField.fromFile(path, offset);
        for(int i = 0; i < monsters.size(); i++)
        {
            setMonsterToRandomPoint(mainField, monsters.get(i).id, offset, emptyCoordinates);
        }
        String command;
        byte playerNumber;
        try
        {
            server = new ServerSocket(port);
            while(true)
            {
                Socket clientSocket = server.accept();
                ObjectOutputStream objOut = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream objIn = new ObjectInputStream(clientSocket.getInputStream());
                for(int i = 0; i < monsters.size(); i++)
                {
                    processCommand(mainField, monsters.get(i).id, monsters.get(i).makeRandomMove(), offset, objLastMoves);
                }
                //DataInputStream in = new DataInputStream(clientSocket.getInputStream());
                //DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
                //playerNumber = objIn.readUTF();
                playerNumber = objIn.readByte();
                command = objIn.readUTF();
                //System.out.println(playerNumber);
                //System.out.println(command);
                if (command.equals("GetLevelPath"))
                {
                    objOut.writeUTF(path);
                    //objOut.flush();
                    System.out.println("Sent");
                    objectsOnFiled = mainField.getObjectPositions(objLastMoves);
                    int startID = 101;
                    boolean idGenerated = false;
                    while (!(idGenerated))
                    {
                        idGenerated = true;
                        for (int i = 0; i < objectsOnFiled.size(); i++)
                        {
                            if (startID == objectsOnFiled.get(i).id)
                            {
                                startID++;
                                idGenerated = false;
                                break;
                            }
                        }
                    }
                    objOut.writeInt(startID);
                    objOut.flush();
                    for (int i = 0; i < emptyCoordinates.size(); i++)
                    {
                        if(isSquareEmpty(emptyCoordinates.get(i).x, emptyCoordinates.get(i).y, mainField, offset))
                        {
                            mainField.setFieldDot(emptyCoordinates.get(i).x, emptyCoordinates.get(i).y, (byte) startID);
                            emptyCoordinates.remove(i);
                            break;
                        }
                    }
                }
                if(!(command.equals("GetField")))
                    processCommand(mainField, playerNumber, command, offset, objLastMoves);
                objectsOnFiled = mainField.getObjectPositions(objLastMoves);
                objOut.writeInt(objectsOnFiled.size());
                for (int i = 0; i < objectsOnFiled.size(); i++)
                {
                    objOut.writeObject(objectsOnFiled.get(i));
                }
                if (command.equals("GetField")) objLastMoves.clear();
                //out.write(mainField.getOneDimensionalField(), 0, mainField.getOneDimensionalField().length);
                objOut.flush();
            }
        }
        catch (Exception ex)
        {
            System.out.println(ex.toString());
        }
    }

    private static void processCommand(GameField gameField, byte playerId, String command, int offset, Map<Byte, String> objLastMoves)
    {
        //byte playerNumb = Byte.parseByte(playerNumber);
        byte playerNumb = playerId;
        PlayerCoord currCoord = new PlayerCoord(0, 0);
        currCoord = currCoord.getPlayerCoord(gameField, playerNumb);
        switch (command) {
            case "UP": {
                gameField.replaceDot(currCoord.x, currCoord.y, currCoord.x, currCoord.y - 1, playerNumb, offset, objLastMoves);
                return;
            }
            case "DOWN":
            {
                gameField.replaceDot(currCoord.x, currCoord.y, currCoord.x, currCoord.y + 1, playerNumb, offset, objLastMoves);
                return;
            }
            case "LEFT":
            {
                gameField.replaceDot(currCoord.x, currCoord.y, currCoord.x - 1, currCoord.y, playerNumb, offset, objLastMoves);
                return;
            }
            case "RIGHT":
            {
                gameField.replaceDot(currCoord.x, currCoord.y, currCoord.x + 1, currCoord.y, playerNumb, offset, objLastMoves);
                return;
            }
            default: return;
        }
    }

    public static ArrayList<Mob> generateMonsters(int amount, byte startId)
    {
        ArrayList<Mob> result = new ArrayList<>();
        for (int i = 0; i < amount; i++)
        {
            result.add(new Mob(startId));
            startId++;
        }
        return result;
    }

    public static void setMonsterToRandomPoint(GameField mainField, byte monsterId, int offset, ArrayList<PlayerCoord> emptyCoordinates)
    {
        int height = mainField.getHeight() / 3;
        int width = mainField.getWidth() / 3;
        int emptySize = emptyCoordinates.size();
        int randIndex;
        do
        {
//            randX = width / 4  + (int) (Math.random() * (width - width / 4));
//            randY = height / 4 + (int) (Math.random() * (height - height / 4));
            randIndex = (int) (Math.random() * emptySize);
            //System.out.println(randIndex);
        } while ((emptyCoordinates.get(randIndex).x < width) && (emptyCoordinates.get(randIndex).y < height));
        mainField.setFieldDot(emptyCoordinates.get(randIndex).x, emptyCoordinates.get(randIndex).y, monsterId);
        emptyCoordinates.remove(randIndex);
    }

    public static Boolean isSquareEmpty(int x, int y, GameField mainField, int offset)
    {
        Boolean result = true;
        for(int i = y; i < y + offset; i++)
        {
            for (int j = x; j < x + offset; j++)
            {
                if(!((((mainField.getField()[i][j] > 40) && (mainField.getField()[i][j] < 51))) || (mainField.getField()[i][j] == 0))) return false;
            }
        }
        return result;
    }
}

class Mob {
    public byte id;

    public Mob(byte recivedId)
    {
        this.id = recivedId;
    }
    public String makeRandomMove()
    {
        double dMove = Math.random() * 4;
        byte move = (byte)dMove;
        switch (move)
        {
            case 0 : return "LEFT";
            case 1 : return "RIGHT";
            case 2 : return "UP";
            case 3 : return "DOWN";
            default: return "RIGHT";
        }
    }
}

class GameField {
    private int width;
    private int height;
    private byte field[][];

    GameField(int height, int width) {
        this.width = width;
        this.height = height;
        this.field = new byte[height][width];
    }

    public byte[] getOneDimensionalField() {
        byte oneDimField[] = new byte[this.getHeight() * this.getWidth()];
        int index = 0;
        for (int i = 0; i < this.height; i++) {
            for (int j = 0; j < this.width; j++) {
                oneDimField[index] = this.field[i][j];
                index++;
            }
        }
        return oneDimField;
    }

    public void setFieldDot(int x, int y, byte value) {
        if (x < this.width & y < this.height) {
            this.field[y][x] = value;
        } else {
            System.out.println("Incorrect cooordinate value");
        }
    }

    public Boolean isCoordExist(int x, int y, int offset)
    {
        return !((x + offset >= width) || (y + offset >= height) || (x < 0) || (y < 0)); // Мб надо добавить - 1
    }

    public void replaceDot(int oldX, int oldY, int newX, int newY, byte value, int offset, Map<Byte, String> objLastMoves)
    {
        if (isCoordEmpty(newX, newY, offset, value))
        {
            this.field[oldY][oldX] = 0; // Заменить на ID травы
            this.field[newY][newX] = value;
            if (newX > oldX)
            {
                objLastMoves.put(value, "RIGHT");
            }
            if (newY > oldY)
            {
                objLastMoves.put(value, "DOWN");
            }
            if (newX < oldX)
            {
                objLastMoves.put(value, "LEFT");
            }
            if (newY < oldY)
            {
                objLastMoves.put(value, "UP");
            }
        }
    }

    public Boolean isCoordEmpty(int x, int y, int offset, byte value)
    {
        List<Byte> correctValues = new ArrayList<>();
        correctValues.add((byte)0);
        for (byte i = 41; i < 51; i++)
        {
            correctValues.add(i);
        }
        correctValues.add(value);
        if (isCoordExist(x, y, offset))
        {
            for (int i = x; i < x + offset; i++)
            {
               if (!(correctValues.contains(this.field[y][i]))) return false;
            }

            for (int i = y; i < y + offset; i++)
            {
                //if (!((this.field[i][x] > 40) && (this.field[i][x] < 51) || (this.field[i][x] == 0))) return false;
                if (!(correctValues.contains(this.field[i][x]))) return false;
            }

            for (int i = x; i < x + offset; i++)
            {
                //if (!((this.field[y + offset - 1][i] > 40) && (this.field[y + offset - 1][i] < 51) || (this.field[y + offset - 1][i] == 0))) return false;
                if (!(correctValues.contains(this.field[y + offset - 1][i]))) return false;
            }

            for (int i = y; i < y + offset; i++)
            {
                //if (!((this.field[i][x + offset - 1] > 40) && (this.field[i][x + offset - 1] < 51) || (this.field[i][x + offset - 1] == 0))) return false;
                if (!(correctValues.contains(this.field[i][x + offset - 1]))) return false;
            }
            return true;
            //return (((this.field[y + offset][x + offset] > 40) && (this.field[y + offset][x + offset] < 51)) || (this.field[y + offset][x + offset] == 0)); // Мб надо добавить - 1
        }
        return false;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public byte[][] getField() {
        return field;
    }


    public void setField(byte[][] field) {
        this.field = field;
    }

    public GameField fromByteArray(byte[] array, int height, int width)
    {
        int counter = 0;
        GameField gf1 = new GameField(height, width);
        for (int i = 0; i < height; i++)
        {
            for (int j = 0; j < width; j++)
            {
                gf1.setFieldDot(j, i, array[counter]);
                counter++;
            }
        }
        return gf1;
    }

    public ArrayList<PlayerCoord> fromFile(String path, int offset) {

        ArrayList<PlayerCoord> emptySquares = new ArrayList<>();
        try (FileInputStream fin = new FileInputStream(path)) {
            byte[] buffer = new byte[fin.available()];
            fin.read(buffer, 0, fin.available());
            int i = 0;
            int j = 0;
            int k = 0;
            while ((buffer[k] != 13) && (k < buffer.length))
            {
                k++;
            }
            k += 2;
            String val = "";
            for (k = k; k < buffer.length; k++)
            {
                switch (buffer[k]) {
                    case 13: {
                        this.field[i][j] = Byte.parseByte(val);
                        if ((Byte.parseByte(val)) > 40 && (Byte.parseByte(val) < 51)) emptySquares.add(new PlayerCoord(i, j));
                        val = "";
                        k++;
                        i += offset;
                        j = 0;
                        break;
                    }
                    case 32:
                    {
                        this.field[i][j] = Byte.parseByte(val);
                        if ((Byte.parseByte(val)) > 40 && (Byte.parseByte(val) < 51)) emptySquares.add(new PlayerCoord(i, j));
                        j += offset;
                        val = "";
                        continue;
                    }
                    default:
                    {
                        val += (char)buffer[k];
                    }
                }
            }
            this.field[i][j] = Byte.parseByte(val);
        } catch (IOException ex) {

            System.out.println(ex.getMessage());
        }
        return emptySquares;
    }

    public ArrayList<ObjToTransfer> getObjectPositions(Map<Byte, String> objLastMove)
    {
        ArrayList<ObjToTransfer> result = new ArrayList<ObjToTransfer>();
        for (int i = 0; i < this.height; i++)
        {
            for (int j = 0; j < this.width; j++)
            {
                byte currFieldValue = this.field[i][j];
                if (((currFieldValue > 100) && (currFieldValue < 112)) || ((currFieldValue > 111) && (currFieldValue < 127)))  //УБРАТЬ Единицу
                {
                    ObjToTransfer obj1 = new ObjToTransfer(currFieldValue, j, i);
                    if (objLastMove.containsKey(currFieldValue))
                    {
                        obj1.lastmove = objLastMove.get(currFieldValue);
                    }
                    else
                    {
                        obj1.lastmove = "STAND";
                    }
                    result.add(obj1);
                }
            }
        }
        return result;
    }
}

class PlayerCoord
{
    int x;
    int y;

    public PlayerCoord(int recX, int recY)
    {
        this.x = recX;
        this.y = recY;
    }

    public PlayerCoord getPlayerCoord(GameField gameField, int playerNumber)
    {
        byte[][] tempField = gameField.getField();
        PlayerCoord coord = new PlayerCoord(-1, -1);
        coord.x = -1;
        coord.y = -1;
        int height = gameField.getHeight();
        int width = gameField.getWidth();
        for(int i = 0; i < height; i++)
        {
            for(int j = 0; j < width; j++)
            {
                if (tempField[i][j] == playerNumber)
                {
                    coord.y = i;
                    coord.x = j;
                    return coord;
                }
            }
        }
        return coord;
    }
}
