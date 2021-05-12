import com.google.gson.*;
import com.google.gson.stream.JsonReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;

public class ResourceCalculator {
    private final Recipe [] recipes;
    private final HashSet<String> raw;
    private final HashMap<String, Recipe> outputMap;

    public ResourceCalculator(String path) {
        this.recipes = loadRecipesFromFile(new File(path));
        this.raw = createRawList();
        this.outputMap = createOutputMap();
    }

    private Recipe [] loadRecipes(FileReader file){
        JsonReader reader = new JsonReader(file);
        Gson gson = new Gson();
        JsonElement element = gson.fromJson(reader, JsonElement.class);
        JsonObject object = element.getAsJsonObject();
        JsonArray array = object.getAsJsonArray("recipes");
        return gson.fromJson(array.toString(), Recipe[].class);
    }

    private HashSet<String> createRawList(){
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
    private HashMap<String, Recipe> createOutputMap(){
        HashMap<String, Recipe> outputs = new HashMap<>();
        for (Recipe recipe : recipes){
            outputs.put(recipe.output.name, recipe);
        }
        return outputs;
    }

    private ResourceList addRawMaterial(Resource resource, ResourceList resourceList) {
        Optional<Resource> output = resourceList.findByName(resource.name);
        if(output.isPresent()){
            output.get().value += resource.value;
        }else{
            resourceList.add(resource);
        }
        return resourceList;
    }

    private ResourceList addCompoundMaterial(Resource resource, ResourceList resourceList) {
        Recipe recipe = outputMap.get(resource.name);
        for (Resource input : recipe.input){
            double value = input.value/recipe.output.value * resource.value;
            resourceList = calculateRaw(new Resource(input.name, value), resourceList);
        }
        return resourceList; //test
    }
    private ResourceList calculateRaw(Resource resource, ResourceList resourceList) {
        if (isRaw(resource)) {
            return addRawMaterial(resource, resourceList);
        }
        return addCompoundMaterial(resource, resourceList);
    }

    private boolean isRaw(Resource resource) {
        return raw.contains(resource.name);
    }

    public Recipe[] loadRecipesFromFile(File file){
        Recipe[] output = new Recipe[0];
        try {
            output = loadRecipes(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return output;
    }

    public ResourceList getCostOfProducing(Resource resource){
        ResourceList resources = new ResourceList();
        return calculateRaw(resource, resources);
    }

}
