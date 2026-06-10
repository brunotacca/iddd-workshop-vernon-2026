## Key Outcomes

The workshop focused on applying **event storming** to model the Done By Me system, progressing from big-picture modeling to design-level detail. Participants practiced identifying domain events, commands, and policies while discovering business rules through collaborative modeling. The session covered **ports and adapters (hexagonal) architecture** for bounded context implementation and contrasted choreographed vs. orchestrated process flows. Key architectural decisions included placing the **process manager in matching's infrastructure** to reduce network calls by half, and extracting bounded contexts from a modular monolith based on **rate of change** rather than scalability alone. 
## Workshop Structure and Event Storming Fundamentals

### Event Storming Approach

Event storming operates at multiple altitudes, from **big picture** (36,000 feet / 11,000 meters) down to near-software design detail. The primary modeling element is the **domain event**, named in past tense following the pattern "noun + verb in past tense" (e.g., "Proposal Submitted" rather than "Submit Proposal"). 
The storming metaphor emphasizes unpredictable, abundant idea generation—like raindrops in a thunderstorm—where participants brainstorm all possible events without worrying about order or correctness initially. One effective starting approach is placing an **icebreaker event** on the board that everyone agrees must happen, then building out from there. 
### Color-Coded Palette

The event storming palette uses specific colors with semantic meaning:
- **Orange**: Domain events (things that happened)
- **Blue**: Commands (imperative actions)
- **Lilac/Purple**: Policies (business rules, "whenever X, then Y")
- **Pale yellow**: Entities/Aggregates
- **Bright yellow**: User roles/Actors
- **Pink**: External systems
- **Red**: Hotspots (questions, problems, areas needing clarification) 

Color consistency matters for visual scanning, though accommodations can be made for colorblind participants using symbolic icons (e.g., person symbol for user roles, squares for state). 
## Done By Me Business Process Modeling

### Core Event Sequence

The fundamental workflow for Done By Me begins with **Proposal Submitted** by a client. This triggers a sequence where doers can accept proposals, leading to **Proposal Accepted** events. However, the team identified that multiple doers might accept the same proposal, requiring a **business policy** to determine which acceptance wins. 
The **pricing policy** emerged as critical, with the team recognizing they must avoid driving prices too low through competitive bidding. A **maximum discount** standard prevents doers from underbidding each other, protecting both preferred doers (the highest quality workers) and maintaining sustainable market rates. 
### Doer Ranking and Client Matching

The system implements a **doer preference ranking** where higher-ranked preferred doers receive priority consideration. If the 9th-ranked doer accepts first but higher-ranked doers (1st or 2nd) have also been notified, the system should wait to offer the best match to premium clients like **Julia** (the CIO persona who offers 20% tips and values quality over price). 
This time-based consideration introduces complexity: the system must balance waiting for optimal matches against response time expectations. The matching process considers both doer rank and client value, with premium clients like Julia paired with top-ranked doers to maximize satisfaction on both sides. 
### Personas Driving Design

Two contrasting personas shape the business model:
- **Julia**: Fortune company CIO, not price-sensitive, offers 20% tips, values quality and convenience
- **Michael**: Retired postal worker on fixed pension, bargain hunter with mobility limitations (old football injury), still maintains neat home 

These personas inform how the system handles pricing policies, doer selection, and service level differentiation. 
## Team Exercise Insights

### Discovery Through Conversation

Participants reported discovering business rules and hotspots they wouldn't have found working alone. The collaborative nature surfaced **many rules and scenarios** through discussion, teaching participants more about the business in the short session than typical individual analysis would. 
Key challenges included:
- **Maintaining consistent detail levels**: Teams easily dove deep on specific topics while leaving other areas unexplored
- **Avoiding CRUD terminology**: Participants were explicitly instructed to think in business language rather than database operations (create, read, update, delete)
- **Accepting imperfection**: Understanding that "it didn't really matter if it was right" because the value lies in discussions and discovering scenarios 

### Model Evolution and Correctness

The workshop reinforced that **"all models are wrong, some are useful"**—models are perspectives, not reality itself. Different teams produced different models for the same system, yet none were inherently wrong; they represented different valid perspectives. 
Complex systems evolve continuously until decommissioned, meaning **the best model exists the day before retirement**. This reality counters developer tendencies toward seeking perfect, unchanging models. The focus should be on useful models that facilitate understanding and conversation rather than absolute correctness. 
## Design-Level Event Storming

