## Concept

**Context Mapping** defines the integration relationships between bounded contexts, specifying how teams collaborate and how models interact across boundaries . The patterns describe both technical integration and organizational dynamics .
**Key Context Mapping Patterns:**
- **Partnership**: Two teams with mutual dependency coordinate closely on integration, sharing responsibility for success through joint planning, testing, and synchronized releases 
- **Customer-Supplier**: Downstream team (customer) depends on upstream team (supplier), with negotiation required for features but no guaranteed acceptance 
- **Conformist**: Downstream team adopts upstream model without translation, accepting it as-is due to lack of resources, time, or influence to maintain separate model 
- **Anti-Corruption Layer (ACL)**: Translation layer protecting downstream bounded context from upstream model changes, preserving local ubiquitous language precision 
- **Open Host Service**: Upstream provides standardized API for multiple consumers, typically with published language defining exchange format 
- **Separate Ways**: Teams decide not to integrate, either duplicating functionality or forking shared code to maintain independent evolution 

**Strategic Design Architecture Layers:** 
1. **Business Architecture**: Where is the money? What does business need? Strategic positioning and innovation decisions
2. **Social Architecture**: Team structure, communication patterns, and human engagement (socio-technical systems)
3. **Technical Architecture**: Technology choices (often over-emphasized at expense of business/social concerns)

**Communication Complexity Formula:** 
- Total channels = n(n-1)/2, where n = number of people
- 5 people = 10 channels
- 7 people = 21 channels  
- 10 people = 45 channels
- 20 people = 190 channels

Each additional person exponentially increases cognitive load through different mental models and interpretations .
## What I understood

### Context Mapping Relationships Are Dynamic and Non-Exclusive

Context mapping patterns describe **team relationships and integration strategies**, not just technical connections . The patterns are **not mutually exclusive**—teams can combine approaches or transition between them as priorities shift .
**Customer-Supplier negotiation dynamics:** 
The upstream supplier may or may not agree to customer requests depending on:
- Customer's business significance (Barnes & Noble vs. small affiliate) 
- Supplier's priorities and capacity 
- Lead time vs. cycle time in delivery estimates 

Even in customer-supplier relationships, the **customer retains choices**: conform to supplier's model, anti-corrupt it, or go separate ways .
**Partnership evolution example:** 
The Done By Me pricing and matching contexts initially maintained a **partnership** for rapid feature development. As pricing evolved toward machine learning and AI-driven algorithms, the relationship shifted to **customer-supplier** because: 
- Pricing team hired data scientists with specialized expertise 
- New use cases (ML training, multiple algorithms) exceeded matching team's domain knowledge 
- Pricing needed faster iteration independent of matching's schedule 

The partnership served its purpose during initial exploitation of the constraint (stop money bleed), but became inefficient as pricing elevated the constraint with advanced capabilities .
### Conformist Pattern: When and Why

**Conformist relationships occur when downstream teams:** 
- Lack time, maturity, motivation, or budget to maintain translation layers
- Face upstream models that are "good enough" for their needs
- Accept cognitive overhead of understanding upstream terminology

**Done By Me scheduling context example:** 
The scheduling supporting subdomain integrates with Google Workspace Calendar as a **conformist**. The downstream scheduling context adopts Google's `Calendar` and `Event` model concepts directly , even though these conflict with domain event terminology used elsewhere .
**Strategic justification:** 
- Scheduling is a **supporting subdomain**, not core—differentiation comes from availability analytics and surge pricing triggers, not calendar implementation itself
- Competing with Google/Microsoft calendar quality is unjustifiable 
- The conformist approach enables rapid delivery while hiding implementation details behind a facade 

**Event enrichment strategy:** 
Even as conformist, scheduling publishes **enriched domain events** (`AvailabilityResolved`, `AvailabilityUnresolved`, `WorkScheduled`) containing metadata beyond simple confirmation: 
- Remaining doer capacity in locality
- Skill availability constraints  
- Triggers for **surge pricing** when scarcity detected 
- Signals for **recruiting** when persistent shortages identified 

