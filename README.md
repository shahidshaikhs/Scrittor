# Scrittor - A simple note app 👋🏻

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

### Building the maintained version

Use Android Studio with **JDK 25** selected as the Gradle JVM. The project now uses Gradle **9.3.1** and Android Gradle Plugin **9.1.1**, so the old Java 25 incompatibility is resolved. Sync the project after opening it.

Install Android SDK Platform **37** and Build Tools **36.0.0** through SDK Manager if prompted. The app targets API **36** and supports Android **6.0 (API 23)** and newer; the minimum increased from API 21 for the updated billing library.

For a command-line build on macOS using Android Studio's bundled JDK:

```sh
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
./gradlew :app:assembleDebug :app:testDebugUnitTest :app:lintDebug :app:assembleRelease
```

Verified on 21 September 2026: debug and unsigned release builds pass, 39 tests pass, and lint has zero errors (314 legacy warnings remain).

The debug APK is generated at `app/build/outputs/apk/debug/app-debug.apk`. Release builds are unsigned. Google Drive sign-in needs OAuth configuration for this package and your signing certificate; Play purchases need a Play test account and configured products. Ordinary local note-taking does not require those services.

Export/import now uses the Android file picker. New backups are versioned JSON. To recover an old local backup, select its `FeedReader.db`; notes and their category names/colors are merged without replacing the live database. The independent category descriptions in `CategoriesHelper.db` are not recovered through this path. Existing Drive backup formats remain readable.

See [the project audit](docs/PROJECT_AUDIT.md) for the original code rating, repaired bugs, test coverage, and remaining limitations.

### What is this app all about?
Scrittor was made with the intention of keeping the process of note taking simple yet appealing to the eye. I wish to continue the same approach throughout its journey.

### When and How?
I started this project when I was in my last year of college (2016-17). I used to use Google keep back then, however, wanted my own simple note taking application and hence Scrittor was born.

### Why no updates for more than 2 years?
When I got my first job, my priority shifted quickly from Android development to web development. I however remember fixing all the known bugs at that time and the app was fairly stable. However, in the next 1-2 years, Google updated a ton of its libraries and I could not find the time to update the same. However, during the lockdown phase I did try to work on Scrittor, however, my PC died (its still dead) and I being 1 in 3445433 developers did not upload the code to Github. 

### Why open sourcing it now?
I finally was able to somehow locate the files and with the help of my friend, Junaid, updated the app. I realised a lot of people faced issues and I would not want that to happen again and hence I am open sourcing the app so that we can take it further together.

### What tech does it use?
It uses Android SDK and Google Drive for backup.

### Contribute to this Repo
- Fork this repository.
- Clone the your new repository to your system.
- Create a new branch
- Add your changes.
- Commit changes and push the new branch.
- Open and submit a PR.

### Do you have any questions?
Please reach out to me via my email "shahid.sk27595@gmail.com"

<a href="https://play.google.com/store/apps/details?id=com.shahid.nid"><img alt="Get it on Google Play" src="https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png" width="200px"/></a>


**Redistributing this app as your own is NOT permitted.**
