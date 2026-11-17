# RepoPlayer

This is the Phase 1 foundation for the Android media center app.

- Android app project skeleton
- Material-based dark theme and settings persistence
- TV-safe foundation with D-pad aware main activity and focusable UI
- Repository JSON model and local storage for repo URLs
- Add/list/remove repo flow with demo repository loading
- Sample repository JSON and legal-source-only content notification

## Build

1. Open the project in Android Studio.
2. Let Gradle sync.
3. Select the `app` configuration and run debug.

## Test

1. Launch the app on a phone emulator.
2. Verify the home screen warning is displayed.
3. Open the Repositories tab and add the sample repo.
4. Confirm the repo appears in the list and can be removed.
5. Toggle the dark mode switch and verify it persists after restart.

## Known limitations

- Full media features are intentionally not implemented yet.
- This phase is a production-ready foundation only.
- No wrapper files are included yet; Android Studio automatically generates them when you sync.
