package app.networking.dto;

import app.model.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DtoUtils {

    public static ProductDto getDto(Product product){
        return new ProductDto(
                product.getId().toString(),
                product.getName(),
                product.getStock().toString(),
                product.getPrice().toString()
        );
    }

    public static Product getFromDto(ProductDto productDto){
        return new Product(
                Long.parseLong(productDto.getId()),
                productDto.getName(),
                Integer.parseInt(productDto.getStock()),
                Double.parseDouble(productDto.getPrice())
        );
    }

    public static Employee getFromDto(EmployeeDto employeeDto){
        if(Objects.equals("Admin", employeeDto.getType())){
            return new Admin(
                    Long.parseLong(employeeDto.getId()),
                    employeeDto.getName(),
                    employeeDto.getSurname(),
                    LocalDate.parse(employeeDto.getBirthdate()),
                    employeeDto.getUsername(),
                    employeeDto.getPassword()
            );
        }
        else if(Objects.equals("Salesman", employeeDto.getType())){
            return new Salesman(
                    Long.parseLong(employeeDto.getId()),
                    employeeDto.getName(),
                    employeeDto.getSurname(),
                    LocalDate.parse(employeeDto.getBirthdate()),
                    employeeDto.getUsername(),
                    employeeDto.getPassword()
            );
        }
        return null;
    }

    public static EmployeeDto getDto(Employee employee){

        if(employee.getClass().getSimpleName().equals("Admin")){
            return new EmployeeDto(
                    employee.getId().toString(),
                    employee.getName(),
                    employee.getSurname(),
                    employee.getBirthdate().toString(),
                    employee.getUsername(),
                    employee.getPassword(),
                    "Admin"
            );
        }
        else if(employee.getClass().getSimpleName().equals("Salesman")){
            return new EmployeeDto(
                    employee.getId().toString(),
                    employee.getName(),
                    employee.getSurname(),
                    employee.getBirthdate().toString(),
                    employee.getUsername(),
                    employee.getPassword(),
                    "Salesman"
            );
        }

        return null;

    }

    public static OrderDto getDto(Order order){
        String id = order.getId().toString();
        String date = order.getDate().toString();
        String payment = order.getPayment().toString();
        String status = order.getStatus().toString();

        List<OrderEntry> entries = order.getOrderEntries();
        OrderEntryDto[] orderEntriesDto = new OrderEntryDto[order.getOrderEntries().size()];
        for(int i=0; i < entries.size(); i++){
            OrderEntry entry = entries.get(i);
            Product product = entry.getProduct();
            orderEntriesDto[i] = new OrderEntryDto(
                    product.getId().toString(),
                    product.getName(),
                    product.getStock().toString(),
                    product.getPrice().toString(),
                    entry.getQuantity().toString()
            );
        }

        return new OrderDto(
                id,
                date,
                payment,
                status,
                new ClientDto(
                        order.getClient().getId().toString(),
                        order.getClient().getName(),
                        order.getClient().getAdress()
                ),
                orderEntriesDto
        );
    }

    public static Order getFromDto(OrderDto orderDto){
        List<OrderEntry> entries = new ArrayList<>();

        for(OrderEntryDto entryDto : orderDto.getOrderEntries()){
            entries.add(new OrderEntry(
                    new Product(
                            Long.parseLong(entryDto.getProductId()),
                            entryDto.getProductName(),
                            Integer.parseInt(entryDto.getProductStock()),
                            Double.parseDouble(entryDto.getProductPrice())
                    ),
                    Integer.parseInt(entryDto.getOrderQuantity())
            ));
        }

        return new Order(
                Long.parseLong(orderDto.getId()),
                LocalDateTime.parse(orderDto.getDate()),
                PaymentMethod.valueOf(orderDto.getPayment()),
                OrderStatus.valueOf(orderDto.getStatus()),
                new Client(
                        Long.parseLong(orderDto.getClientDto().getId()),
                        orderDto.getClientDto().getName(),
                        orderDto.getClientDto().getAddress()
                ),
                entries
        );
    }

    public static ClientDto getDto(Client client){
        return new ClientDto(
                client.getId().toString(),
                client.getName(),
                client.getAdress()
        );
    }

    public static Client getFromDto(ClientDto clientDto){
        return new Client(
                Long.parseLong(clientDto.getId()),
                clientDto.getName(),
                clientDto.getAddress()
        );
    }

}
