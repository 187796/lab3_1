package pl.com.bottega.ecommerce.sales.domain.invoicing;

import org.junit.Test;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductData;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;


import java.awt.print.Book;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.*;

/**
 * Created by student on 2016-04-07.
 */
public class BookKeeperTest {
    InvoiceFactory invoiceFactory;
    BookKeeper bookKeeper;


    @Test
    public void requestForOneElementInvoiceShouldReturnOneElementInvoice(){
        Money money = new Money(0);
        Tax tax = new Tax(money,"aaa");
        invoiceFactory = new InvoiceFactory();
        bookKeeper = new BookKeeper(invoiceFactory);
        InvoiceRequest invoiceRequest = mock(InvoiceRequest.class);
        TaxPolicy taxPolicy = mock(TaxPolicy.class);
        RequestItem requestItem = mock(RequestItem.class);
        List<RequestItem> requestItemList = new ArrayList<>();
        requestItemList.add(requestItem);
        when(invoiceRequest.getItems()).thenReturn(requestItemList);


        when(requestItem.getTotalCost()).thenReturn(money);

        ClientData clientData = mock(ClientData.class);

        ProductData productData = mock(ProductData.class);
        when(productData.getType()).thenReturn(ProductType.STANDARD);
        when(taxPolicy.calculateTax(productData.getType(),money)).thenReturn(tax);

        when(requestItem.getProductData()).thenReturn(productData);
        when(requestItem.getQuantity()).thenReturn(1);

        when(invoiceRequest.getClientData()).thenReturn(clientData);

        Invoice invoice = bookKeeper.issuance(invoiceRequest,taxPolicy);
        assertThat(invoice.getItems().size(),is(1));
    }

}
