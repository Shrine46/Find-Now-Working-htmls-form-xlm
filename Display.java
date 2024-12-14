import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JPanel;

public class Display extends JPanel {
    // private Main grid;

    // public Display(Main grid) {
    //     this.grid = grid;
    // }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(Color.GRAY);
        
        LifeGraphics img = new LifeGraphics(0, 0, 800, 800);
        //img.drawGrid(g, grid.getGrid());
        
    }
}
