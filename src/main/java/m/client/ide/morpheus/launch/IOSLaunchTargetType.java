package m.client.ide.morpheus.launch;

public enum IOSLaunchTargetType {
    DEVICE, SIMULATOR;

    public static IOSLaunchTargetType fromString(String type) {
        if (DEVICE.toString().equals(type))
            return IOSLaunchTargetType.DEVICE;
        else
            return IOSLaunchTargetType.SIMULATOR;
    }
}
