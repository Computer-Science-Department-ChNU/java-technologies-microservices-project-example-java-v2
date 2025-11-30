package ua.edu.chnu.kkn.billing;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import ua.edu.chnu.kkn.billing.model.Invoice;

import java.util.List;

@Path("/invoice")
public class InvoiceResource {

    @GET
    public List<Invoice> allInvoices() {
        return Invoice.listAll();
    }
}
