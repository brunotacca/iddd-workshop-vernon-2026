## Concept

**Domain-Driven Design (DDD)** is a software development approach that emphasizes collaboration between technical and domain experts to create software that reflects the business's mental model and language . At its core, DDD uses **bounded contexts**—explicit boundaries around specific areas of expertise where a consistent **ubiquitous language** is developed and maintained . Each bounded context represents a sphere of knowledge and activity with its own model, concepts, and expressions .
**Strategic design** in DDD focuses on identifying and organizing these bounded contexts, understanding their relationships through **context mapping**, and prioritizing work using business-driven approaches like **Theory of Constraints** . The domain itself is an overloaded term that can refer to the entire business, a problem space, or specific areas of expertise depending on context .
**Theory of Constraints** provides a systematic approach to identifying and addressing the most limiting factor (bottleneck) preventing a system from achieving its goals . The five-step process involves: (1) identifying the constraint, (2) exploiting it with current resources, (3) subordinating everything to support it, (4) elevating it through investment if needed, and (5) preventing inertia by repeating the cycle .
**Context mapping** defines both team relationships and integration patterns between bounded contexts . Key patterns include **partnership** (teams succeed or fail together with coordinated releases) , **shared kernel** (small shared model concepts that become part of multiple ubiquitous languages) , **customer-supplier** (upstream service provides to downstream dependent) , and **anti-corruption layer** (translation layer protecting downstream from upstream changes) .
**Command Query Separation (CQS)** is a design principle where methods either mutate state (commands returning void) or answer state (queries with no side effects), but never both . **Account interface demonstrates CQS: commands** **`credit(Money amount)`** **and** **`debit(Money amount)`** **return void, while query** **`balance()`** **returns Money without side effects** . **CQRS** (Command Query Responsibility Segregation) extends this by segregating commands and queries into separate responsibilities, enabling different scaling and optimization strategies .
## What I understood

### Conway's Law and Organizational Architecture

**Conway's Law** states that "organizations that design systems are constrained to reflect the communication structures of that organization" . Written by Mel Conway in 1967, this principle reveals that a **big ball of mud** results from haphazard, fragmented communication where developers lack real expertise and simply parrot demands from management . The system architecture mirrors the broken organizational structure .
Conway's paper also introduced what Vaughn calls **Conway's First Axiom**: organizations must be flexible and open to reorganizing teams for required communication, with rewards for keeping organizations lean and flexible . This predates the "reverse Conway maneuver" by decades—the idea that you should structure teams according to the architecture you want . However, the reverse Conway maneuver focuses too narrowly on technical architecture while neglecting business and social architecture, which are equally important .
The **business architecture** and **social architecture** of an organization are as critical as the technical architecture . Cross-functional teams within bounded contexts ensure all functions—including UI, backend, and domain expertise—have meaningful roles . This contrasts with separating front-end and back-end teams, which often creates confusion about upstream/downstream relationships and dependencies .
### Bounded Contexts and Subdomains

A **bounded context** is an explicit boundary containing :
- A specific mental model of the business
- A sphere of knowledge and activity (domain expertise)
- An area of communication and learning with domain experts
- An area of innovation
- A single applicable domain model
- A consistent ubiquitous language

