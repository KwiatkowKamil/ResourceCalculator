import java.io.File;
public class Main {
    public static void main(String[] args) {
        ResourceCalculator rc = new ResourceCalculator("/home/soph/IdeaProjects/ResourceCalculator/src/main/resources/recipes.json");
        Resource resource = new Resource("inserter", 3);
        ResourceList rs = rc.getCostOfProducing(resource);
        System.out.println(rs);
    }
}
