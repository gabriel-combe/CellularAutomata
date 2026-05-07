import java.io.IOException;

public class Application
{
    final static int DELAY = 1000;
    
    public static void main(String[] args) throws IOException
    {
        // CA1D cellularAutomata = new CA1D(1200, 720, 1, 3);
        CA2D cellularAutomata = new CA2D(500, 500, 2, 25);

        // Load a map in RLE format
        // LoadMap.load("10enginecordership.rle");
        // LoadMap.load("maps/gardensofeden2009.rle");
        // LoadMap.load("maps/block.rle");
        // LoadMap.load("maps/glider.rle");
        // LoadMap.load("maps/gosperglidergun.rle");

        /*
         * TODO:
         *      - Add a user interface
         */
        
        // Grid initialisation
        // cellularAutomata.init();
        // cellularAutomata.init(0.1);
        cellularAutomata.rectangleCenter(3, 5);
        // cellularAutomata.setGrid(LoadMap.getGrid());

        cellularAutomata.displayGridImage();

        // Neighbour count rules
        cellularAutomata.askRuleString();

        // Display the rules
        cellularAutomata.rules.printRules();

        // Explicit rules
        // cellularAutomata.rules.setRules(new int[]{30});

        // Save initial state (generation 0)
        cellularAutomata.saveGridImage();

        // Main loop for the cellular automata
        while(!cellularAutomata.isFinished){
            try {
                Thread.sleep(DELAY);
            } catch (InterruptedException e) {
                System.out.println(e.toString());
            }

            cellularAutomata.nextGeneration();
            // cellularAutomata.saveGridImage();
            cellularAutomata.displayGridImage();
        }
    }

    
}
