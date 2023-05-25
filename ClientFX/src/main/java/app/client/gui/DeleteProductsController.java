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

import java.net.URL;
import java.util.ResourceBundle;

public class DeleteProductsController extends Controller implements Initializable, AppObserver {

    private Employee employee;

    public void setEmployee(Employee employee) {
        this.employee = employee;
        employeeLabel.setText(employee.getName() + " " + employee.getSurname());

        try{
            observableList.addAll(services.getAllProducts());
        }
        catch(AppException exception){
            Alert alert = new Alert(Alert.AlertType.ERROR, exception.getMessage());
            alert.showAndWait();
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

    private ObservableList<Product> observableList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        nameProductColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        stockProductColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));
        priceProductColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        productsTableView.setItems(observableList);
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
    public void deleteProductAction() {
        try{
            Product product = productsTableView.getSelectionModel().getSelectedItem();
            if(product == null){
                return;
            }
            if(services.isProductPresentInAnyOrder(product)){
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "",
                        new ButtonType("Yes"),
                        new ButtonType("Cancel"));
                alert.getDialogPane().setContent(new Label("The selected product is part of an existent order. " +
                        "Deleting the product will automatically delete all orders that contain this product." +
                        " Do you want to continue?"));
                alert.showAndWait().ifPresent(response -> {
                    if(response.getText().equals("Yes")){
                        try {
                            services.deleteProduct(product);
                        }
                        catch(AppException ex){
                            Alert alert1 = new Alert(Alert.AlertType.ERROR, ex.getMessage());
                            alert1.showAndWait();
                        }
                    }
                });
            }
            else{
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "",
                        new ButtonType("Yes"),
                        new ButtonType("Cancel"));
                alert.getDialogPane().setContent(new Label("Pressing 'Yes' means that the product will be deleted forever. " +
                        "Do you want to continue?"));
                alert.showAndWait().ifPresent(response -> {
                    if(response.getText().equals("Yes")){
                        try{
                            services.deleteProduct(product);
                        }
                        catch(AppException ex){
                            Alert alert1 = new Alert(Alert.AlertType.ERROR, ex.getMessage());
                            alert1.showAndWait();
                        }
                    }
                });
            }
        }
        catch(Exception exception){
            Alert alert = new Alert(Alert.AlertType.ERROR, exception.getMessage());
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