This demonstrates **strategic thinking** even within supporting subdomains—the conformist technical decision doesn't prevent business value extraction .
### Anti-Corruption Layer: Protecting Ubiquitous Language

**ACL purpose:** 
Translation layer that prevents upstream model concepts from "leaking" into downstream bounded context, preserving local language precision and team autonomy.
**Done By Me pricing evolution:** 
Initially, pricing context used **conformist** approach with third-party pricing catalog, directly modeling `WorkService` from upstream to accelerate delivery (exploit constraint phase) .
As pricing introduced **taxonomy and machine learning** (elevate constraint phase), the team implemented ACL to translate upstream `WorkService` to downstream `PricedItem`: 
- **Technical debt recorded**: Team acknowledged initial conformist approach would require refactoring 
- **Language precision**: "Pricing prices priced items, not work services or proposals"—this clarity enables future generalization 
- **Strategic positioning**: `PricedItem` with taxonomy opens possibility for pricing beyond Done By Me's original service marketplace (Amazon's evolution from books to everything) 

**Integration decision trade-offs:** 
- **Conformist favors expediency**: Faster initial delivery, simpler integration
- **ACL favors precision**: Protects ubiquitous language, enables independent evolution, supports long-term strategic flexibility

**Gandalf metaphor:** 
ACL is like Gandalf declaring "You shall not pass" to the Balrog—it actively blocks foreign language concepts from entering the bounded context, even at cost of translation complexity.
### Separate Ways: Strategic Non-Integration

**Separate Ways decisions occur when:** 
- Integration complexity outweighs benefits
- Teams need independent evolution velocity  
- Cost of external service exceeds internal development

**Shared Kernel fork example:** 
If pricing releases `Money 2.0` with breaking changes while matching still uses `Money 1.3`, matching might **fork the shared kernel** rather than refactor: 
- Matching takes ownership of `Money 1.3` codebase
- Pricing proceeds with `Money 2.0` independently  
- Matching's ACL translates `Money 2.0` in pricing events to internal `Money 1.3`

**Trade-off analysis required:** 
- Cost of maintaining forked code vs. cost of refactoring to new version
- Translation complexity added to ACL
- Long-term divergence risk

**Tax calculation service example:** 
Done By Me paid **$1.2M annually** to third-party tax calculation service. Strategic analysis: 
- **Initial development cost**: $250K to build internal tax calculator supporting subdomain
- **Annual maintenance**: $100K (hire mid-level developer for tax rule updates)
- **Payback period**: Less than one year
- **Decision**: Go **separate ways** from external service, develop internal capability

Even though tax calculation is **supporting subdomain** (not differentiating), the cost savings justify the investment .
### Open Host Service and Published Language

**Open Host Service** provides standardized API for multiple downstream consumers, decoupling supplier from individual customer negotiations .
**Technical implementation options:** 
- REST over HTTP
- Messaging (broker/bus like Kafka)
- gRPC
- Legacy: flat files, database integration (not recommended but exists in billion-dollar enterprises) 

**Published Language standards hierarchy:** 
From most abstract/painful to most specific/manageable:
1. **International standards** (e.g., GS1): Extremely comprehensive but painful—**350 classes required** to represent single e-commerce order with name, address, quantity, item, price 
2. **Industry standards** (e.g., HL7, FHIR in healthcare; FIX in finance): Moderately painful, some ambiguity in field definitions 
3. **National/organizational standards**: More practical but still rigid
4. **Product/subdomain level**: Easiest to develop, most fluid, highest rate of breaking changes 

**API versioning strategies:** 
- **Semantic versioning**: Non-breaking changes increment minor/patch version; breaking changes increment major version
- **Content negotiation**: Support multiple API versions simultaneously (increases complexity) 
- **Deprecation timelines**: Communicate breaking changes with lead time for downstream teams to adapt 

**AWS API criticism:** 
Frequently cited complaint is constant breaking changes, forcing downstream teams into perpetual adaptation cycles.
### Big Ball of Mud Integration

