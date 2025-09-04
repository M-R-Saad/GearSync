package org.example.vehicle;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class PartsCardController {

    @FXML
    ImageView partImage;

    @FXML
    Label partName;

    @FXML
    Label partDescription;

    @FXML
    Label partPrice;

    @FXML
    Label stockStatus;

    @FXML
    Spinner<Integer> quantitySpinner;

    @FXML
    Button addToCart;

    private Parts parts;
    private VPCustomerController customerController;

    public Parts getParts() {
        return parts;
    }

    public void setParts(Parts parts) {
        this.parts = parts;
    }

    public void setData(Parts parts, VPCustomerController customerController) {
        this.parts = parts;
        this.customerController = customerController;

        partName.setText(parts.getName());
        partDescription.setText(parts.getDescription());
        partPrice.setText("Price: " + parts.getPrice() + " ৳");

        if (parts.getQuantity() == 0) {
            stockStatus.setText("Out of Stock!");
        } else {
            stockStatus.setText("In Stock: " + parts.getQuantity());
        }

        try {
            Image image = new Image(parts.getPicture(), false);
            partImage.setImage(image);
        }
        catch (Exception e) {
            System.out.println("Image loading failed: " + e.getMessage());
        }

        SpinnerValueFactory<Integer> valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, parts.getQuantity(), 1);
        quantitySpinner.setValueFactory(valueFactory);
    }

    public void setAvailableStock(int newStock) {
        this.parts.setQuantity(newStock);
        stockStatus.setText("Stock: " + newStock);

        if (newStock <= 0) {
            addToCart.setDisable(true);
            stockStatus.setText("Out of Stock");
        }
        else {
            SpinnerValueFactory<Integer> valueFactory =
                    new SpinnerValueFactory.IntegerSpinnerValueFactory(1, newStock, 1);
            quantitySpinner.setValueFactory(valueFactory);
        }
    }

    @FXML
    public void handleAddToCart() {
        int selectedQty = quantitySpinner.getValue();

        for (Parts existing : customerController.partsTable.getItems()) {
            if (existing.getPid() == parts.getPid()) {
                existing.setQuantity(existing.getQuantity() + selectedQty);
                existing.setNetPrice(existing.getQuantity() * existing.getPrice());

                customerController.partsTable.refresh();

                setAvailableStock(parts.getQuantity() - selectedQty);

                customerController.updateTotalPrice();
                return;
            }
        }

        Parts newPart = new Parts(
                parts.getPid(),
                parts.getName(),
                parts.getDescription(),
                parts.getPrice(),
                selectedQty,
                parts.getPicture()
        );
        newPart.setNetPrice(selectedQty * parts.getPrice());
        customerController.partsTable.getItems().add(newPart);

        setAvailableStock(parts.getQuantity() - selectedQty);
        customerController.updateTotalPrice();
    }
}