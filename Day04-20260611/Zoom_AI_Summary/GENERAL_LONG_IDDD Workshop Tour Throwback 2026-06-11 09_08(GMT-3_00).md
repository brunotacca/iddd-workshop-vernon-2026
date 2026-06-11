## Key Outcomes

This was a comprehensive DDD training session led by Vaughn Vernon covering strategic and tactical modeling techniques. The workshop demonstrated how to identify bounded contexts through pivotal events, implement event-sourced aggregates with immutable state, and apply ports-and-adapters architecture in practice. Bruno Tacca and other participants explored practical implementation patterns including CQRS projections, schema registries for event versioning, and strategies for introducing DDD practices in organizations resistant to change. 
## Core Concepts Covered

### Pivotal Events and Bounded Context Identification

**Pivotal events** are domain events that mark significant transitions in the system and influence other parts of the domain.  They can be recognized by what happens immediately after them, as they typically trigger different kinds of activities or concerns. 
The workshop used a "Done By Me" service marketplace example where clients submit proposals for work (like window washing) and doers accept jobs.  Key pivotal events identified included:
- **Proposal Submitted** - marks transition from client interaction to pricing verification 
- **Pricing Verified** - shifts from pricing concerns to scheduling 
- **Proposal Matched** - transitions from matching to fulfillment workflow 
- **Work Fulfilled** - moves to review and payment phase 

**Language changes** serve as primary indicators of bounded context boundaries.  When the language and intent shift significantly across the timeline (from proposals to pricing to scheduling), candidate boundaries emerge.  For example, "proposals," "pricing," and "doers recommended" represent very different kinds of concerns and suggest natural boundaries. 
### Subdomain vs. Bounded Context Distinction

**Subdomains** represent the problem space - areas of business expertise and capability.  **Bounded contexts** represent the solution space - how the software is organized with explicit language boundaries. 
While they often align one-to-one, especially in modular monoliths, the distinction matters: subdomains help identify **who has the expertise** to discuss domain details.  If different people or different roles work on different parts of the timeline, that indicates subdomain changes.  For instance, pricing experts would be consulted for pricing subdomain details, while matching logic would involve different domain experts. 
### Event Storming Execution in Remote Settings

Bruno Tacca specifically asked about facilitating event storming remotely, noting challenges with online participation and tooling. 
Vaughn acknowledged that **big picture event storming doesn't work well online** due to the chaotic, collaborative nature of the activity.  The collision of voices in breakout rooms makes it difficult to interrupt or assist teams.  Key recommendations included:
- **Start with one important (likely pivotal) event** and work outward on either side rather than trying to storm everything at once 
- **Use a "raise hand" rule** for online sessions to manage turn-taking since natural conversation flow breaks down in video calls 
- **Accept that observers may contribute later** - people who seem disengaged in Zoom meetings may surface important observations after the session 
- **Expect language overlaps and duplicates** during initial storming; events with similar names (like "proposal accepted") aren't necessarily duplicates and require discussion to clarify 

For **team-level adoption**, Vaughn recommended starting small rather than trying to change entire organizations.  He advised avoiding DDD jargon and framing it as "collaborative software development" or simply "modeling" to reduce resistance.  The prophet-in-home-territory problem means internal advocates often face more skepticism than external consultants. 
## Tactical Design Patterns

### Domain Events Structure

A **domain event** is an immutable fact representing something that has happened.  It cannot be denied, though observers may not care about it.  Events should be stated in **noun-verb-past tense** format when possible (e.g., "Proposal Submitted," "Pricing Verified"). 
The technical structure includes: 
- **Event ID** - unique identifier for the event instance
- **Occurred On** - timestamp when the event occurred
- **Type Version** - semantic version of the event type itself (e.g., "1.2.0" for PricingVerified)
- **Aggregate/Entity ID** - identity of the thing the event occurred to
- **Essential attributes** - minimum data needed to understand what happened
- **Metadata** - supplementary information like user roles, correlation IDs, or policy identifiers

**Privacy information** should not be directly embedded in events, especially for event sourcing.  Instead, use a **Privacy Info ID** that references an encrypted entity containing sensitive data.  This enables both deletion (removing the entity) and **crypto-shredding** (deleting only the encryption key, making data permanently unrecoverable). 
### Three Event Types by Enrichment Level