### From Big Picture to Implementation

At design level, event storming adds **commands**, **aggregates**, **policies**, and **read models (views)**. The transition from big picture to design involves:
1. **Identifying personas** (e.g., Julia the client)
2. **Mapping commands** (imperative actions like "Submit Proposal")
3. **Creating aggregates** (Proposal entity)
4. **Emitting domain events** (Proposal Submitted)
5. **Projecting views** for user consumption
6. **Publishing integration events** to external contexts 

For example, when Julia submits a proposal:
- Command: "Submit Proposal" (blue)
- Aggregate: "Proposal" is created (pale yellow)
- Domain Event: "Proposal Submitted" (orange)
- View: "Proposal Projection" for UI display
- Integration Event: "Proposal Submitted*" (asterisk denotes external version) published to other contexts 

### Process Managers and Sagas

The **matching process saga** (or process manager) coordinates cross-context workflows. After Proposal Submitted, the saga drives subsequent commands like **Verify Pricing** to the pricing context. Process managers own the orchestration logic and maintain state about where processes are in their lifecycle. 
This approach differs from pure choreography by centralizing coordination, making it easier to understand flow and debug stuck processes. 
## Ports and Adapters (Hexagonal) Architecture

### Core Principles

Ports and adapters architecture divides systems into **inside** (application/domain) and **outside** (infrastructure). The inside contains business logic; the outside handles technical concerns like databases, messaging, and external APIs. 
The architecture has two sides:
- **Driver side**: Where users/systems drive requests into the application (typically left side)
- **Driven side**: Where the application drives requests to infrastructure (typically right side) 

**Adapters** translate between technology and the application's pure language. **Ports** are the interfaces through which adapters communicate with the application—application services on the driver side, repository interfaces on the driven side. 
### Dependency Inversion

The **dependency inversion principle** (the "D" in SOLID) ensures infrastructure depends on domain interfaces, not vice versa. Repository interfaces are defined in the domain model but implemented in infrastructure, creating correct dependency direction. 
This contrasts with traditional layered architectures where domain models incorrectly depended on infrastructure layers. By inverting dependencies, the domain remains pure and technology-agnostic. 
### Detailed Component Flow

In a complete ports and adapters implementation for a bounded context:
**Driver Side:**
- REST controllers/endpoints adapt HTTP requests to commands
- Message topic listeners adapt events to commands
- Both adapters invoke the same **application service (port)** using plain objects
- Application services coordinate use cases without containing business logic 

**Driven Side:**
- **Repository adapters** implement domain repository interfaces for data persistence
- **Domain service adapters** implement domain service interfaces for technical concerns (e.g., gRPC calls to other contexts)
- **Message sender adapters** publish events to external topics
- **Anti-corruption layers** translate external system models to domain language 

The application service orchestrates by:
1. Loading aggregates via repository
2. Dispatching behavior to aggregates
3. Aggregates using domain services when needed
4. Persisting changes via repository
5. Publishing events via message sender 

### Architectural Invariants

Despite increasing component complexity, the fundamental pattern remains: **adapter → port → port → adapter**. Every additional component follows this same pattern; complexity doesn't change the architectural rules. 
Tools like **ArchUnit** in Java can enforce architectural boundaries, asserting that nothing inside the application layer depends on anything except standard libraries, keeping external concerns truly external. 
### Domain Services: Interface vs. Implementation

**Domain services** serve two purposes:
1. **Pure business logic** that doesn't belong to a single aggregate—these don't need separate interfaces
2. **Technical implementations** requiring infrastructure (network calls, external APIs)—these need interfaces in the domain with implementations in infrastructure 

For example, a domain service calling another bounded context via gRPC has its interface defined in the domain (using ubiquitous language) but implemented in infrastructure as an **anti-corruption layer**. The domain model remains ignorant of the technical implementation. 
### Driver vs. Driven Port Differences

Driver-side adapters **use** ports (application services) directly—they don't typically implement interfaces. The adapter translates technology (HTTP, messages) to plain objects the port understands. 
Driven-side adapters **implement** interfaces defined by ports (repositories, domain services). This asymmetry sometimes confuses developers expecting symmetry, but it reflects the different relationship directions. 
Some practitioners advocate for interfaces on both sides for consistency, but this can lead to redundant naming (Calculator interface with CalculatorImpl) without clear benefit. The key is that driver-side ports are already defined interfaces (the application service APIs). 
## Choreography vs. Orchestration

### Pure Choreography Flow

