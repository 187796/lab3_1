package pl.com.bottega.ecommerce.sales.application.api.handler;

import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.application.api.command.AddProductCommand;

public class AddProductCommandBuilder {

    private Id reservationId = Id.generate();
    private Id productId = Id.generate();
    private int quantity = 5;

    public AddProductCommandBuilder() {

    }

    public AddProductCommandBuilder withReservationId(Id reservationId) {
        this.reservationId = reservationId;
        return this;
    }

    public AddProductCommandBuilder withRandomOrderId() {
        this.reservationId = Id.generate();
        return this;
    }

    public AddProductCommandBuilder withProductId(Id productId) {
        this.productId = productId;
        return this;
    }

    public AddProductCommandBuilder withRandomProductId() {
        this.productId = Id.generate();
        return this;
    }

    public AddProductCommandBuilder withQuantity(int quantity) {
        this.quantity = quantity;
        return this;
    }

    public AddProductCommandBuilder withDefaultQuantity() {
        this.quantity = 5;
        return this;
    }

    public AddProductCommand build() {
        return new AddProductCommand(reservationId, productId, quantity);
    }
}