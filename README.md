## Contents
- [Brief](#brief)
- [Components](#components)
  - [grpc](#grpc)
  - [maps](#maps)
  - [sensors](#sensors)
  - [Software](#software)
- [Signing](#signing)
- [Gotchas](#gotchas)
- [References](#references)

### [Brief](#brief)

Trac - demo with maps, location \& sensors using grpc.

### [Components](#components)

### [Signing](#signing)

```
keytool -genkeypair -dname "cn=rcs, ou=trac, o=m0v, c=IN" -alias rcsm0v -keypass <keypass> -keystore ./m0v.keystore -storepass <storepass> -validity 180
Warning:  Different store and key passwords not supported for PKCS12 KeyStores. Ignoring user-specified -keypass value.

keytool -genkeypair -dname "cn=rcs, ou=trac, o=m0v, c=IN" -alias trac -keypass <keypass> -keystore ./m0v.jks -storepass <storepass> -validity 180 -keyalg RSA -storetype JKS

keytool -list -v -keystore m0v.jks
```

### [Gotchas](#gotchas)

Using:
```
~/dev/android/platform-tools/adb -d logcat org.trac.app:V com.google.android.gms:V *:S
```
May come across this:
```
03-25 16:46:28.976 13578 13578 E org.trac.app: Not starting debugger since process cannot load the jdwp agent.
```
Need to unsilence everything like this (remove `*:S`):
```
~/dev/android/platform-tools/adb -d logcat org.trac.app:V
```
To see this:
```
03-25 16:44:18.889 13384 13384 E AndroidRuntime: java.lang.RuntimeException: Unable to start activity ComponentInfo{org.trac.app/org.trac.app.Trac}: java.lang.IllegalStateException: You need to use a Them
e.AppCompat theme (or descendant) with this activity.                                                                                                                                                       
03-25 16:44:18.889 13384 13384 E AndroidRuntime:        at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:3300)                                                                     
03-25 16:44:18.889 13384 13384 E AndroidRuntime:        at android.app.ActivityThread.handleLaunchActivity(ActivityThread.java:3484)                                               
03-25 16:44:18.889 13384 13384 E AndroidRuntime:        at android.app.servertransaction.LaunchActivityItem.execute(LaunchActivityItem.java:86)                                                           
03-25 16:44:18.889 13384 13384 E AndroidRuntime:        at android.app.servertransaction.TransactionExecutor.executeCallbacks(TransactionExecutor.java:108)                                               
03-25 16:44:18.889 13384 13384 E AndroidRuntime:        at android.app.servertransaction.TransactionExecutor.execute(TransactionExecutor.java:68)                                                         
03-25 16:44:18.889 13384 13384 E AndroidRuntime:        at android.app.ActivityThread$H.handleMessage(ActivityThread.java:2123)                                                                           
03-25 16:44:18.889 13384 13384 E AndroidRuntime:        at android.os.Handler.dispatchMessage(Handler.java:109)                                                                                           
03-25 16:44:18.889 13384 13384 E AndroidRuntime:        at android.os.Looper.loop(Looper.java:207)                                                                                                        
03-25 16:44:18.889 13384 13384 E AndroidRuntime:        at android.app.ActivityThread.main(ActivityThread.java:7470)                                                                                      
03-25 16:44:18.889 13384 13384 E AndroidRuntime:        at java.lang.reflect.Method.invoke(Native Method)                                                                                                 
03-25 16:44:18.889 13384 13384 E AndroidRuntime:        at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:524)
03-25 16:44:18.889 13384 13384 E AndroidRuntime:        at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:958)                                                                                   
03-25 16:44:18.889 13384 13384 E AndroidRuntime: Caused by: java.lang.IllegalStateException: You need to use a Theme.AppCompat theme (or descendant) with this activity.                                  
03-25 16:44:18.889 13384 13384 E AndroidRuntime:        at android.support.v7.app.AppCompatDelegateImpl.createSubDecor(AppCompatDelegateImpl.java:555)                                                    
03-25 16:44:18.889 13384 13384 E AndroidRuntime:        at android.support.v7.app.AppCompatDelegateImpl.ensureSubDecor(AppCompatDelegateImpl.java:518)                                                    
03-25 16:44:18.889 13384 13384 E AndroidRuntime:        at android.support.v7.app.AppCompatDelegateImpl.setContentView(AppCompatDelegateImpl.java:466)                                                    
03-25 16:44:18.889 13384 13384 E AndroidRuntime:        at android.support.v7.app.AppCompatActivity.setContentView(AppCompatActivity.java:140)                                                            
03-25 16:44:18.889 13384 13384 E AndroidRuntime:        at org.trac.app.Trac.onCreate(Trac.java:26)                                                                                                       
03-25 16:44:18.889 13384 13384 E AndroidRuntime:        at android.app.Activity.performCreate(Activity.java:7436)                                                                                        
03-25 16:44:18.889 13384 13384 E AndroidRuntime:        at android.app.Activity.performCreate(Activity.java:7426)                                                                  
03-25 16:44:18.889 13384 13384 E AndroidRuntime:        at android.app.Instrumentation.callActivityOnCreate(Instrumentation.java:1286)
03-25 16:44:18.889 13384 13384 E AndroidRuntime:        at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:3279)
03-25 16:44:18.889 13384 13384 E AndroidRuntime:        ... 11 more                                                               
03-25 16:44:18.893  1155  1475 W ActivityManager:   finishTopCrashedActivityLocked Force finishing activity org.trac.app/.Trac              
```
Then add it something like this to `AndroidManifest.xml`:
```
    <application 
        ...
        android:theme="@style/AppTheme">
```
And the corresponding `app/src/main/res/values/styles.xml`


Using `com.google.protobuf:protobuf-gradle-plugin:0.8.5` may lead to `No such property: javaCompilerTask for class: com.android.build.gradle.internal.variant.TestVariantData`. Upgrade to 0.8.6 to solve.

### [References](#references)
+ [routeguide](https://github.com/grpc/grpc-java/examples/android/routeguide)
+ [MapWithMarker](https://github.com/googlesamples/android-samples/tutorials/MapWithMarker) 
+ [Wander](https://github.com/google-developer-training/android-advanced/Wander)
+ [SensorSurvey](https://github.com/google-developer-training/android-advanced/SensorSurvey)
+ [SensorSurvey](https://github.com/google-developer-training/android-advanced/TiltSpot)
+ [Gradle Signing Apk](https://stackoverflow.com/questions/18328730/how-to-create-a-release-signed-apk-file-using-gradle)
+ [Android support libs version](https://stackoverflow.com/questions/42374151/all-com-android-support-libraries-must-use-the-exact-same-version-specification)

### [Appendices](#appendices){#appendices}