**Essential events** contain only the minimum necessary attributes.  For example, Proposal Submitted would include proposal ID, client ID, and client expectations, but nothing more. 
**Rich/enriched events** explicitly provide additional attributes that aren't strictly necessary but are helpful to consumers.  For instance, including the proposal's progress state (what has already occurred) might help downstream consumers make decisions without querying back. 
**Event-carried state transfer (fatty) events** include the entire aggregate state or large portions of it.  These are particularly useful for analytics contexts that need complete snapshots.  However, they carry risk: if consumers query back to the source bounded context after receiving the event, the aggregate may have already changed, creating a **staleness problem**.  Solutions include embedding version numbers so queries can retrieve specific snapshots, or simply accepting eventual consistency. 
### Aggregate Design and Event Sourcing Implementation

**Aggregates** are transactional consistency boundaries.  Everything within an aggregate must be consistent with business rules (invariants) at the end of a transaction.  Other aggregates achieve consistency eventually through domain events. 
Vaughn demonstrated event-sourced aggregate implementation with several key patterns: 
**Factory methods express ubiquitous language** rather than exposing public constructors.  For example, `Proposal.submitFor(client, expectations)` reads naturally and prevents misuse of constructors. 
The implementation separates **command methods** (which apply events) from **when methods** (which mutate state): 
- **Command methods** like `submitFor()` create and apply events to represent state changes 
- **When methods** like `whenProposalSubmitted()` actually mutate the aggregate's state 
- This separation enables **state reconstitution** from event streams without re-adding historical events to the pending events collection 

**Immutable state pattern**: Vaughn uses a single mutable variable (`state`) that holds an immutable `ProposalState` value object.  All attributes within `ProposalState` are `final` (Java) or immutable, and state changes create entirely new `ProposalState` instances rather than mutating fields.  This brings functional programming principles into an imperative environment. 
**Progress tracking with sets**: The `Progress` value object maintains a **set of all events that have occurred** to the aggregate.  This allows order-independent queries like "was pricing verified?" or "was scheduling denied?" without caring about sequence.  When the progress includes submitted, pricing verified, and scheduling verified, the proposal reaches an "acceptable" state. 
**Guarded event application** prevents duplicate processing when events are re-delivered by messaging infrastructure.  For example, `verifyPricing()` checks if pricing was already verified before applying the event again. 
### Repository Pattern for Event Sourcing

The **repository interface** lives in the domain model package as a **driven port**.  It defines only two operations for event-sourced aggregates: 
- `save(proposal)` - persists new events that occurred in the current transaction
- `proposalOf(proposalId)` - reconstitutes aggregate from event stream

The **infrastructure implementation** (adapter) handles serialization/deserialization.  The `JournalProposalRepository` serializes events to JSON when saving and deserializes the event stream when loading. 
**Snapshot optimization**: Rather than replaying thousands of events, the system can snapshot the entire `ProposalState` as a single JSON object periodically.  When reconstituting, it loads the most recent snapshot and replays only subsequent events. 
A critical implementation detail: the repository uses a **public constructor with** **`@DoNotUse`** **annotation** for reconstitution.  This is necessary because Java lacks internal/friend visibility - the constructor must be public for the repository to call it, but convention prevents misuse.  In C#, this would be marked `internal` to restrict visibility to the assembly. 
### Ports and Adapters Architecture

The architecture organizes code into clear layers: 
- **Infrastructure (outside)** - REST controllers, messaging adapters, persistence implementations
- **Application/Query (inside ports)** - command handlers and query handlers
- **Model (core)** - aggregates, entities, value objects, domain events, domain services
- **Infrastructure (driven adapters)** - repository implementations, external service clients

**Application services** (command handlers) serve triple duty as ports, command model components, and application services.  They accept only **primitive types and standard library types** as parameters, never domain model types.  Internally, they construct domain objects (like `Client.from()` and `Expectations.of()`) before calling aggregate methods. 
For example, `ProposalCommands.submitProposal()` receives strings and primitives, constructs `Client` and `Expectations` value objects, calls `Proposal.submitFor()`, and saves the result via the repository. 
**Dependency injection** is accomplished through simple factory methods rather than IoC containers like Spring.  The startup/bootstrap code instantiates services directly, and an `API` class provides lazy initialization of command handlers with their dependencies. 
### CQRS Query Model and Projections

