package app.client.gui;

import app.client.gui.model_for_display.OrderEntryForTableView;
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
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class DisplayProductsController extends Controller implements Initializable, AppObserver {


    private Employee employee;

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
        salesmanLabel.setText(employee.getName() + " " + employee.getSurname());
        populateProductsTable();
    }

    @FXML
    public Label salesmanLabel;
    @FXML
    public TableView<Product> productsTable;
    @FXML
    public TableColumn<Product, String> productsNameColumn;
    @FXML
    public TableColumn<Product, Integer> productsStockColumn;
    @FXML
    public TableColumn<Product, Double> productsPriceColumn;
    @FXML
    public TableView<OrderEntryForTableView> orderTable;
    @FXML
    public TableColumn<OrderEntryForTableView, String> orderProductNameColumn;
    @FXML
    public TableColumn<OrderEntryForTableView, Integer> orderProductQuantityColumn;
    @FXML
    public TextField quantityTextField;

    private final ObservableList<Product> productsObservableList = FXCollections.observableArrayList();
    private final ObservableList<OrderEntryForTableView> orderObservableList = FXCollections.observableArrayList();

    @FXML
    public void logoutAction(){
        try{
            services.logout(employee, null);
            stage.close();
        }
        catch(AppException ex){
            Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage());
            alert.showAndWait();
            System.out.println("Error while logout!");
        }
    }
    @FXML
    public void addProductToOrderAction(){
        try{
            Product product = productsTable.getSelectionModel().getSelectedItem();
            String productName = product.getName();
            Integer quantity = Integer.parseInt(quantityTextField.getText());

            OrderEntryForTableView entry = new OrderEntryForTableView(product, productName, quantity);
            boolean exist = false;
            for(OrderEntryForTableView tableEntry : orderObservableList.stream().toList()){
                if(tableEntry.getProductName().equals(entry.getProductName())){
                    tableEntry.setQuantity(tableEntry.getQuantity() + entry.getQuantity());
                    orderObservableList.set(orderObservableList.indexOf(tableEntry), tableEntry);
                    exist = true;
                }
            }
            if(!exist) {
                orderObservableList.add(entry);
            }
            System.out.println("Added orderEntry In Tabel");
        }
        catch(Exception ex){
            Alert alert = new Alert(Alert.AlertType.WARNING, ex.getMessage());
            alert.showAndWait();
        }
    }
    @FXML
    public void placeOrderAction(){
        //TODO: DESCHIDE PAGINA PENTRU FORMULARUL CLIENTULUI
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/confirm-order.fxml"));
            Parent root = loader.load();

            ConfirmOrderController confirmOrderController = loader.getController();
            confirmOrderController.set(services, stage);

            List<OrderEntry> entries = orderObservableList.stream()
                    .map(orderEntryForTableView -> new OrderEntry(
                            orderEntryForTableView.getProduct(),
                            orderEntryForTableView.getQuantity()
                    )).toList();

            confirmOrderController.setEmployee(employee);
            confirmOrderController.setOrderEntries(entries);

            stage.setScene(new Scene(root));
            stage.show();
        }
        catch(IOException ex){
            System.out.println("Alert during switching controllers from display to confirm!");
            Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage());
            alert.showAndWait();
        }
    }

    @Override
    public void orderUpdate(Order order) throws AppException {
        System.out.println("Order update has been called!");
        Platform.runLater(() -> {
            for(OrderEntry orderEntry : order.getOrderEntries()){
                for(Product product : productsObservableList.stream().toList()){
                    if(product.equals(orderEntry.getProduct())){
                        product.setStock(product.getStock() - orderEntry.getQuantity());
                        productsObservableList.set(productsObservableList.indexOf(product), product);
                    }
                }
            }
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("DisplayProductsController initializing...");

        productsNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        productsStockColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));
        productsPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        orderProductNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        orderProductQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        productsTable.setItems(productsObservableList);
        orderTable.setItems(orderObservableList);

        System.out.println("DisplayProductsController initialized!");
    }


    private void populateProductsTable(){
        try{
            List<Product> productList = services.getAllProducts();
            productsObservableList.addAll(productList);
        }
        catch(AppException ex){
            Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage());
            alert.showAndWait();
        }
    }


}
