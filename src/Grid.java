import java.lang.Math;
import java.util.Random;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;

public abstract class Grid {
    protected int width, height, scale, knn, ruleSize;
    protected int[] rules;
    private static Random rand = new Random();
    
    private BufferedImage image, scaledImage;
    private WritableRaster raster;
    private ColorModel colourModel;
    
    private String windowName;
    
    JFrame imageFrame;
    ImagePanel imagePanel;
    
    protected boolean[][] cells;
    
    public boolean isFinished = false;
    
    public Grid(String name, int width, int height, int scale, int knn){
        this.windowName = name;
        this.width = width;
        this.height = height;
        this.scale = scale;
        this.knn = knn;
        this.ruleSize = (int) Math.pow(2, this.knn - 5);
        this.ruleSize = this.ruleSize == 0 ? 1 : this.ruleSize;
        
        this.rules = new int[this.ruleSize];
        this.rules = randomRules(this.rules.length);

        cells = new boolean[width][height];
        
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
    
    // Generate random rules (Integer format)
    public static int[] randomRules(int length){
        int[] rules = new int[length];
        for(int i = 0; i < length; i++)
            rules[i] = rand.nextInt();
        return rules;
    }

    // Display the set of rules (Integer format)
    public void printRules(){
        for(int rule : this.rules)
            System.out.print(rule + " ");
        System.out.println();
    }

    // Set the rules of the cellular automata
    public void setRules(int[] newRules){
        assert this.rules.length == newRules.length;
        this.rules = newRules;    
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
    private void setCellColour(int x, int y, float red, float green, float blue){
        Color colour  = new Color(red, green, blue);
        
        raster.setDataElements(x, y, colourModel.getDataElements(colour.getRGB(), null));
    }
    
    protected void copyGrid(boolean[][] source, boolean[][] destination){
        for(int i = 0; i < width; i++)
            System.arraycopy(source[i], 0, destination[i], 0, height);
    }
    
    // Retrieve the new state corresponding to the pattern
    protected boolean nextState(int pattern){
        int subRuleIndex = pattern >>> 5;
        int stateIndex = pattern & 31;
        boolean newState = ((this.rules[subRuleIndex] >>> stateIndex) & 1) == 1;
        return newState;
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
