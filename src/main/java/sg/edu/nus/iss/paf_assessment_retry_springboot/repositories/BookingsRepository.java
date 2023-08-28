package sg.edu.nus.iss.paf_assessment_retry_springboot.repositories;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import sg.edu.nus.iss.paf_assessment_retry_springboot.models.Booking;

@Repository
public class BookingsRepository {

    private static final String SQL_FIND_VACANCY = """
            select vacancy from acc_occupancy where acc_id = ?
            """;

    private static final String SQL_UPDATE_OCCUPANCY = """
            update acc_occupancy set vacancy = vacancy - ? 
            where acc_id = ?         
            """;

    private static final String SQL_INSERT_INTO_RESERVATIONS = """
            insert into reservations 
            (resv_id, name, email, acc_id, arrival_date, duration)
            values (?, ?, ?, ?, ?, ?)
            """;
    
    private JdbcTemplate template;

    public BookingsRepository(JdbcTemplate template) {
        this.template = template;
    }

    public Integer findVacancy(String id) {
        SqlRowSet rs = template.queryForRowSet(SQL_FIND_VACANCY, id);
        rs.next();
        return rs.getInt("vacancy");
    }

    public boolean updateIntoOccupancy(int duration, String id) {
        return template.update(
                SQL_UPDATE_OCCUPANCY, 
                duration, 
                id) > 0;
    }

    public boolean insertIntoReservation(Booking booking, String bookingRef) {
        return template.update(
                SQL_INSERT_INTO_RESERVATIONS, 
                bookingRef,
                booking.getName(),
                booking.getEmail(),
                booking.getId(),
                booking.getArrival(),
                booking.getDuration()) > 0;
    }
}
