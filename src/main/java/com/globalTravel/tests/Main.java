package com.globalTravel.tests;

import com.globalTravel.models.Admin;
import com.globalTravel.services.AdminService;
import com.globalTravel.utils.DataSource;

import java.sql.Date;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        //connection test
        DataSource ds= DataSource.getInstance();

//         Créer un objet Admin
        Admin admin = new Admin(
                0, // L'id sera auto-généré par la base de données
                "Homme",
                new Date(1990, 5, 10), // Date de naissance (format année, mois, jour)
                "Tunis, Tunisie",
                "admin@example.com",
                "Admin",
                "password123",
                "John",
                "Doe",
                "123456789",
                "image.jpg",
                "Actif",
                "Super Admin"
        );

// Instancier le service AdminService
        AdminService adminService = new AdminService();

        // Ajouter un Admin dans la base de données
        adminService.ajouter(admin);

        // Récupérer tous les Admins et les afficher
      List<Admin> admins = adminService.rechercher();
       System.out.println("Liste des Admins :");
        for (Admin a : admins) {
            System.out.println(a.getFirstName() + " " + a.getLastName());
        }

       // Modifier un Admin (vous devez avoir l'id de l'Admin à modifier)
        if (!admins.isEmpty()) {
            Admin adminToModify = admins.get(5); // Modifier le premier Admin trouvé
            adminToModify.setFirstName("Jonathan");
            adminService.modifier(adminToModify);
            System.out.println("Admin modifié !");
        }

        // Supprimer un Admin (encore une fois, avec un id valide)
        if (!admins.isEmpty()) {
            Admin adminToDelete = admins.get(5); // Supprimer le premier Admin trouvé
            adminService.supprimer(adminToDelete);
            System.out.println("Admin supprimé !");
        }

    }
}
