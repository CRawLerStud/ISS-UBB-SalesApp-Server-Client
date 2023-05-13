package app.client.gui;

import app.model.Employee;
import app.model.Order;
import app.services.AppException;
import app.services.AppObserver;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

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

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
