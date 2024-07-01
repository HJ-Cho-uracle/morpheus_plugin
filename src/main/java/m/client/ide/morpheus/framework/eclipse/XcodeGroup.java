package m.client.ide.morpheus.framework.eclipse;

public class XcodeGroup {
    private String name;
    private String varName;
    private XcodeGroup parent;

    public XcodeGroup(String name, String varName, XcodeGroup parent) {
        this.name = name;
        this.varName = varName;
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVarName() {
        return varName;
    }

    public void setVarName(String varName) {
        this.varName = varName;
    }

    public XcodeGroup getParent() {
        return parent;
    }

    public void setParent(XcodeGroup parent) {
        this.parent = parent;
    }

}
