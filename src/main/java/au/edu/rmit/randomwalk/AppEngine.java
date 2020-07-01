package au.edu.rmit.randomwalk;

import au.edu.rmit.randomwalk.experiment.*;
import au.edu.rmit.randomwalk.io.FileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Hello world!
 */
@Service
public class AppEngine {

    private static Logger logger = LoggerFactory.getLogger(AppEngine.class);


    public static void main(String[] args) {

        System.out.println(args[0]+"   "+args[1]);

        if(args.length!=2){
            System.out.println("please input three parameters (K boundSel)");
            return;
        }

        switch (args[0]){
            case "K":
            VaryingK varyingK = new VaryingK();
            switch (args[1]){
                case "boundSel" : varyingK.boundSel(); break;
                case "approximateGreedy" : varyingK.approximateGreedy(); break;
                case "dpGreedy" : varyingK.dpGreedy(); break;
                case "matrixSel" : varyingK.matrixSel(); break;
                case "degreeGreedy" : varyingK.degreeGreedy(); break;
            }
            break;
            case "L":
            VaryingL varyingL = new VaryingL();
            switch (args[1]){
                case "boundSel" : varyingL.boundSel(); break;
                case "approximateGreedy" : varyingL.approximateGreedy(); break;
                case "dpGreedy" : varyingL.dpGreedy(); break;
                case "matrixSel" : varyingL.matrixSel(); break;
                case "degreeGreedy" : varyingL.degreeGreedy(); break;
            }
            break;
        }

//        Mem men = new Mem(Integer.parseInt(args[0]));
//        switch (args[1]){
//            case "boundSel" : men.boundSel(); break;
//            case "approximateGreedy" : men.approximateGreedy(); break;
//            case "dpGreedy" : men.dpGreedy(); break;
//            case "matrixSel" : men.matrixSel(); break;
//            case "degreeGreedy" : men.degreeGreedy(); break;
//        }


//        Scalability scalability = new Scalability(Integer.parseInt(args[0]));
//        switch (args[1]){
//            case "boundSel" : scalability.boundSel(); break;
//            case "approximateGreedy" : scalability.approximateGreedy(); break;
//        }


    }
}
