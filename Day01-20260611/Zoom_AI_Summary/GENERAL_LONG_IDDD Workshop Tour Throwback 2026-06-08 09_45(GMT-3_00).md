## Key Outcomes

This workshop introduced strategic domain-driven design (DDD) principles through the lens of a startup case study called "Done By Me" (a home services marketplace). Vaughn demonstrated how to identify business constraints using **Competition is for losers** as a guiding principle for differentiation, apply Theory of Constraints to prioritize core domain work, and use context mapping to define team relationships and integration patterns.  The session established that **pricing emerged as the primary constraint** requiring immediate attention, not matching or user management, because it directly impacts revenue and doer retention in the Done By Me platform. 
## Core DDD Concepts Introduced

### Bounded Context Fundamentals

**A bounded context represents a boundary containing a specific mental model and ubiquitous language** developed through real conversations between domain experts and technical teams.  The context is deliberately kept small and focused on a single area of expertise—a sphere of knowledge and activity.  Within each bounded context, teams develop a **ubiquitous language** expressed through software concepts, commands, and events that flow between model elements. 
**The DRY principle applies to knowledge, not just code**—if knowledge belongs to a specific bounded context, it should not be repeated elsewhere, even if the code looks different.  This means duplicate code may be acceptable if it represents different conceptual knowledge in different contexts. 
### Conway's Law and Organizational Design

**Organizations that design systems are constrained to produce designs reflecting their communication structures**, as stated in Conway's Law from 1967.  The critical insight: **design efforts should be organized according to the need for communication**, not just technical architecture.  This principle predates the "reverse Conway maneuver" by decades and emphasizes that business architecture and social architecture are as important as technical architecture. 
**Conway's First Axiom** states that organizations must remain flexible and open to reorganizing teams for required communication patterns, rewarding managers who keep organizations lean and flexible.  This flexibility manifests in bounded contexts as lean, adaptable team structures versus the inflexible, overweight nature of big balls of mud. 
### Domain Types and Investment Strategy

The **core domain** represents differentiating value and innovation—the organization's "secret sauce" where capital expenditure (CapEx) is justified.  This is where **Competition is for losers** applies: creating monopolistic advantages through innovation rather than competing on existing capabilities. 
**Supporting subdomains** provide non-differentiating but necessary capabilities that don't exist as purchasable products, representing operational expenses (OpEx) driven by core domain needs.  **Generic subdomains** are completely non-differentiating capabilities that already exist as products or services—candidates for code generation or third-party solutions. 
## Done By Me Case Study: Problem Space Analysis

### Business Model and Current State

Done By Me operates as a **home services marketplace matching clients (homeowners needing work) with doers (service providers)**, taking commission on completed transactions.  The company exists in two parallel timelines: one where they built without DDD knowledge and created a big ball of mud within a year, and another where they started with DDD principles. 
The legacy system contains five implicit subdomains tangled together: **matching, payments, financial accounts, users, and services catalog**—none properly modularized with clear boundaries.  This architecture reflects haphazard, ad-hoc communication structures with fragmented expertise and broken information flow. 
### Critical Business Constraints Identified

**Pricing dysfunction emerged as the primary constraint** blocking business success through multiple failure modes: 
**Services identification and pricing are inconsistent** across the catalog, with the same pricing applied broadly across entire continents or nations despite vastly different cost-of-living conditions.  This one-size-fits-all approach fails in markets like San Jose, California versus rural areas. 
**Matching has become high-touch**, forcing clients into excessive decision-making they don't want, creating friction in the user experience.  The system **evolved into an auction market** where non-preferred (unvetted, low-skilled) doers can underbid qualified preferred doers, driving prices below sustainable levels. 
**Preferred doers—the platform's most valuable "employees"—are being priced out** of work they're qualified to perform, violating the Southwest Airlines principle: "We love our customers, but we really love our employees."  When preferred doers can't earn fair wages, the platform loses its most important asset.
### Julia's Window Washing Scenario

A concrete example illustrated the pricing failure: **Julia, a premium client and Fortune company CIO in San Jose, submits a proposal for window washing** with catalog price $225, but her house has 38 windows (10 extra-large), requires inside-outside service, and she's requesting Saturday 7am service in Silicon Valley. 
**The $225 catalog price bears no relationship to actual service requirements or local market conditions**. When Julia offers even $225 plus tip, the bidding system allows non-preferred doers to drop the price to $150, ensuring preferred doers reject the work and Julia receives disappointing service from unqualified providers.  This scenario exemplifies why the company is "bleeding money" and failing to retain either quality doers or satisfied clients. 
## Theory of Constraints Application

