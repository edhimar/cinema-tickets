import org.junit.Test;
import uk.gov.dwp.uc.pairtest.TicketService;
import uk.gov.dwp.uc.pairtest.TicketServiceImpl;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.domain.Type;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;



public class TicketServiceTest {

    private TicketService ticketService = new TicketServiceImpl();


    @Test(expected = InvalidPurchaseException.class)
    public void it_should_not_accept_account_id_less_than_zero() {
        Long accountId = -2L;
        TicketTypeRequest [] ticketTypeRequests = {
                new TicketTypeRequest(Type.CHILD, 10),
                new TicketTypeRequest(Type.INFANT, 12),
                new TicketTypeRequest(Type.ADULT, 8)
        };

        ticketService.purchaseTickets(accountId, ticketTypeRequests);
    }


    @Test(expected = InvalidPurchaseException.class)
    public void it_should_not_accept_child_and_infant_ticket_only() {
        Long accountId = 2L;
        TicketTypeRequest [] ticketTypeRequests = {
                new TicketTypeRequest(Type.CHILD, 10),
                new TicketTypeRequest(Type.INFANT, 8)
        };

        ticketService.purchaseTickets(accountId, ticketTypeRequests);
    }

    @Test(expected = InvalidPurchaseException.class)
    public void it_should_not_accept_empty_ticket_list_request() {
        Long accountId = 2L;
        TicketTypeRequest [] ticketTypeRequests = {};
        ticketService.purchaseTickets(accountId, ticketTypeRequests);
    }

    @Test(expected = InvalidPurchaseException.class)
    public void it_should_not_accept_null_ticket_list_request() {
        Long accountId = 2L;
        ticketService.purchaseTickets(accountId, null);
    }

    @Test(expected = InvalidPurchaseException.class)
    public void it_should_not_accept_more_than_twenty_ticket_by_request() {
        Long accountId = 2L;

        TicketTypeRequest [] ticketTypeRequests = {
                new TicketTypeRequest(Type.CHILD, 10),
                new TicketTypeRequest(Type.ADULT, 11)
        };

        ticketService.purchaseTickets(accountId, ticketTypeRequests);
    }

    @Test
    public void it_should_accept_valid_request() {
        Long accountId = 2L;

        TicketTypeRequest [] ticketTypeRequests = {
                new TicketTypeRequest(Type.INFANT, 5),
                new TicketTypeRequest(Type.CHILD, 5),
                new TicketTypeRequest(Type.ADULT, 10)
        };

        ticketService.purchaseTickets(accountId, ticketTypeRequests);
    }

}
