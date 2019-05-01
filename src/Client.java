import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class Client {
    public static void main(String args[])
    {
        try
        {
            int x = 10;
            while(x > 0)
            {
                Socket clientSocket = new Socket("127.0.0.1", 11000);
                DataInputStream in = new DataInputStream(clientSocket.getInputStream());
                DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
                out.writeUTF("1");
                out.flush();
                out.writeUTF("UP");
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
                x--;
                System.out.println("----------------------------");
            }
        }
        catch (Exception ex)
        {
            System.out.println(ex.toString());

        }

    }
}