### Five-Step Framework

**Theory of Constraints provides the strategic methodology for prioritizing improvement work** through five iterative steps: 
1. **Identify the constraint**: Find the bottleneck preventing the system from achieving its goal 
2. **Exploit the constraint**: Get maximum value from current resources without major investment, ensuring unnecessary activities don't slow it down 
3. **Subordinate everything to the constraint**: Ensure all other processes support the constraint's pace; non-constraint resources shouldn't produce more than the constraint can handle 
4. **Elevate the constraint**: If still limiting after steps 2-3, invest in increasing capacity through new equipment, staff, or process improvements 
5. **Prevent inertia**: Once elevated, a new constraint emerges—return to step 1 and repeat continuously 

### Pricing as Primary Constraint

**Pricing was identified as the most limiting constraint using "where's the money?" analysis**—the fundamental question for for-profit businesses.  While matching, user accounts, and other areas had problems, **pricing dysfunction directly prevented revenue generation and doer retention**, making it the bottleneck requiring immediate attention. 
The constraint manifests as: **matching is a function of pricing**—without fair pricing policies, matching cannot succeed regardless of algorithmic sophistication.  Notification timing for doers and other matching inefficiencies are merely annoyances, not revenue blockers. 
### Three-Stage Pricing Solution

**Exploit the constraint (Stage 1)**: Establish average pricing based on large metropolitan areas (San Jose, San Francisco, Seattle, New York, Boston) rather than national averages, and **implement maximum discount limits to prevent destructive bidding**.  This may price out rural doers temporarily but protects the core business in primary markets. 
**Subordinate to the constraint (Stage 2)**: Integrate with external **services pricing SaaS** that provides more sophisticated pricing data across regions, leveraging existing market intelligence rather than building from scratch. 
**Elevate the constraint (Stage 3)**: Deploy **machine learning (marketed as AI for funding purposes)** using historical data from matching and payments to continuously refine pricing policies, learning from pricing gaps and market trends.  This creates ongoing competitive advantage through data-driven pricing optimization. 
## Strategic Solutions and Subdomain Extraction

### Core Domain: Pricing as Bubble Context

**Create a "bubble context" for pricing within the legacy monolith**—a modularized core domain (done-by-me.pricing) that operates with clear boundaries inside the big ball of mud.  This approach enables innovation without requiring immediate microservice extraction or complete system rewrite. 
The pricing context implements **policy-based pricing with exact service identification using taxonomy**, replacing the flawed suggestion-based system.  The taxonomy structure resembles REST URLs: `home-maintenance/windows/washing/scope/inside-outside?window-count=38&size-extra-tall=10` , enabling precise service specification and dynamic pricing calculation. 
### Supporting Domain: Profiles

**Extract user information into a dedicated Profiles bounded context** separate from the legacy Users module, capturing richer information about both clients and doers.  This includes **preferred doer status, review history, and bidirectional reviews** (doers rating clients on ease of working relationship). 
Profiles integrates with purchased **Identity and Access Management (IAM)** solutions like AWS Cognito, Google Identity, or Supabase for authentication, separating identity concerns from profile data.  Under GDPR and similar regulations, certain profile information (preferred status, reviews) remains permissible while sensitive data receives appropriate protection. 
### Generic Subdomains: Payments and Scheduling

**Payments must be extracted from the core monolith into a separate microservice with isolated database** to address security vulnerabilities where any developer could query sensitive financial data.  This extraction enables **multiple payment gateway options** (Stripe, Google Pay, Apple Pay) and better pricing negotiation through specialized vendors. 
**Scheduling can leverage third-party calendar systems** (Google Calendar, Microsoft 365) as a generic subdomain rather than building custom scheduling.  This addresses the problem of notifying unavailable doers by integrating with their actual availability calendars. 
### Services Catalog with Taxonomy

