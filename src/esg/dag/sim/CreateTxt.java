package esg.dag.sim;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class CreateTxt {
    public void createTxtFile(ArrayList<ArrayList<Float>> list, String name) throws FileNotFoundException {

        // for the header, since it might be different, later on we can create sampler header row for the txt file
        PrintWriter pr = new PrintWriter("./"+name+".txt");
        pr.println("column");
        for (int i=0; i<list.size() ; i++){
            for(int j = 0; j < list.get(i).size(); j++) {
                pr.print(list.get(i).get(j));
                pr.print(" ");
            }
            pr.println("");
        }
        pr.close();
    }
}