In choreographed architecture, bounded contexts react to events autonomously without central coordination:
1. **Matching** emits "Proposal Submitted"
2. **Pricing** receives event, adapts to command, processes, emits "Pricing Verified"
3. **Profiles** receives "Proposal Submitted", processes, emits "Doer Matched"
4. Multiple contexts may receive the same event (e.g., "Doer Matched" goes to both Pricing and Recruiting)
5. **Scheduling** eventually receives events, processes, emits back to Matching
6. **Matching** sends notifications to doers 

This flow involves **no human interaction** after initial proposal submission—pure system choreography handles the entire workflow. 
### Choreography Limitations

Pure choreography creates **tight coupling** through partnership relationships. Matching becomes dependent on the exact flow and message formats of every participating context. Understanding the overall process requires tracing through multiple contexts, and debugging failures becomes difficult since no component owns the overall flow. 
### Orchestration with Process Manager

An **orchestrator (process manager or saga)** centralizes coordination:
- Receives events from all contexts
- Emits commands to appropriate contexts
- Owns **content-based routing** logic
- Maintains process state for debugging and monitoring
- Provides single point to understand overall flow 

**Deployment consideration**: Initially deploy the process manager in **Matching's infrastructure** rather than as a separate microservice. This reduces network calls by half—instead of 12 network hops, only 6 are needed. Half as many network calls means half as many failure opportunities. 
The process manager depends on **schemas from a schema registry** to understand message formats from different contexts. 
## Modular Monolith to Microservices Evolution

### Starting with Modular Monolith

All bounded contexts can initially deploy in a **single process** as a modular monolith. Each context maintains the same ports and adapters architecture, with clear boundaries between modules. The only difference from microservices is deployment—all contexts share the same process space. 
Benefits include:
- No network latency between contexts
- Simplified deployment and operations
- Same architectural patterns as microservices
- Easy extraction later when needed 

### Strategic Extraction Criteria

Extract bounded contexts to microservices based on:
1. **Rate of change**: Pricing needs more frequent releases than the rest of the monolith, shifting from partnership to upstream-downstream relationship
2. **Security**: Financial Accounts and Identity & Access Management extracted to isolate sensitive data and prevent unauthorized access
3. **Scalability**: Extract when specific contexts need independent scaling (but this is less common than rate of change) 

**Extraction is minimally invasive**: If using real messaging (RabbitMQ, Kafka) across modules, extraction only requires changing adapters to use network protocols instead of in-process calls. Database access remains unchanged. The internal architecture stays identical. 
### Example Evolution Path

Starting configuration:
- All contexts in monolith
- Pricing tightly coupled with Matching

After extraction:
- **Pricing** becomes separate microservice (rate of change driver)
- **Financial Accounts** extracted (security driver)  
- **Identity & Access Management** extracted (security driver)
- Remaining contexts stay in monolith
- Network introduced only where necessary 

## Development Tools and Practices

### Vlingo/Xoom Microservices Library

The **Vlingo/Xoom library** (now part of the Xoom project) enables **modular monolith development** with:
- In-memory databases for rapid iteration
- In-memory message topics
- Separate threads for asynchronous message delivery
- Event sourcing journal
- Key-value store
- No network or external database dependencies during modeling 

**Critical principle**: "Do not introduce the network too early. Do not introduce a specific database too early." These create hassles when the focus should be business process and models. One practitioner spent an entire week just trying to provision Azure Cosmos DB instead of modeling. 
### Domo Modeling Tool

Vaughn demonstrated **Domo**, a modeling tool he's developing (available at [DomoRobo.com](https://domorobo.com), with a next-generation version in progress). Domo supports:
- **Event storming models** with full palette support
- **Context maps** that auto-sync with event storming
- **Business model canvas**
- **Multiple architecture views**: Hexagonal ports and adapters, flow architecture, cloud architecture (AWS, Azure, Google Cloud, hybrid)
- **C4 diagrams** (system context, containers, components)
- **Architecture Decision Records (ADRs)** linked to specific architectural elements
- **Mind mapping and impact mapping**
- **Topo architecture** showing flow between bounded contexts with detailed internal structure 

The tool enables **collaborative real-time editing** via WebSockets (though this feature was experiencing deployment issues during the demonstration). 
## Impact Mapping: Doing the Right Thing on Purpose

### Backward Feature Development Problem