**Implement hierarchical taxonomy for service identification** replacing the vague, hard-to-search legacy catalog.  The taxonomy enables **client-driven options** through guided questions: indoor/outdoor cleaning, total window count, extra-large window count, generating precise **scope parameters like** **`home-maintenance/windows/washing/scope/inside-outside?window-count=38&size-extra-tall=10`**. 
**Identify core services** (most frequently used) for initial taxonomy implementation, then expand coverage over time rather than attempting complete catalog transformation upfront. 
## Context Mapping Relationships

### Partnership Pattern

**Partners succeed or fail together** in a partnership relationship where both bounded contexts coordinate releases and support each other's needs without requiring formal requests ("you don't have to say please").  This is a **heavy relationship requiring significant coordination** including joint planning meetings, synchronized releases, and integration testing between contexts. 
In the Done By Me example, **Matching and Pricing initially operated as partners**, with Matching emitting `ProposalSubmitted` events that Pricing consumed, and Pricing emitting `PricingVerified` or `PricingRejected` events back to Matching.  Both sides maintained **anti-corruption layers (ACL)** to translate between their respective models, weakening coupling despite tight coordination. 
**Event-driven choreography provides learning opportunities** that synchronous queries cannot: Pricing learns from pricing gaps and trends (clients offering lower prices, market competition signals), enabling continuous pricing model refinement.  Queries only return current known state; commands and events create analyzable history for machine learning. 
### Customer-Supplier Pattern

**Customer-Supplier establishes upstream-downstream dependency** where the upstream supplier provides services the downstream customer depends on, but the customer no longer dictates supplier priorities as in partnership.  The **upstream context uses Open Host Service** to expose its API, and the **downstream context uses Anti-Corruption Layer** to translate upstream concepts into its own model. 
In the evolved Done By Me architecture, **Pricing moved upstream as supplier with Matching as downstream customer**.  Matching still emits `ProposalSubmitted` but must translate this to Pricing's `VerifyPricing` command in its ACL, adapting to Pricing's published API rather than Pricing adapting to Matching. 
**Upstream means "flow of influence"**—like pouring mercury in water, pollution flows downstream, not upstream.  However, in practice, **downstream teams often exert de facto upstream influence** when they control requirements or funding, creating tension between technical and organizational upstream/downstream definitions. 
### Shared Kernel Pattern

**Shared Kernel enables multiple teams to agree on a small, shared model** that becomes part of each team's ubiquitous language.  The emphasis is on **"kernel" (small)**—like a single kernel on a corn ear compared to the entire field, stalk, or even single ear. 
The **Monetary shared kernel** illustrates proper usage: Pricing team owns a library containing `Money`, `Currency`, `Tax`, and `MoneyExchangeService` domain concepts that Matching team requested to reuse rather than reimplementing.  Only the owning team (Pricing) can modify the shared kernel; other teams must request changes. 
**Shared Kernel is NOT for sharing domain events**—events represent temporal occurrences in one context that others may consume, but they should not become part of multiple contexts' core models.  The **Roles shared kernel** (containing `Client` and `Doer` as value objects with just name and identity) demonstrates appropriate scope, while Profiles context maintains these as full entities/aggregates with mutable state. 
**General-purpose concepts work better than specific ones** in shared kernels—teams struggle to agree on specific domain concepts but can align on foundational elements like Money or basic roles.  In large enterprises, discovering which teams have shareable kernels and achieving agreement on contents presents significant challenges. 
### Published Language Pattern

**Published Language defines what external parties can know about a bounded context's internal operations**, often expressed through domain events that signal state changes.  This creates a **contract separate from the command/query interface** governed by the Law of Demeter (rule of least knowledge). 
The **Account interface example demonstrates command/query separation:** **`void credit(Money amount)`****,** **`void debit(Money amount)`****,** **`Money balance()`** shows what external parties can do with accounts, while published events reveal what happened inside the context after commands execute. 
**Published Language could be context-specific or organization-wide**—Done By Me could establish a company-wide published language where all contexts understand `ProposalSubmitted`, or each context could maintain its own published language requiring translation at boundaries. 
## Command Query Separation (CQS) and CQRS

### CQS Fundamentals

