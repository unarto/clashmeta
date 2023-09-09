## Clash Meta for Android

A Graphical user interface of [Clash.Meta](https://github.com/MetaCubeX/Clash.Meta) for Android

### Feature

Feature of [Clash.Meta](https://github.com/MetaCubeX/Clash.Meta)

[<img src="https://fdroid.gitlab.io/artwork/badge/get-it-on.png"
     alt="Get it on F-Droid"
     height="80">](https://f-droid.org/packages/com.github.metacubex.clash.meta/)

### Requirement

- Android 5.0+ (minimum)
- Android 7.0+ (recommend)
- `armeabi-v7a` , `arm64-v8a`, `x86` or `x86_64` Architecture

### Build

1. Update submodules

   ```bash
   git submodule update --init --recursive
   ```

2. Install **OpenJDK 11**, **Android SDK**, **CMake** and **Golang**

3. Create `local.properties` in project root with

   ```properties
   sdk.dir=/path/to/android-sdk
   ```

4. Create `signing.properties` in project root with

   ```properties
   keystore.path=/path/to/keystore/file
   keystore.password=<key store password>
   key.alias=<key alias>
   key.password=<key password>
   ```

5. Build

   ```bash
   ./gradlew app:assembleMeta-AlphaRelease
   ```
### Some features that are unavailable because they are somehow infeasible in principle:

1. Hysteria's FakeTCP mode: Superuser privilege, i.e. Root privilege, is required to run FakeTCP mode, which is currently unavailable in Clash Meta for Android.

2. Tunnels function: Because the TUN stack used by Clash Meta for Android is not Sing-TUN but Tun2Socket, so it can't be used.

3. Process Rules: After version 2.8.5, the implementation of getting process names has been changed, so it can't be used on Clash Meta for Android due to insufficient privileges to get the corresponding package names.

4. GeoXUrl parameter: Because Clash Meta uses Geo database, there is no corresponding implementation of this function in the original Clash for Android, if we switch the kernel directly, it will lead to the Geo database can not be saved locally, so we added an additional implementation of saving Geo database, which also leads to the inability to set the GeoXUrl parameter.

You can install Box4Magisk module to use the above function after getting Superuser privilege, i.e. Root privilege.
