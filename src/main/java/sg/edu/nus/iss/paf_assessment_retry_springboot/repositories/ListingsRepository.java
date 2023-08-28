package sg.edu.nus.iss.paf_assessment_retry_springboot.repositories;

import java.util.List;

import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class ListingsRepository {
    
    private static final String C_NAME = "listings";

    private MongoTemplate template;

	public ListingsRepository(MongoTemplate template) {
		this.template = template;
	}

	public List<String> findAllCountries() {
		return template.findDistinct(new Query(), 
                "address.country", 
                C_NAME, 
                String.class);
	}

    public List<Document> findAccomodationList(
            String country,
            int numberOfPerson,
            int priceMin,
            int priceMax) 
    {
        Query query = Query.query(
            Criteria.where("address.country").is(country)
                .and("accommodates").is(numberOfPerson)
                .and("price").gte(priceMin).lte(priceMax));

        query.fields().include(
                "_id", 
                "address.street", 
                "price", 
                "images.picture_url");
        
        return template.find(query, Document.class, C_NAME);
    }

    public Document findAccomodationById(String id) {
        Query query = Query.query(Criteria.where("_id").is(id));
        query.fields().include(
            "_id",
            "description",
            "address.street",
            "address.suburb",
            "address.country",
            "price",
            "amenities",
            "images.picture_url");

        return template.find(query, Document.class, C_NAME).get(0);
    }
}