**Command Query Separation (CQS), defined by Dr. Bertrand Meyer in the Eiffel language**, establishes that **interfaces should contain only two method types: commands that mutate state without returning values, and queries that return state without mutations**.  The principle: **answering a question cannot change the answer**—if you want to know account balance before and after a credit operation, you must query separately before and after the command. 
The **Account interface demonstrates CQS: commands** **`credit(Money amount)`** **and** **`debit(Money amount)`** **return void, while query** **`balance()`** **returns Money without side effects**.  To learn the balance after crediting, **you must execute the command, then separately query the new balance**—the command itself reveals nothing about resulting state. 
**CQS differs fundamentally from typical API design** where operations often return updated state, conflating command execution with query response.  This separation enables **event sourcing and learning from command execution history** rather than just knowing current state through queries. 
### CQRS Architecture

**Command Query Responsibility Segregation (CQRS) extends CQS by segregating commands and queries into separate responsibilities**, potentially with separate interfaces: `AccountCommands` containing only command methods and `AccountQueries` containing only query methods.  This is **not about eventual consistency or separate databases**—those are optional architectural choices enabled by CQRS, not requirements. 
**CQRS enables independent scaling** based on read/write ratios: if 80% of transactions are read-only, query infrastructure can scale 80% higher than command infrastructure without over-provisioning command capacity.  **Queries can read from the same database instance and tables that commands write to**, maintaining transactional consistency, or can use separate optimized read stores with eventual consistency. 
**Removing write transaction contention from read operations** improves overall throughput since reads don't block on write locks.  The architectural flexibility allows teams to optimize command and query paths independently based on actual performance characteristics rather than assuming they require identical infrastructure. 
## UI Integration Patterns

### Micro Frontends and Composite UI

**Micro frontends solve the problem of displaying information from multiple bounded contexts** without creating tight coupling between backend services and monolithic frontend applications.  Each bounded context team **develops UI components that know how to render their domain concepts** given only an identifier or small set of parameters. 
The **Profiles team creates a component that renders client or doer information** when handed a role identifier; the **Matching team creates components for proposal display**; the **Scheduling team creates availability components**.  These components **plug into composite pages** where an API gateway or coordinator manages the overall page assembly, but each component independently fetches its data from its owning bounded context. 
**Cross-functional teams within bounded contexts eliminate frontend/backend splits**—the team owns the complete vertical slice including UI components, APIs, domain logic, and data storage for their bounded context.  When organizational constraints force frontend/backend team separation, **the UI team may constitute a separate bounded context if they maintain their own model**, with context mapping defining the upstream/downstream relationship. 
**Backend-for-Frontend (BFF) patterns and GraphQL** can coordinate data aggregation from multiple contexts without forcing the UI to make numerous individual service calls.  The key principle: **UI components should request data from their owning contexts, not reach across context boundaries** to query foreign domain concepts directly. 
## Transformation and Modernization Strategy

### Stepwise Refinement from Big Ball of Mud

**Transformation begins by identifying implicit subdomains within the legacy system** and carving out modular areas of expertise even before extraction.  In Done By Me's legacy system, conceptual boundaries exist for matching, payments, financial accounts, users, and services catalog despite lacking technical modularity. 
**Create bubble contexts for new core domains within the monolith** rather than attempting wholesale microservice extraction.  The pricing core domain exists as `done-by-me.pricing` module with clear boundaries, integrating with legacy systems through lightweight, single-threaded mechanisms without network overhead. 
**Avoid the common failure pattern of extracting 46 subdomains simultaneously**—teams cannot correctly identify boundaries without understanding dependencies, and premature extraction creates distributed big balls of mud with hardened incorrect boundaries.  Instead, **let dependency needs drive extraction**: when the new core domain depends on legacy capabilities, those dependencies reveal which supporting/generic subdomains warrant extraction next. 
### Security-Driven Extraction

**Security vulnerabilities justify immediate extraction** even for non-core domains: payments and financial accounts must move to separate microservices with isolated databases because the current architecture allows any developer to query sensitive financial data.  This extraction also enables **multiple payment gateway options** and better vendor negotiation. 
**Identity and Access Management (IAM) should be purchased rather than built**, using services like AWS Cognito, Google Identity, or Supabase that include GDPR compliance built-in.  Separating authentication (IAM) from user profiles (Profiles bounded context) allows each to evolve independently while maintaining security boundaries. 
**Crypto-shredding provides GDPR "right to be forgotten" compliance** by encrypting PII with user-specific keys, then deleting the encryption key to make data unrecoverable.  A **generic PII management service** could serve 50-70% of industries facing GDPR compliance challenges, representing significant market opportunity (jokingly named "[PII.AI](https://pii.ai)" for funding appeal). 
### Cognitive Load and Team Capacity

