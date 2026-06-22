# FinPilot 🚀

**FinPilot** is a sophisticated personal investment portfolio tracker and analyzer for Android. Designed as a pet project to explore Clean Architecture and Android hardware integration, it helps users manage their assets, understand their risk exposure, and receive rule-based "AI" insights to optimize their financial strategy.

## ✨ Features

- **Portfolio Management**: Easily add and track assets across various categories including Stocks, ETFs, Crypto, Bonds, Cash, Real Estate, and more.
- **Intelligent Risk Profiling**: Define your investor persona (Conservative, Balanced, or Aggressive) to tailor analysis to your goals.
- **Deep Analytics Engine**:
    - **Asset Allocation**: Visual breakdown of your portfolio.
    - **Risk Scoring**: A weighted 0-10 risk index based on your holdings.
    - **Diversification Score**: Measures how well your capital is spread across different asset classes.
- **AI-Powered Insights**: A local, rule-based engine that generates:
    - Executive summaries of your current stance.
    - Risk diagnosis and diversification gap analysis.
    - Actionable plans and target allocation suggestions.
- **Hardware Integration**:
    - **Shake to Refresh**: Uses the Accelerometer to trigger a quick portfolio re-analysis.
    - **Ambient Adaptive UI**: Uses the Light Sensor to toggle between Day and Night analysis modes automatically.

## 🛠 Tech Stack

- **Language**: [Kotlin](https://kotlinlang.org/)
- **UI**: XML Layouts with Material Components
- **Architecture**: Repository Pattern with clear separation of Domain and Data layers.
- **Minimum SDK**: 24 (Android 7.0)
- **Target SDK**: 36 (Android 15)

## 🏗 Project Structure

- `data/`: Handles data persistence and repository logic.
- `domain/`: Contains the core business logic, including `PortfolioAnalyzer` and the `PortfolioInsightEngine`.
- `model/`: Data classes representing Assets, Categories, and Risk Profiles.
- `ui/`: Activity and Adapter implementations.
- `view/`: Custom UI components like `PortfolioChartView`.

## 🚀 Getting Started

1. Clone the repository.
2. Open the project in **Android Studio (Ladybug or newer)**.
3. Sync Project with Gradle Files.
4. Run the app on an emulator or a physical device with a light sensor and accelerometer to experience the full feature set.

## 📸 Screenshots

*(Add screenshots here)*

---
*Created as a pet project by Astmirzhan.*
