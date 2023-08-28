package sg.edu.nus.iss.paf_assessment_retry_springboot.controllers;

import java.util.List;
import org.bson.Document;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.validation.Valid;
import sg.edu.nus.iss.paf_assessment_retry_springboot.models.Booking;
import sg.edu.nus.iss.paf_assessment_retry_springboot.models.Util;
import sg.edu.nus.iss.paf_assessment_retry_springboot.services.ListingsService;

@RestController
@RequestMapping(path = "/api")
public class ListingsController {
    
    private ListingsService listingsSvc;

	public ListingsController(ListingsService listingsSvc) {
		this.listingsSvc = listingsSvc;
	}

	@GetMapping(path = "/countries", 
            produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getAllCountries() {

		List<String> countryList = listingsSvc.findAllCountries();
		
		if (countryList.isEmpty())
			return ResponseEntity.internalServerError().build();

		return ResponseEntity.ok(
                Json.createArrayBuilder(countryList).build().toString());
	}

    @GetMapping(path = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getAccomodationLists(
            @RequestParam String country,
            @RequestParam int numberOfPerson,
            @RequestParam int priceMin,
            @RequestParam int priceMax) 
    {
        List<Document> listings = listingsSvc.findAccomodationList(
                country, 
                numberOfPerson, 
                priceMin, 
                priceMax);
        
        if (listings.isEmpty())
            return ResponseEntity.notFound().build();
        
        JsonArrayBuilder arr = Json.createArrayBuilder();
        listings.stream()
                .map(v -> {return Util.documentToListingJSON(v);})
                .forEach(v -> arr.add(v));
        
        JsonArray listingsArr = arr.build();
        return ResponseEntity.ok(listingsArr.toString());
    }

    @GetMapping(path = "/details/{id}", 
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getAccomodationById(
            @PathVariable(required = true) String id) 
    {
        Document details = listingsSvc.getAccomodationById(id);

        if (details == null) 
            return ResponseEntity.notFound().build();
           
        return ResponseEntity.ok(
                Util.documentToDetailsJSON(details).toString());
    }

    @PostMapping(path = "/booking")
    public ResponseEntity<String> createBooking(
            @RequestBody @Valid Booking booking,
            BindingResult binding,
            Model model) 
    {
        System.out.println("\n\nIn controller createBooking()\n\n");
        if (binding.hasErrors()) {
            JsonObjectBuilder builder = Json.createObjectBuilder();
            binding.getAllErrors()
                .forEach(error -> {
                    builder.add(
                        ((FieldError) error).getField(), 
                        error.getDefaultMessage());
                    }
                );
            JsonObject obj = builder.build();
            return ResponseEntity
                    .status(HttpStatusCode.valueOf(400))
                    .body(obj.toString());
        }

        try {
            String bookingRef = listingsSvc.createBooking(booking);
            JsonObject obj = Json.createObjectBuilder()
                    .add("bookingRef", bookingRef)
                    .build();

            return ResponseEntity.ok(obj.toString());

        } catch (Exception ex) {

            JsonObject error = Json.createObjectBuilder()
                    .add("error", ex.getMessage())
                    .build();

            return ResponseEntity.status(HttpStatusCode.valueOf(500))
                    .body(error.toString());
        }
    }
}
