## Key Outcomes

The workshop session covered **strategic design patterns in Domain-Driven Design**, focusing on context mapping relationships (Customer-Supplier, Conformist, Separate Ways) and their practical application to the Done By Me case study . The team explored how **pricing context evolved from partnership to upstream supplier** as business priorities shifted toward machine learning and dynamic pricing strategies . Bruno Tacca and other participants engaged in collaborative exercises using Miro boards to map bounded contexts and integration patterns, with discussions extending to AI-assisted development using Claude and the challenges of maintaining DDD principles in code generation .
## Context Mapping Relationships Explained

### Customer-Supplier Pattern

**Pricing context serves as upstream supplier to matching context** in Done By Me's evolved architecture . The downstream matching team must negotiate features with pricing, who may deliver 65% of requested functionality by the original date plus 30 days, or 100% by date plus 40 days . This relationship reflects **real-world power dynamics** where the upstream team (like Amazon or AWS) may not prioritize individual downstream requests unless the customer has significant influence (like Barnes & Noble versus a small affiliate) .
**Lead time versus cycle time** becomes critical in these negotiations—teams must clarify whether "date plus 30" includes built-in delays before work begins or represents the entire delivery cycle . The most dangerous assumption is believing you're in a partnership when actually operating as a customer, which can result in **missed deadlines and incorrect expectations** that impact core business operations .
### Partnership Dynamics

Partnership relationships work best when **one physical team operates as multiple virtual teams** under unified project management, making coordination automatic rather than requiring constant negotiation . In Done By Me's initial phase, matching and pricing maintained partnership while establishing core services identification and policy-based pricing in large metropolitan areas .
However, **partnerships naturally evolve as priorities diverge**—when pricing context began introducing machine learning, data training, and algorithmic approaches requiring data scientists, the partnership became unsustainable . The cognitive load of maintaining tight coordination across fundamentally different technical domains outweighed the benefits .
### Conformist Approach

The **scheduling context demonstrates conformist relationship** with Google Workspace Calendar, adopting upstream model concepts like "event" and "calendar" directly rather than translating them . This decision favored **expediency over precision** in ubiquitous language—the team cannot justify competing with Google's calendar capabilities and simply conforms to their model .
Bruno Tacca questioned whether domain experts in pricing must use upstream terminology like "work" and "service" . The answer clarifies that **conformist relationships apply at integration boundaries**—internally, pricing maintains its own language (like "priced item") while the integration layer handles upstream model concepts .
### Separate Ways Strategy

Teams choose **separate ways when integration costs outweigh benefits** . Done By Me faced this decision when considering whether to fork the shared monetary kernel at version 1.3 while pricing moved to breaking changes in version 2.0 . Matching could maintain the forked 1.3 version and own that code, accepting the translation overhead when consuming pricing events containing Money 2.0 .
Another practical example: **replacing a $1.2 million annual tax calculation service** with an internal supporting subdomain estimated at $250,000 initial development plus $100,000 yearly maintenance . Despite tax calculation not being a core differentiator, the financial savings justify the separate ways decision .
## Anti-Corruption Layer Strategy

### Purpose and Implementation

**Anti-Corruption Layer (ACL) protects downstream bounded contexts** from upstream model influences, translating external concepts into the local ubiquitous language . In the pricing context, the ACL translates "Proposal Submitted" events into "Verify Pricing" commands using pricing's own terminology . Vaughn Vernon characterized ACL as **"Gandalf saying 'you shall not pass'"**—preventing foreign language concepts from contaminating the bounded context .
The complexity of ACL varies significantly—**integrating with Big Ball of Mud systems** requires substantial translation effort, potentially using tools like Debezium to listen to database transaction logs and derive domain events from committed records . This approach feeds events into Kafka topics that downstream contexts can consume through their ACLs .
### When to Anti-Corrupt

