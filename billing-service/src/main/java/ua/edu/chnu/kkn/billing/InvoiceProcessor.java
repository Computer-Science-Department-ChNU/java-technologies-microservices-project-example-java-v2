package ua.edu.chnu.kkn.billing;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import ua.edu.chnu.kkn.billing.model.Invoice;
import ua.edu.chnu.kkn.billing.model.ReservationInvoice;
import io.vertx.core.json.JsonObject;

@ApplicationScoped
public class InvoiceProcessor {

    @Incoming("invoices")
    @Outgoing("invoices-requests")
    public Message<Invoice> processInvoice(Message<JsonObject> message) {
        ReservationInvoice invoiceMessage = message
                .getPayload().mapTo(ReservationInvoice.class);
        Invoice.Reservation reservation = invoiceMessage.reservation;
        Invoice invoice = new Invoice(invoiceMessage.price,
                false, reservation);
        invoice.persist();
        Log.info("Processing invoice: " + invoice);
        return message.withPayload(invoice);
    }
}
