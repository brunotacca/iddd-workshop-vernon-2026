## Key Outcomes

This was an introductory Domain-Driven Design (DDD) workshop focused on strategic design principles, Conway's Law, and Theory of Constraints applied to a fictional startup called "Done By Me."  The session established that **pricing is the primary business constraint** requiring immediate attention, followed by service catalog improvements and user account security. 
## Core Concepts Covered

### Domain-Driven Design Fundamentals

- **Bounded Context**: A boundary with specific mental model and language where concepts have precise meaning within that context 
- **Ubiquitous Language**: Team-developed vocabulary used consistently across code, documentation, and conversations within a bounded context 
- **Subdomain Types**: 
    - **Core Domain**: Differentiating value, secret sauce, innovation (CapEx investment)
    - **Supporting Subdomain**: Non-differentiating but necessary, doesn't exist as purchasable product
    - **Generic Subdomain**: Non-differentiating, can be purchased or already exists (OpEx)

### Conway's Law Application

Organizations that design systems are constrained to produce designs reflecting their communication structures.  Conway's First Axiom: Organizations must be flexible and reorganize teams for required communication, rewarding lean and flexible design.  The Done By Me big ball of mud reflects haphazard, fragmented communication with no real expertise. 
### Theory of Constraints Framework

Five-step process applied to identify and address bottlenecks: 
1. **Identify** the constraint (bottleneck preventing goal achievement)
2. **Exploit** the constraint (maximize output with current resources)
3. **Subordinate** everything to the constraint (synchronize all processes)
4. **Elevate** the constraint (invest to increase capacity if still limiting)
5. **Prevent inertia** (repeat cycle when new constraint emerges)

## Done By Me Case Study Analysis

### Current Problems Identified

**Matching Subdomain**: 
- Services identification and pricing inconsistent across broad geographic areas
- High-touch process forcing unwanted client decisions
- Auction market allows non-preferred doers to underbid, driving away qualified professionals

**Services Catalog**: 
- Pricing based on suggestions rather than policies
- Services hard to find and describe
- Optional, negotiable pricing enables race-to-bottom bidding

**User Accounts**: 
- Platform lacks knowledge about users (clients and doers)
- No tracking of doer availability for specific dates/times
- Missing profiles for both user types

**Payments**: 
- Embedded in monolith creates security vulnerabilities
- Tangled implementation limits payment options and gateway choices
- Difficult to negotiate better pricing or improve speed

**Personal Information**: 
- Poor protection of private data
- Location and contact information easily leaked or exposed
- Not GDPR compliant

### Strategic Solution: Pricing as Core Domain

**Where's the Money**: Pricing identified as primary constraint because commission percentages decrease as prices drop, and preferred doers (the company's best "employees") are driven away by low bids. 
**Theory of Constraints Application**: 
1. **Exploit**: Set average pricing based on large metropolitan areas (not middle America); implement maximum discount limits
2. **Subordinate**: Use external SaaS for services pricing catalog
3. **Elevate**: Leverage machine learning (marketed as AI) using historical matching and payment data to continuously improve pricing accuracy

### Proposed Subdomain Solutions

**Pricing (New Core Domain)**: 
- Create bubble context within big ball of mud for new pricing module
- Implement taxonomy-based service identification: `home-maintenance/windows-washing/scope/inside-outside/window-cleaning/count=38/size-extra-tall=10`
- Policy-based pricing replacing suggestions
- Initially focus on most frequently used services

**Identity & Access**: 
- Purchase third-party solution (AWS Cognito, Google Identity, Supabase)
- Separate profiles subdomain for client/doer information
- Track doer availability through scheduling integration (Google Calendar, Microsoft 365)

**Payments Extraction**: 
- Extract to separate microservice with isolated database
- Enable multiple gateway options (Stripe, Google Pay, Apple Pay)
- Improve security through separation and dedicated identity/access controls

## Context Mapping Patterns

### Partnership

