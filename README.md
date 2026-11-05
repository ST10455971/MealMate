MealMate – Recipe & Meal Planner

OPSC6312 Part 3 – Android Prototype Development

Student Information

Student Name: Sisanda Makhanye

Student Number: ST10455971

Group: 14

Module: OPSC6312

Project: MealMate – Recipe & Meal Planner

Overview

MealMate is an Android recipe and meal-planning application developed using Kotlin in Android Studio. The application integrates with TheMealDB REST API to allow users to search for recipes, view recipe details, save favourite recipes, plan meals, and manage shopping lists.

Main Features

* User Registration
* Secure Login
* Dashboard with Recipe Search
* REST API Integration (TheMealDB)
* Recipe Details
* Favourite Recipes
* Weekly Meal Planner
* Shopping List
* User Settings
* Dietary preferences
* Logout

Technologies Used

* Kotlin
* Android Studio
* Retrofit
* Firebase Authentication
* TheMealDB REST API
* Room DATABASE
* GitHub Actions

REST API Used

TheMealDB API provides recipe information including recipe names, ingredients, categories, cuisines, and cooking instructions.

##  Additional Features

###  1. Recipe Exploration & Hourly Trending
- **Real-Time Search**: Search thousands of recipes by name or ingredient with smooth typing debounce.
- **Category Filtering**: Instant filtering by popular categories including *Chicken*, *Beef*, *Seafood*, *Vegetarian*, and *Pasta*.
- **Hourly Trending Recipes**: Dynamic "Trending This Hour" section that automatically rotates curated recipe collections every hour based on the time of day.

###  2. Calendar-Based Meal Planner
- **Interactive Top Calendar**: View and select dates of the current week with a horizontal calendar selector.
- **Categorized Slots**: Plan *Breakfast*, *Lunch*, *Dinner*, and *Dessert* for any selected date.
- **Seamless Addition**: Add any recipe directly into your meal planner with one tap.

###  3. Grouped Shopping List with Persistence
- **Recipe-Grouped Ingredients**: Ingredients are automatically aggregated from planned meals and grouped cleanly under their recipe title and scheduled day.
- **Interactive Check-Off**: Mark off items with strike-through styling.
- **Persistent Checked State**: Checked items stay saved in `SharedPreferences` across app restarts.

###  4. Cooking Mode & Kitchen Timer
- **Step-by-Step Cooking Guide**: Step through recipe instructions with interactive Previous/Next controls.
- **Built-In Countdown Timer**: Kitchen timer with quick preset buttons (`+1m`, `+5m`, `+10m`) and Start/Pause/Reset controls.

###  5. Serving-Size Calculator
- **Dynamic Quantity Scaling**: Interactive `-` and `+` serving size controls scale ingredient quantities dynamically (e.g., scaling from 2 to 4 servings).

###  6. Pantry Ingredient Tracker
- **Kitchen Inventory**: Add, view, and manage ingredients you already have at home with custom quantities.
- **RoomDB Persistence**: Pantry items save directly to the local SQLite/Room database.

###  7. Recipe Ratings & Reviews
- **Star Ratings**: Rate recipes (1–5 stars) and write custom review notes.
- **Community Summaries**: View average rating scores and review comments on recipe detail pages.

###  8. Weekly Meal Budget Tracker
- **Grocery Budget Limits**: Set a target weekly budget limit (in ZAR R).
- **Meal Cost Estimations**: Track estimated costs of planned meals and monitor remaining budget with a visual progress bar.

###  9. South African Multi-Language Support
- **5 Supported Languages**:
  - 🇿🇦 **English** (`en`)
  - 🇿🇦 **isiZulu** (`zu`)
  - 🇿🇦 **isiXhosa** (`xh`)
  - 🇿🇦 **Afrikaans** (`af`)
  - 🇿🇦 **Sesotho** (`st`)
- **Dynamic Locale Switching**: Instantly re-inflates the application in the selected language.

###  10. Security & Single Sign-On (SSO)
- **Password Encryption**: SHA-256 password hashing with salt.
- **Password Visibility Toggle**: Interactive show/hide eye toggle (`TextInputLayout`).
- **Strict Validation**: Enforces 8+ characters, numeric digits, and special characters.
- **Google Single Sign-On (SSO)**: Native Google Account Chooser integration via `GoogleSignInClient` and Firebase Auth.

###  11. Real-Time Notifications
- **5 Custom Alert Channels**:
  -  **Recipe of the Day**: Daily recommendations tailored to user tastes.
  -  **Meal Plan Reminders**: Scheduled alerts for planned meal menus.
  -  **Seasonal & Holiday Dishes**: Festive menu ideas for upcoming holidays.
  -  **Re-engagement Prompts**: Quick 15-minute meal suggestions.
  -  **Saved Recipe Updates**: Notifications when saved recipes get featured.

