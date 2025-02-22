package com.globalTravel.tests;

import com.globalTravel.models.activity.Activity;
import com.globalTravel.models.activity.Review;
import com.globalTravel.models.activity.TypeActivity;
import com.globalTravel.models.car.CarDriver;
import com.globalTravel.models.car.Offer;
import com.globalTravel.models.car.PrivateCar;
import com.globalTravel.models.car.Route;
import com.globalTravel.models.flight.*;

import com.globalTravel.models.hotel.Chambre;
import com.globalTravel.models.hotel.Hotel;
import com.globalTravel.models.hotel.Reservation_hotel;
import com.globalTravel.models.user.Admin;
import com.globalTravel.services.activity.ActivityService;
import com.globalTravel.services.activity.ReviewService;
import com.globalTravel.services.car.CarDriverService;
import com.globalTravel.services.car.OfferService;
import com.globalTravel.services.car.PrivateCarService;
import com.globalTravel.services.car.RouteService;
import com.globalTravel.services.flight.AirlineService;
import com.globalTravel.services.flight.FlightService;
import com.globalTravel.services.flight.TicketService;


import com.globalTravel.services.hotel.ChambreService;
import com.globalTravel.services.hotel.HotelService;
import com.globalTravel.services.hotel.Reservation_hotelService;
import com.globalTravel.services.user.AdminService;
import com.globalTravel.utils.DataSource;
import java.sql.Timestamp;
import java.util.List;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

        public class Main {
            public static void main(String[] args) {
                //connection test
                DataSource ds= DataSource.getInstance();



    }

}
