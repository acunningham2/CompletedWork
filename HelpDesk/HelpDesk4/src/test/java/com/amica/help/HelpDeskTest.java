package com.amica.help;

import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import com.amica.help.Ticket.Priority;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.equalTo;

/**
 * Unit test for the {@link HelpDesk} class.
 * 
 * @author Will Provost
 */
public class HelpDeskTest {

	public static final String TECH1 = "TECH1";
	public static final String TECH2 = "TECH2";
	public static final String TECH3 = "TECH3";

	public static final int TICKET1_ID = 1;
	public static final String TICKET1_ORIGINATOR = "TICKET1_ORIGINATOR";
	public static final String TICKET1_DESCRIPTION = "TICKET1_DESCRIPTION";
	public static final Priority TICKET1_PRIORITY = Priority.LOW;
	public static final int TICKET2_ID = 2;
	public static final String TICKET2_ORIGINATOR = "TICKET2_ORIGINATOR";
	public static final String TICKET2_DESCRIPTION = "TICKET2_DESCRIPTION";
	public static final Priority TICKET2_PRIORITY = Priority.HIGH;
	
	public static final String TAG1 = "TAG1";
	public static final String TAG2 = "TAG2";
	public static final String TAG3 = "TAG3";
	
	private HelpDesk helpDesk = new HelpDesk();
	private Technician tech1;
	private Technician tech2;
	private Technician tech3;

	/**
	 * Custom matcher that checks the contents of a stream of tickets
	 * against expected IDs, in exact order;
	 */
	public static class HasIDs extends TypeSafeMatcher<Stream<? extends Ticket>> {

		private String expected;
		private String was;
		
		public HasIDs(int... IDs) {
			int[] expectedIDs = IDs;
			expected = Arrays.stream(expectedIDs)
					.mapToObj(Integer::toString)
					.collect(Collectors.joining(", ", "[ ", " ]"));		
		}
		
		public void describeTo(Description description) {
			
			description.appendText("tickets with IDs ");
			description.appendText(expected);
		}
		
		@Override
		public void describeMismatchSafely
				(Stream<? extends Ticket> tickets, Description description) {
			description.appendText("was: tickets with IDs ");
			description.appendText(was);
		}

		protected boolean matchesSafely(Stream<? extends Ticket> tickets) {
			was = tickets.mapToInt(Ticket::getID)
					.mapToObj(Integer::toString)
					.collect(Collectors.joining(", ", "[ ", " ]"));
			return expected.equals(was);
		}
		
	}
	public static Matcher<Stream<? extends Ticket>> hasIDs(int... IDs) {
		return new HasIDs(IDs);
	}
// Step5 uses a generic stream matcher:
//	public static Matcher<Stream<? extends Ticket>> hasIDs(Integer... IDs) {
//		return HasKeys.hasKeys(Ticket::getID, IDs);
//	}

	@BeforeEach
	public void addTechnician() {
		helpDesk.addTechnician(TECH1, TECH1, 1);
		helpDesk.addTechnician(TECH2, TECH2, 2);
		helpDesk.addTechnician(TECH3, TECH3, 3);

		Iterator<Technician> iterator = helpDesk.getTechnicians().iterator();
		tech1 = iterator.next();
		tech2 = iterator.next();
		tech3 = iterator.next();

		Clock.setTime(100);
	}

	public void createTicket1() {
		helpDesk.createTicket(TICKET1_ORIGINATOR, TICKET1_DESCRIPTION, TICKET1_PRIORITY);
	}

	public void createTicket2() {
		helpDesk.createTicket(TICKET2_ORIGINATOR, TICKET2_DESCRIPTION, TICKET2_PRIORITY);
	}

	@Test
	public void createTicketTest() {
		createTicket1();
		createTicket2();
		Ticket ticket2 = helpDesk.getTicketByID(TICKET2_ID);

		assertEquals(TICKET2_DESCRIPTION, ticket2.getDescription());
	}

	@Test
	public void nullTicketTest() {
		assertNull(helpDesk.getTicketByID(1));
	}

	@Test
	public void noTechnicianTest() {
		HelpDesk helpdesk = new HelpDesk();
		assertThrows(IllegalStateException.class, () -> helpdesk.createTicket(TICKET1_ORIGINATOR, TICKET1_DESCRIPTION, TICKET1_PRIORITY));
	}

	@Test
	public void ticketAssignmentTest1() {
		createTicket1();
		assertThat(tech1, is(helpDesk.getTicketByID(1).getTechnician()));
		assertEquals(1L, tech1.getActiveTickets().count());
	}

	@Test
	public void ticketAssignmentTest2() {
		createTicket1();
		Ticket ticket1 = helpDesk.getTicketByID(1);
		tech2.assignTicket(ticket1);

		createTicket2();
		assertThat(tech3, is(helpDesk.getTicketByID(2).getTechnician()));
	}

	@Test
	public void ticketStatusTest() {
		createTicket1();
		createTicket2();

		helpDesk.getTicketByID(2).resolve("Resolved");
		assertThat(1L, equalTo(helpDesk.getTicketsByStatus(Ticket.Status.RESOLVED).count()));
		assertThat(1L, equalTo(helpDesk.getTicketsByStatus(Ticket.Status.ASSIGNED).count()));
	}

	@Test
	public void ticketNotStatusTest() {
		createTicket1();
		createTicket2();

		Stream<Ticket> tickets = helpDesk.getTicketsByNotStatus(Ticket.Status.WAITING);
		assertThat(true, equalTo(new HasIDs(2,1).matchesSafely(tickets)));
	}

	@Test
	public void ticketTagTest() {
		createTicket1();
		helpDesk.getTicketByID(1).addTag(new Tag(TAG1));
		createTicket2();
		helpDesk.getTicketByID(2).addTag(new Tag(TAG2));

		Stream<Ticket> tickets = helpDesk.getTicketsWithAnyTag(TAG2);
		assertThat(true, equalTo(new HasIDs(2).matchesSafely(tickets)));
	}

	@Test
	public void ticketTextTest() {
		createTicket1();
		createTicket2();
		helpDesk.getTicketByID(1).addNote("Note");
		helpDesk.getTicketByID(2).addNote("Note2");

		Stream<Ticket> tickets = helpDesk.getTicketsByText("Note");
		assertThat(true, equalTo(new HasIDs(1).matchesSafely(tickets)));
	}

	@Test
	public void ticketTechnicianTest() {
		createTicket1();
		createTicket2();

		Stream<Ticket> tickets = helpDesk.getTicketsByTechnician(tech1.getID());
		assertThat(true, equalTo(new HasIDs(1).matchesSafely(tickets)));
	}
}

