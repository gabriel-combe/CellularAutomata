import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;

public abstract class CellularAutomata {
    protected int width, height, scale, knn;

    protected Rule rules;
    
    private BufferedImage image, scaledImage;
    private WritableRaster raster;
    private ColorModel colourModel;

    private InputStreamReader isr;
    private BufferedReader br;
    
    private String windowName;
    
    JFrame imageFrame;
    ImagePanel imagePanel;
    
    protected boolean[][] cells;
    
    public boolean isFinished = false;
    
    public CellularAutomata(String name, int width, int height, int scale, int knn){
        this.windowName = name;
        this.width = width;
        this.height = height;
        this.scale = scale;
        this.knn = knn;

        this.rules = new Rule(this.knn);

        this.isr = new InputStreamReader(System.in);
        this.br = new BufferedReader(this.isr);

        this.cells = new boolean[width][height];
        
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        raster = image.getRaster();
        colourModel = image.getColorModel();

        scaledImage = new BufferedImage((scale * width), (scale * height), BufferedImage.TYPE_INT_RGB);
        
        imageFrame = new JFrame(this.windowName);
        
        imagePanel = new ImagePanel(scaledImage);        
        imageFrame.add(imagePanel);
        imageFrame.setSize(((scale * width) + 16), ((scale * height) + 36));
        imageFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        imageFrame.setVisible(true);
    }
    
    // Update the display of the grid
    protected void updateGridImage(){
        for(int i = 0; i < width; i++) {
            for(int j = 0; j < height; j++) {
                if(cells[i][j])
                    setCellColour(i, j, 1.0F, 1.0F, 1.0F);
                else
                    setCellColour(i, j, 0.0F, 0.0F, 0.0F);
            }
        }
        
        Graphics2D scaledImageGraphicsContext = scaledImage.createGraphics();
        scaledImageGraphicsContext.drawImage(image, 0, 0,(scale * width), (scale * height), null);
        scaledImageGraphicsContext.dispose();
    }
    
    public void displayGridImage(){
        imageFrame.validate();
        imageFrame.repaint();
    }
    
    // Set the color of a cell
    public void setCellColour(int x, int y, float red, float green, float blue){
        Color colour  = new Color(red, green, blue);
        
        raster.setDataElements(x, y, colourModel.getDataElements(colour.getRGB(), null));
    }
    
    // Copy the source grid in the destination grid
    protected void copyGrid(boolean[][] source, boolean[][] destination){
        for(int i = 0; i < width; i++)
            System.arraycopy(source[i], 0, destination[i], 0, height);
    }

    // Ask the user the rule (classic rule format)
    public void askRuleString(){
        System.out.print("Input your rule string (format Bb0,b1,.../Ss0,s1,...): ");

        try{
            this.rules.genRules(br.readLine());
        }catch(Exception e){
            System.out.println(e);
            askRuleString();
        }
    }
    
    abstract public void init();
    abstract public void init(double density);
    abstract public void nextGeneration();
            
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
    
    private class ImagePanel extends JPanel
    {
        private BufferedImage image;
    
        public ImagePanel(BufferedImage image)
        {  
            this.image = image;
        }
    
        public void paintComponent(Graphics g)   
        {  
            super.paintComponent(g);
        
            if (image == null) {
                return;
            }
    
            g.drawImage(image, 0, 0, null);
        }
    }
}
