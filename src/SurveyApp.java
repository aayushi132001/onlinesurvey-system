import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SurveyApp {
    private Connection conn;

    public SurveyApp() {
        try {
            Class.forName("org.sqlite.JDBC");
            // Update the connection string to the correct path
            String dbPath = "jdbc:sqlite:C:/Users/AAYUSHI/Desktop/DATABASE/survey/survey.db";
            conn = DriverManager.getConnection(dbPath);
            if (conn != null) {
                System.out.println("Connected to the database successfully");
            } else {
                System.err.println("Failed to make connection!");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver class not found: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Error connecting to database: " + e.getMessage());
        }
    }

    public void createTables() {
        if (conn == null) {
            System.err.println("Connection is null. Cannot create tables.");
            return;
        }

        try (PreparedStatement stmt = conn.prepareStatement("CREATE TABLE IF NOT EXISTS customers (id INTEGER PRIMARY KEY, name TEXT, email TEXT);")) {
            stmt.executeUpdate();
            System.out.println("Customers table created successfully");
        } catch (SQLException e) {
            System.err.println("Error creating customers table: " + e.getMessage());
        }

        try (PreparedStatement stmt = conn.prepareStatement("CREATE TABLE IF NOT EXISTS surveys (id INTEGER PRIMARY KEY, question TEXT);")) {
            stmt.executeUpdate();
            System.out.println("Surveys table created successfully");
        } catch (SQLException e) {
            System.err.println("Error creating surveys table: " + e.getMessage());
        }

        try (PreparedStatement stmt = conn.prepareStatement("CREATE TABLE IF NOT EXISTS responses (id INTEGER PRIMARY KEY, customer_id INTEGER, survey_id INTEGER, response TEXT);")) {
            stmt.executeUpdate();
            System.out.println("Responses table created successfully");
        } catch (SQLException e) {
            System.err.println("Error creating responses table: " + e.getMessage());
        }
    }

    public void addCustomer(String name, String email) {
        if (conn == null) {
            System.err.println("Connection is null. Cannot add customer.");
            return;
        }

        try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO customers (name, email) VALUES (?, ?);")) {
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.executeUpdate();
            System.out.println("Customer added successfully");
        } catch (SQLException e) {
            System.err.println("Error adding customer: " + e.getMessage());
        }
    }

    public void addSurvey(String question) {
        if (conn == null) {
            System.err.println("Connection is null. Cannot add survey.");
            return;
        }

        try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO surveys (question) VALUES (?);")) {
            stmt.setString(1, question);
            stmt.executeUpdate();
            System.out.println("Survey added successfully");
        } catch (SQLException e) {
            System.err.println("Error adding survey: " + e.getMessage());
        }
    }

    public void submitResponse(int customerId, int surveyId, String response) {
        if (conn == null) {
            System.err.println("Connection is null. Cannot submit response.");
            return;
        }

        try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO responses (customer_id, survey_id, response) VALUES (?, ?, ?);")) {
            stmt.setInt(1, customerId);
            stmt.setInt(2, surveyId);
            stmt.setString(3, response);
            stmt.executeUpdate();
            System.out.println("Response submitted successfully");
        } catch (SQLException e) {
            System.err.println("Error submitting response: " + e.getMessage());
        }
    }

    public List<String> getResponses(int surveyId) {
        List<String> responses = new ArrayList<>();
        if (conn == null) {
            System.err.println("Connection is null. Cannot get responses.");
            return responses;
        }

        try (PreparedStatement stmt = conn.prepareStatement("SELECT response FROM responses WHERE survey_id = ?;")) {
            stmt.setInt(1, surveyId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    responses.add(rs.getString("response"));
                }
            }
            System.out.println("Responses fetched successfully");
        } catch (SQLException e) {
            System.err.println("Error getting responses: " + e.getMessage());
        }
        return responses;
    }

    public void sendPromotionalEmail(String email, String message) {
        // Implement email sending logic here
        System.out.println("Sending email to " + email + ": " + message);
    }

    public static void main(String[] args) {
        SurveyApp app = new SurveyApp();
        app.createTables();

        app.addCustomer("John Doe", "john@example.com");
        app.addCustomer("Jane Doe", "jane@example.com");

        app.addSurvey("What do you think of our product?");
        app.addSurvey("How likely are you to recommend us?");

        app.submitResponse(1, 1, "It's great!");
        app.submitResponse(1, 2, "Very likely");
        app.submitResponse(2, 1, "It's okay");
        app.submitResponse(2, 2, "Not very likely");

        List<String> responses = app.getResponses(1);
        System.out.println("Responses to survey 1:");
        for (String response : responses) {
            System.out.println(response);
        }

        app.sendPromotionalEmail("john@example.com", "Thanks for taking our survey!");
    }
}
