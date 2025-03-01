package com.globalTravel.controllers.backoffice;

import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Label;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.globalTravel.models.activity.Activity;
import com.globalTravel.services.activity.ActivityService;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

public class DashBoardContent {

    @FXML private Label totalBookingsLabel;
    @FXML private Label revenueLabel;
    @FXML private Label newActivitiesLabel;

    @FXML private PieChart bookingsPieChart;
    @FXML private LineChart<String, Number> revenueTrendChart;

    @FXML
    private TableView<Activity> topActivitiesTable;

    @FXML
    private TableColumn<Activity, String> activityNameColumn;

    @FXML
    private TableColumn<Activity, String> typeColumn;

    @FXML
    private TableColumn<Activity, String> revenueColumn;

    @FXML
    private TableColumn<Activity, String> startDateColumn;

    @FXML
    private TableColumn<Activity, String> descriptionColumn;

    @FXML
    private TableColumn<Activity, String> locationColumn;

    @FXML
    private TableColumn<Activity, String> endDateColumn;

    private final ActivityService activityService = new ActivityService();

    @FXML
    public void initialize() {
        updateQuickStats();
        setupBookingsPieChart();
        setupRevenueTrendChart();
        setupTopActivitiesTable();
    }

    private void updateQuickStats() {
        List<Activity> activities = activityService.rechercher();
        int totalBookings = activities.size();
        double totalRevenue = activities.stream().mapToDouble(Activity::getPrixTotal).sum();
        int newActivities = activities.stream().filter(activity -> isNewActivity(activity)).toArray().length;

        totalBookingsLabel.setText(String.valueOf(totalBookings));
        revenueLabel.setText("$" + String.format("%.2f", totalRevenue));
        newActivitiesLabel.setText(String.valueOf(newActivities));
    }

    private boolean isNewActivity(Activity activity) {
        long currentTime = System.currentTimeMillis();
        long activityTime = activity.getDateDebut().getTime();
        return (currentTime - activityTime) < (7 * 24 * 60 * 60 * 1000); // 7 days
    }

    private void setupBookingsPieChart() {
        List<Activity> activities = activityService.rechercher();
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

        activities.stream()
                .collect(Collectors.groupingBy(Activity::getTypeActivity, Collectors.counting()))
                .forEach((type, count) -> pieChartData.add(new PieChart.Data(type.toString(), count)));

        bookingsPieChart.setData(pieChartData);
    }

    private void setupRevenueTrendChart() {
        List<Activity> activities = activityService.rechercher();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Monthly Revenue");

        activities.stream()
                .collect(Collectors.groupingBy(activity -> getMonthFromTimestamp(activity.getDateDebut()), Collectors.summingDouble(Activity::getPrixTotal)))
                .forEach((month, revenue) -> series.getData().add(new XYChart.Data<>(month, revenue)));

        revenueTrendChart.getData().add(series);
    }

    private String getMonthFromTimestamp(Timestamp timestamp) {
        return new SimpleDateFormat("MMM").format(timestamp);
    }

    private void setupTopActivitiesTable() {
        // Lier les colonnes aux propriétés de Activity
        activityNameColumn.setCellValueFactory(new PropertyValueFactory<>("nomActivity"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("typeActivity"));
        revenueColumn.setCellValueFactory(new PropertyValueFactory<>("prixTotal"));
        startDateColumn.setCellValueFactory(new PropertyValueFactory<>("dateDebut"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("localisation"));
        endDateColumn.setCellValueFactory(new PropertyValueFactory<>("dateFin"));

        // Récupérer les activités et les trier par prix (du plus cher au moins cher)
        List<Activity> activities = activityService.rechercher();
        activities.sort((a1, a2) -> Integer.compare(a2.getPrixTotal(), a1.getPrixTotal()));

        // Ajouter les activités triées au tableau
        ObservableList<Activity> topActivities = FXCollections.observableArrayList(activities);
        topActivitiesTable.setItems(topActivities);
    }
}