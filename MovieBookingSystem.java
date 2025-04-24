import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

class Movie {
    String title;
    String time;
    String theater;

    Movie(String title, String time, String theater) {
        this.title = title;
        this.time = time;
        this.theater = theater;
    }

    @Override
    public String toString() {
        return title + " at " + time + " in " + theater;
    }
}

class Booking {
    String username;
    Movie movie;
    int seats;

    Booking(String username, Movie movie, int seats) {
        this.username = username;
        this.movie = movie;
        this.seats = seats;
    }

    @Override
    public String toString() {
        return "User: " + username + ", Movie: " + movie + ", Seats: " + seats;
    }
}

public class MovieBookingSystem {
    static java.util.List<Movie> movies = new java.util.ArrayList<>();
    static String currentUser = null;

    public static void main(String[] args) {
        loadMovies();
        SwingUtilities.invokeLater(() -> new LoginSignupGUI());
    }

    public static void setCurrentUser(String user) {
        currentUser = user;
        new MainMenuGUI();
    }

    static void loadMovies() {
        movies.add(new Movie("Avengers: Endgame", "6:00 PM", "PVR Cinemas"));
        movies.add(new Movie("Inception", "9:00 PM", "INOX"));
        movies.add(new Movie("Interstellar", "3:00 PM", "Cinepolis"));
    }

    static java.util.List<Movie> getMovies() {
        return movies;
    }

    static String getCurrentUser() {
        return currentUser;
    }
}

class LoginSignupGUI extends JFrame {
    public LoginSignupGUI() {
        setTitle("Online Movie Booking - Login / Signup");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(3, 1, 10, 10));

        JButton loginBtn = new JButton("Login");
        JButton signupBtn = new JButton("Signup");

        loginBtn.addActionListener(e -> new LoginWindow());
        signupBtn.addActionListener(e -> new SignupWindow());

        add(new JLabel("Welcome to Online Movie Booking System", SwingConstants.CENTER));
        add(loginBtn);
        add(signupBtn);

        setVisible(true);
    }
}

class LoginWindow extends JFrame {
    public LoginWindow() {
        setTitle("Login");
        setSize(300, 200);
        setLayout(new GridLayout(3, 2));

        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField();
        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField();
        JButton loginBtn = new JButton("Login");

        loginBtn.addActionListener(e -> {
            String user = userField.getText();
            String pass = new String(passField.getPassword());
            if (validateLogin(user, pass)) {
                JOptionPane.showMessageDialog(this, "Login successful");
                dispose();
                MovieBookingSystem.setCurrentUser(user);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials");
            }
        });

        add(userLabel); add(userField);
        add(passLabel); add(passField);
        add(new JLabel()); add(loginBtn);

        setVisible(true);
    }

    boolean validateLogin(String user, String pass) {
        try (BufferedReader br = new BufferedReader(new FileReader("users.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(user) && parts[1].equals(pass)) {
                    return true;
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading user file");
        }
        return false;
    }
}

class SignupWindow extends JFrame {
    public SignupWindow() {
        setTitle("Signup");
        setSize(300, 200);
        setLayout(new GridLayout(3, 2));

        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField();
        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField();
        JButton signupBtn = new JButton("Signup");

        signupBtn.addActionListener(e -> {
            String user = userField.getText();
            String pass = new String(passField.getPassword());
            try (BufferedWriter bw = new BufferedWriter(new FileWriter("users.txt", true))) {
                bw.write(user + "," + pass);
                bw.newLine();
                JOptionPane.showMessageDialog(this, "Signup successful!");
                dispose();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error saving user");
            }
        });

        add(userLabel); add(userField);
        add(passLabel); add(passField);
        add(new JLabel()); add(signupBtn);

        setVisible(true);
    }
}

class MainMenuGUI extends JFrame {
    public MainMenuGUI() {
        setTitle("Main Menu");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 1, 10, 10));

        JButton viewBtn = new JButton("View Movies");
        JButton bookBtn = new JButton("Book Tickets");
        JButton myBookingsBtn = new JButton("My Bookings");
        JButton logoutBtn = new JButton("Logout");

        viewBtn.addActionListener(e -> viewMovies());
        bookBtn.addActionListener(e -> bookTickets());
        myBookingsBtn.addActionListener(e -> viewBookings());
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginSignupGUI();
        });

        add(viewBtn);
        add(bookBtn);
        add(myBookingsBtn);
        add(logoutBtn);

        setVisible(true);
    }

    void viewMovies() {
        StringBuilder sb = new StringBuilder("Available Movies:\n");
        int i = 1;
        for (Movie m : MovieBookingSystem.getMovies()) {
            sb.append(i++).append(": ").append(m).append("\n");
        }
        JOptionPane.showMessageDialog(this, sb.toString());
    }

    void bookTickets() {
        java.util.List<Movie> movies = MovieBookingSystem.getMovies();
        String[] options = new String[movies.size()];
        for (int i = 0; i < movies.size(); i++) {
            options[i] = movies.get(i).toString();
        }
        String selected = (String) JOptionPane.showInputDialog(this, "Choose a movie:", "Book Tickets",
                JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

        if (selected == null) return;
        int index = Arrays.asList(options).indexOf(selected);
        Movie movie = movies.get(index);

        String seatStr = JOptionPane.showInputDialog(this, "Enter number of seats:");
        if (seatStr == null) return;

        try (BufferedWriter bw = new BufferedWriter(new FileWriter("bookings.txt", true))) {
            bw.write(MovieBookingSystem.getCurrentUser() + "," + movie.title + "," + movie.time + "," + movie.theater + "," + seatStr);
            bw.newLine();
            JOptionPane.showMessageDialog(this, "Tickets booked successfully!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving booking.");
        }
    }

    void viewBookings() {
        StringBuilder sb = new StringBuilder("Your Bookings:\n");
        try (BufferedReader br = new BufferedReader(new FileReader("bookings.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(MovieBookingSystem.getCurrentUser())) {
                    sb.append("Movie: ").append(parts[1]).append(" at ").append(parts[2])
                      .append(" in ").append(parts[3]).append(", Seats: ").append(parts[4]).append("\n");
                }
            }
            JOptionPane.showMessageDialog(this, sb.toString());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading bookings.");
        }
    }
}