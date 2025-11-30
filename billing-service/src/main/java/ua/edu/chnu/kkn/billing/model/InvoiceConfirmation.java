package ua.edu.chnu.kkn.billing.model;

public class InvoiceConfirmation {

    public Invoice invoice;

    public boolean paid;

    public InvoiceConfirmation(Invoice invoice, boolean paid) {
        this.invoice = invoice;
    }
}
