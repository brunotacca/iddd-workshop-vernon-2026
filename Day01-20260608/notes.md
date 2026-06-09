# Intro

-- 9h00

People Reasons:
- Sell to leadership (How to)
- Knowledge to teach students
- Practice knowledge
- Better Understanding
- Validate Knowledge

AI - How is it affecting?
- Hinders learning (students)
- People being pushed to use AI, frake productivity
- Works better with robust practices, people dont know how to peer review
- Grim future - "code doesnt matter"?

# Bit of overview on DDD

--- 9h30

IDDD Material - Miro

Why DDD? BBoMud - How to exit that?
Have you seen? One namespace - Hundreds of entities/classes.

Bounded Context - Ubiquitous Language 
-> Tech, Real Business Domain Expert, Tech, Tech
-> People have to talk!
-> Getting together, getting into agreement.

There are always multiple bounded contexts.
- Same word, different definitions - language overlaps
- Language doesnt overlap, but team uses different words to describe related things
- - Tasks <> Proposal <> PricedItem

A domaain -> The problem space
- CORE SUBDOMAIN - Related to CapEx - An Investment
- GENERIC SUBDOMAIN - Related to OpEx
- Supporting - Driven by the Core, Can shift to Generic
- These are important to differentiate the business

--- 10h00

Context Mapping: Defining relation between teams, integration across boundaries

Quick speedup - Strategic -> Tactical -> CQRS/EventSource -> Monolith/Microservice -> Tools for DDD

-> Break! 10h05

--- 10h20 - Back

Conway's Law explanation <> relation with BBoM.
Executive mgmt doesnt care about code... they care about outcomes.

Inverse Conways Manuever -> Fault: "Set teams for the arch you want" but what about comms?
The business arch and the social arch is as important as the software arch.
We tend to leave out 2/3 of the problem (The business and social), while focusing on software.

BBoM -> Overweight, static (Change it and breaks)
DDD -> Light and flexible (Change it and it adapts)

Theory of Constraints - Phoenix Project

--- 10h38

Miro: How to Implement with DDD

Core Domain (Revenue driver)
- Area of Expertise (A domain expert is neeeded)
- Sphere of Knowledge and activity
- Areea of Innovaation
- Single domain model applicable (1 domain model codifies the UB LANG)
- Expresses UB LANG of the Context

DRY - Dont Repeat Yourself (in Knowledge)

--- 10h45

Peter Thiel: Competition is for losers. 
-> "Create a Monopoly"
Invention is included in Innovation.

! As long as you can create a unique module for this area of expertise - you can work in a core domain.

!!! Insight
from the BBoM: 
- Core domain -> Identify and Extract (or develop a new)
- Since this domain is still dependant on the BBoM, you can identify these more clearly as supporting/generic.
- The boundaries are almost always blurry, you will not be right, it's tricky.
- Isolating them into microservices is risky and costly to change later.

--- 11h00 - Break
--- 11h10

Checklist manifesto (Surgeons puting up checklist to not forget)

"Our 2026 corporate pillars isn't a strategy"
- Action Agenda: Identify the things you want to change.

Tools:
- Theory of Constraints
- Systems Thinking
- SMART
- Event Storming
- Impact Mapping
- Scenarios (Given When Then)
- Bounded Context <> Context Mapping <> Topo Architecture
- Wardley Mapping
- Cynefin Framework (Snowden)
- Celebration Grid
- Acceptance Tests (Domain model works the Business intends it to work?)

Circular Arrows (Tools) = Iteration = Continuous Improvement

--- 11h25

DoneByMe - Match Clients with Doers
- Preferred Doers are highly reviewed doers
- TC - Focus on identifying and addressing the most limiting factor (constraint) that stands in the way of achieving a goal

Where is the money? Think about it when going through the exercise

DoneByMe - Cards+Problem Stateements.
-> Collected from interviews with Clients and Doers

Pricing? As core domains.
-- Bubble Context, inside the BBoM.

Exercise Theory of Constraints, to find important concerns for the business.
- They might be good candidates for core subdomains, or supp or generic.

"Do we usee BC and SubD as synonyms?"
- Not synonyms, but a way to understand a subdomain is, is understanding a business caapability
- business cap is a way for the business to suceed.
- Business Capability 1:1 Subdomain 1:1 Bounded Context

--- 12h30

Context Mapping - Team Relations - Link with Team Topologies.

The best first step (Map AS IS - The present)
Then, Map the Future!
- Start small. Stick to a problem space (instead of the whole enterprise)
- Human brains can deal with 7+-2 concepts at one time. (or 5+-2)


!! Shared Kernel:
- 2 teams aggrees on a small set of concepts (like a monetary model).
- Specific owner (one team owns it), the other asks for changes.

--- 13h00 End (Break) Customer-Supplier