**Anti-corruption is critical** when integrating with legacy systems lacking clear bounded contexts .
**Debezium + Kafka pattern:** 
- **Debezium** listens to database transaction logs
- Extracts committed records (guaranteed consistency)
- Publishes to **Kafka topics** as event stream
- Downstream contexts consume and translate to domain events via ACL

This enables **event-driven integration** with legacy systems that don't natively publish domain events .
**Bubble Context strategy:** 
Develop new bounded context **inside** legacy system using:
- Isolated module with unique namespace
- ACL to translate implicit legacy subdomains
- Gradual extraction as bubble proves value

### Theory of Constraints Applied to Pricing Context

**Why pricing moved upstream from matching:** 
The Done By Me pricing context evolution followed Theory of Constraints phases: 
1. **Identify constraint**: Pricing weakness causing money bleed (auction market dynamics) 
2. **Exploit constraint**: Set average price based on large metro areas (San Francisco, Chicago, Boston), prevent bidding, cap discount percentage 
3. **Subordinate to constraint**: Integrate third-party service for zip code-level pricing accuracy 
4. **Elevate constraint**: Introduce ML/AI on proprietary data, hire data scientists, implement dynamic pricing policies 

**Pricing policy complexity:** 
- Weekday vs. weekend vs. holiday pricing
- Special event pricing (concerts, conferences)
- **Surge pricing** based on doer scarcity by locality and time zone 
- Real-time reaction to market dynamics (potentially hourly adjustments) 

**Cognitive load justification:** 
Matching team lacks data science expertise required to understand ML algorithms and training processes. Forcing conformist relationship would impose **unsustainable cognitive load** on matching team trying to comprehend both their domain and pricing's specialized domain .
**Iteration velocity:** 
Pricing must iterate rapidly as business scales—more customers, more doers, more localities. Customer-supplier relationship with pricing upstream allows pricing team to move independently without blocking matching team on every change .
### Orchestration vs. Choreography for Messaging Complexity

**Choreography:** 
- Services react to events independently based on convention
- Like choreographed dance—each dancer moves in response to others' positions
- **Problem**: Difficult to debug when failures occur (murder mystery across services) 

**Orchestration:** 
- Central orchestrator manages workflow state
- Better observability and error handling
- **Recommendation**: Use orchestrators when process has **more than 3 potential failure points** 

**Orchestrator composition strategy:** 
To prevent orchestrator language from growing unbounded across multiple domains: 
- Create **sub-orchestrators** for process segments  
- Higher-level orchestrator listens to sub-orchestrator outcomes
- Each orchestrator maintains focused, domain-specific language 

**Temporal and alternatives:** 
- **Temporal**: Most mature orchestration platform, expensive 
- **Competitors**: More affordable options with similar scalability/reliability 
- **NServiceBus**: Strong saga pattern support, originally .NET-focused, expanding language support 

### Social Architecture and Team Size

**Team Topologies guidance:** 
- **10 people is make-or-break threshold** for cognitive load 
- Beyond 10, communication overhead exceeds team's ability to stay aligned
- **45 communication channels** at 10 people already strains capacity 

**Two-pizza team (Amazon):** 
Roughly 8 people if everyone eats 2 slices from large pizza—aligns with cognitive load research on maximum effective team size .
**Team vs. reporting organization:** 
"Team" can mean different things:
- **Working team**: 5-10 people collaborating daily on shared context
- **Reporting organization**: 20+ people subdivided into multiple bounded contexts

A 20-person "team" might actually be 3-4 working teams, each owning separate bounded contexts under shared management .
**Partnership works best with virtual teams:** 
Partnership pattern most effective when single physical team splits into virtual teams for multiple bounded contexts under unified product management—automatic alignment reduces partnership coordination overhead .
## What I didn't fully get

### When Multiple Bounded Contexts Exist Within Single Subdomain