###  12. Offline Mode & RoomDB Sync
- **Full Offline Availability**: Favorites, Meal Plans, and Pantry items persist locally using Android Room DB.
- **Connectivity Monitoring**: Real-time status indicator showing *"Online - Synced with Cloud"* or *"Offline Mode - Local RoomDB Active"*.

---

##  Tech Stack & Libraries

- **Language**: Kotlin 2.0
- **UI Architecture**: Native Android XML Views with **ViewBinding** (No Jetpack Compose)
- **Navigation**: Activity & Kotlin Fragment Manager (`AppCompatActivity`, `Fragment`)
- **Database**: Room Database (SQLite ORM) with Coroutines `Flow`
- **Networking**: Retrofit 2 + Gson Converter (TheMealDB REST API integration)
- **Authentication**: Firebase Auth & Google Play Services Auth (Google SSO)
- **Image Loading**: Coil (`io.coil-kt:coil`)
- **Background Work**: AndroidX WorkManager
- **CI/CD**: GitHub Actions Workflows (`.github/workflows/android-ci.yml`)

---

##  Project Structure

```
com.example.mealmate/
│
├── adapters/
│   ├── CalendarDayAdapter.kt
│   ├── MealPlanAdapter.kt
│   ├── PantryAdapter.kt
│   ├── RecipeAdapter.kt
│   ├── ShoppingAdapter.kt
│   └── TrendingAdapter.kt
│
├── data/
│   ├── api/
│   │   ├── MealApiService.kt
│   │   └── RetrofitClient.kt
│   ├── database/ (Room DB & Entities)
│   ├── local/
│   │   ├── AppDatabase.kt
│   │   ├── Entities.kt
│   │   └── MealDao.kt
│   ├── model/
│   │   └── Recipe.kt
│   └── repository/
│       ├── MealRepository.kt
│       └── RecipeRepository.kt
│
├── fragments/
│   ├── BudgetFragment.kt
│   ├── CookingModeFragment.kt
│   ├── DashboardFragment.kt
│   ├── FavouriteFragment.kt
│   ├── LanguageFragment.kt
│   ├── LoginFragment.kt
│   ├── MealPlannerFragment.kt
│   ├── PantryFragment.kt
│   ├── ProfileFragment.kt
│   ├── RecipeDetailFragment.kt
│   ├── RegisterFragment.kt
│   ├── SettingsFragment.kt
│   ├── ShoppingListFragment.kt
│   └── SplashFragment.kt
│
├── ui/
│   ├── theme/ (Color & Theme definitions)
│   └── viewmodel/
│       └── MealViewModel.kt
│
└── utils/
    ├── LocaleHelper.kt
    ├── NotificationHelper.kt
    ├── PasswordUtils.kt
    └── PreferenceHelper.kt
```

---

##  Color Palette

| Color Name | Hex Code | Usage |
| :--- | :--- | :--- |
| **GreenPrimary** | `#2F6B4F` | Main brand color, headings, titles, active navigation |
| **GreenLight** | `#5C8D72` | Subtitles, secondary text, input hints |
| **OrangeAccent** | `#F28C52` | Buttons, highlights, call-to-action controls |
| **CreamBackground** | `#FFFBF5` | Main screen background |
| **White** | `#FFFFFF` | Cards, input fields, bottom navigation background |
| **DarkText** | `#2D2D2D` | Headings and primary body text |
| **LightGrey** | `#F2F2F2` | Dividers, subtle borders |

---

##  Building & Running

### Prerequisites
- Android Studio 2026.1.4+ (or Ladybug/Koala+)
- JDK 17 or JDK 21
- Android SDK 34+

### Clone & Build
```bash
# Clone the repository
git clone https://github.com/ST10455971/MealMate.git

# Navigate to project root
cd MealMate

# Build Debug APK using Gradle Wrapper
./gradlew assembleDebug

# Run Unit Tests
./gradlew testDebugUnitTest
```

---

##  Continuous Integration (CI)

This project uses **GitHub Actions** (`.github/workflows/android-ci.yml`) to automatically build and test on every push or pull request to the `main` branch:
- Automated checkout & JDK 21 setup
- Unit test execution (`testDebugUnitTest`)
- Debug APK assembly (`assembleDebug`)
- Test report and APK artifact uploads

---

##  License

This project is developed as part of an Android Application Development assignment. All rights reserved.
>>>>>>> a8c0291 (Update README.md, GitHub Actions CI, and complete MealMate features)
>
Video Demonstration

Video Link: https://youtu.be/uw0j5gIsFJY?si=jVCWgZDaAU8blkmE

Installation

1. Install the APK.
2. Open MealMate.
3. Register or log in.
4. Search recipes and explore the application.

Project Status

This project represents the completed prototype for OPSC6312 Part 2.
