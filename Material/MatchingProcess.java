package co.donebyme.matching.infra.messaging;

import co.donebyme.matching.infra.API;
import co.donebyme.matching.infra.persistence.MatchingJournal;
import co.donebyme.matching.model.Id;
import co.donebyme.matching.model.proposal.PricingVerified;
import co.donebyme.matching.model.proposal.Proposal;
import co.donebyme.pricing.command.AnalyzePricing;
import co.vaughnvernon.mockroservices.journal.Journal;
import co.vaughnvernon.mockroservices.messagebus.Message;
import co.vaughnvernon.mockroservices.messagebus.MessageBus;
import co.vaughnvernon.mockroservices.messagebus.Subscriber;
import co.vaughnvernon.mockroservices.messagebus.Topic;
import co.vaughnvernon.mockroservices.model.Command;
import co.vaughnvernon.mockroservices.model.SourcedEntity;

public class MatchingProcess implements Subscriber {

  private Journal journal = MatchingJournal.journal;
  
  public static void start() {
    final MatchingProcess process = new MatchingProcess();
    final MessageBus messageBus = MessageBus.start("donebyme");
    final Topic allTopic = messageBus.openTopic("all");
    allTopic.subscribe(process);
  }
  
  @Override
  public void handle(Message message) {
    switch (message.type) {
    // From Matching
    case "donebyme.matching.ProposalSubmitted":
      append(new AnalyzePricing(...));
      break;
    // From Pricing
    case "donebyme.pricing.PricingVerified":
      API.proposal().verifyPricing(pricingVerified.originatorId);
      break;
    // From Pricing
    case "donebyme.pricing.PricingRejected":
      API.proposal().denyPricing(pricingDenied.originatorId, pricingDenied.suggestedPrice);
      break;
    // From Matching
    case "donebyme.matching.PricingVerified":
      append(new RecommendDoers(...));
      break;
    // From Matching
    case "donebyme.matching.PricingDenied":
      API.resubmissions().handleUnderpriced(event.proposalId, event.resubmissionCount, event.suggestedPrice));
      break
    // From Profile
    case "donebyme.profile.DoersRecommended":
      API.proposal().poolSkilledDoers(doersRecommended.taskId, doersRecommended.doers);
      break;
    // From Matching
    case "donebyme.matching.SkilledDoersPooled":
      append(new LocateAvailabilty(...));
      break;
    // From Scheduling
    case "donebyme.scheduling.AvailabiltyLocated":
      API.proposal().mergeSkilledDoers(availability.taskId, doers);
      break;
    // From Matching
    case "donebyme.matching.SkilledDoersMerged":
      append(new NotifyAvailableDoers(...));
      break;
    // From Doers Mobile App
    case "donebyme.doersontherun.ProposalAccepted":
      API.proposal().accept(accepted.doerId);
      break;
    }

    if (API.proposal().isAcceptable()) {
      // ...
    }
  }
  
  private void append(Command command) {
    journal.write(command.correlationId, command);
  }
  
  private Command commandFor(Step step) {
    return new RecommendDoers(...);
  }
}
