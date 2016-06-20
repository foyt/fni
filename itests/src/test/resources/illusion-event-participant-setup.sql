insert into
  IllusionEventParticipant (id, role, event_id, user_id, created)
values 
  ({id|1}, '{role|PARTICIPANT}', {eventId|1}, {userId|2}, PARSEDATETIME('1 1 2010', 'd M yyyy'));