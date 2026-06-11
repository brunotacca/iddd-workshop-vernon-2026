## Concept

**Pivotal events** are significant domain events in an event timeline that mark major transitions in business logic or language, often indicating **candidate boundaries** between subdomains or bounded contexts . They are recognized by observing what happens immediately after the event—if the language, intent, or concerns shift noticeably, the preceding event is likely pivotal .
**Bounded contexts** are hard boundaries that separate distinct models and ubiquitous languages within a system . In a modular monolith, top-level modules (e.g., **co.donebyme.matching.model.proposal**) act as hard boundaries even though they share a deployment unit . Each bounded context maintains its own model, repository interfaces, and domain events .
**Tactical modeling tools** include **entities**, **value objects**, **aggregates**, **domain events**, **domain services**, and **factories** . Entities have unique identity and may be mutable; value objects are immutable, side-effect-free, and describe or measure entities . Aggregates are transactional consistency boundaries—everything within an aggregate must satisfy business invariants at transaction commit . Domain events are immutable facts stated in noun-verb-past-tense (e.g., **co.donebyme.matching.ProposalSubmitted**), capturing what happened in the domain .
**Event sourcing** persists state as a sequence of domain events rather than current-state snapshots . The aggregate's state is reconstituted by replaying ("re-wenning") all events from the event stream . This approach provides a complete audit trail and enables temporal queries .
**CQRS (Command Query Responsibility Segregation)** separates the command model (which mutates state) from the query model (which provides read-optimized views) . Commands invoke methods on aggregates; queries read from projections or views built by subscribing to domain events .
## What I understood

### Identifying Pivotal Events and Boundaries

Vaughn demonstrated how to identify pivotal events by walking through a timeline for the "done by me" matching context . For example, after **proposal submitted**, the next event is **pricing verified**—this transition from proposal concerns to pricing concerns signals a pivotal event . Similarly, **doers recommended** and **schedule availability located** represent shifts in language and intent, marking additional boundaries .
When marking pivotal events across a timeline, vertical boundaries appear where language and responsibility change . These boundaries suggest candidate subdomains or bounded contexts . In the "done by me" system, boundaries include **matching**, **pricing**, **scheduling**, **notifications**, and **fulfillment** .
Bruno asked about the difference between **bounded contexts** (hard boundaries) and **modules** (softer boundaries) . Vaughn clarified that in a modular monolith, top-level modules *are* hard boundaries at the highest organizational level (e.g., `donebyme.matching`), even though the deployment is a single artifact . The namespace structure reflects this: `co.donebyme.matching.model.proposal` shows a clear boundary at the `matching` level .
### Domain Events: Structure and Versioning

A domain event is a **fact**—something undeniable that has happened . Events are typically named in **noun-verb-past-tense** (e.g., **proposal submitted**, **pricing verified**, **doers recommended**) . An abstract `DomainEvent` base class includes the **event ID**, **occurred-on timestamp**, and optionally a **type version** for schema evolution .
Vaughn emphasized **type versioning** for events: when an event's structure changes in a breaking way (e.g., GDPR requires removing a field), the major version increments (e.g., from `1.2.0` to `2.0.0`) . A **schema registry** tracks these versions and enforces compatibility rules . Vaughn's open-source **SchemaMinder** tool demonstrates this pattern .
When publishing events outside a bounded context, the **internal namespace** should be hidden. For example, internally the event might live at `co.donebyme.matching.model.proposal.ProposalSubmitted`, but externally it's published as `co.donebyme.matching.ProposalSubmitted` . This protects internal model details and allows independent evolution .
Events can be **essential** (minimal attributes), **enriched** (additional useful properties), or **event-carried state transfer** (full payload) . Essential events include only necessary data (e.g., proposal ID, client ID, expectations) . Enriched events add extra fields like progress state for convenience . Event-carried state transfer (the "fatty event") includes the entire aggregate state, useful for analytics but risking stale data if queried later .
**Metadata** on events can include user roles, correlation IDs (originator ID), causal IDs (pricing history ID), and policy IDs . The **originator ID** is a correlation ID linking the event back to its source aggregate (e.g., matching's proposal ID becomes pricing's originator ID) .
**Privacy information** should not be directly embedded in events. Instead, use a **privacy info ID** referencing a separate encrypted entity . When a user requests data deletion, either delete the entity or use **crypto shredding** (delete the encryption key, rendering data unrecoverable) .
### Aggregates and Tactical Patterns