**Command and query models are separate** in CQRS.  The event-sourced aggregate (command model) has **no getter methods or read accessors**.  All queries go through the query model, which consists of **views** built by **projections**. 
**Projections** listen to domain events and build queryable views: 
- Each event contributes to or generates a view
- Views are designed to **avoid joins** - each view should contain all data needed for a specific UI or report use case 
- The query side provides methods like `getProposalView(id)` or `getAllProposalViewsFor(clientId)` 

For **distributed data across bounded contexts**, several composition strategies exist: :
- **Micro-frontend/composite UI** - each bounded context provides its own UI fragment; the client assembles them (e.g., client name comes from User context, proposal details from Matching context) 
- **Aggregated view context** - a separate context subscribes to events from multiple bounded contexts and builds unified views 
- **GraphQL gateway** - queries multiple bounded contexts and joins results at the API gateway layer 

The trade-off: **live composition** (querying multiple sources) provides fresher data but higher latency, while **static composition** (aggregated views) is faster but risks staleness. 
### Domain Services

**Domain services** are stateless operations that don't naturally belong to a single entity.  They typically **crosscut multiple aggregates** or enforce policies requiring examination of multiple instances. 
The `RankingCalculatorService` example demonstrates this pattern:  It loads multiple doer aggregates from a repository, ranks them according to business rules, and returns a `RankedDoers` value object (a sorted list of `RankedDoer` instances).  This logic doesn't belong to any single doer entity.
Domain services can be **passed as parameters to aggregate methods** or can **call aggregate behaviors from outside**.  However, they must remain stateless regarding business logic, though they may hold repository references for lookups. 
When injecting into aggregates, **pass as method parameters** rather than holding as aggregate state.  This keeps the aggregate's dependencies explicit and avoids hidden coupling. 
### Value Objects

**Value objects** are not "things" in the model but rather **identify, describe, measure, qualify, or quantify** things.  They are: 
- **Immutable** - state never changes after construction
- **Side-effect free** - behaviors return new instances rather than mutating
- **Functionally equivalent** - equality based on type and all attribute values, not identity
- **Hashable consistently** - equal values must have identical hash codes

For example, `Money` with amount 100 equals another `Money` with amount 100, but never equals `Age` with years 100, even if both have the same numeric value.  Type matters for equality. 
Value objects **can have rich behavior** despite immutability.  Methods like `money1.plus(100)` return a new `Money` instance rather than mutating the original.  This is **replacement rather than mutation**. 
**Client and Doer** in the Matching context are value objects, even though they're entities in the Profiles context.  This demonstrates how the same concept can be modeled differently depending on context needs - Matching doesn't need the full entity, just an identifier and perhaps a rank. 
## Advanced Topics

### Schema Registry and Event Versioning

**Schema registries** hold the types of a bounded context's **published language** - the commands and events exposed to other contexts.  They track **type versions** using semantic versioning (major.minor.patch). 
**Breaking changes require major version increments**.  For example, if GDPR compliance requires removing personal data from events, `PricingRejected` version 2.0.0 would be incompatible with 1.x versions.  The registry enforces this: you cannot persist an event with a breaking change without incrementing the major version. 
**Backward-compatible changes** (like adding optional fields) only increment minor or patch versions.  Consumers dependent on version 1.1.0 can safely consume 1.2.0 events. 
Vaughn demonstrated **SchemaMinder**, an open-source schema registry he developed with Claude Code.  It dynamically shows updates and enforces versioning rules.  Commercial alternatives include AWS EventBridge Schema Registry, Red Hat Schema Registry, and Confluent Schema Registry. 
The **Cloud Events** standard exists but has been "largely ignored" according to Vaughn. 
### Upstream-Downstream Relationships

