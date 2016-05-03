package pl.com.bottega.ecommerce.sales.application.api.handler;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.application.api.command.AddProductCommand;
import pl.com.bottega.ecommerce.sales.application.api.handler.AddProductCommandHandler;
import pl.com.bottega.ecommerce.sales.domain.client.Client;
import pl.com.bottega.ecommerce.sales.domain.client.ClientRepository;
import pl.com.bottega.ecommerce.sales.domain.equivalent.SuggestionService;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.Product;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductRepository;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sales.domain.reservation.Reservation;
import pl.com.bottega.ecommerce.sales.domain.reservation.Reservation.ReservationStatus;
import pl.com.bottega.ecommerce.sales.domain.reservation.ReservationRepository;
import pl.com.bottega.ecommerce.sharedkernel.Money;
import pl.com.bottega.ecommerce.system.application.SystemContext;
import pl.com.bottega.ecommerce.system.application.SystemUser;

public class AddProductCommandHandlerTest {
	private AddProductCommandHandler addProductCommandHandler;
	private ReservationRepository reservationRepository;
	private ProductRepository productRepository;
	private SuggestionService suggestionService;
	private ClientRepository clientRepository;
	private SystemContext systemContext;
	
	private Id reservationId = Id.generate();
	private Id productId = Id.generate();
	private Id productNotAvailableId = Id.generate();
	private Id clientId = Id.generate();
	private Reservation reservation = new Reservation(reservationId, ReservationStatus.OPENED, new ClientData(clientId, "name"), new Date());
	private Product product = new Product(productId, new Money(123), "product", ProductType.FOOD);
	private Product productNotAvailable = new Product(productNotAvailableId, new Money(123), "productNotAvailable", ProductType.FOOD);
	private Product suggestedProduct = new Product(Id.generate(), new Money(123), "product2", ProductType.FOOD);
	
	@Before
	public void setUp(){
		productNotAvailable.markAsRemoved();
		addProductCommandHandler = new AddProductCommandHandler();
		reservationRepository = mock(ReservationRepository.class);
		productRepository = mock(ProductRepository.class);
		suggestionService = mock(SuggestionService.class);
		clientRepository = mock(ClientRepository.class);
		systemContext = mock(SystemContext.class);
		
		when(reservationRepository.load(reservationId)).thenReturn(reservation);
		when(productRepository.load(productId)).thenReturn(product);
		when(productRepository.load(productNotAvailableId)).thenReturn(productNotAvailable);
		when(suggestionService.suggestEquivalent(any(Product.class), any(Client.class))).thenReturn(suggestedProduct);
		when(clientRepository.load(clientId)).thenReturn(new Client());
		when(systemContext.getSystemUser()).thenReturn(new SystemUser(clientId));
		
		addProductCommandHandler.setClientRepository(clientRepository);
		addProductCommandHandler.setProductRepository(productRepository);
		addProductCommandHandler.setReservationRepository(reservationRepository);
		addProductCommandHandler.setSuggestionService(suggestionService);
		addProductCommandHandler.setSystemContext(systemContext);
	}
	
	@Test
	public void saveProductIdAvailable(){
		AddProductCommand addProductCommand = new AddProductCommand(reservationId, productId, 1);
		addProductCommandHandler.handle(addProductCommand);
		verify(reservationRepository, times(1)).load(reservationId);
		verify(reservationRepository, times(1)).save(any(Reservation.class));
		verify(productRepository, times(1)).load(productId);
	}
	
	@Test
	public void saveProductIdNotAvailable(){
		AddProductCommand addProductCommand = new AddProductCommand(reservationId, productNotAvailableId, 1);
		addProductCommandHandler.handle(addProductCommand);
		verify(reservationRepository, times(1)).load(reservationId);
		verify(productRepository, times(1)).load(productNotAvailableId);
		verify(clientRepository, times(1)).load(clientId);
		verify(suggestionService, times(1)).suggestEquivalent(any(Product.class), any(Client.class));
		verify(reservationRepository, times(1)).save(any(Reservation.class));
	}
}
