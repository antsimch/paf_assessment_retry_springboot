package sg.edu.nus.iss.paf_assessment_retry_springboot.models;

public class UpdateException extends Exception {
    
    private String message;

    public UpdateException(String message) {
        super();
        this.message = message;
    }
}
