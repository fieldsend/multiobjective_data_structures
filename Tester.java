import java.util.Random;
import java.util.Set;
import java.util.Iterator;
/**
 * Write a description of class Tester here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Tester
{
    public static void main(String[] args) throws IllegalNumberOfObjectives {
        Random rng = new Random();
        ParetoSetManager manager = BiObjectiveSetManager.managerFactory(0L);
        final int MAX_REPS = 1000000;
        ProxySolution s = new ProxySolution(new double[]{3, 3});
        manager.add(s);
        double[] min = {s.getFitness(0), s.getFitness(1)};
        for (int i=1; i<MAX_REPS; i++) {
            //Solution e = manager.getRandomMember();
            do
                s = new ProxySolution(new double[]{rng.nextGaussian(), rng.nextGaussian()});
            while(s.getFitness(0) + s.getFitness(1) < 1);
            //s = new ProxySolution(new double[]{e.getFitness(0)+rng.nextGaussian()*0.01, e.getFitness(1)+rng.nextGaussian()*0.01});
            //s = new ProxySolution(new double[]{(double)i, (double) -i});
            manager.add(s);
            for (int j = 0; j<min.length; j++)
                if (s.getFitness(j) < min[j])
                    min[j] = s.getFitness(j);
            //System.out.println("Random values: "+ e.getFitness(0) + ", "+ e.getFitness(1));
          
        }
        Set<Solution> set = manager.getContents();
        
        Iterator<Solution> iterator = set.iterator();
        // now iterator over this, reoving each dominated element in turn, until first non-dominated element 
        // reached, in which case break out
        while (iterator.hasNext()){
            Solution e = iterator.next();
            System.out.println(e.getFitness(0) + ", " + e.getFitness(1));  
        }
        System.out.println(manager.size());
        System.out.println("Min values: "+ min[0] + ", "+ min[1]);
        
    }
}
