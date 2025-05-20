package com.globalTravel.controllers.frontoffice;

import com.globalTravel.models.car.PrivateCar;
import com.globalTravel.services.activity.ActivityService;
import com.globalTravel.services.car.CarReservationService;
import com.globalTravel.services.car.PrivateCarService;
import com.globalTravel.services.flight.FlightBookingService;
import com.globalTravel.services.flight.FlightService;
import com.globalTravel.services.hotel.HotelService;
import com.globalTravel.services.hotel.Reservation_hotelService;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class FrontOfficeContent implements FrontNavigatable {

    @FXML private Label labelUserName;
    @FXML private Label labelTotalFlights;
    @FXML private Label labelTotalCars;
    @FXML private Label labelTotalHotels;
    @FXML private Label labelTotalActivities;
    // Pie Charts
    @FXML
    private PieChart bookingDistribution;
    @FXML
    private PieChart revenueDistribution;



    private PrivateCarService privateCarService = new PrivateCarService();
    private HotelService hotelService = new HotelService();
    private FlightService flightService = new FlightService();
    private ActivityService activityService = new ActivityService();
    private Reservation_hotelService reservation_hotelService = new Reservation_hotelService();
    private CarReservationService carReservationService = new CarReservationService();
    private FrontOffice frontOfficeController;

    @FXML
    public void initialize() {

        // Initialize Charts
        setupCharts();

    }

    /**
     * Set up the pie charts with sample data.
     */
    private void setupCharts() {
        int totalFlights = flightService.rechercher().size();
        int totalCars = privateCarService.rechercher().size();
        int totalHotels = hotelService.rechercher().size();
        int totalActivities = activityService.rechercher().size();

        int totalHotelReservations = reservation_hotelService.rechercher().size();
        int totalCarReservations = carReservationService.rechercher().size();

        labelTotalFlights.setText(String.valueOf(totalFlights));
        labelTotalCars.setText(String.valueOf(totalCars));
        labelTotalHotels.setText(String.valueOf(totalHotels));
        labelTotalActivities.setText(String.valueOf(totalActivities));

        // Booking Distribution Chart
        ObservableList<PieChart.Data> bookingData = FXCollections.observableArrayList(
                new PieChart.Data("Flights", totalFlights),
                new PieChart.Data("Cars", totalCars),
                new PieChart.Data("Hotels", totalHotels),
                new PieChart.Data("Activities", totalActivities)
        );
        bookingDistribution.setData(bookingData);

        // Revenue Distribution Chart
        ObservableList<PieChart.Data> revenueData = FXCollections.observableArrayList(
                new PieChart.Data("Cars", totalCarReservations),
                new PieChart.Data("Hotels", totalHotelReservations)
        );
        revenueDistribution.setData(revenueData);
    }


    @Override
    public void setFrontOfficeController(FrontOffice frontOfficeController) {
        this.frontOfficeController = frontOfficeController;

        if (frontOfficeController.getCurrentUser() != null)
             labelUserName.setText(frontOfficeController.getCurrentUser().getFirstName()+" "+frontOfficeController.getCurrentUser().getLastName());
    }
}