**Human brains can handle seven plus-or-minus-two concepts simultaneously** (some argue five plus-or-minus-two), limiting how many bounded contexts or subdomains a team can effectively manage.  This cognitive constraint, not just technical complexity, determines **appropriate team size and context ownership**. 
**Team Topologies' cognitive load framework** provides the best guidance for determining how many bounded contexts one team should handle—there's no fixed number, only the question of what cognitive load the team can sustain without becoming ineffective.  A five-developer startup team should focus on a small problem space domain rather than attempting enterprise-wide transformation. 
**Most bounded contexts are stream-aligned teams** in Team Topologies terminology, including platform teams which are stream-aligned internally even though they provide platform services to other teams.  The focus remains on delivering value streams, whether those streams directly serve business capabilities or provide technical platforms enabling other teams. 
## GDPR and Privacy Compliance

### Current Implementation Challenges

**GDPR compliance remains largely manual** across European companies, with teams tracking data processing, data subject requests, and right-to-be-forgotten workflows without comprehensive third-party solutions.  The **definition of PII continues evolving**—vehicle identification numbers (VINs) now qualify as PII in automotive manufacturing, catching engineers and business stakeholders unaware. 
**AI and LLM data usage has superseded GDPR as the primary privacy concern** in 2025, with organizations requiring on-premises hosting and extensive policy discussions before allowing any data to flow to AI systems.  GDPR remains important but no longer dominates hiring or project discussions as it did previously. 
**Data subject requests require tracking data flow across CRMs, analytics platforms, and numerous systems**, with companies manually maintaining this mapping without effective automation tools.  The **right to be forgotten** extends beyond simple deletion to understanding how data propagates through interconnected systems. 
### Technical Solutions

**Crypto-shredding offers elegant GDPR compliance** by storing PII encrypted with user-specific keys in a key-value store organized by category (e.g., CRM), area within category, property, and encrypted value.  Deleting the encryption key renders data unrecoverable, satisfying right-to-be-forgotten requirements without complex data deletion workflows. 
**Social and organizational factors complicate technical solutions**—manufacturing companies express "general distrust that technology can always go wrong," fearing accidental key deletion despite crypto-shredding's technical elegance.  This suggests **50% of industries could benefit from a standardized PII management solution** while others require custom approaches due to risk tolerance or regulatory constraints. 
**California and other US states now implement GDPR-like regulations**, expanding the market for privacy compliance solutions beyond Europe, though implementation remains state-by-state rather than federal.  This fragmentation increases complexity for companies operating across multiple jurisdictions. 
## Workshop Pedagogy and Learning Approach

### Case Study Methodology

**Two parallel timelines for Done By Me** illustrate DDD impact: one where the startup built without DDD knowledge and created a big ball of mud requiring rescue after one year, and another where they started with DDD principles.  This "string theory" approach demonstrates **strategic decision consequences** without requiring participants to experience multi-year project failures. 
**Vaughn serves as domain expert and co-founder** in the Done By Me scenario, providing business context and strategic direction while acknowledging Alberto Brandolini's "I'm never wrong" t-shirt philosophy—the instructor must maintain authority to create coherent learning experiences even while encouraging debate. 
**Concrete scenarios using Given-When-Then format** drive strategic analysis: "Given a customer needs work done, when doers can drive down the price, then the customer does not get work fulfilled as wanted and preferred doers don't get the work."  These scenarios work at multiple abstraction levels from high-level strategy to low-level acceptance tests. 
### Tools and Frameworks Introduction

**Multiple strategic tools were introduced** for later deep-dive sessions: Event Storming, Impact Mapping, SMART goals (Specific, Measurable, Achievable, Relevant, Time-bound), Cynefin framework, Wardley mapping, celebration grids, and topological architecture diagrams.  Each tool addresses specific aspects of strategic design without requiring sequential application—teams use them "just in time when needed." 
**Cynefin framework's five domains** (clear, complicated, complex, chaotic, disorder) help teams understand problem complexity levels.  **Core domains typically exist in the complex domain** where questions aren't yet known, requiring experimental approaches to discover differentiating value.  Clear domains have best practices; complicated domains have knowable solutions; complex domains require probe-sense-respond experimentation. 
**The Checklist Manifesto and Good Strategy/Bad Strategy** provide complementary reading on
