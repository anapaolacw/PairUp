# Advanced Pairing Algorithm Documentation

## Overview

The PairUp app now uses an **advanced partnership tracking algorithm** that maximizes variety in player pairings across multiple rounds. This ensures players experience different partnerships and opponents throughout the tournament, making it more fair and engaging.

## How It Works

### 1. Partnership & Opponent History Tracking

The algorithm maintains two key data structures:

```kotlin
private val partnershipHistory = mutableMapOf<String, Int>()
private val opponentHistory = mutableMapOf<String, Int>()
```

- **Partnership History**: Tracks how many times each pair of players has been teammates
- **Opponent History**: Tracks how many times each pair of players has faced each other

### 2. Scoring System

When creating a new round, the algorithm evaluates potential groupings of 4 players using a scoring system:

- **Partnership Penalty**: Weight = 10
  - Higher penalty for repeated partnerships
  - Strongly prioritizes new teammate combinations
  
- **Opponent Penalty**: Weight = 3
  - Moderate penalty for repeated opponents
  - Allows some repeat matchups but minimizes them

**Lower score = Better pairing** (less repetition)

### 3. Greedy Algorithm with Multiple Attempts

For each group of 4 players:

1. **Generate 50 random combinations** (or 2× player count, whichever is smaller)
2. **Evaluate each combination** using the scoring system
3. **Try both possible team configurations**:
   - Config 1: [P1+P2] vs [P3+P4]
   - Config 2: [P1+P3] vs [P2+P4]
4. **Select the configuration with the lowest score**
5. **Use the best group found** across all attempts

### 4. Fair Rest Distribution

When player count isn't divisible by 4:
- Players who rested in the previous round are **prioritized to play**
- Ensures no player sits out multiple consecutive rounds

## Example Scenario

### Tournament with 10 Players (3 rounds)

**Round 1:**
- Match 1: [P1+P2] vs [P3+P4] (Score: 0 - all new)
- Match 2: [P5+P6] vs [P7+P8] (Score: 0 - all new)
- Resting: P9, P10

**Round 2:**
Algorithm prioritizes P9 and P10 to play:
- Match 1: [P9+P1] vs [P10+P5] (Score: 0 - new partnerships)
- Match 2: [P2+P7] vs [P3+P8] (Score: 0 - new partnerships)
- Resting: P4, P6

**Round 3:**
Algorithm prioritizes P4 and P6, while avoiding repeated partnerships:
- Match 1: [P4+P5] vs [P6+P9] (Score: 0 or low)
- Match 2: [P1+P7] vs [P2+P10] (Score: low - minimizes repeats)
- Resting: P3, P8

## Key Benefits

### ✅ Maximum Variety
- Players partner with different people each round
- Reduces the chance of playing against the same opponents repeatedly

### ✅ Fair Play
- Everyone gets relatively equal rest time
- No one sits out multiple consecutive rounds

### ✅ Smart Configuration Selection
- For any 4 players, chooses the team split that creates the most variety
- Example: If P1-P2 already partnered before, it might pair [P1+P3] vs [P2+P4] instead

### ✅ Scalable
- Works with any number of players (minimum 4)
- Handles tournaments with many rounds efficiently

## Algorithm Complexity

- **Time**: O(n × attempts) per round, where attempts = min(50, players × 2)
- **Space**: O(n²) for tracking all player pair combinations
- **Optimized**: Early exit when perfect score (0) is found

## Code Structure

```
TournamentManager.kt
├── createNewRound()              // Main entry point
├── buildHistoryFromTournament()  // Analyzes past rounds
├── selectPlayingPlayers()        // Handles rest rotation
├── createOptimizedMatches()      // Groups players optimally
├── selectBestMatchGroup()        // Finds best 4-player group
├── calculateGroupScore()         // Evaluates group quality
├── calculateConfigScore()        // Scores a team configuration
└── createMatchFromGroup()        // Creates match with best config
```

## Persistence with SharedPreferences

Tournament state is automatically saved after every change:
- New round creation
- Score updates
- Tournament end

This allows users to:
- Close the app mid-tournament
- Resume exactly where they left off
- Preserve all partnership history for optimal pairings

## Future Enhancements

Potential improvements to the algorithm:

1. **Constraint Satisfaction**: Use CSP solver for optimal global solution
2. **Weighted History Decay**: Prioritize recent partnerships over older ones
3. **Skill-Based Balancing**: Factor in player rankings for competitive balance
4. **Custom Penalties**: Allow tournament organizers to adjust weights
5. **Analytics Dashboard**: Show each player's partnership/opponent statistics

## Testing the Algorithm

To verify the algorithm works:

1. Start a tournament with 8 players
2. Play 4+ rounds
3. Observe that:
   - Different partnerships each round
   - Varied opponents across rounds
   - Fair rest distribution (if applicable)
   - No repeated partnerships until necessary

## Mathematical Optimality

With **n players**:
- Total possible partnerships: C(n,2) = n×(n-1)/2
- In a tournament with **n/4 matches per round**:
  - Each round uses n/2 partnerships
  - Optimal diversity achieved when history is evenly distributed

The algorithm approaches this optimum through:
- Greedy selection with multiple random attempts
- Penalty-based scoring to avoid repeats
- Configuration flexibility (2 options per 4 players)

