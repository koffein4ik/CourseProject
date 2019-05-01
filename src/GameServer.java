import java.io.*;
import java.net.Socket;
import java.net.ServerSocket;

public class GameServer
{
    private static ServerSocket server;

    public static void main(String args[])
    {
        final int port = 11000;
        GameField mainField = new GameField(10, 20);
        byte a = 1;
        byte block = 10;
        mainField.setFieldDot(5,5, a);
        mainField.setFieldDot(0,0, block);
        mainField.setFieldDot(1, 0, block);
        String command;
        String playerNumber;
        try
        {
            server = new ServerSocket(port);
            while(true)
            {
                Socket clientSocket = server.accept();
                DataInputStream in = new DataInputStream(clientSocket.getInputStream());
                DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
                playerNumber = in.readUTF();
                command = in.readUTF();
                System.out.println(playerNumber);
                System.out.println(command);
                processCommand(mainField, playerNumber, command);
                out.write(mainField.getOneDimensionalField(), 0, mainField.getOneDimensionalField().length);
                out.flush();
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