When a bounded context is **downstream** from an upstream open-host service, it must know the upstream's **published language types**.  For example, Matching (downstream) sends `VerifyPricing` commands to Pricing (upstream). 
The downstream context defines these external types in its **messaging infrastructure layer**, not the domain model.  The `Subscriber` receives events with their full package names (e.g., `co.donebyme.pricing.model.PricingVerified`) and **translates** them to domain model concepts. 
For instance, the external event's `originatorId` maps to Matching's `proposalId`.  The infrastructure adapter performs this translation before passing data to the application service. 
**Anti-corruption layer pattern**: The subscriber acts as a protective layer, ensuring external concepts don't leak into the domain model's ubiquitous language. 
### Data Mesh Principles

**Data mesh** extends bounded context thinking to analytics.  Each bounded context owns not just its operational database but also a **data product** designed for analytical queries. 
Key principles: 
- **Data product ownership** aligns with operational data ownership - the same cross-functional team owns both 
- **Lineage tracking** is critical - knowing where data originates, who owns it, and whether copies are stale 
- **Enabling teams** from data warehouse/lake/mart groups can help bounded context teams build data products without creating centralized bottlenecks 

The traditional **centralized data warehouse approach fails 85% of the time** because central teams don't understand the data, can't keep up with change requests, and have long delivery timelines.  By the time they fix data issues, the data has changed again. 
Data products can serve dual purposes: both as **analytical datasets** and as **query model views** for operational systems.  Some contexts may maintain multiple data products - one for views, another for deeper analytics. 
### Event Sourcing Advantages and Trade-offs

**Event sourcing** stores every state change as a discrete event rather than updating current state.  This provides:
- **Complete audit trail** - every action that changed the aggregate is recorded 
- **Regulatory compliance** - critical for financial services, healthcare, equities trading where regulators require detailed history 
- **Temporal queries** - reconstruct state at any point in time by replaying events up to that version 
- **Event-driven integration** - events naturally publish to other bounded contexts 

Traditional databases already use a form of event sourcing - the **transaction log** records every change and enables replication by replaying the log. 
The trade-off: **more classes and complexity**.  Each state transition requires a distinct event class, which can feel like "a lot of changes" compared to simply adding methods to existing classes.  However, this granularity provides the audit trail and is essential in regulated industries. 
### Organizational Change Strategies

Bruno Tacca asked how to push for DDD and event storming when you're an individual contributor rather than a consultant brought in by leadership. 
Vaughn's advice: 
- **Start at team level** - don't try to influence the entire organization 
- **Avoid jargon** - don't sell "DDD" or "event storming" as products; frame it as "collaborative software development" or "modeling" 
- **Expect resistance** - even invoking authority figures like Martin Fowler may not help ("Martin Fowler has his opinions") 
- **People defend their turf** - they want familiar technologies on their CVs and resist unfamiliar approaches 

Even as an established author, Vaughn faces pushback when consulting.  At one Fortune 500 company, a chief architect tried to get him fired on day one, and only intervention from the CTO on the last day with the message "we are using DDD and event storming, explain to me if you don't like it" resolved the situation. 
**Smaller companies** where the CIO/CTO is involved from the beginning provide the most influence.  **Larger organizations** create more friction and noise that must be navigated carefully. 
Swarup shared his approach: after an initial event storming session with an external consultant, he's been conducting **one-on-one "event pairing" sessions** over six months, acting as the glue that assembles the complete picture.  People are "blissfully oblivious that there's a domain modeling exercise going on," and he expects another six months to convince budget holders to adopt the resulting architecture. 
## Implementation Details

### Namespace and Package Organization

**Internal model organization** uses detailed namespaces: 
```plaintext
co.donebyme.matching.model.proposal
```
This shows: company domain (co.donebyme), bounded context (matching), model layer, and specific aggregate (proposal). 
**External published events** use simplified namespaces: 
```plaintext
co.donebyme.matching.ProposalSubmitted
```
This hides internal structure and may use different event names externally than internally.  For example, internal `ProposalSubmitted` might be published as `ProposalReceived` if that better serves consumers. 
**Privacy data filtering**: When publishing externally, remove `privacyInfoId` and other sensitive attributes that should remain internal. 
### Handling Conflicts and Late Declines

