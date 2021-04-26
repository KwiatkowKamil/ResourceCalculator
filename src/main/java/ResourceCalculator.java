import com.google.gson.*;
import com.google.gson.stream.JsonReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;

public class ResourceCalculator {
    private Recipe [] recipes;
    private HashSet<String> raw;
    private HashMap<String, Recipe> outputMap;

    private void loadRecipes(FileReader fileReader){
        JsonReader reader = new JsonReader(fileReader);
        Gson gson = new Gson();
        JsonElement element = gson.fromJson(reader, JsonElement.class);
        JsonObject object = element.getAsJsonObject();
        JsonArray array = object.getAsJsonArray("recipes");
        recipes = gson.fromJson(array.toString(), Recipe[].class);
    }

    private HashSet<String> getRawList(){
        HashSet<String> inputs = new HashSet<>();
        HashSet<String> outputs = new HashSet<>();
        for (Recipe recipe : recipes){
            for (Resource inputName : recipe.input){
                inputs.add(inputName.name);
            }
            outputs.add(recipe.output.name);
        }
        inputs.removeAll(outputs);
        return inputs;
    }
    private HashMap<String, Recipe> getOutputMap(){
        HashMap<String, Recipe> outputs = new HashMap<>();
        for (Recipe recipe : recipes){
            outputs.put(recipe.output.name, recipe);
        }
        return outputs;
    }

    private ResourceList calculateRaw(Resource resource, ResourceList resourceList) {
        String name = resource.name;
        if (raw.contains(name)){
            Optional<Resource> output = resourceList.findByName(name);
            ResourceList finalResourceList = resourceList;
            output.ifPresentOrElse(outResource -> outResource.value += resource.value, () -> finalResourceList.add(resource));
            return finalResourceList;
        }else{
            Recipe recipe = outputMap.get(name);
            for (Resource input : recipe.input){
                double value = input.value/recipe.output.value * resource.value;
                resourceList = calculateRaw(new Resource(input.name, value), resourceList);
            }
            return resourceList;
        }
    }

    public void loadRecipesFromFile(File file){
        try {
            loadRecipes(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    public ResourceList getCostOfProducing(Resource resource){
        raw = getRawList();
        outputMap = getOutputMap();
        ResourceList resources = new ResourceList();
        return calculateRaw(resource, resources);
    }



    public static void main(String[] args) {
        File file = new File("/home/soph/IdeaProjects/ResourceCalculator/src/main/resources/recipes.json");
        ResourceCalculator rc = new ResourceCalculator();
        rc.loadRecipesFromFile(file);
        Resource resource = new Resource("inserter", 1);
        ResourceList rs = rc.getCostOfProducing(resource);
        System.out.println(rs);
    }

}