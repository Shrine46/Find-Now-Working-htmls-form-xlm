import java.awt.*;

public class LifeGraphics {
    private int x;
    private int y;
    private int width;
    private int height;

    public LifeGraphics(int x, int y, int w, int h){
        this.x = x;
        this.y = y;     
        this.width = w;
        this.height = h;
    }

    public void drawGrid(Graphics g, String[][] grid){
        int lineX = 0;
        int cellWidth = width/grid.length;
        int cellX = -cellWidth;
        int cellY = -cellWidth;
        

        g.setColor(new Color(0,0,0));
        g.drawRect(0, 0, width, height);
        // g.drawLine(x + width / 2, y, x + width / 2, y + height); // Vertical line
        // g.drawLine(x, y + height / 2, x + width, y + height / 2); // Horizontal line

    }

    public void clearGrid(Graphics g, String[][] grid) {
        
    }
}