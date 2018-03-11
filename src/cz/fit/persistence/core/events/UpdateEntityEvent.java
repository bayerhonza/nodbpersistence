package cz.fit.persistence.core.events;

public class UpdateEntityEvent extends EntityEvent {

    public EntityEventType TYPE = EntityEventType.UPDATE_EVENT;

    public UpdateEntityEvent(Object object, EventSource eventSource) {

        super(object, eventSource);
    }
}
