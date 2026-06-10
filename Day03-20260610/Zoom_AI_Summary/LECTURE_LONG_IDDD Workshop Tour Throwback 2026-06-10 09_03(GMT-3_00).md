## Concept

**Event Storming** is a collaborative modeling technique that uses colored sticky notes to visualize business processes and software systems at various levels of detail—from big picture (high-level process flow) to design level (detailed component interactions) . The core elements include:
- **Events** (orange): Facts that happened in the past, named as noun + verb in past tense (e.g., "Proposal Submitted") 
- **Commands** (blue): Imperative actions that trigger events 
- **Actors/Personas** (yellow): Users or roles who initiate commands 
- **Policies** (lilac): Business rules that react to events 
- **Aggregates** (pale yellow): Entities that handle commands and emit events 
- **External Systems** (pink): Integration points 

**Hexagonal Architecture** (Ports and Adapters) separates the application's core business logic from technical infrastructure by defining clear boundaries . The architecture consists of:
- **Inside (Application/Domain)**: Pure business logic without technical dependencies 
- **Outside (Infrastructure)**: Technical implementations (databases, REST, messaging) 
- **Ports**: Interfaces that define how the inside communicates 
- **Adapters**: Technical implementations that translate between outside technology and inside interfaces 

The **driver side** receives requests from users/external systems, while the **driven side** contains implementations for persistence, messaging, and external service calls .
## What I understood

### Event Storming Fundamentals

The workshop demonstrated event storming using the "Done by Me" home services platform as the primary example. The process begins with identifying domain events—things that have already happened in the business process . Vaughn emphasized that events should be named in past tense (e.g., "Proposal Submitted" rather than "Submit Proposal") , following a pattern of noun followed by verb in past tense where the noun indicates *where* it happened and the verb indicates *what* happened .
Two fundamental approaches exist for starting event storming :
1. **Sequential approach**: Start with an icebreaker event that everyone agrees must happen, then build forward and backward from there 
2. **Storming approach**: Rapidly place events in any order as they come to mind, embracing the chaotic "raindrop" nature of brainstorming 

The "storming" metaphor is intentional—like raindrops in a thunderstorm that are unpredictable and unevenly spaced, ideas should flow freely without worrying about correctness or order initially . The goal is to **drive conversation** and generate a plethora of thoughts rather than achieve perfection .
### Business Process Modeling in Done by Me

The workshop walked through modeling the proposal submission and matching workflow . Key events identified included:
- **Proposal Submitted**: When a client creates a service request 
- **Proposal Accepted**: When a doer agrees to take the job 
- **Proposal Awarded**: The final selection of a doer 

Critical business policies emerged during the discussion :
- **Pricing policy**: Maximum discount limits to prevent doers from undercutting each other and driving prices too low 
- **Ranking policy**: Preferred doers get priority consideration, with top-ranked doers receiving first opportunity even if lower-ranked doers accept first 
- **Premium client benefits**: Clients like Julia (CIO who offers 20% tips) should be matched with top-ranked doers to ensure mutual satisfaction 

The modeling revealed time-based considerations—if a 9th-ranked doer accepts first but higher-ranked preferred doers have also been notified, the system should wait to offer the best possible match rather than immediately awarding to the first acceptor .
### Event Storming Levels of Detail

Vaughn described event storming as operating at different "altitudes" :
- **Big picture**: 11,000 meters high, showing overall process flow with events and basic policies 
- **Process level**: More detail about actors, commands, and business rules 
- **Design level**: Nearly software component design, showing specific aggregates, commands, domain events, projections, and integration events 

At design level, the model shows personas (like Julia the premium client), specific commands they execute ("Submit Proposal"), the aggregate that handles it (Proposal), the domain event emitted ("Proposal Submitted"), view projections created, and integration events published to other bounded contexts .
### Hexagonal Architecture Structure

The architecture separates concerns through clear boundaries . The **strict layers architecture** (UI → Application → Domain → Infrastructure) creates problems because the domain model must depend on infrastructure, creating wrong-direction dependencies . The **dependency inversion principle** flips infrastructure to the top so it depends on interfaces defined below .
Hexagonal architecture simplifies this to inside/outside with two sides :
**Driver side** (left):
- REST controllers/endpoints that receive HTTP requests 
- Message listeners that consume events from topics 
- These adapters translate technical requests into plain objects that ports (application services) can understand 