Partners succeed or fail together; heavy coordination required with mutual support.  Both contexts use anti-corruption layers despite tight coordination. Matching and Pricing initially partnered, with Matching depending on `PricingVerified` and `PricingRejected` events. 
**Event-Driven Learning**: Commands and events enable learning from trends (pricing gaps, regional variations, competitive pressure) versus queries which only return current state. 
### Shared Kernel

Focus on **kernel** (small size), not sharing.  Two teams agree to share small model concepts becoming part of both ubiquitous languages.  
**Done By Me Examples**: 
- **Monetary kernel** (owned by Pricing): Money, Currency, Tax types, and exchange domain service shared with Matching
- **Roles kernel**: Client and Doer as value objects (identity and name only), while Profiles context maintains full entities/aggregates

**Challenges**: Teams must know each other exists, agree on what to share, and maintain shared code without breaking dependents. 
### Customer-Supplier

Upstream supplier provides service to downstream customer who depends on it.  Pricing moved upstream using Open Host Service pattern, exposing its own API. Matching (downstream) translates `ProposalSubmitted` to `VerifyPricing` command sent upstream, maintaining anti-corruption layer. 
## Design Principles Discussed

### DRY (Don't Repeat Yourself)

Applies to **knowledge**, not code.  If code captures specific knowledge sphere, don't repeat that knowledge. Two different code implementations can violate DRY if they repeat the same conceptual knowledge. 
### CQS/CQRS

**Command Query Separation (CQS)**: Interface methods are either commands (mutate state, return void) or queries (answer state without modification).  Dr. Bertrand Meyer's principle: "Answering a question cannot change the answer." 
**Command Query Responsibility Segregation (CQRS)**: Separates commands and queries into distinct responsibilities, enabling independent scaling (80% reads vs 20% writes) and different optimization strategies.  Does not require eventual consistency or separate databases—queries can read from same transactionally-consistent database as commands. 
### Law of Demeter

Rule of least knowledge: External contracts define what others can know about bounded context.  Events published as part of Published Language context mapping relationship represent additional contract beyond command/query interface. 
### Team Cognitive Load

Maximum bounded contexts per team determined by cognitive load capacity (Team Topologies principle), not arbitrary limits.  Human brains handle 7±2 concepts simultaneously; avoid designing systems with more interconnected contexts than teams can comprehend. 
## Technical Patterns Referenced

### Micro Frontends

Composite UI pattern where bounded contexts own UI components.  Components receive IDs and fetch their own data via REST from owning context (Profiles, Matching, Scheduling). Solves cross-context UI data display without coupling backend models. 
### GDPR Compliance

Crypto-shredding suggested as key-value store solution: encrypt PII with keys that can be deleted to fulfill "right to be forgotten."  Challenge noted: definition of PII evolves (now includes vehicle registration numbers), and engineers often unaware of what constitutes PII. 
## Participant Questions Addressed

**Bruno Tacca**: Asked whether this strategic exercise uses Theory of Constraints to find good domain/boundary candidates—confirmed yes, focusing on identifying most limiting constraint first. 
**Julia**: Questioned if pricing always answers "where's the money" for any business—clarified that for e-commerce specifically, pricing is critical, but calculation (core) differs from payment processing (generic). 
**Theodoros**: Suggested matching (due to availability issues) as primary constraint—instructor clarified doer notification annoyance is less critical than revenue loss from pricing problems. 
**Swarup**: Questioned if command vs query distinction is just semantics—explained CQS/CQRS principles showing architectural significance beyond naming. 
**Gus**: Observed front-end/back-end team splits often mirror context mapping patterns—confirmed UI can be separate bounded context, especially with micro frontends approach. 
## Action Items

- **All Participants**: Read 9-10 page Done By Me company story PDF provided with workshop materials 
- **Tomorrow's Session**: Begin hands-on exercises iterating on strategic design concepts 
- **Instructor**: Continue context mapping pattern coverage (remaining relationships not yet covered) 

## Next Workshop Session

Continue with remaining context mapping relationships and begin tactical modeling exercises including modules, entities, value objects, domain events, aggregates, event sourcing, and CQRS patterns. 
