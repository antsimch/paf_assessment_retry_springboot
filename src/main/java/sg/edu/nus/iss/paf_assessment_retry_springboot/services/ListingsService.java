package sg.edu.nus.iss.paf_assessment_retry_springboot.services;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import org.bson.Document;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sg.edu.nus.iss.paf_assessment_retry_springboot.models.Booking;
import sg.edu.nus.iss.paf_assessment_retry_springboot.models.UpdateException;
import sg.edu.nus.iss.paf_assessment_retry_springboot.repositories.BookingsRepository;
import sg.edu.nus.iss.paf_assessment_retry_springboot.repositories.ListingsRepository;

@Service
public class ListingsService {
    
    private ListingsRepository listingsRepo;

    private BookingsRepository bookingsRepo;

	public ListingsService(
            ListingsRepository listingsRepo, 
            BookingsRepository bookingsRepo) 
    {
		this.listingsRepo = listingsRepo;
        this.bookingsRepo = bookingsRepo;
    }
	
	public List<String> findAllCountries() {
		return listingsRepo.findAllCountries();
	}

    public List<Document> findAccomodationList(
            String country,
            int numberOfPerson,
            int priceMin,
            int priceMax) 
    {
        return listingsRepo.findAccomodationList(
                country, 
                numberOfPerson, 
                priceMin, 
                priceMax);
    }
    
    public Document getAccomodationById(String id) {
        return listingsRepo.findAccomodationById(id);        
    }

    @Transactional(rollbackFor = { UpdateException.class, SQLException.class })
    public String createBooking(Booking booking) throws UpdateException {

        if (booking.getDuration() > bookingsRepo.findVacancy(booking.getId()))
            throw new UpdateException("No vacancy available");

        bookingsRepo.updateIntoOccupancy(
                booking.getDuration(), 
                booking.getId());
        
        String bookingRef = UUID.randomUUID().toString().substring(0, 8);
        bookingsRepo.insertIntoReservation(booking, bookingRef);

        return bookingRef;
    }
}