**Driven side** (right):
- Repository implementations for database access 
- Message senders that publish to topics 
- Anti-corruption layers that use gRPC/REST to call other bounded contexts 
- Domain service implementations that contain technical logic 

The key principle: **adapters deal with technology, ports deal with plain objects** . Driver-side adapters translate technology (HTTP, messaging protocols) into plain Java/C#/TypeScript objects that application services can process . Driven-side adapters implement interfaces defined in the domain/application layer, providing technical implementations while keeping the core technology-agnostic .
### Application Services vs Domain Services

**Application services** (ports) act as use case coordinators . They:
- Retrieve aggregates from repositories 
- Dispatch commands to aggregate methods 
- Orchestrate the workflow without containing business logic 

**Domain services** contain business logic that doesn't belong to a single aggregate . Two types exist:
1. **Pure domain services**: Business logic implementations with no technical dependencies (no interface needed) 
2. **Technical domain services**: Interfaces defined in the domain model but implemented in infrastructure for technical operations like calling other bounded contexts via gRPC 

The domain model references domain service interfaces using ubiquitous language terms, but infrastructure provides the actual implementation that makes network calls or performs other technical operations .
### Modular Monolith Considerations

In a modular monolith where bounded contexts run in the same process, anti-corruption layers still use the same interface-based design . The only difference: instead of making network calls, adapters make direct method calls to other modules . This approach:
- Maintains loose coupling through interfaces 
- Eliminates network failure risks 
- Preserves the same architectural patterns 
- Allows future extraction to microservices with minimal changes 

Vaughn emphasized using the **principle of least knowledge**—even in-process communication should go through adapters and interfaces rather than direct dependencies .
### Choreography vs Orchestration

**Choreography** uses pure event-driven communication where each bounded context reacts to events and emits new events . In the Done by Me example:
1. Matching emits "Proposal Submitted" 
2. Pricing receives it, processes, emits "Pricing Verified" 
3. Profiles receives "Proposal Submitted", emits "Doers Matched" 
4. Events flow through multiple contexts in a chain reaction 

The drawback: **tight coupling** where Matching has partnership dependencies with every bounded context in the flow . Debugging becomes difficult because no single place tracks the overall process state .
**Orchestration** uses a process manager (saga) that owns the workflow :
- Receives events from bounded contexts 
- Emits commands to drive the next steps 
- Tracks process state and handles failures 
- Provides content-based routing 

Vaughn recommended deploying the process manager **inside the Matching bounded context's infrastructure** initially . This reduces network calls by half compared to deploying it as a separate microservice—cutting failure opportunities in half .
### Event Storage and Persistence

Each bounded context stores its own domain events in its own database . Events from Pricing go into the Pricing database, events from Matching go into the Matching database . This supports:
- Bounded context autonomy 
- Event sourcing capabilities 
- Audit trails and temporal queries 

Vaughn noted he keeps domain events forever and would explain the detailed rationale (why events, why event sourcing) in the next session .
### Microservices Library for Prototyping

Vaughn distributed a library called "Microservices" (likely VLINGO/XOOM) for rapid prototyping . Benefits include:
- Run modular monolith with multiple bounded contexts in same process 
- In-memory databases and persistence 
- In-memory message topics with async delivery via threads 
- Event sourcing journal and key-value store included 
- Focus on business modeling without network/database complexity 

His guidance: **do not introduce the network too early, do not introduce specific databases too early** . These create hassles when the focus should be business process and models . He shared an anecdote of wasting a week trying to provision Azure Cosmos DB when the client wanted to see repository implementation instead of modeling the domain .
### Extraction Strategy: Monolith to Microservices

