# BlurBehind

**A library that is not limited to the internals of the application and is used to achieve a background effect similar to ios frosted glass.**<br>
**一个不局限于应用内部的用于实现类似于ios毛玻璃背景效果的库。**

## 特色

1. 没有Activity的应用也能实现背景毛玻璃的效果
2. 可以实现全局性的背景毛玻璃的效果，比如使用壁纸主题的应用，可以实现桌面毛玻璃的效果；

## BlurBehind提供3个API

默认方式模糊背景

```Java
BlurBehind.with(Context context, ImageView view);
```

设置模糊背景的大小

```Java
BlurBehind.with(Context contex).fromScreenshot(int width, int height0).into(ImageView view);
```

自定义模糊背景

```java
BlurBehind.with(Context contex).fromScreenshot(IScreenshot iSurfaceControl).into(ImageView view);
```

