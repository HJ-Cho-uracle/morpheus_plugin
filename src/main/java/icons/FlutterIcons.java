package icons;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

public class FlutterIcons {
  private static Icon load(String path) {
    return IconLoader.getIcon(path, FlutterIcons.class);
  }

  public static final Icon Flutter_13 = load("/icons/flutter/flutter_13.png");
  public static final Icon Flutter_13_2x = load("/icons/flutter/flutter_13@2x.png");
  public static final Icon Flutter_64 = load("/icons/flutter/flutter_64.png");
  public static final Icon Flutter_64_2x = load("/icons/flutter/flutter_64@2x.png");
  public static final Icon Flutter = load("/icons/flutter/flutter.png");
  public static final Icon Flutter_2x = load("/icons/flutter/flutter@2x.png");
  public static final Icon Flutter_inspect = load("/icons/flutter/flutter_inspect.png");
  public static final Icon Flutter_test = load("/icons/flutter/flutter_test.png");
  public static final Icon Flutter_badge = load("/icons/flutter/flutter_badge.png");

  public static final Icon Phone = load("/icons/flutter/phone.png");
  public static final Icon Feedback = load("/icons/flutter/feedback.png");
  public static final Icon RefreshItems = load("/icons/flutter/refresh_items.png");

  public static final Icon Dart_16 = load("/icons/flutter/dart_16.svg");

  public static final Icon HotReload = load("/icons/flutter/hot-reload.png");
  public static final Icon HotRestart = load("/icons/flutter/hot-restart.png");

  public static final Icon HotReloadRun = load("/icons/flutter/reload_run.png");
  public static final Icon HotReloadDebug = load("/icons/flutter/reload_debug.png");

  public static final Icon DebugBanner = load("/icons/flutter/debugBanner.png");
  public static final Icon DebugPaint = load("/icons/flutter/debugPaint.png");
  public static final Icon RepaintRainbow = load("/icons/flutter/repaintRainbow.png");

  public static final Icon BazelRun = load("/icons/flutter/bazel_run.png");

  public static final Icon CustomClass = load("/icons/flutter/custom/class.png");
  public static final Icon CustomClassAbstract = load("/icons/flutter/custom/class_abstract.png");
  public static final Icon CustomFields = load("/icons/flutter/custom/fields.png");
  public static final Icon CustomInterface = load("/icons/flutter/custom/interface.png");
  public static final Icon CustomMethod = load("/icons/flutter/custom/method.png");
  public static final Icon CustomMethodAbstract = load("/icons/flutter/custom/method_abstract.png");
  public static final Icon CustomProperty = load("/icons/flutter/custom/property.png");
  public static final Icon CustomInfo = load("/icons/flutter/custom/info.png");

  public static final Icon AndroidStudioNewProject = load("/icons/flutter/template_new_project.png");
  public static final Icon AndroidStudioNewPackage = load("/icons/flutter/template_new_package.png");
  public static final Icon AndroidStudioNewPlugin = load("/icons/flutter/template_new_plugin.png");
  public static final Icon AndroidStudioNewModule = load("/icons/flutter/template_new_module.png");

  public static final Icon AttachDebugger = load("/icons/flutter/attachDebugger.png");

  // Flutter Inspector Widget Icons.
  public static final Icon Accessibility = load("/icons/flutter/inspector/balloonInformation.png");
  public static final Icon Animation = load("/icons/flutter/inspector/resume.png");
  public static final Icon Assets = load("/icons/flutter/inspector/any_type.png");
  public static final Icon Async = load("/icons/flutter/inspector/threads.png");
  public static final Icon Diagram = load("/icons/flutter/inspector/diagram.png");
  public static final Icon Input = load("/icons/flutter/inspector/renderer.png");
  public static final Icon Painting = load("/icons/flutter/inspector/colors.png");
  public static final Icon Scrollbar = load("/icons/flutter/inspector/scrollbar.png");
  public static final Icon Stack = load("/icons/flutter/inspector/value.png");
  public static final Icon Styling = load("/icons/flutter/inspector/atrule.png");
  public static final Icon Text = load("/icons/flutter/inspector/textArea.png");

