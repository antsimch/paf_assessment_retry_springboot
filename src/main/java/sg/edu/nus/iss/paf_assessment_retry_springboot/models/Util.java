package sg.edu.nus.iss.paf_assessment_retry_springboot.models;

import java.util.ArrayList;

import org.bson.Document;
import org.springframework.stereotype.Component;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;

@Component
public class Util {

    /*
     * Utility method to map the document returned from repository to the below JSON format
     * {
     *  id: string
     *  addressStreet: string
     *  price: number
     *  image: string
     * }
     */
    public static JsonObject documentToListingJSON(Document doc) {
        return Json.createObjectBuilder()
                .add("id", doc.getString("_id"))
                .add("addressStreet", doc.get("address", Document.class)
                        .getString("street"))
                .add("price", doc.getDouble("price"))
                .add("image", doc.get("images", Document.class)
                        .getString("picture_url"))
                .build();
    }

    /*
     * Utility method to map the document returned from repository to the below JSON format
     * {
     *  id: string
     *  description: string
     *  address: string[]
     *  price: number
     *  amenities: string[]
     * }
     */
    public static JsonObject documentToDetailsJSON(Document doc) {
        JsonArrayBuilder addressArr = Json.createArrayBuilder();
        doc.get("address", Document.class)
                .forEach((key, value) -> addressArr.add(value.toString()));

        JsonArrayBuilder amenitiesArr = Json.createArrayBuilder();
        doc.get("amenities", ArrayList.class).stream()
                .forEach(value -> amenitiesArr.add(value.toString()));

        return Json.createObjectBuilder()
                .add("id", doc.getString("_id"))
                .add("description", doc.getString("description"))
                .add("address", addressArr)
                .add("price", doc.getDouble("price"))
                .add("amenities", amenitiesArr)
                .add("image", doc.get("images", Document.class)
                        .getString("picture_url"))
                .build();
    }
}