Even in **partnership relationships, teams should anti-corrupt** unless translation is trivially simple and the contexts share genuinely common concepts . The integration decision framework weighs **expediency versus precision**—early in Done By Me's development, pricing conformed to third-party service pricing catalogs for speed, recording this as technical debt . Later, when introducing taxonomy and machine learning, pricing implemented ACL to translate "work service" into "priced item," favoring precision in ubiquitous language over integration convenience .
Bruno Tacca's team discussed whether **multiple bounded contexts can exist within one subdomain** . The answer clarified that different verification workflows (asynchronous, payment-based, external service) sharing the same entity don't necessarily justify separate bounded contexts—this likely represents **deployment options within a single bounded context** rather than distinct linguistic boundaries .
## Theory of Constraints Applied to Pricing Evolution

### Four-Stage Evolution

Done By Me's pricing context evolved through **Theory of Constraints stages** that justified the upstream positioning :
**Stage 1 - Exploit the constraint:** Set average price based on highest large metropolitan area (San Francisco, Los Angeles, Chicago, Boston) and prevent bidding wars that created auction market dynamics . This **immediate action stopped money bleeding** even though it priced out smaller markets .
**Stage 2 - Subordinate everything:** Integrate external service providing average pricing by zip code or postal code for more accurate regional pricing .
**Stage 3 - Elevate the constraint:** Introduce machine learning and AI on proprietary data, adding data scientists to the team .
**Stage 4 - Precision pricing:** Implement **pricing policies covering midweek, weekend, holiday, special occasion, and surge pricing** . Surge pricing responds to scarcity signals from scheduling context (availability resolved/unresolved events) on potentially hourly basis in different localities .
### Why Matching Cannot Conform

**Cognitive load prevents matching from conforming to pricing's complex model** . Pricing involves data scientists working with machine learning algorithms, multiple policy engines, and dynamic market response mechanisms . If matching conformed, the team would need to understand both matching domain logic and pricing's sophisticated technical implementation .
Diego summarized the key insight: **context boundaries must account for separate evolution trajectories**, not just current state . Pricing iterates much faster now, potentially adjusting calculations hourly as business dynamics shift, while matching maintains more stable workflows .
## Scheduling as Strategic Supporting Subdomain

### Business Value Beyond Core

The **scheduling context qualifies as supporting rather than core** because calendar functionality doesn't differentiate Done By Me's business—everyone could implement scheduling with effort . However, it provides **strategic value through specialized integration** with matching workflows and event publication .
When proposals match between client and doer, scheduling marks the doer's calendar and publishes **availability resolved and availability unresolved events** with rich metadata . These events enable:
- **Surge pricing** when doer availability drops to critical thresholds (Nicholas identified this revenue opportunity) 
- **Recruiting signals** when specific localities lack sufficient skilled doers 
- **Reliability scoring** where doers with higher resolved availability rates get recommended more often (Theodoros suggested this) 

Bruno Tacca recognized that **supporting subdomains can evolve toward core** if business strategy shifts—if Done By Me's leadership sees dynamic pricing and surge capabilities as key differentiators, scheduling's role might be reconsidered . However, Vaughn clarified that **scheduling itself won't handle pricing logic**—it only reports market conditions that pricing context leverages .
## Open Host Service and Published Language

### API Abstraction Patterns

**Open Host Service provides standardized API** for upstream contexts, abstracting internal implementation details . The API can use REST over HTTP, messaging via broker/bus, gRPC, or even legacy approaches like flat file writing or database integration . Done By Me's pricing context exposes Open Host Service receiving "Verify Pricing" commands asynchronously via message bus, responding with "Pricing Verified" or "Pricing Rejected" events .
Gus raised concerns about **handling failures in asynchronous messaging**—when requests can't be fulfilled due to validation errors or race conditions, synchronous APIs return immediate error responses . The domain event pattern addresses this: **pricing publishes rejection events with enriched context** explaining why verification failed (such as surge pricing now active) . This approach maintains asynchronous benefits while providing clear failure semantics .
### Published Language Standards Hierarchy

