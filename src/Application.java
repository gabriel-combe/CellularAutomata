public class Application
{
    final static int DELAY = 100;
    
    public static void main(String[] args)
    {
        // CA1D cellularAutomata = new CA1D(1200, 720, 1, 3);
        CA2D cellularAutomata = new CA2D(500, 500, 2, 25);

        /*
         * TODO:
         *      - Implement the possibility to load initial map setup using RLE format
         *      - Add a user interface
         */

        // Neighbour count rules
        cellularAutomata.askRuleString();

        // Display the rules
        // cellularAutomata.printRules();
        
        // Grid initialisation
        // cellularAutomata.init();
        // cellularAutomata.init(0.1);
        cellularAutomata.rectangleCenter(3, 5);

        // Explicit rules
        // cellularAutomata.rules.setRules(new int[]{30});
        
        cellularAutomata.displayGridImage();
        
        // Main loop for the cellular automata
        while(!cellularAutomata.isFinished){
            try {
                Thread.sleep(DELAY);
            } catch (InterruptedException e) {
                System.out.println(e.toString());
            }

            cellularAutomata.nextGeneration();
            
            cellularAutomata.displayGridImage();
        }
    }

    
}
