package uk.gov.dwp.uc.pairtest;

import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.paymentgateway.TicketPaymentServiceImpl;
import thirdparty.seatbooking.SeatReservationService;
import thirdparty.seatbooking.SeatReservationServiceImpl;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.domain.Type;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;


public class TicketServiceImpl implements TicketService {
    /**
     * Should only have private methods other than the one below.
     */

    private static final int INFANT_PRICE = 0;
    private static final int CHILD_PRICE = 10;
    private static final int ADULT_PRICE = 20;

    private TicketPaymentService ticketPaymentService = new TicketPaymentServiceImpl();
    private SeatReservationService seatReservationService = new SeatReservationServiceImpl();


    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {

        List<TicketTypeRequest> ticketList = ticketTypeRequests != null ? List.of(ticketTypeRequests) : null;

        if (accountId < 0 || !validTicketRequest.test(ticketList)) {
            throw new InvalidPurchaseException();
        }

        Integer totalAmountToPay = ticketList.stream()
                                .map(t -> getTicketPrice(t.type(), t.noOfTickets()))
                                .reduce(0, (price1, price2)-> price1 + price2);

        ticketPaymentService.makePayment(accountId, totalAmountToPay);

        Integer totalSeatsToAllocate = getTotalSeatsToAllocate(ticketList);
        seatReservationService.reserveSeat(accountId, totalSeatsToAllocate);

        Integer totalTickets = ticketQuantity.apply(ticketList);

        System.out.println(String.format("Total amount to pay is: £%s ", totalAmountToPay));
        System.out.println(String.format("Total seats to allocate is: %s ", totalSeatsToAllocate));
        System.out.println(String.format("Total of tickets is: %s ", totalTickets));

    }


    private int getTotalSeatsToAllocate(List<TicketTypeRequest> ticketTypeRequestList) {
        return ticketTypeRequestList.stream()
                .filter(t-> !Type.INFANT.equals(t.type()))
                .map(t -> t.noOfTickets()).reduce(0, (quantity1, quantity2) -> quantity1 + quantity2);
    };

    private Predicate<List<TicketTypeRequest>> containsChildTicketOnly = list -> list.stream().filter(t-> Type.ADULT.equals(t.type())).toList().size() == 0;

    private Predicate<List<TicketTypeRequest>> validTicketRequest = ticketList -> ticketList != null && !containsChildTicketOnly.test(ticketList) && ticketList.size() > 0 && this.ticketQuantity.apply(ticketList) <= 20;

    private Function<List<TicketTypeRequest>, Integer> ticketQuantity = list -> list.stream().map(t -> t.noOfTickets()).reduce(0, (quantity1, quantity2) -> quantity1 + quantity2);

    private int getTicketPrice(Type type, int nOfTickets) {
        return switch (type) {
            case INFANT -> INFANT_PRICE * nOfTickets;
            case CHILD -> CHILD_PRICE * nOfTickets;
            case ADULT -> ADULT_PRICE * nOfTickets;
        };
    }

}