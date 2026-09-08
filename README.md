# CEP Motorcycle System: AI-Driven Telemetry & Safety
An edge-AI telemetry and adaptive rider intelligence platform that retrofits modern mid-capacity motorcycles into smart, connected vehicles using an ultra-low-cost hybrid architecture. 

**Academic Affiliation:** B.Tech Information Technology, Pimpri Chinchwad College of Engineering

### ⚙️ System Architecture
* **Hardware Bridge:** ELM327 Bluetooth Scanner (v1.5) via Euro 5 to 16-pin OBD-II Adapter.
* **Edge AI:** TensorFlow Lite models running locally for zero-latency inference.
* **Mobile HUD:** Custom Android interface built in Kotlin.

### 🧠 Machine Learning Core
* **Rider Profiling:** Unsupervised K-Means clustering categorizes behavior into Eco, Touring, or Aggressive profiles based on telemetry and IMU data.
* **Adaptive Shift Intelligence:** Random Forest Regressor calculates dynamic shift points based on engine load and road incline to maximize fuel efficiency.

### 🚀 Setup Instructions
1. Navigate to `/Machine-Learning` to view the Python data pipelines and synthetic dataset generation.
2. Open `/Android-App` in Android Studio. Sync Gradle and run on a physical device.
3. Place `cep_shift_model.tflite` in the `app/src/main/assets/` directory before building.