  public static final Icon ExpandProperty = load("/icons/flutter/inspector/expand_property.png");
  public static final Icon CollapseProperty = load("/icons/flutter/inspector/collapse_property.png");

  // Flutter Outline Widget Icons.
  public static final Icon Column = load("/icons/flutter/preview/column.png");
  public static final Icon Padding = load("/icons/flutter/preview/padding.png");
  public static final Icon RemoveWidget = load("/icons/flutter/preview/remove_widget.png");
  public static final Icon Row = load("/icons/flutter/preview/row.png");
  public static final Icon Center = load("/icons/flutter/preview/center.png");
  public static final Icon Container = load("/icons/flutter/preview/container.png");
  public static final Icon Up = load("/icons/flutter/preview/up.png");
  public static final Icon Down = load("/icons/flutter/preview/down.png");
  public static final Icon ExtractMethod = load("/icons/flutter/preview/extract_method.png");

  // Flutter profiler
  public static final Icon Snapshot = load("/icons/flutter/profiler/snapshot_color_1x_24dp.png");
  public static final Icon ResetMemoryStats = load("/icons/flutter/profiler/reset_icon.png");

  public static class State {
    public static final Icon RedProgr = load("/icons/flutter/perf/RedProgr.png"); // 16x16
    public static final Icon RedProgr_1 = load("/icons/flutter/perf/RedProgr_1.png"); // 16x16
    public static final Icon RedProgr_2 = load("/icons/flutter/perf/RedProgr_2.png"); // 16x16
    public static final Icon RedProgr_3 = load("/icons/flutter/perf/RedProgr_3.png"); // 16x16
    public static final Icon RedProgr_4 = load("/icons/flutter/perf/RedProgr_4.png"); // 16x16
    public static final Icon RedProgr_5 = load("/icons/flutter/perf/RedProgr_5.png"); // 16x16
    public static final Icon RedProgr_6 = load("/icons/flutter/perf/RedProgr_6.png"); // 16x16
    public static final Icon RedProgr_7 = load("/icons/flutter/perf/RedProgr_7.png"); // 16x16
    public static final Icon RedProgr_8 = load("/icons/flutter/perf/RedProgr_8.png"); // 16x16

    public static final Icon YellowProgr = load("/icons/flutter/perf/YellowProgr.png"); // 16x16
    public static final Icon YellowProgr_1 = load("/icons/flutter/perf/YellowProgr_1.png"); // 16x16
    public static final Icon YellowProgr_2 = load("/icons/flutter/perf/YellowProgr_2.png"); // 16x16
    public static final Icon YellowProgr_3 = load("/icons/flutter/perf/YellowProgr_3.png"); // 16x16
    public static final Icon YellowProgr_4 = load("/icons/flutter/perf/YellowProgr_4.png"); // 16x16
    public static final Icon YellowProgr_5 = load("/icons/flutter/perf/YellowProgr_5.png"); // 16x16
    public static final Icon YellowProgr_6 = load("/icons/flutter/perf/YellowProgr_6.png"); // 16x16
    public static final Icon YellowProgr_7 = load("/icons/flutter/perf/YellowProgr_7.png"); // 16x16
    public static final Icon YellowProgr_8 = load("/icons/flutter/perf/YellowProgr_8.png"); // 16x16

    public static final Icon GreyProgr_1 = load("/icons/flutter/perf/GreyProgr_1.png"); // 16x16
    public static final Icon GreyProgr_2 = load("/icons/flutter/perf/GreyProgr_2.png"); // 16x16
    public static final Icon GreyProgr_3 = load("/icons/flutter/perf/GreyProgr_3.png"); // 16x16
    public static final Icon GreyProgr_4 = load("/icons/flutter/perf/GreyProgr_4.png"); // 16x16
    public static final Icon GreyProgr_5 = load("/icons/flutter/perf/GreyProgr_5.png"); // 16x16
    public static final Icon GreyProgr_6 = load("/icons/flutter/perf/GreyProgr_6.png"); // 16x16
    public static final Icon GreyProgr_7 = load("/icons/flutter/perf/GreyProgr_7.png"); // 16x16
    public static final Icon GreyProgr_8 = load("/icons/flutter/perf/GreyProgr_8.png"); // 16x16
  }
}
