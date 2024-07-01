package m.client.ide.morpheus.framework.template;

import com.android.tools.idea.wizard.template.AssetNameConverter;
import com.google.common.base.CaseFormat;
import com.google.common.io.Resources;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class MorpheusTemplateHelper {
    private static final String LICENSE_ID = "%LICENSE%";
    private static final String APP_NAME = "%APP_NAME%";
    private static final String CPU_TYPES = "%CPU_TYPES%";
    private static final String START_PAGE = "%START_PAGE%";
    private static final String TEMPLATE_TYPE = "%TEMPLATE_TYPE%";

    @NotNull
    public static List<String> getLicenses() {
        ArrayList<String> licenses = new ArrayList<>();

        licenses.add("mcore.edu");

        return licenses;
    }

    public static enum TemplateType {
        JAVA_OBJC_EMPTY(0), other(1);

        private final int value;

        TemplateType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static TemplateType valueOf(int value) {
            switch (value) {
                case 1:
                    return TemplateType.other;
                default:
                    return TemplateType.JAVA_OBJC_EMPTY;
            }
        }

        @Contract(pure = true)
        @Override
        public @NotNull String toString() {
            switch (this) {
                case other:
                    return "other";
                default:
                    return "java-objc-empty";
            }
        }
    }

    public static String[] getCommands(String morpheus, String projectName, @NotNull MorpheusAppTemplateData templateData) {
        String[] commands;
        switch (templateData.getType()) {
            case other:
            default:
                commands = new String[] { morpheus, "create", projectName, "-c",
                        "{" +
                            "\"license\": \"" + templateData.getApplicationId() + "\"," +
                            "\"androidAppName\": \"" + templateData.getAndroidAppName() + "\"," +
                            "\"iosAppName\": \"" + templateData.getAndroidAppName() + "\"," +
//                            "\"appName\": \"" + templateData.getAndroidAppName() + "\"," +
                            "\"cpus\":[" + makeCpuStrings(templateData.getCpus()) + "]," +
                            "\"startpage\": \"\"," +
                            "\"libraries\":[" +
                                "{\"name\":\"@morpheus/core\",\"version\":\"0.0.1\"}," +
                                "{\"name\":\"@morpheus/addon-db\",\"version\":\"0.0.1\"}]," +
                            "\"template\": \"" + templateData.getTemplateName()/*java-objc-empty*/ + "\"" +
//                            "\"icons\": {" +  // (선택) + 하위 값도 선택임.
//                                "\"android\": [{" +
//                                    "\"density\": \"hdpi\", \"imgPath\": \"/temp/icons/mipmap-hdpi/ic_launcher.png\"}," +
//                                    "{\"densit\": \"mdpi\", \"imgPath\": \"/temp/icons/android/mipmap-mdpi/ic_launcher.png\"}, " +
//                                    "{\"density\": \"xhdpi\", \"imgPath\": \"/temp/icons/android/mipmap-xhdpi/ic_launcher.png\"}, " +
//                                    "{\"density\": \"xxhdpi\", \"imgPath\": \"/temp/icons/android/mipmap-xxhdpi/ic_launcher.png\"}], " +
//                                "\"ios\": \"/temp/icons/ios/1024.png\"" +
//                            "}" +
                        "}"};
        }
        return commands;
    }

    private static final String[] CPUS = { "arm64-v8a", "armeabi-v7a", "x86", "x86_64" };
    private static String makeCpuStrings(@NotNull List<Boolean> cpus) {
//        \"arm64-v8a\"," + "\"armeabi-v7a\",\"x86\",\"x86_64\"
        String cpuString = "";
        for(int i=0; i<cpus.size(); i++) {
            Boolean cpu = cpus.get(i);
            if(cpu) {
                cpuString += '\"' + CPUS[i] + '\"';
            }
            if(i+1 < cpus.size()) {
                Boolean nextCpu = cpus.get(i + 1);
                if(nextCpu) {
                    cpuString += ',';
                }
            }
        }

        return cpuString;
    }

    @NotNull
    public static final String activityToLayout(@NotNull String activityName, @Nullable String layoutName) {
        Intrinsics.checkNotNullParameter(activityName, "activityName");
        return ((CharSequence)activityName).length() > 0 ? (new AssetNameConverter(AssetNameConverter.Type.ACTIVITY, activityName)).overrideLayoutPrefix(layoutName).getValue(AssetNameConverter.Type.LAYOUT) : "";
    }

    // $FF: synthetic method
    public static String activityToLayoutDefault(String var0, String var1, int var2, Object var3) {
        if ((var2 & 2) != 0) {
            var1 = null;
        }

        return activityToLayout(var0, var1);
    }

    @NotNull
    public static final String fragmentToLayout(@NotNull String fragmentName, @Nullable String layoutName) {
        Intrinsics.checkNotNullParameter(fragmentName, "fragmentName");
        String var2;
        if (((CharSequence)fragmentName).length() > 0) {
            AssetNameConverter var10000 = new AssetNameConverter(AssetNameConverter.Type.FRAGMENT, fragmentName);
            String var10001 = layoutName;
            if (layoutName == null) {
                var10001 = "fragment";
            }

            var2 = var10000.overrideLayoutPrefix(var10001).getValue(AssetNameConverter.Type.LAYOUT);
        } else {
            var2 = "";
        }

        return var2;
    }

    // $FF: synthetic method
    public static String fragmentToLayout$default(String var0, String var1, int var2, Object var3) {
        if ((var2 & 2) != 0) {
            var1 = null;
        }

        return fragmentToLayout(var0, var1);
    }

    @NotNull
    public static final String classToResource(@NotNull String name) {
        Intrinsics.checkNotNullParameter(name, "name");
        return ((CharSequence)name).length() > 0 ? (new AssetNameConverter(AssetNameConverter.Type.CLASS_NAME, name)).getValue(AssetNameConverter.Type.RESOURCE) : "";
    }

    @NotNull
    public static final String toUpperCamelCase(@NotNull String string) {
        Intrinsics.checkNotNullParameter(string, "string");
        if (string.length() <= 1) {
            return string;
        } else {
            int var1 = string.length();
            StringBuilder var2 = new StringBuilder(var1);
            StringBuilder $this$toUpperCamelCase_u24lambda_u2d1 = var2;
            char previous = ' ';
            CharSequence $this$forEach$iv = (CharSequence)string;
            CharSequence var8 = $this$forEach$iv;

            char element$iv;
            for(int var9 = 0; var9 < var8.length(); previous = element$iv) {
                element$iv = var8.charAt(var9);
                ++var9;
                if (element$iv != '_') {
                    if (((CharSequence)$this$toUpperCamelCase_u24lambda_u2d1).length() == 0) {
                        $this$toUpperCamelCase_u24lambda_u2d1.append(Character.toUpperCase(element$iv));
                    } else if (Character.isUpperCase(StringsKt.last((CharSequence)$this$toUpperCamelCase_u24lambda_u2d1))) {
                        $this$toUpperCamelCase_u24lambda_u2d1.append(Character.toLowerCase(element$iv));
                    } else if (previous == '_') {
                        $this$toUpperCamelCase_u24lambda_u2d1.append(Character.toUpperCase(element$iv));
                    } else if (Character.isUpperCase(previous)) {
                        $this$toUpperCamelCase_u24lambda_u2d1.append(Character.toLowerCase(element$iv));
                    } else {
                        $this$toUpperCamelCase_u24lambda_u2d1.append(element$iv);
                    }
                }
            }

            String var10000 = var2.toString();
            Intrinsics.checkNotNullExpressionValue(var10000, "StringBuilder(capacity).…builderAction).toString()");
            return var10000;
        }
    }

    @NotNull
    public static final String camelCaseToUnderlines(@NotNull String string) {
        Intrinsics.checkNotNullParameter(string, "string");
        String var10000 = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, string);
        Intrinsics.checkNotNullExpressionValue(var10000, "UPPER_CAMEL.to(CaseForma…LOWER_UNDERSCORE, string)");
        return var10000;
    }

    @NotNull
    public static final String underscoreToCamelCase(@NotNull String string) {
        Intrinsics.checkNotNullParameter(string, "string");
        String var10000 = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, string);
        Intrinsics.checkNotNullExpressionValue(var10000, "LOWER_UNDERSCORE.to(Case…rmat.UPPER_CAMEL, string)");
        return var10000;
    }

    @NotNull
    public static final String underscoreToLowerCamelCase(@NotNull String string) {
        Intrinsics.checkNotNullParameter(string, "string");
        String var10000 = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, string);
        Intrinsics.checkNotNullExpressionValue(var10000, "LOWER_UNDERSCORE.to(Case…rmat.LOWER_CAMEL, string)");
        return var10000;
    }

    @NotNull
    public static final String escapeKotlinIdentifier(@NotNull String identifier) {
        Intrinsics.checkNotNullParameter(identifier, "identifier");
        CharSequence var10000 = (CharSequence)identifier;
        String[] var1 = new String[]{"."};
        return CollectionsKt.joinToString((Iterable)StringsKt.split(var10000, var1, false, 0), (CharSequence)".", (CharSequence)null, (CharSequence)null, 0, (CharSequence)null, (Function1)null);
    }

    @Nullable
    public static final String extractClassName(@NotNull String string) {
        Intrinsics.checkNotNullParameter(string, "string");
        String $this$dropWhile$iv = string;
        int var4 = 0;
        int var5 = string.length();

        int it;
        String var10000;
        while(true) {
            if (var4 < var5) {
                int index$iv = var4++;
                it = $this$dropWhile$iv.charAt(index$iv);
                if (!Character.isJavaIdentifierStart(Character.toUpperCase((char)it))) {
                    continue;
                }

                var10000 = $this$dropWhile$iv.substring(index$iv);
                Intrinsics.checkNotNullExpressionValue(var10000, "this as java.lang.String).substring(startIndex)");
                break;
            }

            var10000 = "";
            break;
        }

        $this$dropWhile$iv = var10000;
        CharSequence $this$filterTo$iv$iv = (CharSequence)$this$dropWhile$iv;
        Appendable destination$iv$iv = (Appendable)(new StringBuilder());
        it = 0;
        int var18 = $this$filterTo$iv$iv.length();

        while(it < var18) {
            int index$iv$iv = it++;
            char element$iv$iv = $this$filterTo$iv$iv.charAt(index$iv$iv);
            if (Character.isJavaIdentifierPart(element$iv$iv)) {
                try {
                    destination$iv$iv.append(element$iv$iv);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        var10000 = ((StringBuilder)destination$iv$iv).toString();
        Intrinsics.checkNotNullExpressionValue(var10000, "filterTo(StringBuilder(), predicate).toString()");
        String javaIdentifier = var10000;
        if (StringsKt.isBlank((CharSequence)javaIdentifier)) {
            return null;
        } else {
            char var13 = Character.toUpperCase(StringsKt.first((CharSequence)javaIdentifier));
            var10000 = javaIdentifier.substring(1);
            Intrinsics.checkNotNullExpressionValue(var10000, "this as java.lang.String).substring(startIndex)");
            String var14 = var10000;
            return var13 + var14;
        }
    }

    @NotNull
    public static final String layoutToActivity(@NotNull String name) {
        Intrinsics.checkNotNullParameter(name, "name");
        return (new AssetNameConverter(AssetNameConverter.Type.LAYOUT, name)).getValue(AssetNameConverter.Type.ACTIVITY);
    }

    @NotNull
    public static final String layoutToFragment(@NotNull String name) {
        Intrinsics.checkNotNullParameter(name, "name");
        return (new AssetNameConverter(AssetNameConverter.Type.LAYOUT, name)).getValue(AssetNameConverter.Type.FRAGMENT);
    }

    @NotNull
    public static final String getMaterialComponentName(@NotNull String mavenCoordinate, boolean useAndroidX) {
        Intrinsics.checkNotNullParameter(mavenCoordinate, "mavenCoordinate");
        if (!useAndroidX) {
            return mavenCoordinate;
        } else {
            Pair[] var3 = new Pair[]{TuplesKt.to("android.arch.core.util.Function", "androidx.arch.core.util.Function"), TuplesKt.to("android.arch.lifecycle.LiveData", "androidx.lifecycle.LiveData"), TuplesKt.to("android.arch.lifecycle.MutableLiveData", "androidx.lifecycle.MutableLiveData"), TuplesKt.to("android.arch.lifecycle.Observer", "androidx.lifecycle.Observer"), TuplesKt.to("android.arch.lifecycle.Transformations", "androidx.lifecycle.Transformations"), TuplesKt.to("android.arch.lifecycle.ViewModel", "androidx.lifecycle.ViewModel"), TuplesKt.to("android.arch.lifecycle.ViewModelProvider", "androidx.lifecycle.ViewModelProvider"), TuplesKt.to("android.support.annotation.NonNull", "androidx.annotation.NonNull"), TuplesKt.to("android.support.annotation.Nullable", "androidx.annotation.Nullable"), TuplesKt.to("android.support.annotation.StringRes", "androidx.annotation.StringRes"), TuplesKt.to("android.support.constraint.ConstraintLayout", "androidx.constraintlayout.widget.ConstraintLayout"), TuplesKt.to("android.support.constraint.Guideline", "androidx.constraintlayout.widget.Guideline"), TuplesKt.to("android.support.design.widget.AppBarLayout", "com.google.android.material.appbar.AppBarLayout"), TuplesKt.to("android.support.design.widget.BottomNavigationView", "com.google.android.material.bottomnavigation.BottomNavigationView"), TuplesKt.to("android.support.design.widget.BottomSheetDialog", "com.google.android.material.bottomsheet.BottomSheetDialog"), TuplesKt.to("android.support.design.widget.CollapsingToolbarLayout", "com.google.android.material.appbar.CollapsingToolbarLayout"), TuplesKt.to("android.support.design.widget.CoordinatorLayout", "androidx.coordinatorlayout.widget.CoordinatorLayout"), TuplesKt.to("android.support.design.widget.FloatingActionButton", "com.google.android.material.floatingactionbutton.FloatingActionButton"), TuplesKt.to("android.support.design.widget.NavigationView", "com.google.android.material.navigation.NavigationView"), TuplesKt.to("android.support.design.widget.Snackbar", "com.google.android.material.snackbar.Snackbar"), TuplesKt.to("android.support.design.widget.TabLayout", "com.google.android.material.tabs.TabLayout"), TuplesKt.to("android.support.design.widget.TabLayout", "com.google.android.material.tabs.TabLayout"), TuplesKt.to("android.support.test.InstrumentationRegistry", "androidx.test.platform.app.InstrumentationRegistry"), TuplesKt.to("android.support.test.runner.AndroidJUnit4", "androidx.test.ext.junit.runners.AndroidJUnit4"), TuplesKt.to("android.support.test.runner.AndroidJUnitRunner", "androidx.test.runner.AndroidJUnitRunner"), TuplesKt.to("android.support.v17.leanback.app.BackgroundManager", "androidx.leanback.app.BackgroundManager"), TuplesKt.to("android.support.v17.leanback.app.BrowseSupportFragment", "androidx.leanback.app.BrowseSupportFragment"), TuplesKt.to("android.support.v17.leanback.app.DetailsSupportFragment", "androidx.leanback.app.DetailsSupportFragment"), TuplesKt.to("android.support.v17.leanback.app.DetailsSupportFragmentBackgroundController", "androidx.leanback.app.DetailsSupportFragmentBackgroundController"), TuplesKt.to("android.support.v17.leanback.app.ErrorSupportFragment", "androidx.leanback.app.ErrorSupportFragment"), TuplesKt.to("android.support.v17.leanback.app.VideoSupportFragment", "androidx.leanback.app.VideoSupportFragment"), TuplesKt.to("android.support.v17.leanback.app.VideoSupportFragmentGlueHost", "androidx.leanback.app.VideoSupportFragmentGlueHost"), TuplesKt.to("android.support.v17.leanback.media.MediaPlayerAdapter", "androidx.leanback.media.MediaPlayerAdapter"), TuplesKt.to("android.support.v17.leanback.media.PlaybackTransportControlGlue", "androidx.leanback.media.PlaybackTransportControlGlue"), TuplesKt.to("android.support.v17.leanback.widget.AbstractDetailsDescriptionPresenter", "androidx.leanback.widget.AbstractDetailsDescriptionPresenter"), TuplesKt.to("android.support.v17.leanback.widget.Action", "androidx.leanback.widget.Action"), TuplesKt.to("android.support.v17.leanback.widget.ArrayObjectAdapter", "androidx.leanback.widget.ArrayObjectAdapter"), TuplesKt.to("android.support.v17.leanback.widget.ClassPresenterSelector", "androidx.leanback.widget.ClassPresenterSelector"), TuplesKt.to("android.support.v17.leanback.widget.DetailsOverviewRow", "androidx.leanback.widget.DetailsOverviewRow"), TuplesKt.to("android.support.v17.leanback.widget.FullWidthDetailsOverviewRowPresenter", "androidx.leanback.widget.FullWidthDetailsOverviewRowPresenter"), TuplesKt.to("android.support.v17.leanback.widget.FullWidthDetailsOverviewSharedElementHelper", "androidx.leanback.widget.FullWidthDetailsOverviewSharedElementHelper"), TuplesKt.to("android.support.v17.leanback.widget.HeaderItem", "androidx.leanback.widget.HeaderItem"), TuplesKt.to("android.support.v17.leanback.widget.ImageCardView", "androidx.leanback.widget.ImageCardView"), TuplesKt.to("android.support.v17.leanback.widget.ListRow", "androidx.leanback.widget.ListRow"), TuplesKt.to("android.support.v17.leanback.widget.ListRowPresenter", "androidx.leanback.widget.ListRowPresenter"), TuplesKt.to("android.support.v17.leanback.widget.OnActionClickedListener", "androidx.leanback.widget.OnActionClickedListener"), TuplesKt.to("android.support.v17.leanback.widget.OnItemViewClickedListener", "androidx.leanback.widget.OnItemViewClickedListener"), TuplesKt.to("android.support.v17.leanback.widget.OnItemViewSelectedListener", "androidx.leanback.widget.OnItemViewSelectedListener"), TuplesKt.to("android.support.v17.leanback.widget.PlaybackControlsRow", "androidx.leanback.widget.PlaybackControlsRow"), TuplesKt.to("android.support.v17.leanback.widget.Presenter", "androidx.leanback.widget.Presenter"), TuplesKt.to("android.support.v17.leanback.widget.Row", "androidx.leanback.widget.Row"), TuplesKt.to("android.support.v17.leanback.widget.RowPresenter", "androidx.leanback.widget.RowPresenter"), TuplesKt.to("android.support.v4.app.ActivityOptionsCompat", "androidx.core.app.ActivityOptionsCompat"), TuplesKt.to("android.support.v4.app.Fragment", "androidx.fragment.app.Fragment"), TuplesKt.to("android.support.v4.app.FragmentActivity", "androidx.fragment.app.FragmentActivity"), TuplesKt.to("android.support.v4.app.FragmentManager", "androidx.fragment.app.FragmentManager"), TuplesKt.to("android.support.v4.app.FragmentPagerAdapter", "androidx.fragment.app.FragmentPagerAdapter"), TuplesKt.to("android.support.v4.app.NotificationCompat", "androidx.core.app.NotificationCompat"), TuplesKt.to("android.support.v4.app.NotificationCompat.CarExtender", "androidx.core.app.NotificationCompat.CarExtender"), TuplesKt.to("android.support.v4.app.NotificationCompat.CarExtender.UnreadConversation", "androidx.core.app.NotificationCompat.CarExtender.UnreadConversation"), TuplesKt.to("android.support.v4.app.NotificationManagerCompat", "androidx.core.app.NotificationManagerCompat"), TuplesKt.to("android.support.v4.app.RemoteInput", "androidx.core.app.RemoteInput"), TuplesKt.to("android.support.v4.content.ContextCompat", "androidx.core.content.ContextCompat"), TuplesKt.to("android.support.v4.media.MediaBrowserServiceCompat", "androidx.media.MediaBrowserServiceCompat"), TuplesKt.to("android.support.v4.view.ViewPager", "androidx.viewpager.widget.ViewPager"), TuplesKt.to("android.support.v4.widget.DrawerLayout", "androidx.drawerlayout.widget.DrawerLayout"), TuplesKt.to("android.support.v4.widget.NestedScrollView", "androidx.core.widget.NestedScrollView"), TuplesKt.to("android.support.v7.app.ActionBar", "androidx.appcompat.app.ActionBar"), TuplesKt.to("android.support.v7.app.AppCompatActivity", "androidx.appcompat.app.AppCompatActivity"), TuplesKt.to("android.support.v7.graphics.Palette", "androidx.palette.graphics.Palette"), TuplesKt.to("android.support.v7.widget.GridLayoutManager", "androidx.recyclerview.widget.GridLayoutManager"), TuplesKt.to("android.support.v7.widget.LinearLayoutManager", "androidx.recyclerview.widget.LinearLayoutManager"), TuplesKt.to("android.support.v7.widget.RecyclerView", "androidx.recyclerview.widget.RecyclerView"), TuplesKt.to("android.support.v7.widget.Toolbar", "androidx.appcompat.widget.Toolbar"), TuplesKt.to("android.support.wear.widget.BoxInsetLayout", "androidx.wear.widget.BoxInsetLayout"), TuplesKt.to("android.support.wear.widget.SwipeDismissFrameLayout", "androidx.wear.widget.SwipeDismissFrameLayout")};
            Map mapping = MapsKt.mapOf(var3);
            String androidXCoordinate = (String)mapping.get(mavenCoordinate);
            if (androidXCoordinate == null) {
                String var6 = "\nUnknown dependency.\nIt is recommended to avoid this method in favor of using androidX dependencies directly and setting androidX TemplateConstraint to true\n";
                throw new IllegalArgumentException(var6.toString());
            } else {
                return androidXCoordinate;
            }
        }
    }

    @NotNull
    public static final String extractLetters(@NotNull String string) {
        Intrinsics.checkNotNullParameter(string, "string");
        CharSequence $this$filterTo$iv$iv = (CharSequence)string;
        Appendable destination$iv$iv = (Appendable)(new StringBuilder());
        int var6 = 0;
        int var7 = $this$filterTo$iv$iv.length();

        while(var6 < var7) {
            int index$iv$iv = var6++;
            char element$iv$iv = $this$filterTo$iv$iv.charAt(index$iv$iv);
            if (Character.isLetter(element$iv$iv)) {
                try {
                    destination$iv$iv.append(element$iv$iv);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        String var10000 = ((StringBuilder)destination$iv$iv).toString();
        Intrinsics.checkNotNullExpressionValue(var10000, "filterTo(StringBuilder(), predicate).toString()");
        return var10000;
    }

    @NotNull
    public static final URL findResource(@NotNull Class contextClass, @NotNull File from) {
        Intrinsics.checkNotNullParameter(contextClass, "contextClass");
        Intrinsics.checkNotNullParameter(from, "from");
        String var10001 = from.getPath();
        Intrinsics.checkNotNullExpressionValue(var10001, "from.path");
        URL var10000 = Resources.getResource(contextClass, "/" + StringsKt.replace(var10001, '\\', '/', false));
        Intrinsics.checkNotNullExpressionValue(var10000, "getResource(contextClass…ath.replace('\\\\', '/')}\")");
        return var10000;
    }

    @NotNull
    public static final String packageNameToDomain(@NotNull String packageName) {
        Intrinsics.checkNotNullParameter(packageName, "packageName");
        CharSequence var10000 = (CharSequence)packageName;
        String[] var2 = new String[]{"."};
        Iterable $this$filter$iv = (Iterable)StringsKt.split(var10000, var2, false, 0);
        Collection destination$iv$iv = (Collection)(new ArrayList());
        Iterator var7 = $this$filter$iv.iterator();

        Object element$iv$iv;
        String it;
        boolean var10;
        while(var7.hasNext()) {
            element$iv$iv = var7.next();
            it = (String)element$iv$iv;
            var10 = false;
            destination$iv$iv.add(StringsKt.trim((CharSequence)it).toString());
        }

        $this$filter$iv = (Iterable)((List)destination$iv$iv);
        destination$iv$iv = (Collection)(new ArrayList());
        var7 = $this$filter$iv.iterator();

        while(var7.hasNext()) {
            element$iv$iv = var7.next();
            it = (String)element$iv$iv;
            var10 = false;
            if (((CharSequence)it).length() > 0) {
                destination$iv$iv.add(element$iv$iv);
            }
        }

        String domain = CollectionsKt.joinToString((Iterable)CollectionsKt.reversed((Iterable)((List)destination$iv$iv)), (CharSequence)".", (CharSequence)null, (CharSequence)null, 0, (CharSequence)null, (Function1)null);
        return ((CharSequence)domain).length() == 0 ? "example.com" : domain;
    }

    private static final List<String> kotlinKeywords;

    static {
        String[] var0 = new String[]{"package", "as", "typealias", "class", "this", "super", "val", "var", "fun", "for", "null", "true", "false", "is", "in", "throw", "return", "break", "continue", "object", "if", "try", "else", "while", "do", "when", "interface", "typeof"};
        kotlinKeywords = CollectionsKt.listOf(var0);
    }
}
