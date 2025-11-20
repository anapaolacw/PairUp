# PairUp - Pickleball Tournament Manager

A Android app for organizing Pickleball tournaments with **intelligent player pairing** and automatic score tracking.

## ✨ Key Features

### 🎯 Advanced Pairing Algorithm
- **Maximizes variety** - Players get different partners each round
- **Tracks partnership history** - Avoids repeated pairings across rounds
- **Tracks opponent history** - Minimizes playing against the same people
- **Smart team configuration** - Automatically selects the best team split for any group of 4
- See [ALGORITHM.md](ALGORITHM.md) for detailed explanation

### 💾 Automatic Tournament Persistence
- **Auto-save with SharedPreferences** - Tournament state saved after every action
- **Resume capability** - Close and reopen the app without losing progress
- **History preserved** - Partnership tracking persists across app sessions

### 🎲 Flexible Player Management
### 🎲 Flexible Player Management
- Add players by name for personalized tournaments
- Add players by count for quick anonymous tournaments (numbered 1, 2, 3...)
- Minimum 4 players required

### 🔄 Smart Round Management
- View all matches in the current round
- Enter scores for each match
- Progress to next round with **re-optimized pairings**
- Track which players are resting each round
- **Fair rest rotation** - Players who sat out won't sit consecutively

### 📊 Comprehensive Score Tracking
### 📊 Comprehensive Score Tracking
- Individual player statistics across all matches
- Tracks wins, games played, and total score
- Players maintain their stats even when partnered with different players
- **Real-time statistics** preserved across rounds

### 🏆 Tournament Results
- Final leaderboard ranked by wins, then total score
- Complete statistics for all players
- Shows games played, wins, and total points
- Option to start a new tournament

## 🎮 How to Use

1. **Start Tournament**: Choose between adding named players or anonymous players by count
2. **Add Players**: 
   - For named: Enter each player's name
   - For anonymous: Enter the total number of players (minimum 4)
3. **Play Rounds**: 
   - View match pairings
   - Enter scores for each match as they complete
   - Start next round when all matches are finished
4. **End Tournament**: View final rankings and player statistics

## Technical Details

- **Architecture**: MVVM (Model-View-ViewModel)
- **Language**: Kotlin
- **UI**: Material Design 3 Components with ViewBinding
- **Minimum SDK**: 24 (Android 7.0)
- **Target SDK**: 34

## Project Structure

```
app/src/main/java/com/example/pairup/
├── data/           # Data models (Player, Match, Round, Tournament)
├── logic/          # Business logic (TournamentManager)
├── viewmodel/      # ViewModels for state management
├── adapter/        # RecyclerView adapters
└── *.kt            # Activities and Dialogs
```

## Building

```bash
./gradlew build
```

## Running

Open the project in Android Studio and run on an emulator or physical device.