An **aggregate** is a transactional consistency boundary: all state within the aggregate must satisfy **invariants** (business rules) at the end of each transaction . For example, if a proposal's progress includes "pricing denied," then the proposal's suggested price must be greater than zero .
The **aggregate root entity** (e.g., `Proposal`) represents the aggregate concept and enforces invariants . In the code examples, `Proposal` is the root entity, not "ProposalAggregate"—technical terms should not pollute the ubiquitous language .
**Four rules of aggregate design** :
1. **Protect true invariants** inside the aggregate boundary .
2. **Design small aggregates** to minimize contention (multiple users modifying the same instance causes transaction failures) .
3. **Reference other aggregates by identity only** (not by object pointer), enabling distributed storage and keeping aggregates small .
4. **Use eventual consistency** to update other aggregates or bounded contexts via domain events .

**Factory methods** express the ubiquitous language. Instead of a public constructor, `Proposal.submitFor(client, expectations)` reads naturally and prevents misuse . The method creates the aggregate, applies a `ProposalSubmitted` event, and returns the new instance . This pattern separates command methods (which apply events) from "when" methods (which mutate state during reconstitution) .
**Value objects** are immutable and side-effect-free . They describe, measure, qualify, or quantify entities but are not "things" themselves . Two value objects are equal if their type and all attributes match . For example, `Money(100)` ≠ `Money(200)` and `Money(100)` ≠ `Age(100)` because types differ . When a value object changes, a new instance replaces the old one (functional replacement, not mutation) .
In the code, `ProposalState` is a value object holding all aggregate state as `public final` fields . The aggregate holds a single mutable reference to `ProposalState`, but the state itself is immutable . This functional approach simplifies event sourcing: each event application creates a new `ProposalState` instance .
**Entities** have unique identity and may be mutable . Identity should use **UUIDs**, **KSUIDs**, or **ULID** (not auto-increment sequences) to avoid predictability and enable distributed generation . **UUID version 7** and **KSUID** are K-sortable (sortable by time) yet unpredictable .
**Domain services** are stateless operations that don't belong on a single entity . For example, `RankingCalculatorService.rankAmong(doers)` loads multiple doer aggregates, ranks them, and returns a `RankedDoers` value object . Domain services can be passed as parameters to aggregate methods or called from application services .
### Event Sourcing and CQRS