The workshop detailed a complex scenario demonstrating eventual consistency: 
1. **Proposal matched to David** for Saturday morning work 
2. **David accepts**, triggering a 2-hour acceptance window for other doers 
3. **Maria (higher-ranked) accepts** but after the 2-hour window expires 
4. **Proposal unavailable event** sent to Maria; David is awarded the work 
5. **Backup doer policy** adds Maria to backup list in case David declines 
6. **David declines later** (wife reminds him of family commitment) 
7. **Latent decline policy** activates, offering work to Maria from backup list 
8. **Maria accepts in time** and is matched 

This demonstrates how **policies** (ProposalAcceptancePolicy, ProposalBackupDoerPolicy, ProposalLatentDeclinePolicy) coordinate complex business rules across time.  Even if David hadn't declined, Maria's backup status gives her priority for similar future proposals. 
### Arbitration and Review Workflow

After work completion, the system handles disputes: 
- **Reviews** collected from both client and doer 
- **Dispute initiated** if either party is unsatisfied (doer did poor work, or client added unreported work) 
- **Arbitration context** handles human-mediated resolution 
- **Profile updates** occur based on review outcomes, affecting future rankings 
- **Payment processing** completes after successful review or arbitration 

The workshop noted this area needed more detailed storming to fully flesh out the dispute workflow. 
## Questions and Clarifications

### Aggregate vs. Entity Distinction

Christine asked why Proposal is an aggregate rather than just an entity.  Vaughn clarified: **the aggregate root IS an entity**.  The aggregate is the conceptual boundary, and the root entity is named for that concept.  In business discussions, use "entity" or even just "data" to avoid confusing non-technical stakeholders with the term "aggregate." 
### Factory Method and Deserialization

Cristóbal raised concerns about deserializing aggregates when constructors are private and only factory methods are public.  For **event sourcing**, the repository uses a special public constructor marked `@DoNotUse` that accepts an event stream.  This constructor calls `when` methods to rebuild state without re-applying events to the pending events collection. 
For **traditional persistence** with OR mapping, annotations/attributes can be placed on the `ProposalState` class, which is easily serialized as a single JSON object for snapshots.  The same infrastructure handles both serialization for persistence and deserialization for reconstitution. 
### View vs. Repository Query

Júlia asked about the difference between querying a view and querying through the repository.  The repository query retrieves the **full aggregate** on the command side for executing commands.  The view query retrieves **read-optimized projections** on the query side for display.  In event sourcing, aggregates have no getters - if you need to display data, you must query the view model. 
### Emitting Events in Aggregates

Taras asked whether events emitted by aggregates are the same events consumed by other contexts.  Yes - events published to the message bus are consumed by subscribers in other bounded contexts.  The `MatchingJournalPublisher` listens to the matching journal and publishes events to the "all" topic.  Subscribers filter for events they care about (like `PricingVerified` or `SchedulingVerified`). 
### Domain Service Injection

Civio asked about injecting domain services into entities.  Vaughn recommended **passing as method parameters** rather than holding as state: "When you say inject, that to me has the meaning that it holds the state. I would pass it in."  This keeps dependencies explicit and prevents hidden coupling between aggregates and services. 
## Workshop Logistics and Resources

### Source Code Access

All examples are available in both **Java and C#** in the shared drive.  The code demonstrates:
- Event-sourced aggregates with immutable state patterns 
- Ports and adapters architecture with clear layering 
- CQRS with projections building views 
- Messaging infrastructure with subscribers and publishers 

Vaughn committed to uploading the **latest code version** that matches what he demonstrated, as participants noted some differences from the shared version. 
### Miro Boards and Diagrams

The workshop Miro boards will be uploaded after the session ends.  They include:
- Detailed event storming timelines with aggregates, commands, and events 
- Source code mappings showing which packages contain which patterns 
- Choreography and orchestration examples through fulfillment, reviews, and arbitration 

### Certificate of Completion

Christine and Haikel requested certificates for professional development reimbursement.  Vaughn confirmed this is common (especially in Germany) and asked them to email the registration address for certificate generation. 
### Future Workshops and Community

Vaughn is teaching two more weeks of workshops - one at the original time and one starting three hours later (5pm-9pm GMT-7) for teams in Mexico and South America who found 5am/6am too early. 
He