**Published language complexity correlates inversely with usability** . International standards like GS1 (e-commerce data exchange) require instantiating **350 classes to represent a single order** containing one person's name, address, and purchase of one $5 item . The abstraction attempts to solve every problem, creating massive overhead .
The hierarchy from most to least painful :
- **International standards** (GS1, ISO) - extremely abstract, comprehensive but cumbersome
- **Industry standards** (FIX for financial exchange, HL7 for healthcare) - domain-specific but still complex; HL7 standardizes record types but not field contents, requiring publisher-specific interpretation 
- **National standards** - country-specific regulations and formats
- **Enterprise/product standards** - organization-level conventions
- **Context/subdomain standards** - easiest to develop, most fluid, potentially higher breaking change rate 

Done By Me could establish **matching-level or product-level published language** as the most practical approach, though this requires more active version management .
## Orchestration Versus Choreography

### Managing Complex Workflows

**Orchestrators reduce fear of messaging** by centralizing workflow logic rather than depending on independent services following choreographed conventions . In choreography, when one dancer (service) makes a move, other dancers respond based on shared understanding—like proposal submitted event triggering pricing verification . When failures occur, **debugging choreography resembles solving a murder mystery** across distributed services .
Swarup identified the challenge: **orchestrator language grows as more services participate** . The solution involves **composing orchestrators hierarchically**—create sub-process orchestrators for specific workflow segments, then higher-level orchestrators listen to those outcomes . This prevents monolithic orchestrator bloat while maintaining centralized visibility .
### Temporal and Alternatives

**Temporal represents the mature but expensive option** for orchestration platforms . Googling "Temporal competitors" reveals affordable alternatives offering similar scalability and reliability for smaller organizations . For **Saga pattern management specifically**, NServiceBus provides pre-built support, originally .NET-focused but potentially expanded to other languages and messaging mechanisms like RabbitMQ and Azure .
The key distinction: Temporal provides workflow infrastructure requiring custom Saga implementation, while NServiceBus embeds Saga pattern awareness directly .
## Social and Technical Architecture Balance

### Communication Channel Mathematics

The **formula for total communication channels** equals n(n-1)/2 where n represents team size . This creates exponential growth:
- 5 people = 10 channels
- 7 people = 21 channels  
- 10 people = 45 channels
- 20 people = 190 channels 

The challenge isn't simultaneous conversation but **cognitive load of different mental models** . When one person says "product," others think of sellable items, revenue drivers, or value delivery—but in mathematics context, "product" means multiplication result . Each team member interprets terminology through their unique background, learning experiences, and environmental context .
### Optimal Team Sizing

**Team Topologies suggests 10 people as the breaking point**—beyond this, cognitive load from communication channels exceeds team capacity for shared understanding . Amazon's **two-pizza team concept** (roughly 8 people given typical US large pizza with 8 slices) aligns with this threshold .
However, "team" definition matters—**some organizations use "team" for reporting structures** of 20+ people who don't actually function together . These larger groups typically divide into smaller working units, each focused on different bounded contexts .
### Three Critical Architectures

Vaughn emphasized that **business architecture and social architecture deserve priority over technical architecture** . Technical architecture (Kafka, Kubernetes, other "K words") dominates attention but shouldn't drive decisions . Business architecture addresses where money flows, funding sources, strategic positioning, and innovation focus . Social architecture recognizes that **people make outcomes happen**—without engagement and business connection opportunities, both attitude and results suffer .
## AI-Assisted Development with Claude

### Current Capabilities and Evolution

Vaughn works with **Claude Code daily, often until late night despite early morning workshops**, describing the productivity as "just churning" . He joked that his GDPR compliance work was "already acquired by Google" to illustrate the development pace . Everything he asserted about AI coding agents **one year ago is now wrong**, and even statements from November's ISAQB Architecture Gathering in Berlin no longer hold .
**Claude has "totally dominated" the market** according to Vaughn's assessment, with colleagues abandoning Codex entirely . Quality increases occur at exponential rates—developers who dismissed Claude six months ago are "totally out of touch now" . The tool has progressed from generating mostly tables when asked for graphics to producing sophisticated code implementations .
### DDD Skills and Limitations

