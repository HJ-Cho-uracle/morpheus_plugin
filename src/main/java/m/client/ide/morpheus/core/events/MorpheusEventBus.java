package m.client.ide.morpheus.core.events;

import com.google.common.eventbus.EventBus;
import m.client.ide.morpheus.core.constants.Const;

public enum MorpheusEventBus {
	INSTANCE;

	private final EventBus eventBus; 

	private MorpheusEventBus() {
		eventBus = new EventBus();
	}

	public void register(Object obj) {
		eventBus.register(obj);
	}

	public void unregister(Object obj) {
		eventBus.unregister(obj);
	}

	public void post() {
		eventBus.post(Const.EMPTY_STRING);
	}
}
