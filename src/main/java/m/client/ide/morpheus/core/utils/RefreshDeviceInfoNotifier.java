package m.client.ide.morpheus.core.utils;

import com.intellij.util.messages.Topic;

public interface RefreshDeviceInfoNotifier {
    enum Type {
        simulator, device
    }

    @Topic.ProjectLevel
    Topic<RefreshDeviceInfoNotifier> REFRESH_SIMULATOR_TOPIC =
            Topic.create(Type.simulator.name(), RefreshDeviceInfoNotifier.class);

    @Topic.ProjectLevel
    Topic<RefreshDeviceInfoNotifier> REFRESH_DEVICE_TOPIC =
            Topic.create(Type.device.name(), RefreshDeviceInfoNotifier.class);

    void beforeAction();
    void afterAction();
}