Typical development workflow: A manager dictates a feature ("this is exactly what we need"), teams immediately write user stories, and development begins—**completely backwards**. No one proves the feature is actually needed. 
### Impact Mapping Structure

Impact mapping works **goal-first**:
1. **Goal**: Strategic objective tied to a real problem (e.g., "Fair pricing as soon as possible")
2. **WHO (Actors)**: Which user roles must change behavior to achieve the goal?
3. **HOW (Impacts)**: What behavioral impacts on each actor are needed?
4. **WHAT (Deliverables)**: Only after understanding impacts, determine what software/features to build 

### Discovery Benefits

Impact mapping reveals that some solutions are **non-technical** or low-tech (e.g., email campaigns, spam) rather than complex software. Features are justified by their impact on actor behavior, which supports the strategic goal. User stories and specification by example come last, after the impact chain is validated. 
The approach ensures teams build features that actually matter rather than implementing pulled-from-the-air ideas with no validation. 
## Strategic vs. Tactical Design

### Strategic Foundations Required

**Business architecture** and **social architecture** must be addressed before technical architecture, otherwise development is "just guesswork." Understanding the business model, organizational structure, and team dynamics informs technical decisions. 
### Avoiding CRUD Mentality

CRUD (create, read, update, delete) thinking treats software as pure data collection, forcing users to become the intelligence layer. Evidence: sticky notes around users' desks with process instructions because "nobody thought of the business process, they just thought of collecting the data." 
Developers often prioritize technology (Kafka, Kubernetes) and resume building over business process understanding, leading to systems that require users to compensate for missing intelligence. 
### Domain-Driven Design Without Saying It

Best practice: **Don't advertise that you're using DDD**. Organizations have often failed with scaled agile, Scrum, and other methodologies, making them wary of "the next DD thing." The workshop itself demonstrates actual agile practices—adjusting immediately based on feedback and thinking through problems before knowing all answers. 
## Code Examples and Tomorrow's Topics

### Provided Code Structure

The Google Drive contains code examples in **Java and C#** showing:
- Bounded contexts matching the Done By Me system (matching, pricing, profiles, scheduling)
- Full layering implementation
- Event sourcing and CQRS patterns
- Process management examples
- Repository and adapter implementations 

The pricing context intentionally omits machine learning/AI implementations ("otherwise I would have to charge you millions"). 
### Upcoming Workshop Content

Tomorrow's sessions cover:
1. **Bounded context identification from event storming** (added based on participant question)
2. **Tactical modeling** (aggregates, entities, value objects, domain events)
3. **Data mesh** architecture
4. **Code walkthrough** of the provided examples
5. **Why events and event sourcing** (answering the "why store events forever" question) 

## Teaching Philosophy and Workshop Dynamics

### Public vs. Private Workshops

Public workshops excel because participants **choose to attend and want to learn**. Private corporate workshops often have 75% of students attending only because management required it—they don't pay attention, get lost during exercises, and resist engagement. 
### Feedback Challenges

Online teaching is exhausting, especially when only 3 of 25 participants have cameras on, providing zero feedback. Critical comments come from off-camera "snipers" with no opportunity for dialogue. One PayPal review rated strategic design teaching poorly despite rating all its components (bounded contexts, context mapping, ubiquitous language) highly—a logical impossibility since those topics **are** strategic design. 
### Event Storming with Business Experts

When working with non-technical business people:
- Don't enforce strict naming conventions (noun-verb-past tense)
- Don't correct colors obsessively
- Let them express events however makes sense to them
- Developers should "reach up to their level" rather than pulling business down to technical level
- **"The business is always right"**—they may not frame things perfectly for software, but that can be refined later 

One practitioner runs event storming sessions with business stakeholders **using only events and hotspots**—no commands, policies, or technical elements. The business just needs to express the flow; technical teams can add detail afterward. 
### Recommended Session Length

Event storming is mentally exhausting. Optimal approach: **3-4 hours maximum per session**, then break for sleep. Brains process and resolve questions during rest. Three half-day sessions (9-12 hours total) provide deep system understanding—far faster than typical approaches where teams spend 12 hours trying to understand a single source file. 
### Product Experience as Design Goal

The **MacBook Pro unboxing experience**—shrink-wrapped white box, hesitation to break the seal, "rays of light shining out"—represents the quality target. Despite potential criticisms of Apple, their product experience is exceptional. Done By Me should aim to give clients and doers that same level of thoughtful, delightful experience. 
This target requires thinking beyond data capture to understanding and designing for the complete user experience. 
