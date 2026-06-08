package co.donebyme.matching.model.proposal;


// Modeling Uncertainty
// (1) PricingAccepted, (2) PricingToleranceIdentified, (3) DoersRecommended, (4) AvailabilityLocated
// Progress: knows what has happened and what has not happened
// 	- isAcceptable(): true
//     when: Submitted, PricingAccepted, PricingTolerance, DoersRecommended, AvailabilityLocated
//	   then: NotifyAvailableDoers
//	- isMatched(): true
//     when: *, Matched
//	   then: ProposalMatched
//	   and then: ProposalAlternateDoerAccepted
//
//	Saga / ProcessManager / Partial Steps Possible
//	- isAcceptable(): false && isPricingValidated(): true
//		via: Event Enrichment: Minimal: PricingAccepted(pricingTolerance), PricingTolerance(pricingAccepted)
//		via: Event Enrichment: All Progressions
//		when: Submitted, PricingAccepted, PricingTolerance
//		then: take partial step


public final class Proposal extends SourcedEntity<DomainEvent> {

	private ProposalState state;

	// client 1: Proposal proposal = new Proposal(client, expectations)

	// test 1: Proposal proposal = new Proposal(client, expectations)

	// test 2: val proposal = Proposal.submitFor(client, expectations)
	//		   assertEquals(...)
	//         assertTrue(...)

	//	All:
	//			assertFalse(proposal.progress.isPricingAccepted())
	//			assertTrue(proposal.progress().isSumbitted())
	//			assertTrue(proposal.hasEvent())
	//			assertEquals(ProposalSubmitted.class, proposal.event().getClass())

	public static Proposal submitFor(Client client, Excpectations excpectations) {
		return new Proposal(client, expectations)
	}

	// Michael: offers 50% below fair pricing
	// 			val proposal = Proposal.submitFor(client, expectations50Below())
	//			proposal.resubmitWith(expectations)
	//			assertTrue(proposal.progress().isResumbitted())
	//			assertEquals(ProposalResubmitted.class, proposal.event().getClass())

	// Command: CQS Command-Query Separation

	// Michael: UI/REST
	public void resubmitWith(Excpectations expectations) {
		if (!progress.isResumbittedFor(expectations)) {
			// NOT YET EVENT SOURCED
			this.expectations = expectations;
			// List<Progression> [Submitted, PricingRejected, Resubmitted(count)]
			this.progress = progress.resubmitted();
		}
	}

	// Julia: offers Weekend(100%) + 20%

	public void acceptPricing() {
		if (!progress.isPricingAccepted()) {
			progress = progress.pricingAccepted(); // List<Progression> [Submitted, PricingAccepted]
			events.add(new PricingAccepted(...))
		}
	}

	// Michael: offers 50% below fair pricing

	public void rejectPricing(Money suggestedPrice) {
		// NOT YET EVENT SOURCED
		Money rejectedPrice = expectations.price;
		if (!progress.isPricingRejected(rejectedPrice, suggestedPrice)) {
			progress = progress.pricingRejected(rejectedPrice, suggestedPrice); // List<Progression> [Submitted, PricingRejected]
		}
	}

	public Progress progress() {
		return progress;
	}

	// this happend for construction and for every reconstitution of Proposal
	protected void whenProposalSubmitted(ProposalSubmitted event) {
		this.state = new ProposalState(event.id, event.client, event.excpectations, Progress.Submitted)
	}

	protected ProposalState state() {
		return state;
	}

	// this happens once per Proposal
	private Proposal(Client client, Excpectations excpectations) {
		emit(new ProposalSubmitted(...))
	}

	// repository --- TESTS
	public Proposal(List<DomainEvent> stream) {
		super(stream);
	}
}

public final class ProposalState {
	public final Id id; 							// value
	public final Client client; 					// value
	public final Excpectations excpectations;		// value
	public final Progress progress;				// value

	ProposalState with(Excpectations expectations) {
		return new ProposalState(this.id, this.client, expectations, this.progress)
	}
}

public abstract SourcedEntity<T> {
	private List<DomainEvent> applied;		// value / event

	public List<DomainEvent> applied() { return Collections.unmodifiableList(events); }

	protected SourcedEntity(List<DomainEvent> stream) {
		for (DomainEvent event : stream){
			this.lookupHandler(event).run();
		}
	}

	protected void apply(DomainEvent event) {
		this.events.add(event));
		this.lookupHandler(event).run();
	}

	private Method lookupHandler(DomainEvent event) {

	}
}

public class PostgresProposalRepository implements ProposalRepository {

	public Proposal proposalOf(Id id) {
		List<DomainEvent> stream = store.readAll("events", id);
		return new Proposal(stream)
	}

	public save(Proposal proposal) {
		// ATOMIC TRANSACTION
		store.write("proposals", proposal);
		store.writeAll("events", proposal.events());
	}
}
