package m.client.ide.morpheus.framework.template;

import kotlin.jvm.internal.Intrinsics;
import m.client.ide.morpheus.framework.cli.jsonParam.LibraryParam;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MorpheusAppTemplateData {
    private @NotNull String androidAppName;
    private String androidPackage;
    private String iOSAppName;
    private String iOSBundleId;
    private @NotNull String applicationId; // 라이센스 app id
    private @NotNull List<Boolean> cpus;

    private List<LibraryParam> libraries;

    private MorpheusTemplateHelper.TemplateType type;

    public MorpheusAppTemplateData(@NotNull String applicationId,
                                   @NotNull String androidAppName, @NotNull String androidPackageName,
                                   @NotNull String iOSAppName, @NotNull String iOSBundleId,
                                   @NotNull List<Boolean> cpus, @Nullable List<LibraryParam> libraries, MorpheusTemplateHelper.TemplateType type) {
        Intrinsics.checkNotNullParameter(androidAppName, "myApp");
        Intrinsics.checkNotNullParameter(applicationId, "license");
        Intrinsics.checkNotNullParameter(cpus, "cpus");
        this.applicationId = applicationId;
        this.androidAppName = androidAppName;
        this.androidPackage = androidPackageName;
        this.iOSAppName = iOSAppName;
        this.iOSBundleId = iOSBundleId;
        this.cpus = cpus;
        this.libraries = libraries;
        this.type = type;
    }


    @NotNull
    public final MorpheusAppTemplateData copy(@NotNull String license,
                                              @NotNull String androidAppName, @NotNull String androidPackage,
                                              @NotNull String iOSAppName, @NotNull String iOSBundleId,
                                              @NotNull List<Boolean> cpus, @Nullable List<LibraryParam> libraries, MorpheusTemplateHelper.TemplateType type) {
        Intrinsics.checkNotNullParameter(androidAppName, "myApp");
        Intrinsics.checkNotNullParameter(license, "license");
        Intrinsics.checkNotNullParameter(cpus, "cpus");
        return new MorpheusAppTemplateData(license, androidAppName, androidPackage, iOSAppName, iOSBundleId, cpus, libraries, type);
    }

    @NotNull
    public String toString() {
        return "MorpheusAppTemplateData(License=" + this.applicationId +
                ", AppName:" + androidAppName + ", Package:" + androidPackage +
                ", templateType=" + this.type  + ")";
    }

    public int hashCode() {
        int result = this.applicationId.hashCode();
        result = result * 31 + this.androidAppName.hashCode();
        result = result * 31 + this.cpus.hashCode();
        result = result * 31 + this.libraries.hashCode();
        result = result * 31 + this.type.hashCode();
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof MorpheusAppTemplateData)) {
            return false;
        } else {
            MorpheusAppTemplateData var2 = (MorpheusAppTemplateData)other;
            if (!Intrinsics.areEqual(this.applicationId, var2.applicationId)) {
                return false;
            } else if (!Intrinsics.areEqual(this.androidAppName, var2.androidAppName)) {
                return false;
            } else if (!Intrinsics.areEqual(this.androidPackage, var2.androidPackage)) {
                return false;
            } else if (!Intrinsics.areEqual(this.iOSAppName, var2.iOSAppName)) {
                return false;
            } else if (!Intrinsics.areEqual(this.iOSBundleId, var2.iOSBundleId)) {
                return false;
            } else if (!Intrinsics.areEqual(this.cpus, var2.cpus)) {
                return false;
            } else if (!Intrinsics.areEqual(this.libraries, var2.libraries)) {
                return false;
            } else {
                return this.type == var2.type;
            }
        }
    }

    public @NotNull String getAndroidAppName() {
        return androidAppName;
    }

    public @NotNull String getApplicationId() {
        return applicationId;
    }

    public List<Boolean> getCpus() {
        return cpus;
    }

    public List<LibraryParam> getLibraries() {
        return libraries;
    }

    public MorpheusTemplateHelper.TemplateType getType() {
        return type;
    }

    public void setAndroidAppName(String androidAppName) {
        this.androidAppName = androidAppName;
    }

    public String getAndroidPackage() {
        return androidPackage;
    }

    public void setAndroidPackage(String androidPackage) {
        this.androidPackage = androidPackage;
    }

    public String getiOSAppName() {
        return iOSAppName;
    }

    public void setiOSAppName(String iOSAppName) {
        this.iOSAppName = iOSAppName;
    }

    public String getiOSBundleId() {
        return iOSBundleId;
    }

    public void setiOSBundleId(String iOSBundleId) {
        this.iOSBundleId = iOSBundleId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public void setCpus(List<Boolean> cpus) {
        this.cpus = cpus;
    }

    public void setLibraries(List<LibraryParam> libraries) {
        this.libraries = libraries;
    }

    public void setType(MorpheusTemplateHelper.TemplateType type) {
        this.type = type;
    }

    public String getTemplateName() {
        return this.type.toString();
    }
}
