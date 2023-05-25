package app.client.gui;

import app.model.Employee;
import app.model.Product;
import app.services.AppException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AddProductController extends Controller implements Initializable {


    private Employee employee;

    public void setEmployee(Employee employee) {
        this.employee = employee;
        employeeLabel.setText(employee.getName() + " " + employee.getSurname());
    }

    public Label employeeLabel;
    public TextField nameTextField;
    public TextField stockTextField;
    public TextField priceTextField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    public void backAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/backoffice.fxml"));
            Parent root = loader.load();

            BackofficeController controller = loader.getController();
            controller.set(services, stage);
            controller.setEmployee(employee);

            services.changeObserverForClient(employee, null);

            stage.setScene(new Scene(root));

            stage.setOnCloseRequest(event -> {
                controller.logoutAction();
                stage.close();
            });
        } catch (AppException | IOException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    public void logoutAction() {
        try {
            services.logout(employee, null);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Parent root = loader.load();

            LoginController controller = loader.getController();
            controller.set(services, stage);

            Scene newScene = new Scene(root);
            stage.setScene(newScene);

            stage.setOnCloseRequest(event -> {
                stage.close();
            });
        } catch (AppException | IOException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage());
            alert.showAndWait();
            System.out.println("Logout error " + ex.getMessage());
        }
    }

    @FXML
    public void addProductAction() {
        try{

            String name = nameTextField.getText();
            Integer stock = Integer.parseInt(stockTextField.getText());
            Double price = Double.parseDouble(priceTextField.getText());

            Product product = new Product(-1L, name, stock, price);
            services.addProduct(product);

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Product saved!");
            alert.showAndWait();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/backoffice.fxml"));
            Parent root = loader.load();

            BackofficeController controller = loader.getController();
            controller.set(services, stage);
            controller.setEmployee(employee);

            stage.setScene(new Scene(root));
            stage.show();

        }
        catch(NullPointerException | NumberFormatException | AppException | IOException exception){
            Alert alert = new Alert(Alert.AlertType.ERROR, exception.getMessage());
            alert.showAndWait();
        }
    }
}
