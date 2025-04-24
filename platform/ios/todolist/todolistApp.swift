//
//  todolistApp.swift
//  todolist
//
//  Created by Vadim Yaroschuk on 23.04.25.
//

import SwiftUI
import Shared

@main
struct todolistApp: App {
    init() {
        Dependencies().doInitIosDependencies()
    }
    
    var body: some Scene {
        WindowGroup {
            ComposeView().ignoresSafeArea()
        }
    }
}

struct ComposeView : UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        return MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