Alin asked whether verification subdomain with three different verification workflows (async, payment-based, external provider) should be modeled as **one subdomain with multiple bounded contexts** or **multiple subdomains** .
**Vaughn's clarification:** 
The three verification flows are **deployment options, not separate bounded contexts**. They share the same ubiquitous language and business rules (all verify the same entity type), but scale differently or have different entry points .
**Microservice ≠ Bounded Context:** 
You might deploy three entities from one bounded context into separate microservices for scaling, but they remain part of the **same bounded context and subdomain**. The deployment is a **technical decision**, not a domain modeling decision .
**When multiple bounded contexts DO make sense:** 
Vaughn acknowledged cases exist but struggled to articulate them immediately—promised to revisit after workshop. This suggests the pattern is rare and context-dependent, likely emerging when:
- Sub-processes have fundamentally different domain experts
- Language diverges significantly despite shared business capability  
- Different lifecycle management or regulatory requirements

Bruno's observation adds nuance: a supporting subdomain **can evolve into core** if business recognizes strategic value (e.g., surge pricing analytics becoming revenue driver) . At that point, it might justify splitting into multiple bounded contexts as complexity warrants deeper specialization.
### Technical Debt vs. Ward Cunningham's Original Intent

Vaughn strongly criticized **semantic diffusion** of "technical debt" from its original meaning .
**Ward Cunningham's definition:** 
- Debt = **lack of business knowledge** reflected in domain model
- You deliver wrong model knowingly to get feedback faster  
- Interest accrues as you fight constant translation between business language and outdated model
- "Bankruptcy" = software becomes unmaintainable due to accumulated model misalignment

**Modern misuse:** 
People call architectural choices "technical debt":
- "We're not using Postgres transactions correctly—technical debt!"
- "We should use hexagonal architecture instead of layers—technical debt!"

These are **technical decisions or bugs**, not debt in Cunningham's sense .
**Why the term lost meaning:** 
- **Domain models are rare**—most systems use anemic data models with setters, not rich domain models 
- Without domain models, the original context (model-business misalignment) doesn't apply
- Term got co-opted for any technical work backlog item 

**What to call other technical issues:** 
Juan asked what to call non-domain-model technical issues if not "technical debt." Vaughn's response: call them work items, to-dos, backlog items—just be aware they're **not what Ward Cunningham meant** .
**Recorded debt in Done By Me pricing:** 
The pricing context **recorded technical debt** when initially conforming to third-party catalog's `WorkService` model, knowing they'd need to refactor to `PricedItem` later as taxonomy and ML capabilities emerged . This is **legitimate technical debt**—model misalignment with business vision, deliberately accepted for faster feedback.
### Context Maps Belong to Teams, Not Shared Universally

Theodoros asked whether context maps are **created collaboratively** with business stakeholders .
**Vaughn's clarification:** 
- **Context map belongs to the team** drawing it—it's their perspective on integration relationships 
- In **partnership**, both teams share the context map 
- In **customer-supplier**, the customer team owns their context map showing their dependencies 

**Team collaboration on context maps:** 
- Get team together to agree on relationships and patterns
- Use "wisdom of the crowd" to validate understanding
- Include documentation from upstream teams (especially 800-pound gorillas like AWS/Google)
- If possible, talk to humans on upstream team about integration expectations 

**Supplier teams also draw context maps:** 
Even upstream suppliers benefit from context mapping to:
- Understand general needs of downstream consumers
- Practice **contract-driven development** for published language 
- Accept input from downstream even if not guaranteed to implement 

**Bringing recommendations vs. collaborative design:** 
Theodoros asked if individual can draft context map then bring to group. Vaughn confirmed this works when **time is constrained**, but author must remain **open to feedback**—it's a recommendation, not decree .
### Assuming Wrong Relationship Type

**Danger of misidentifying relationship:** 
If you **assume partnership** but upstream treats you as low-priority customer:
- You expect automatic feature acceptance—they ghost your requests 
- You miss deadlines because you didn't account for negotiation/rejection 
- Your core business suffers from wrong expectations 

