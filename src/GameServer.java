import java.io.*;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.ArrayList;

public class GameServer
{
    private static ServerSocket server;

    public static void main(String args[])
    {
        int offset = 16;
        String path = "E:\\CourseProject\\src\\Levels\\level2.txt";
        final int port = 11000;
        int width = 0;
        int height = 0;
        ArrayList<ObjToTransfer> objectsOnFiled = new ArrayList<>();
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
        GameField mainField = new GameField(height, width);
        mainField.fromFile(path);
        String command;
        String playerNumber;
        try
        {
            server = new ServerSocket(port);
            while(true)
            {
                Socket clientSocket = server.accept();
                ObjectOutputStream objOut = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream objIn = new ObjectInputStream(clientSocket.getInputStream());
                //DataInputStream in = new DataInputStream(clientSocket.getInputStream());
                //DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
                playerNumber = objIn.readUTF();
                command = objIn.readUTF();
                System.out.println(playerNumber);
                System.out.println(command);
                if (command.equals("GetLevelPath"))
                {
                    objOut.writeUTF(path);
                    objOut.flush();
                    System.out.println("Sent");
                    continue;
                }
                if(!(command.equals("GetField")))
                    processCommand(mainField, playerNumber, command);
                for (int i = 0; i < objectsOnFiled.size(); i++)
                {
                    objOut.writeObject(objectsOnFiled.get(i));
                }
                //out.write(mainField.getOneDimensionalField(), 0, mainField.getOneDimensionalField().length);
                objOut.flush();
            }
        }
        catch (Exception ex)
        {
            System.out.println(ex.toString());
        }
    }

    private static void processCommand(GameField gameField, String playerNumber, String command)
    {
        byte playerNumb = Byte.parseByte(playerNumber);
        PlayerCoord currCoord = new PlayerCoord();
        currCoord = currCoord.getPlayerCoord(gameField, playerNumb);
        switch (command) {
            case "UP": {
                gameField.replaceDot(currCoord.x, currCoord.y, currCoord.x, currCoord.y - 1, playerNumb);
                return;
            }
            case "DOWN":
            {
                gameField.replaceDot(currCoord.x, currCoord.y, currCoord.x, currCoord.y + 1, playerNumb);
                return;
            }
            case "LEFT":
            {
                gameField.replaceDot(currCoord.x, currCoord.y, currCoord.x - 1, currCoord.y, playerNumb);
                return;
            }
            case "RIGHT":
            {
                gameField.replaceDot(currCoord.x, currCoord.y, currCoord.x + 1, currCoord.y, playerNumb);
                return;
            }
            default: return;
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

    public Boolean isCoordExist(int x, int y)
    {
        return !((x >= width) || (y >= height) || (x < 0) || (y < 0));
    }

    public void replaceDot(int oldX, int oldY, int newX, int newY, byte value)
    {
        if (isCoordEmpty(newX, newY))
        {
            this.field[oldY][oldX] = 0;
            this.field[newY][newX] = value;
        }
    }

    public Boolean isCoordEmpty(int x, int y)
    {
        if (isCoordExist(x, y))
        {
            return (this.field[y][x] == 0);
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

    public GameField fromFile(String path) {

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
                        val = "";
                        k++;
                        i++;
                        j = 0;
                        break;
                    }
                    case 32:
                    {
                        this.field[i][j] = Byte.parseByte(val);
                        j++;
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
        return null;
    }
}

class PlayerCoord
{
    int x;
    int y;

    public PlayerCoord getPlayerCoord(GameField gameField, int playerNumber)
    {
        byte[][] tempField = gameField.getField();
        PlayerCoord coord = new PlayerCoord();
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
