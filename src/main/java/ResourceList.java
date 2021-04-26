import java.util.ArrayList;
import java.util.Optional;

public class ResourceList {
    public ArrayList<Resource> resources;
    public void add(Resource resource){
        resources.add(resource);
    }

    public ResourceList() {
        this.resources = new ArrayList<>();
    }

    public Optional<Resource> findByName(String name){
        return resources.stream().filter(c -> c.name.equals(name)).findAny();
    }
    public String toString(){
        StringBuilder output = new StringBuilder();
        for (Resource resource : resources){
            output.append(resource.value).append(" ").append(resource.name).append("\n");
        }
        return output.toString();
    }
}