Vaughn illustrated a realistic evolution path :
**Starting point**: Modular monolith with all bounded contexts in one deployment 
**First extraction - Pricing**: Extract due to **rate of change** (needs more frequent releases than other teams can keep up with), shifting from partnership to upstream-downstream relationship 
**Second extraction - Financial/IAM**: Extract due to **security requirements** (prevent unauthorized access to sensitive financial and identity data) 
The architecture changes minimally during extraction :
- Same adapters, just now using real network calls instead of in-process 
- If already using real messaging (RabbitMQ, Kafka), no change at all 
- Database access unchanged 

This demonstrates the power of hexagonal architecture—the inside remains stable while the outside adapters handle deployment changes .
### Workshop Exercise Experience

Students worked in teams on event storming the Done by Me system. Key learnings reported :
- Discovered many business rules and hotspots just by talking through the process 
- Learned more about the business in the short session than would have working alone 
- Challenge of maintaining consistent detail level—easy to dive too deep on specific topics 
- Realization that correctness matters less than the discussions and scenarios surfaced 

Vaughn emphasized: **all models are wrong, some are useful** . Models are wrong because they are perspectives, not reality itself . Different teams had different models, none absolutely wrong—they represent different valid perspectives .
**Models never reach perfection**. Complex systems evolve continuously until the day before retirement . The best the model will ever be is the day before the system is decommissioned .
### Practical Event Storming Tips

**When working with business experts** (non-developers) :
- Don't insist on strict naming conventions (noun-verb-past-tense) 
- Don't gatekeep with too many rules about colors and formats 
- Let business people state events as commands if that's natural to them 
- You can always refine the language later 

Diego shared his team's approach: with business stakeholders, use **only events and hotspots** initially . This makes it easier for them to understand and express the flow. Technical teams can later add commands, policies, and subsystem details .
**Optimal workshop duration**: 3-4 hours maximum per session . Event storming is mentally exhausting . Better approach: multiple half-day sessions with sleep in between, allowing the brain to process and generate new questions . In about 12 hours total (three 4-hour sessions), teams can achieve deep system understanding—far faster than trying to understand existing code .
### DomoRobo Modeling Tool

