import java.io.Serializable;

public class ObjToTransfer implements Serializable {
    public int id;
    public int x;
    public int y;
    public String lastmove = "";

    public ObjToTransfer(int id, int x, int y)
    {
        this.id = id;
        this.x = x;
        this.y = y;
    }
}
