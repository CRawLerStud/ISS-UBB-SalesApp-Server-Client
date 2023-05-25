package app.client.gui;

import app.model.*;
import app.services.AppException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Text;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ConfirmOrderController extends Controller {

    private List<OrderEntry> orderEntryList;
    private Employee employee;

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
        employeeLabel.setText(employee.getName() + " " + employee.getSurname());
    }

    public List<OrderEntry> getOrderEntries() {
        return orderEntryList;
    }

    public void setOrderEntries(List<OrderEntry> orderEntryList) {
        this.orderEntryList = orderEntryList;
        entriesObservableList.addAll(orderEntryList.stream()
                .map(orderEntry -> {
                    String returnString = "";
                    returnString += orderEntry.getProduct().getName();
                    returnString += " x " + orderEntry.getQuantity();
                    returnString += "; TOTAL = ";

                    Double totalValue = orderEntry.getProduct().getPrice() * orderEntry.getQuantity();
                    DecimalFormat df = new DecimalFormat("#.00");
                    String totalString = df.format(totalValue);
                    returnString += totalString + " RON";

                    return returnString;

                }).toList());
        paymentMethodsObservableList.addAll(new ArrayList<>(
                List.of(PaymentMethod.CASH,
                        PaymentMethod._30_DAYS_LOAN,
                        PaymentMethod.BANK_TRANSFER)
                )
        );
        orderEntriesListBox.setItems(entriesObservableList);
        paymentMethodSelect.setItems(paymentMethodsObservableList);

        Double totalValue = calculateTotal(orderEntryList);
        DecimalFormat df = new DecimalFormat("#.00");
        String str = df.format(totalValue);
        orderValueText.setText(str + " RON");
    }

    private Double calculateTotal(List<OrderEntry> entries){
        double total = 0.0;
        for(OrderEntry entry : entries){
            total += entry.getProduct().getPrice() * entry.getQuantity();
        }
        return total;
    }

    @FXML
    private Label employeeLabel;
    @FXML
    private TextField clientNameTextField;
    @FXML
    private TextField addressTextField;
    @FXML
    private ComboBox<PaymentMethod> paymentMethodSelect;
    @FXML
    private ListView<String> orderEntriesListBox;
    @FXML
    private Text orderValueText;

    private ObservableList<String> entriesObservableList = FXCollections.observableArrayList();
    private ObservableList<PaymentMethod> paymentMethodsObservableList = FXCollections.observableArrayList();

    @FXML
    public void logoutAction() {
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
    public void confirmOrderAction(){
        try{
            String clientName = clientNameTextField.getText();
            String clientAddress = addressTextField.getText();
            PaymentMethod paymentMethod = paymentMethodSelect.getValue();

            if(clientAddress.equals("") || clientName.equals("")){
                throw new AppException("Fields must not be empty!");
            }

            if(paymentMethod == null){
                throw new AppException("Please, select a payment method!");
            }

            Client client = new Client(-1L, clientName, clientAddress);
            client = services.addClient(client);

            Order order = new Order(-1L , LocalDateTime.now(), paymentMethod, OrderStatus.PLACED, client, orderEntryList);
            services.addOrder(order);

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Order saved!");
            alert.showAndWait();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/display-products.fxml"));
            Parent root = loader.load();

            DisplayProductsController controller = loader.getController();
            controller.set(services, stage);
            controller.setEmployee(employee);

            stage.setScene(new Scene(root));
            stage.show();

        }
        catch(AppException | IOException ex){
            Alert alert = new Alert(Alert.AlertType.WARNING, ex.getMessage());
            alert.showAndWait();
        }
    }
}
