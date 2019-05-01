import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class Player {
    String playername;
    int playerhealth;
    int playermodel;
    Socket clientSocket;
    DataInputStream in;
    DataOutputStream out;

    public Player(String name, int playermodel)
    {
        this.playername = name;
        this.playermodel = playermodel;
        this.playerhealth = 100;
    }

    public String getPlayername() {
        return playername;
    }

    public void setPlayername(String playername) {
        this.playername = playername;
    }

    public int getPlayerhealth() {
        return playerhealth;
    }

    public void setPlayerhealth(int playerhealth) {
        this.playerhealth = playerhealth;
    }

    public int getPlayermodel() {
        return playermodel;
    }

    public void setPlayermodel(int playermodel) {
        this.playermodel = playermodel;
    }

    public void sendCommand(String command)
    {
        try
        {
            this.clientSocket = new Socket("127.0.0.1", 11000);
            this.in = new DataInputStream(clientSocket.getInputStream());
            this.out = new DataOutputStream(clientSocket.getOutputStream());
            out.writeUTF("1"); // Изменить на playername
            out.flush();
            out.writeUTF(command);
            out.flush();
            byte gameField[] = new byte[10 * 20];
            in.read(gameField);
            int counter = 0;
            for (int i = 0; i < 10; i++)
            {
                for (int j = 0; j < 20; j++)
                {
                    System.out.print(gameField[counter]);
                    counter++;
                }
                System.out.println();
            }
            System.out.println("----------------------------");
        }
        catch (Exception ex)
        {
            ex.toString();
        }
    }
}