The same concept (like "product") can exist in multiple bounded contexts with completely different meanings and implementations . For example, a product in a **market sales context** differs from a product in a **Product Information Management (PIM) context**, which differs from products in **payments** and **fulfillment** contexts . This is the fundamental reason for DDD—instead of fighting differences or co-locating them in one class, you recognize and embrace that concepts vary across business areas .
**Business capabilities** are what the business must do to succeed . When drilled down to the right granularity (achievable in software), they identify **subdomains** . Generally, one bounded context supports one subdomain in a one-to-one relationship, though a subdomain is not the same thing as a bounded context—it's simply a good goal to maintain this mapping .
Subdomains fall into three categories based on their strategic value :
1. **Core domain** (differentiating value, "secret sauce," innovation, CapEx investment) —this is where **competition is for losers** applies, meaning you create monopolistic differentiation rather than competing on the same features 
2. **Supporting subdomain** (non-differentiating but necessary, doesn't exist as purchasable product, OpEx) 
3. **Generic subdomain** (non-differentiating, already exists or can be purchased, OpEx) 

### Done By Me Case Study: Identifying Constraints

The **Done By Me** case study illustrates strategic analysis using Theory of Constraints . Done By Me is a startup matching clients (people needing home services) with doers (skilled service providers) . After one year without DDD, the system became a big ball of mud with five implicit subdomains: matching, payments, financial accounts, users, and services catalog .
The critical business question is **"where is the money?"** . Through surveys and interviews with clients and doers, Done By Me identified severe constraints :
**Matching problems** :
- Services identification and pricing are inconsistent
- Pricing is the same across broad geographic areas (entire continents/nations) despite varying cost of living 
- High-touch process requiring excessive client decisions 
- Auction market where non-preferred (unqualified) doers underbid preferred doers 

**Services catalog problems** :
- Pricing based on suggestions, not policies—it's optional and negotiable 
- Services are hard to find and describe 

**User accounts problems** :
- Platform knows little about users 
- No tracking of doer availability for specific dates/times 

**Payments problems** :
- Embedded in monolith, creating security vulnerabilities 
- Difficult to offer payment options or use different gateways 

**Personal information problems** :
- Poor protection of private information 
- Not GDPR compliant 

The **Julia scenario** illustrates the pricing constraint . Julia (a premium client, CIO of a Fortune company in San Jose, California) submits a proposal for window washing: 38 windows (10 extra-large), indoor/outdoor, Saturday 7 AM, offering $225 or even $150 . The catalog price is $225 across the entire United States . This pricing is completely inadequate for Silicon Valley and the scope of work . When interviewed, Julia expressed uncertainty about fair pricing, and preferred doers felt insulted by underbidding .
### Applying Theory of Constraints to Pricing

**Pricing is the most limiting constraint** because it directly impacts revenue—as prices drop through bidding, commission percentages drop proportionally . The analogy to **Southwest Airlines' motto** clarifies this: "We love our customers, but we really love our employees" . For Done By Me, the preferred doers are the key employees—losing them through unfair pricing destroys the business .
The five-step constraint resolution for pricing :
1. **Identify**: Pricing is weak and constrains ability to make money 
2. **Exploit**: Set average pricing based on large metropolitan areas (San Jose, San Francisco, Seattle, NYC, Boston) rather than middle America; prevent bidding past a maximum discount —this may price out doers in low-cost areas like "Burnt Mattress, Idaho" temporarily, but focuses on high-value markets first 
3. **Subordinate**: Use external SaaS for services pricing if available 
4. **Elevate**: Implement machine learning (called "AI" for funding purposes) using data from matching and payments to continuously improve pricing 
5. **Prevent inertia**: Continue iterating on pricing policies 

The **solution approach** includes :
- **Identify core services**: Focus on most frequently used services first 
- **Policy-based pricing**: Move from suggestions to enforceable policies 
- **Exact service identification**: Use **taxonomy with scope parameters like** **`home-maintenance/windows/washing/scope/inside-outside?window-count=38&size-extra-tall=10`** 
- **Client options**: Structured choices (indoor/outdoor checkboxes, window count, extra-large count) instead of free-form descriptions 

Additional subdomain solutions identified :
- **Identity and Access**: Purchase solution (AWS Cognito, Google Identity, Supabase) to replace embedded users 
- **Profiles**: New supporting subdomain for client/doer information, reviews, and preferred status 
- **Scheduling**: Use third-party calendar (Google Calendar, Microsoft 365) 
- **Payments**: Extract to separate microservice with separate database for security; enable multiple gateways (Stripe, Google Pay, Apple Pay) 

### Modularization and Bubble Context

Within a big ball of mud, you can create **modules** for implicit subdomains (pricing, foreign exchange, reconciliation) . A **bubble context** is a modularized core domain created separately within the legacy system . For Done By Me, `done-by-me.pricing` would be the initial module housing the new core domain .
This approach addresses the **innovator's dilemma** . Instead of spreading new concepts throughout the existing system (which would fragment code and contribute to the big ball of mud), you create a separate bounded context within the legacy arena but play a different game—"like playing chess in the Middle Ages battle zone" . The bubble context depends on the big ball of mud initially, but over time you identify and extract supporting and generic subdomains .
A critical mistake is attempting to extract dozens of microservices from a big ball of mud by identifying boundaries without prioritization . This fails because you won't get boundaries right initially, and you'll create hard boundaries prematurely . Instead, let the new core domain reveal what needs extraction through actual dependencies .
**Stepwise refinement** enables working within the big ball of mud by carving out modular areas of expertise as unique modules, treating them as core domains within the legacy . This is more practical than attempting wholesale transformation .
### Context Mapping Relationships

**Partnership** is a heavy relationship requiring significant coordination . Partners succeed or fail together, with synchronized releases and integration tests (PACT contract testing recommended) . The relationship implies "you don't have to say please"—teams surface needs and support each other without begging . However, tight coupling can be weakened by using **anti-corruption layers** on both sides, translating between contexts even in partnership .
In the Done By Me example, matching emits `ProposalSubmitted` events, which pricing's ACL translates to `VerifyPricing` commands . Pricing responds with `PricingVerified` (including how far below fair pricing was allowed) or `PricingRejected` (with suggested price) . This choreography enables learning from trends—not just answering "what is the price?" but understanding pricing gaps, regional variations, and competitive pressures . **Executing commands and emitting events enables learning; query results only reflect what you already know** .
**Shared kernel** focuses on the word "kernel"—it must be small, like a single kernel of corn on an ear, compared to the entire cornfield . Two teams agree to share concepts that become part of both ubiquitous languages . More general concepts work better than specific ones . Challenges include: (1) knowing the other team exists, (2) agreeing on what to share, and (3) getting the owning team to maintain it in shareable form .
In Done By Me, the pricing team owns the **monetary kernel** containing `Money`, `Currency`, `Tax`, and a currency exchange domain service . Matching requested this because they deal with money but don't want to develop their own money type . Another shared kernel contains `Client` and `Doer` as value objects (name and identity only), while the profiles context has full entities/aggregates for these .
A common misconception is that shared kernel enables tight coupling of all bounded contexts—this is wrong . The focus is on small, agreed-upon concepts, not wholesale sharing .
**Customer-supplier** is the most common relationship . The upstream supplier provides services to the downstream customer, who depends on the upstream . In Done By Me, when the partnership between matching and pricing evolved, pricing moved upstream . Pricing now uses **open host service** (exposing its own API), requiring matching's ACL to translate `ProposalSubmitted` to `VerifyPricing` commands in the upstream API's terms .
The upstream/downstream metaphor uses water flow: pollution poured upstream flows downstream, not upstream . However, in practice, influence can flow opposite to dependency—a back-end team might call the front-end "downstream" when the front-end actually dictates requirements (making the back-end downstream in reality) .
**Micro front-ends** (composite UI) solve the problem of showing information from multiple contexts in one UI . Each bounded context develops UI components that plug in when given relevant IDs (e.g., doer profile component receives doer ID and fetches data via REST from browser) . Components can coordinate through API gateways or backend-for-frontend (BFF) patterns .
### Command Query Separation and CQRS

**CQS** (Command Query Separation) is a design principle from Dr. Bertrand Meyer's Eiffel programming language and "Design by Contract" work . It mandates that interface methods fall into two strict categories :
- **Commands**: Only mutate state, return void, provide no answers 
- **Queries**: Only answer state, do not modify state before answering 

Martin Fowler's formulation: "Answering a question cannot change the answer" . **The Account interface example demonstrates command/query separation:** **`void credit(Money amount)`****,** **`void debit(Money amount)`****,** **`Money balance()`** . To know the balance before or after a credit/debit, you must explicitly query for it—the command itself reveals nothing .
**CQRS** takes CQS one step further by segregating commands and queries as separate responsibilities . Instead of one `Account` interface, you'd have `AccountCommands` (with only credit/debit methods) and `AccountQueries` (with only balance method) . This is the essence of CQRS—not necessarily eventual consistency or separate databases .
Queries can come directly from the same database instance and tables that commands wrote to, maintaining transactional atomic consistency . The separation enables different scaling strategies: if 80% of transactions are read-only, you can scale query nodes differently than command nodes (e.g., 7 query nodes to 3 command nodes) . Reads are faster than writes, and avoiding write-transaction contention on reads further improves performance .
**Events are part of bounded context contracts**, not the command interface itself . The **Law of Demeter** (rule of least knowledge) says the outside world only knows what the interface reveals—in this case, the command methods . The bounded context can have an additional contract via **published language** context mapping, which declares what events will be emitted when internal state changes .
### DRY Principle and Knowledge Silos

**DRY (Don't Repeat Yourself)** is about knowledge, not code . If code captures a specific sphere of knowledge and activity, that knowledge should exist in a single bounded context . Duplicate code is acceptable if the two pieces represent different concepts . Conversely, two different implementations (different algorithms or logic) can violate DRY if they represent the same knowledge in different places .
**Knowledge needs to be in silos** . While "don't silo" is common advice, domain knowledge must be contained within bounded context boundaries to maintain the integrity of the ubiquitous language .
### Team Cognitive Load and Flexibility

Human brains can handle **seven plus or minus two concepts** simultaneously (some argue five plus or minus two) . This means even if you can manage nine concepts, you cannot keep 15 in your head at once . Therefore, context mapping should focus on manageable problem spaces, not the entire enterprise .
**Team Topologies** provides guidance on cognitive load—how many bounded contexts can one team handle? . The answer depends on what the team can cognitively manage without being lazy . Most bounded contexts in normal enterprises are **stream-aligned teams** . Even platform teams are stream-aligned internally—they develop value streams, just technical ones rather than direct business capabilities .
Conway's paper emphasized that **"flexibility of organization is important to effective design"** and that **"the prevailing system concept may need to change"** because initial designs are almost never the best . Organizations must reward design managers for keeping teams lean and flexible . This is reflected in bounded contexts being lean and flexible (easy to change without breaking), unlike big balls of mud that are overweight and static .
### GDPR and Privacy Data

GDPR compliance is a significant concern, though it's "no longer trendy" as a hiring focus since the basics are now covered . However, **AI and LLM data feeding** has revived privacy concerns—organizations now require extensive policy discussions before using systems that might feed data to AI . Even if a system isn't actively using AI, the perception triggers compliance reviews .
**Personally Identifiable Information (PII)** definitions keep evolving—even vehicle registration numbers or OEM-provided vehicle IDs are now considered PII in manufacturing contexts, though many engineers and business folks remain unaware . The "right to be forgotten" is one aspect, but **data subject requests** require tracking how user data flows through systems (CRMs, analytics, etc.) . Most companies manually track this because third-party systems don't manage the complexity .
**Crypto shredding** (deleting encryption keys to render data unreadable) is a technical solution, but social factors complicate adoption—industries with distrust of technology (e.g., manufacturing) fear accidental key deletion . Still, a key-shredding solution could address 50-70% of industries facing GDPR challenges, representing a significant market opportunity . Vaughn jokingly suggested creating [PII.AI](https://pii.ai) as a generic subdomain product for this space .
Identity and access solutions like AWS Cognito, Google Identity, and Supabase likely already provide GDPR compliance, though manual handling is still common in some European companies .
## What I didn't fully get

### When to Use Partnership vs. Customer-Supplier

The distinction between **partnership** and **customer-supplier** relationships can be subtle. Partnership implies both teams succeed or fail together with coordinated releases , while customer-supplier has a clear upstream provider and downstream consumer . However, the Done By Me example shows these relationships can evolve—matching and pricing started as partners but later shifted to customer-supplier when the partnership became impractical .
The key differentiator seems to be **mutual dependency and planning burden**. Partnership requires synchronized releases, joint integration tests, and continuous coordination . Customer-supplier allows the upstream to evolve independently (within its published language contract) without requiring downstream approval for every change . Both can use anti-corruption layers to reduce coupling .
The decision likely depends on:
- Whether both teams need features from each other simultaneously (partnership)
- Whether one team can provide a stable API that the other consumes (customer-supplier)
- The organizational cost of coordination (partnership is "heavy") 

### Shared Kernel vs. Published Language for Events

The discussion about why domain events should not be in a shared kernel  reveals a nuanced distinction. Shared kernel concepts become part of both teams' ubiquitous languages . If `ProposalSubmitted` were in a shared kernel, pricing would have to understand proposals internally .
Instead, events are part of a **published language** context mapping relationship . A published language declares "this is what you can know about what's going on inside me" . Events can be part of this published language without becoming shared kernel .
However, Vaughn acknowledged that events *could* be part of a published language if teams agree on a "Done By Me published language" rather than separate "pricing" and "matching" published languages . The critical point is that even if pricing knows about `ProposalSubmitted` through shared understanding, it still translates the event to a `VerifyPricing` command in its anti-corruption layer rather than letting the event directly drive outcomes .
This suggests the real principle is: **external events should always be translated to internal commands** to maintain bounded context autonomy, regardless of whether the event structure is shared knowledge .
### UI Teams as Bounded Contexts

The question of whether front-end and back-end teams constitute separate bounded contexts  touches on organizational realities versus DDD ideals. Vaughn's answer was nuanced:
- **Ideally**, teams should be cross-functional within a bounded context 
- **If separation is unavoidable**, the UI may be a separate bounded context, especially if it has its own model 
- **Upstream/downstream confusion** is common—a "back-end" team might call the front-end "downstream" when in reality the front-end dictates requirements (making the back-end downstream) 

The **micro front-ends** pattern resolves this by making each bounded context responsible for its own UI components . The profiles team develops the profile component, the matching team develops the matching component, etc. These plug into a composite UI, eliminating the need for a separate "front-end team" that spans multiple contexts .
This aligns with the principle that a bounded context should contain all capabilities needed to deliver value—including UI, backend, and domain expertise .
### Extracting Microservices from Big Ball of Mud

The warning against extracting dozens of microservices from a big ball of mud by identifying boundaries upfront  raises the question: how do you know when you've identified the right boundaries?
Vaughn's answer is **dependency-driven extraction** . Start with a new core domain outside (or within via bubble context) the big ball of mud. This core domain will depend on parts of the legacy system. Those dependencies reveal what supporting and generic subdomains should be extracted next .
However, the extracted boundaries may not contain *only* what the current core domain needs—there might be more concepts in the big ball of mud that belong in the extracted subdomain, but you may not recognize them until the next effort . This suggests an **iterative discovery process** where boundaries are refined over multiple extraction cycles.
The mistake is thinking you can analyze the entire big ball of mud upfront and correctly identify all 46 subdomains and their mappings . You won't get it right, and you'll create hard boundaries prematurely . Instead, let actual needs drive extraction priorities.
### Scope of "Domain" in Different Contexts

The overloading of the word "domain" in DDD  can be confusing. It can mean:
1. **The entire business** (the enterprise domain) 
2. **A problem space** you're working in (which may be smaller than the enterprise) 
3. **A bounded context** (a sphere of knowledge and activity) 
4. **A subdomain** (a business capability) 
5. **The Cynefin framework domains** (clear, complicated, complex, chaotic, confused) 

When discussing strategic design, "domain" typically refers to bounded contexts and subdomains . When discussing problem spaces, it refers to the area of the software being focused on, which involves at least one core domain plus legacy systems and supporting/generic subdomains .
The **Cynefin framework** uses "domain" differently—to categorize the type of problem you face . The **complex domain** is where core domain work typically happens because it involves experimentation and discovering differentiating value . This is distinct from the "complicated domain" (where you know what questions to ask) or "clear domain" (where best practices exist) .
Context clues usually clarify which meaning applies, but it's worth noting the ambiguity.
## Might show up on the exam

### Core Definitions and Principles

- [ ] **Bounded context definition**: Explicit boundary containing a specific mental model, sphere of knowledge and activity, area of communication/learning