Civio's insight: Even if feature takes 40 days instead of expected timeline, **knowing the true relationship lets you manage** around the constraint . Ignorance prevents adaptation.
Swarup added: Making decisions based on false relationship assumptions causes **drift from reality** with cascading consequences across strategy .
### Surfacing Hidden Boundaries in Socio-Technical Systems

Swarup's yellow team noted strategic design helps **surface hidden boundaries** in socio-technical systems .
**Billing account example:** 
In B2B software, "billing account" meant different things to different teams for 15 years: 
- **Payments team**: Container for payment methods (one account = multiple payment methods)
- **IAM team**: Container for admin rights (one account = one set of permissions)

This **hidden boundary** worked until customer requested decoupling payments from billing—suddenly every billing account holder had admin rights because teams' implicit coupling was exposed .
**Strategic design reveals these boundaries** by forcing explicit ubiquitous language definition per bounded context, making implicit assumptions visible before they cause production incidents .
## Might show up on the exam

### Context Mapping Pattern Characteristics

- **Partnership**: Mutual dependency, coordinated releases, joint testing, shared success/failure responsibility 
- **Customer-Supplier**: Downstream depends on upstream, negotiation required, no guaranteed feature acceptance, supplier sets priorities 
- **Conformist**: Downstream adopts upstream model without translation, due to resource/time/influence constraints 
- **Anti-Corruption Layer**: Translation layer protecting downstream ubiquitous language from upstream changes 
- **Open Host Service**: Standardized API for multiple consumers, typically with published language 
- **Separate Ways**: No integration, independent evolution, possible code forking or duplication 

### Strategic Design Architecture Layers

1. **Business Architecture**: Where is money? What does business need? Innovation strategy 
2. **Social Architecture**: Team structure, communication patterns, human engagement   
3. **Technical Architecture**: Technology choices (often over-emphasized) 

Priority order matters—business and social architecture should drive technical decisions, not vice versa .
### Communication Channel Formula

- **Formula**: n(n-1)/2 where n = number of people 
- **7 people** = 21 channels 
- **10 people** = 45 channels (Team Topologies threshold) 
- **20 people** = 190 channels 

Each person adds exponential cognitive load due to different mental models and interpretations .
### Integration Decision Trade-offs

- **Conformist**: Favors expediency, faster delivery, simpler integration, accepts cognitive overhead 
- **Anti-Corruption Layer**: Favors precision, protects ubiquitous language, enables independent evolution, adds translation complexity 

Record technical debt when choosing conformist with intent to refactor later as domain knowledge improves .
### Published Language Standards Hierarchy

From most abstract/painful to most practical: 
1. **International** (GS1): 350 classes for simple order 
2. **Industry** (HL7, FIX): Moderate pain, field ambiguity 
3. **National/Organizational**: More practical, still rigid
4. **Product/Subdomain**: Easiest to develop, most fluid, highest breaking change rate 

Higher abstraction = more pain due to attempting to solve every problem (canonical model trap) .
### Orchestration vs. Choreography

- **Choreography**: Services react independently to events, difficult to debug failures ("murder mystery") 
- **Orchestration**: Central workflow management, better observability, use when **>3 failure points** 
- **Composition strategy**: Sub-orchestrators for segments, higher-level orchestrator for coordination 

### Ward Cunningham's Technical Debt

**Original meaning:** 
- Debt = delivering wrong domain model to get feedback faster
- Interest = fighting translation between business language and outdated model  
- Bankruptcy = software unmaintainable due to model-business misalignment

**Not** architectural choices, database configurations, or general backlog items .
Reference: [C2.com](https://c2.com) wiki and YouTube video where Ward explains original intent .
### Theory of Constraints in Pricing Context

**Four phases applied:** 
1. **Identify**: Pricing weakness causing money bleed 
2. **Exploit**: Set average metro price, prevent bidding, cap discounts 
3. **Subordinate**: Integrate third-party zip code pricing 
4. **Elevate**: ML/AI on proprietary data, dynamic policies, hire data scientists 

Each phase justified different integration pattern (partnership → customer-supplier) as complexity and iteration velocity increased
