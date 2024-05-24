package m.client.ide.morpheus.launch;

public enum IOSDeviceType {
    iPhone, iPad;

    public static IOSDeviceType fromString(String type) {
        if (iPad.toString().equals(type))
            return IOSDeviceType.iPad;
        else
            return IOSDeviceType.iPhone;
    }
}