The official **Claude Skill for Domain-Driven Design available from Anthropic is "not very good"** . Created by someone who released 70+ programming skills (TDD, BDD, etc.), it appears generated rather than crafted, resulting in overly generalized and small skill definitions . Despite Anthropic's **$1.5 billion settlement with book publishers** for borrowing content (including three of Vaughn's DDD books and Eric Evans' Blue Book), Claude still can't implement DDD correctly . The vast internet of poor DDD examples outweighs the influence of authoritative sources .
Vaughn develops **custom Claude skills honed to specific technology stacks**, including DDD naming skills because Claude drifts toward technical naming even when attempting to follow DDD principles . His approach:
1. Provide high-level feature outline to Claude Code 
2. Have Claude generate implementation plan 
3. Iterate on the plan before coding 
4. Work on one feature at a time within single bounded context, or at most two contexts 
5. Read generated code but far less than three months prior 

Jan-Alexander asked about **spec-driven development concerns** . Vaughn clarified his approach creates small specs iteratively rather than waterfall-style comprehensive specifications upfront—this remains incremental "vibe coding" with somewhat high-level instructions .
### Training Data Quality Problem

Diego asked whether specifications use **ubiquitous language in plain English** . Gus emphasized that **doing workshops like this becomes more important** as AI proliferates—being able to converse with book authors provides extremely valuable knowledge that counteracts the flood of poor examples . The amount of people who've spent meaningful time with DDD remains small, yet everyone now has access to hastily generated DDD skills .
Vaughn expressed gratitude for **public workshop participants who care and engage**, contrasting this with corporate private workshops where attendees are forced to attend, stay off-camera, don't pay attention, and then report the workshop as horrible despite being unable to answer a single DDD question . He questioned whether he's failing but affirmed he's "giving this all I've got" and teaching for minimal cost because he enjoys teaching and wants people to learn .
## Collaborative Exercise Insights

### Miro Board Activities

Teams worked in **five breakout rooms (Blue, Green, Yellow, Orange, Purple)** using shared Miro workspace to answer "What is strategic design?" . Vaughn encouraged returning to the same room across exercises and viewed landing in different rooms from colleagues as positive—working with different people creates more realistic settings .
**Purple team used AI to synthesize their post-it notes**—Theodore took a screenshot, fed it to Claude, then asked for progressive refinement into four key areas . This demonstrated practical AI application for meeting facilitation and synthesis .
**Yellow team couldn't stop working after time expired**, continuing to add insights . Vaughn related to this, requesting 15-minute advance notice before meals because once thoughts start flowing, it's hard to walk away without dumping them out or risk losing the train of thought .
### Purple Team Synthesis

Christine (a professor at a public college equivalent to US community college) presented Purple team's AI-refined summary organized into four areas :
**Alignment:** Subdomains, bounded contexts, context mappings, and ubiquitous language form the foundation .
**Focus on value:** Identifying core domain and following the money allows resource focus on true business differentiators, uncovering constraints and unknowns to act upon .
**Bridging gaps:** Bringing business and engineering teams into the same room fosters alignment and understanding between different types of people with different needs .
**Organizational success:** Strategic design aids in structuring teams and overall organization to better support business goals, with Conway's Law coming into play .
Vaughn particularly appreciated **"follow the money" language**—while not a DDD-specific term, it captures the strategic essence . He noted Christine's university being public/non-profit represents a positive model, contrasting with much of US higher education operating for profit .
### Yellow Team Analysis

Swarup presented Yellow team's work, highlighting several key points :
- **Identifying boundaries in socio-technical systems** through bounded contexts 
- **Establishing ubiquitous language** where different people often speak the same words but mean different things 
- **Context mapping patterns reflecting team interaction patterns** 
- **Identifying core, supporting, and generic capabilities**, with Swarup noting Purple's "follow the money" language perfectly captures this concept 
- **Providing big picture and long-term perspective** because strategic design at high altitude changes less quickly, allowing room for iteration 

