package app.client.gui;

import app.model.Employee;
import app.model.Order;
import app.services.AppException;
import app.services.AppObserver;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class BackofficeController extends Controller implements Initializable {

    private Employee employee;

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
        salesmanLabel.setText(employee.getName() + " " + employee.getSurname());
    }

    @FXML
    private Label salesmanLabel;

    @FXML
    public void logoutAction(){
        try{
            services.logout(employee, null);
            stage.close();
        }
        catch(AppException ex){
            Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage());
            alert.showAndWait();
            System.out.println("Logout error " + ex.getMessage());
        }
    }

    @FXML
    public void addProductViewAction(){

    }
    @FXML
    public void deleteProductViewAction(){

    }
    @FXML
    public void updateProductViewAction(){

    }
    @FXML
    public void createOrderViewAction(){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/display-products.fxml"));
            Parent root = loader.load();

            DisplayProductsController controller = loader.getController();
            controller.set(services, stage);
            controller.setEmployee(employee);

            services.changeObserverForClient(employee, controller);

            stage.setScene(new Scene(root));
            stage.show();
        }
        catch(IOException | AppException ex){
            Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage());
            alert.showAndWait();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
