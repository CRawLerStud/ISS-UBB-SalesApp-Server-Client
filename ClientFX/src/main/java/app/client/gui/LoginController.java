package app.client.gui;

import app.model.Employee;
import app.services.AppException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class LoginController extends Controller{

    @FXML
    public TextField usernameField;
    @FXML
    public PasswordField passwordField;

    @FXML
    private void loginAction(){
        String username = usernameField.getText();
        String password = passwordField.getText();

        try{
            Employee employee = services.login(username, password, null);

            if(employee.getClass().getSimpleName().equals("Admin")){

                FXMLLoader backOfficeLoader = new FXMLLoader(getClass().getResource("/backoffice.fxml"));
                Parent root = backOfficeLoader.load();

                BackofficeController backofficeController = backOfficeLoader.getController();
                backofficeController.set(services, stage);
                backofficeController.setEmployee(employee);

                stage.setScene(new Scene(root));

                stage.setOnCloseRequest(event -> {
                    backofficeController.logoutAction();
                    stage.close();
                });

            }
            else if(employee.getClass().getSimpleName().equals("Salesman")){
                FXMLLoader displayProductsLoader = new FXMLLoader(getClass().getResource("/display-products.fxml"));
                Parent root = displayProductsLoader.load();

                DisplayProductsController displayProductsController = displayProductsLoader.getController();
                displayProductsController.set(services, stage);
                displayProductsController.setEmployee(employee);

                services.changeObserverForClient(employee, displayProductsController);

                stage.setScene(new Scene(root));

                stage.setOnCloseRequest(event -> {
                    displayProductsController.logoutAction();
                    stage.close();
                });
            }
            else{
                throw new AppException("Invalid type of employee found!");
            }
        }
        catch(AppException | IOException e){
            Alert alert = new Alert(Alert.AlertType.WARNING, e.getMessage());
            alert.showAndWait();
        }
    }

}