In **event sourcing**, aggregate state is persisted as a sequence of domain events . The `ProposalRepository` saves only new events from the `appliedEvents` collection, not the full state . To reconstitute an aggregate, the repository loads the event stream and replays each event through the aggregate's "when" methods .
The private constructor `Proposal(Stream<DomainEvent> stream)` is used only by the repository during reconstitution . It calls `apply(event)` for each event, but does *not* add those events to the `appliedEvents` collection (they're historical, not new) . Only new commands add events to `appliedEvents` for persistence .
**Snapshotting** optimizes reconstitution: periodically save a snapshot of the aggregate's state (e.g., `ProposalState` serialized to JSON) . On load, start from the snapshot and replay only subsequent events . This avoids replaying thousands of events for long-lived aggregates .
**CQRS** separates command and query responsibilities . The command model (aggregates, repositories) handles writes; the query model (views, projections) handles reads . In event sourcing, aggregates have *no getters*—you cannot query the command model directly . Instead, **projections** subscribe to domain events and build **views** (read-optimized DTOs) .
For example, `ProposalView` is a queryable DTO built by a projection that listens to `ProposalSubmitted`, `PricingVerified`, etc. . The application service queries `ProposalQueries.getProposalView(id)` to retrieve the view . Views can be pre-joined to avoid runtime joins, optimizing for specific UI needs .
### Ports and Adapters Architecture

Vaughn's code follows **ports and adapters** (hexagonal architecture) . The structure is:
- **Infrastructure (outside)**: HTTP controllers, messaging subscribers/publishers, repository implementations .
- **Application (inside, driver port)**: Command handlers (e.g., `ProposalCommands.submitProposal`) that orchestrate use cases .
- **Query (inside, driver port)**: Query handlers (e.g., `ProposalQueries.getProposalView`) for CQRS reads .
- **Model (inside)**: Aggregates, entities, value objects, domain events, repository interfaces (driven ports) .

The application service receives **primitives** (strings, integers), not domain types . It constructs domain objects (e.g., `Client.from(clientId)`, `Expectations.ofSubmitted(...)`) and calls aggregate methods . The repository interface is a **driven port** in the model package; the infrastructure provides the adapter implementation (e.g., `JournalProposalRepository`) .
**Dependency injection** is simplified: instead of Spring or other IoC containers, Vaughn uses factory methods and lazy initialization in an `API` class . For example, `API.proposals()` returns a lazily instantiated `ProposalCommands` with its repository . This avoids framework complexity while maintaining testability .
### Event-Driven Choreography vs. Orchestration

The code demonstrates **choreography**: when `Proposal` emits `ProposalSubmitted`, the `MatchingJournalPublisher` publishes it to the message bus . Subscribers in other contexts (e.g., pricing, scheduling) listen to the "all" topic and filter for events they care about . This is a simplified "all topic" approach (Fred George's pattern) .
In a **partnership** relationship, both contexts publish events the other consumes . In an **upstream-downstream** (open-host service) relationship, the downstream context sends commands to the upstream via its API and receives events back . For example, matching sends `VerifyPricing` command to pricing's open-host service; pricing responds with `PricingVerified` event .
The **schema registry** defines the **published language**: the types (commands, events) and their versions that a context exposes . Matching depends on pricing's schema version (e.g., `PricingRejected 1.2.0`). When pricing releases a breaking change (`2.0.0`), matching must upgrade before the old version is deprecated .
### Bruno's Questions on Adoption and Remote Facilitation

Bruno asked how to push for DDD and event storming as an individual contributor (not a consultant) . Vaughn advised: **start at the team level**, don't try to change the entire organization . Avoid jargon like "DDD" or "event storming"—frame it as **collaborative software development** or **modeling** . People resist unfamiliar terms and defend their turf .
Vaughn shared a story: before his books, he had disappointing conversations where people rejected domain models in favor of service-oriented architecture (for resume purposes) . Even invoking Martin Fowler's authority didn't help ("Martin Fowler has his opinions") . At a new job, Vaughn would face the same resistance despite his four published books .
Bruno also asked about **remote event storming** with many people on Zoom . Vaughn noted that **big picture storming is chaotic** and doesn't work well online—voices collide, facilitators can't easily interrupt, and people tend to listen passively rather than participate . In physical spaces, facilitators can step up and get attention; on Zoom, it takes a minute or more to break into a conversation .
Vaughn suggested using **breakout rooms** for smaller groups (5-6 people) during design-level storming, as done in the workshop . For big picture online, consider a **raise-hand rule** where people must raise their hand to speak, preventing chaos . Observations from quiet participants may surface later, so they should still attend .
Swarup shared his experience: getting the right people in the room is difficult, and event storming often doesn't translate into material outcomes . His current approach is **event pairing** (one-on-one sessions) over months, acting as the glue that integrates knowledge, while people remain "blissfully oblivious" that domain modeling is happening . Vaughn agreed this long-term, low-key approach can work .
Vaughn recounted his own struggles as a consultant: at Intuit, a chief architect tried to get him fired on day one . On the last day, the CTO announced to 100 people that the company would use DDD and event storming, and dissenters could explain to him why not—everyone went silent, and the chief architect who opposed Vaughn walked up and said "it's been such a pleasure working with you" . Vaughn emphasized he's not immune to resistance; success often requires executive sponsorship .
### Topography Architecture (Not Topology)

Vaughn clarified the term **topography architecture** (not topology) . Topology is the study of elastic shape deformation; topography is the art of graphic delineation (like a topo map) . A **topo map** shows elevation with contour lines—the closer the lines, the steeper the terrain .
In software, a **topo map** of bounded contexts shows not only the contexts but also the **cognitive steepness** inside each context . Steeper contours indicate higher complexity or difficulty in understanding/modifying that context . This helps teams assess ascent and descent (how hard it is to interact with a context) .
On the diagram, each bounded context is labeled with its **subdomain** and **business capability** . Swarup asked if "business capability" refers to the **BIZBOK** (Business Architecture Body of Knowledge) definition . Vaughn said the closest definition is IBM's business capabilities framework, but he's unfamiliar with BIZBOK specifically . He noted there are three architectures: **business architecture**, **social architecture**, and **technical architecture**—technology people often ignore the first two .
## What I didn't fully get

### When to Use Enriched vs. Essential Events

Vaughn explained three event types: essential, enriched, and event-carried state transfer . **Essential events** include only necessary attributes (e.g., proposal ID, client ID, expectations) . **Enriched events** add extra fields like progress state for convenience . **Event-carried state transfer** includes the entire aggregate state .
The choice depends on trade-offs. Essential events keep payloads small and force consumers to query back for more data, ensuring they get the latest state but adding latency . Enriched events reduce round trips but may include data that becomes stale . Event-carried state transfer is useful for analytics, where a data product aggregates all relevant data into one place, but it has higher risk of staleness .
Vaughn suggested manufacturing event-carried state transfer events only at the **external boundary** for analytics, not for internal domain events . This way, the domain model stays lean, and analytics contexts get pre-joined data .
### Protecting Privacy in Events

Events should not embed sensitive data directly. Instead, use a **privacy info ID** referencing a separate encrypted entity . This entity holds private data encrypted with a key . When a user requests deletion (GDPR right to erasure), either delete the entity or use **crypto shredding**—delete the encryption key so the data can never be decrypted .
Vaughn noted that even if the entity is deleted, the privacy info ID remains in the event, pointing to nothing . A query for the entity returns null, signaling deletion . Alternatively, crypto shredding leaves the entity in place but permanently inaccessible . The choice depends on whether you want a record that the entity existed (keep the entity, shred the key) or not (delete the entity) .
### When to Use Domain Services vs. Aggregate Methods

A **domain service** is used when behavior doesn't naturally belong on a single aggregate or when it crosscuts multiple aggregates . For example, `RankingCalculatorService.rankAmong(doers)` loads multiple doer aggregates from the repository, ranks them by score, and returns a sorted list . This logic doesn't belong on one doer aggregate because it requires comparing many doers .
Domain services are **stateless** from a business logic perspective (they hold no persistent state) . They may hold repository references to perform lookups . You can pass a domain service as a parameter to an aggregate method, or call aggregate methods from the domain service .
Civio asked if you can inject a domain service into an entity . Vaughn clarified: **pass it as a parameter**, not as constructor injection (which implies holding state) . For example, `proposal.verifyPricing(pricingService)` passes the service in, and the aggregate calls it during the method . This keeps the aggregate focused on its own invariants while delegating crosscutting concerns to the service .
### Event Sourcing: Why Separate Command and "When" Methods?

In event sourcing, **command methods** (e.g., `submitFor`, `verifyPricing`) apply events, and **"when" methods** (e.g., `whenProposalSubmitted`, `whenPricingVerified`) mutate state . This separation seems redundant—why not just mutate state in the command method? 
The reason: **reconstitution**. When loading an aggregate from the event store, you replay all historical events through the "when" methods *without* adding them to the `appliedEvents` collection . If you called `submitFor` again, it would apply a new `ProposalSubmitted` event, duplicating history . The "when" methods are idempotent state transitions; the command methods are one-time actions .
Taras asked if the same event (e.g., `PricingVerified`) is used both internally (for reconstitution) and externally (published to other contexts) . Vaughn confirmed: yes, the same event type is used, but the external version may have a different namespace and filtered attributes . Internally, `PricingVerified` lives in `co.donebyme.matching.model.proposal`; externally, it's published as `co.donebyme.matching.PricingVerified` .
### Handling Failures and Idempotency

Vaughn mentioned **guarded** methods to handle duplicate event delivery . For example, `verifyPricing` checks if pricing has already been verified before applying the event . If `PricingAccepted` is re-delivered by the messaging system, the aggregate skips it, avoiding duplicate state transitions .
This is simpler than deduplication or resequencing at the infrastructure level . The aggregate itself knows whether a transition has already occurred (e.g., by checking the `Progress` value object, which holds a set of completed steps) . The set ensures order-independence: you can check "was pricing verified?" without caring when it happened relative to other steps .
The `Progress` value object is a clever pattern: it holds a `Set<ProgressStep>` of all completed steps (e.g., `SUBMITTED`, `PRICING_VERIFIED`, `SCHEDULING_VERIFIED`) . You can ask `progress.isPricingVerified()` or `progress.isAcceptable()` (submitted + pricing verified + scheduling verified) . This makes business rules readable and avoids complex state machines .
### Data Mesh and Bounded Context Views

Julia asked about **data mesh** in the context of bounded contexts . Vaughn explained that in data mesh, each bounded context owns a **data product** in addition to its operational database . The data product is optimized for analytics and owned by the same cross-functional team that owns the operational model .
A data product can include **views** (read-optimized projections) or aggregated analytical data . For example, instead of querying matching, profiles, and user contexts separately to build a UI, you can aggregate all that data into a single **view context** (a data product) . This trades freshness (risk of stale data) for performance (no runtime joins) .
**Lineage** is critical in data mesh: you must track where data originates and who owns it . Just because you have a copy doesn't mean you own it, and your copy may be stale . The alternative is **live composition** (query each context in real time) vs. **static composition** (pre-aggregate into a view) . Choose based on your staleness tolerance and performance needs .
Vaughn noted that 85% of data warehouse efforts fail because centralized teams don't understand the data and can't keep up with change requests . Data mesh solves this by decentralizing ownership: the team that understands the domain also owns the analytical data product .
### Why Not Use ORMs or Annotations?

Vaughn expressed dislike for **annotations** (Java) or **attributes** (C#) for OR mapping . He prefers separating persistence concerns from the domain model . In his code, the repository handles serialization (to JSON for event sourcing) . The aggregate has no persistence annotations, keeping the model clean .
For event sourcing, the `JournalProposalRepository` serializes events to JSON and stores them with a sequence number . On load, it deserializes the event stream and passes it to the aggregate's private constructor . For snapshotting, `ProposalState` is serialized to JSON as a single object, making snapshots trivial .
Cristóbal asked about deserializing aggregates without a public constructor . Vaughn explained that the repository uses a **public constructor that takes a stream of events** . This constructor is marked "do not use" by convention (Java lacks internal visibility like C#) . In C#, you could use `internal` to restrict access to the assembly . For tests, the constructor is accessible because tests use the same package name .
### When to Use Exceptions vs. Events for Failures

Alin asked how to handle business rule failures: should you throw exceptions or emit events? . Vaughn said: **if it's a business failure, emit an event** (e.g., `PricingRejected`) . Exceptions are for exceptional, unexpected situations, not predictable business outcomes . A business person should understand the failure, so it should be an event they can see .
For example, if Michael's proposed price is too low, pricing emits `PricingRejected` with a suggested price . This is not exceptional—it's a normal outcome the system must handle . The aggregate listens for `PricingRejected` and updates its state accordingly (e.g., increments the suggested price) .
### Aggregate Size and Contention

Vaughn emphasized **design small aggregates** to avoid contention . If two users modify the same aggregate instance simultaneously, one transaction will fail (optimistic locking) . Large aggregates increase the likelihood of contention because they hold more data that more users might want to change .
By **referencing other aggregates by identity only**, you keep aggregates small and avoid loading unnecessary data . For example, `Proposal` holds a `Client` value object with only the client ID and name, not the full client entity . If you need more client data, query the profiles context by ID .
This also supports **distributed storage**: one aggregate instance can be in partition A, another in partition B . Object pointers don't work across partitions, but IDs do . This is critical for cloud-native, scalable systems .
### Tell, Don't Ask

**Tell, Don't Ask** is a pattern from Alex Sharp's *Smalltalk by Example* . Instead of asking an object for its data, making a decision, and setting data back, **tell the object what to do** and let it mutate its own state . This encapsulates business logic in the object itself .
For example, instead of:
```java
if (proposal.getProgress().isPricingVerified()) {
    proposal.setProgress(proposal.getProgress().add(SCHEDULING_VERIFIED));
}
```
You write:
```java
proposal.verifyScheduling();
```
The aggregate method handles the logic internally . This keeps behavior close to data and prevents anemic domain models (data holders with no behavior) .
## Might show up on the exam

### Pivotal Events and Boundaries

- **Pivotal event**: A domain event that marks a significant transition in language or intent, indicating a candidate boundary between subdomains .
- Recognize pivotal events by observing what happens *after* the event—if concerns shift (e.g., from proposals to pricing), the preceding event is pivotal .
- Mark boundaries where language changes (e.g., "proposal submitted" → "pricing verified" → "doers recommended") .
- Boundaries can indicate subdomains, bounded contexts, or even technical contexts (
