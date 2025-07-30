//
// Created by Luke Catherall on 30/07/2025.
//

import Foundation
import UIKit
import shared

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {
    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
    ) -> Bool {
        KoinKt.initKoinIos() // Call shared DI init
        return true
    }
}