Jan-Alexander added that **context-cutting is iterative**—starting with small contexts, iterating, and deciding whether to combine them or keep them separate .
### Hidden Boundaries in Monoliths

Swarup shared a **concrete example of hidden boundaries** from B2B software experience . The billing account concept meant different things to different teams for 15 years until a customer request exposed the problem :
- **Payments team:** Billing account is a container for payment methods (one account, multiple payment methods) 
- **Identity and Access Management team:** Billing account is a container for admin rights 

When a customer wanted to decouple payments from billing, the web team created multiple billing accounts (one per payment method), inadvertently **giving every person with a billing account admin rights on the web portal** . This coupling existed because different teams interpreted the same concept differently—a boundary always existed but remained hidden .
## Technical Debt: Original Meaning Versus Semantic Diffusion

### Ward Cunningham's Intent

Vaughn expressed frustration that **technical debt has lost its original meaning** through semantic diffusion . Ward Cunningham's intent focused on **business knowledge gaps, not architectural choices** . The original concept: teams don't understand the business problem, so their domain model is wrong, but they deliver anyway to get feedback, recording debt because fighting the old model will constantly require translation .
Cunningham explained this to **financial people questioning why developers keep making changes** . Using the debt metaphor they understood: taking on debt requires repayment, ignoring principal means paying more interest, and continuing that path leads to bankruptcy—the same fate awaits software if technical debt isn't addressed . The metaphor was **for financial stakeholders, not a technical architecture checklist** .
### What It's Not

Modern usage treats technical debt as **any suboptimal technical decision**—like not using PostgreSQL transactions correctly or choosing layers over hexagonal architecture . While these can be called technical debt, that's not Cunningham's original intent . Since **domain models are rare** (most systems use anemic data models with setters, not true domain models), the original meaning has been completely lost .
Vaughn's recommendation: **watch Cunningham's YouTube video** where he reads his [C2.com](https://c2.com) wiki entry on technical debt, possibly with additional ad-lib insights . Understanding the original intent helps teams recognize when they're actually accumulating business knowledge debt versus simply creating technical work items .
Juan asked what to call non-business-knowledge issues . Vaughn suggested creating work items or backlog items without necessarily labeling them technical debt, though acknowledged people can use the term—just be aware it's not what Cunningham meant .
## Context Mapping Exercise Results

### Upstream-Downstream Visualization

Teams translated the **partnership context map into customer-supplier relationship** showing pricing upstream from matching . The exercise required including context map pattern names, upstream/downstream relationships, and event flow direction .
**Red team discussion focused on why matching wouldn't conform to pricing** . Nicholas suggested it relates to the average price example where matching calls it "original price" or "starting input," requiring upstream context to understand the value's true meaning .
### Theory of Constraints Justification

Vaughn used the **four Theory of Constraints stages** to explain why matching cannot conform . The "stop the money bleed" priority drove initial decisions—setting average price based on highest large metropolitan area (San Francisco, Los Angeles, Boston, Chicago) and preventing bidding wars . This **deliberately priced out smaller markets** but fixed the immediate problem .
The progression through subordinate (external zip code pricing service) and elevate (machine learning with data scientists) stages created **fundamental differences in team composition and iteration speed** . Pricing now includes data scientists working with algorithms, potentially adjusting calculations hourly as business dynamics shift . If matching conformed, they'd need to understand both domains—an unsustainable cognitive load .
Diego's summary captured the key learning: **cannot think only about current state when defining bounded contexts**—must consider separate evolution trajectories . The pricing context will continue rapid iteration with machine learning improvements while matching maintains more stable workflows .
### Pricing Policy Complexity

**Pricing policies add further complexity** beyond algorithmic pricing :
- Midweek versus weekend pricing differentials
- Holiday pricing adjustments  
- Special occasion pricing (Vaughn joked about Elton John's "fourth final tour ever")
- Surge pricing responding to scarcity 

Surge operates **differently across time zones and localities**—New York City might surge while
