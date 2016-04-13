package pl.com.bottega.ecommerce.sales.domain.invoicing;

import org.junit.Before;
import org.junit.Test;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductData;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;

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

	private InvoiceRequest invoiceRequest;
	private TaxPolicy taxPolicy;
	private RequestItem requestItem;
	private List<RequestItem> requestItemList;
	private ClientData clientData;
	private ProductData productData;
	Money money;
	Tax tax;

	@Before
	public void setUp() {
		invoiceFactory = new InvoiceFactory();
		bookKeeper = new BookKeeper(invoiceFactory);
		;
		money = new Money(0);
		tax = new Tax(money, "aaa");
		requestItemList = new ArrayList<>();
		invoiceRequest = mock(InvoiceRequest.class);
		taxPolicy = mock(TaxPolicy.class);
		requestItem = mock(RequestItem.class);
		clientData = mock(ClientData.class);
		productData = mock(ProductData.class);
		
		when(invoiceRequest.getItems()).thenReturn(requestItemList);
		when(requestItem.getTotalCost()).thenReturn(money);
		when(productData.getType()).thenReturn(ProductType.STANDARD);
		when(taxPolicy.calculateTax(productData.getType(), money)).thenReturn(tax);

		when(requestItem.getProductData()).thenReturn(productData);
		when(requestItem.getQuantity()).thenReturn(1);

		when(invoiceRequest.getClientData()).thenReturn(clientData);
	}

	@Test
	public void requestForOneElementInvoiceShouldReturnOneElementInvoice() {
		requestItemList.add(requestItem);
		

		Invoice invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);
		assertThat(invoice.getItems().size(), is(1));
	}

	@Test
	public void requestForTwoElementInvoiceShouldCallCalculateTaxMethodTwoTimes() {
		requestItemList.add(requestItem);
		requestItemList.add(requestItem);

		Invoice invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);
		verify(taxPolicy, times(2)).calculateTax(productData.getType(), money);
	}
	
	@Test
	public void requestForZeroElementInvoiceShouldReturnZeroElementInvoice(){
		Invoice invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);
		assertThat(invoice.getItems().size(),is(0));
	}
	
	@Test
	public void requestForZeroElementInvoiceShouldCallCalculateTaxMethodZeroTimes(){
		Invoice invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);
		verify(taxPolicy,times(0)).calculateTax(productData.getType(), money);
	}

}