Vaughn demonstrated [DomoRobo.com](https://domorobo.com), a modeling tool he's rebuilding that supports :
- Event storming models with properties and module definitions 
- Context maps that auto-sync with event storming 
- Business model canvas 
- Multiple architecture views: hexagonal, flow/cloud, C4 diagrams 
- Architecture Decision Records (ADRs) linked to specific components 
- Topo architecture showing flow between bounded contexts 
- Impact mapping for goal-driven feature discovery 

The tool was experiencing WebSocket persistence issues during the demo, so he couldn't provide immediate access but promised to share links once fixed .
### Impact Mapping for Feature Discovery

Impact mapping provides a structured approach to **doing the right thing on purpose** rather than building features pulled from thin air . The levels are :
1. **Goal**: Strategic objective tied to a real problem (e.g., "Fair pricing as soon as possible") 
2. **Actors (WHO)**: User roles whose behavior must change to reach the goal 
3. **Impacts (HOW)**: Specific behavioral changes needed in each actor 
4. **Deliverables (WHAT)**: Software features, or potentially non-software solutions like email campaigns 

This reverses the typical backwards process where managers demand features, teams write user stories, and no one validates whether the feature actually solves a problem . Impact mapping ensures every deliverable traces back to a validated strategic goal .
### Workshop Context and Teaching Philosophy

Vaughn expressed strong preference for public workshops over corporate training . In public workshops, attendees choose to be there and want to learn. In corporate settings, often 75% attend because management required it—they don't pay attention, get lost during exercises, and provide poor feedback .
He shared a frustrating PayPal experience where reviewers rated him highly on teaching bounded contexts, context mapping, and ubiquitous language, but gave him a "2" on strategic design—despite those topics *being* strategic design . The disconnect highlighted how corporate review processes can be arbitrary and contradictory .
Online teaching exhaustion is real, especially when only 3 of 25 people have cameras on and critical feedback comes from anonymous "snipers" . Vaughn appreciated this workshop's engaged participants with cameras on .
## What I didn't fully get

### Pricing Policy Implementation Details

While the workshop identified that a **maximum discount policy** prevents doers from racing to the bottom , the specific implementation remained vague . Vaughn marked it as "needing some kind of pricing policy" without diving into whether this would be:
- A hard constraint enforced at proposal submission time
- A validation rule that rejects proposals exceeding the discount
- A ranking factor that deprioritizes aggressive discounters
- Integration with the average metropolitan area pricing mentioned in context mapping discussions 

The relationship between the pricing policy and the "Verify Pricing" command that the process manager dispatches remained unclear . This would likely be covered in more detail during tactical modeling.
### View Projections and CQRS Relationship

At design level, the model shows a "Proposal Projection" policy triggered by "Proposal Submitted" . Haikel asked when to use the "view/query" icon . Vaughn explained that even without using event sourcing and CQRS technically, the **mental model** still applies :
- Events from event sourcing project into view models for querying 
- Even querying the same relational database you write to can be thought of as a "view query" 
- The separation of write models (aggregates) from read models (views) helps thinking even in traditional architectures 

However, the specific mechanics of how projections update, handle eventual consistency, and serve queries remained abstract. Diego mentioned using projections as "data source for policies" , suggesting views feed back into business rules, but this circular relationship wasn't fully explored.
### Domain Service Interface Necessity

Taras questioned why domain services need interfaces at all—"domain is domain, how many implementations could you have?" . Vaughn clarified:
- **Pure business logic domain services don't need interfaces** 
- **Interfaces make sense when the domain concept requires technical implementation** in infrastructure (like calling another bounded context via gRPC) 

The example from the Red Book (Chapter 11): the Collaboration context needs to check if a user has the "moderator" role, which requires calling the Identity and Access Management context. The domain model defines the interface using ubiquitous language, but infrastructure provides the technical REST/gRPC implementation .
The subtlety: the domain service interface represents a domain concept (checking authorization) but requires technical implementation. This differs from repositories, which are clearly technical, and pure domain services, which are purely business logic.
### Driver-Side Port Interfaces Debate

Sergei raised confusion about driver vs driven side ports . On the **driven side**, adapters implement abstractions (interfaces) defined by ports in the core. But on the **driver side**, adapters *use* abstractions rather than implementing them—they call application services directly .
Vaughn acknowledged some practitioners (particularly in the .NET world) insist on interfaces for driver-side ports too, creating `ICalculator` with `CalculatorImpl` . He finds this excessive—it creates consistency but adds unnecessary indirection . The debate reflects different schools of thought on whether architectural consistency (interfaces everywhere) outweighs pragmatism (interfaces only where needed for dependency inversion).
The practical question: should REST controllers depend on concrete application service classes or interfaces? Vaughn's position: concrete classes are fine on the driver side because you're not inverting dependencies—you're just calling into your own application .
### Process Manager Deployment Trade-offs

While Vaughn recommended deploying the process manager inside Matching's infrastructure initially to reduce network calls by 50% , the long-term trade-offs remained unexplored:
- When does the process manager warrant extraction to its own microservice?
- How do you handle process manager scaling independently of Matching?
- What happens when multiple bounded contexts need to orchestrate their own processes—do they each embed their own process managers?
- How do you query process state when it's embedded in another service's infrastructure?

The guidance to "at least start with" embedding it suggests eventual extraction, but the decision criteria weren't specified .
### Schema Registry Integration

Vaughn mentioned the matching process manager depends on "schemas from their schema registry" , but didn't elaborate on:
- What schema registry technology (Confluent Schema Registry, AWS Glue, etc.)
- How schema evolution is managed across bounded contexts
- Whether schemas define commands, events, or both
- How schema compatibility is enforced (backward, forward, full)

This likely connects to the integration events and anti-corruption layers discussed earlier, but the operational details remain for future sessions.
### Measuring Performance for Architecture Decisions

When Sergei asked about network calls and timeouts in domain service implementations, Vaughn emphasized "you have to measure it" . He referenced Martin Thompson's advice: "His very first answer is measure it" .
This raises questions about:
- What metrics matter for deciding between synchronous (gRPC) vs asynchronous (messaging) integration?
- At what latency threshold does a domain service call become unacceptable?
- How do you measure and monitor these calls in production?
- What tooling supports these measurements (distributed tracing, APM)?

The principle is clear (measure, don't assume), but the practical implementation of measurement remained abstract.
## Might show up on the exam

### Event Storming Essentials

- [ ] **Event naming convention**: Noun + verb in past tense (e.g., "Proposal Submitted" not "Submit Proposal") 
- [ ] **Color coding**: Orange for events, blue for commands, yellow for actors, lilac for policies, pale yellow for aggregates 
- [ ] **Two starting approaches**: Sequential (one agreed event, build from there) vs storming (rapid chaotic placement) 
- [ ] **Three detail levels**: Big picture (11,000m altitude), process level (more detail), design level (near-software components) 
- [ ] **"All models are wrong, some are useful"**: Models are perspectives, not reality; different teams will have different valid models 
- [ ] **Evolution over perfection**: Complex systems evolve until retirement; models never reach perfection 
- [ ] **Optimal session length**: 3-4 hours max, brain needs sleep to process between sessions 
- [ ] **With business experts**: Use only events and hotspots initially, don't enforce strict naming/color rules 

### Hexagonal Architecture Core Principles

- [ ] **Inside vs Outside**: Inside = application/domain (business logic), Outside = infrastructure (technology) 
- [ ] **Ports**: Interfaces that define communication boundaries 
- [ ] **Adapters**: Technical implementations that translate between technology and business logic 
- [ ] **Driver side**: Receives requests (REST endpoints, message listeners) 
- [ ] **Driven side**: Implements outbound calls (repositories, message senders, external service calls) 
- [ ] **Key principle**: Adapters deal with technology, ports deal with plain objects 
- [ ] **Dependency direction**: Infrastructure depends on domain/application, never the reverse 

### Application vs Domain Services

- [ ] **Application service role**: Use case coordinator, orchestrates workflow, no business logic 
- [ ] **Application service actions**: Retrieves aggregates, dispatches to domain methods, uses repositories 
- [ ] **Domain service - pure**: Business logic that doesn't belong to single aggregate, no interface needed 
- [ ] **Domain service - technical**: Interface in domain model, technical implementation in infrastructure (e.g., gRPC calls) 
- [ ] **Interface decision**: Use interface when domain concept requires technical implementation, skip for pure business logic 

### Modular Monolith Patterns

- [ ] **Anti-corruption layer in monolith**: Still uses interfaces, just makes method calls instead of network calls 
- [ ] **Principle of least knowledge**: Even in-process, communicate through adapters/interfaces 
- [ ] **Extraction strategy**: Extract for rate of change (frequent releases) or security (sensitive data), not just scale 
- [ ] **Extraction impact**: Minimal code changes if using hexagonal architecture—adapters change, inside stays same 

### Choreography vs Orchestration

- [ ] **Choreography**: Pure event-driven, each context reacts to events and emits new ones 
- [ ] **Choreography drawback**: Tight coupling, partnership with every context in flow, hard to debug 
- [ ] **Orchestration**: Process manager/saga receives events and emits commands, owns workflow state 
- [ ] **Orchestration advantages**: Tracks process state, handles failures, provides content-based routing 
- [ ] **Initial deployment**: Put process manager in originating bounded context's infrastructure to reduce network calls by 50% 

### Event Persistence

- [ ] **Bounded context autonomy**: Each bounded context stores its own events in its own database 
- [ ] **Event retention**: Keep domain events forever for audit, temporal queries, event sourcing 

### Impact Mapping Structure

- [ ] **Level 1 - Goal**: Strategic objective tied to real problem 
- [ ] **Level 2 - Actors (WHO)**: User roles whose behavior must change 
- [ ] **Level 3 - Impacts (HOW)**: Specific behavioral changes needed 
- [ ] **Level 4 - Deliverables (WHAT)**: Software features or non-software solutions 
- [ ] **Principle**: Every feature traces back to validated strategic goal, not manager's intuition 

### Prototyping Best Practices

- [ ] **Don't introduce network too early**: Use in-memory implementations during modeling 
- [ ] **Don't introduce specific databases too early**: Focus on business models, not technical hassles 
- [ ] **Microservices library benefits**: In-memory persistence, messaging, event sourcing for rapid prototyping 


