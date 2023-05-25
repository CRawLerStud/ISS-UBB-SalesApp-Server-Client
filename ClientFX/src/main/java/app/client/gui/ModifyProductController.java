package app.client.gui;

import app.model.Employee;
import app.model.Order;
import app.model.OrderEntry;
import app.model.Product;
import app.services.AppException;
import app.services.AppObserver;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ModifyProductController extends Controller implements Initializable, AppObserver {
    private Employee employee;

    public void setEmployee(Employee employee) {
        this.employee = employee;
        employeeLabel.setText(employee.getName() + " " + employee.getSurname());

        productsTableView.setItems(observableList);

        try{
            observableList.addAll(services.getAllProducts());
        }
        catch(AppException ex){
            Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage());
            alert.showAndWait();
            System.out.println(ex.getMessage());
        }
    }

    @FXML
    public TableView<Product> productsTableView;
    @FXML
    public TableColumn<Product, String> nameProductColumn;
    @FXML
    public TableColumn<Product, Integer> stockProductColumn;
    @FXML
    public TableColumn<Product, Double> priceProductColumn;
    @FXML
    public Label employeeLabel;
    @FXML
    public TextField nameTextField;
    @FXML
    public TextField stockTextField;
    @FXML
    public TextField priceTextField;

    private ObservableList<Product> observableList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        nameProductColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        stockProductColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));
        priceProductColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        productsTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                nameTextField.setText(newSelection.getName());
                stockTextField.setText(newSelection.getStock().toString());
                priceTextField.setText(newSelection.getPrice().toString());
            }
        });
    }

    @FXML
    public void logoutAction(){
        try {
            services.logout(employee, null);
            stage.close();
        } catch (AppException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage());
            alert.showAndWait();
            System.out.println("Logout error " + ex.getMessage());
        }
    }

    @FXML
    public void updateProductAction() {
        try{
            String name = nameTextField.getText();
            Integer stock = Integer.valueOf(stockTextField.getText());
            Double price = Double.valueOf(priceTextField.getText());
            Long ID = productsTableView.getSelectionModel().getSelectedItem().getId();

            services.updateProduct(new Product(ID, name, stock, price));

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "The product has been updated!");
            alert.showAndWait();
        }
        catch(Exception ex){
            Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage());
            alert.showAndWait();
        }
    }

    @Override
    public void deleteProductUpdate(Product product) throws AppException {
        System.out.println("Delete Product Update has been called!");
        Platform.runLater(() -> {
            observableList.remove(product);
        });
    }
    @Override
    public void orderUpdate(Order order) throws AppException {
        System.out.println("Order update has been called!");
        Platform.runLater(() -> {
            for(OrderEntry orderEntry : order.getOrderEntries()){
                for(Product product : observableList.stream().toList()){
                    if(product.equals(orderEntry.getProduct())){
                        product.setStock(product.getStock() - orderEntry.getQuantity());
                        observableList.set(observableList.indexOf(product), product);
                    }
                }
            }
        });
    }

    @Override
    public void addProductUpdate(Product product) throws AppException {
        System.out.println("Product update has been called!");
        Platform.runLater(() -> {
            observableList.add(product);
        });
    }

    @Override
    public void productUpdate(Product product) throws AppException {
        System.out.println("Product update has been called!");
        Platform.runLater(() -> {
            for(Product observableProduct : observableList.stream().toList()){
                if(observableProduct.equals(product)){
                    observableList.set(observableList.indexOf(observableProduct), product);
                }
            }
        });
    }

}
