import SwiftUI
import ComposeApp

@main
struct iOSApp: App {

    init() {
        // Initialize Koin when the app starts
        KoinInitKt.startKoinIos()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}