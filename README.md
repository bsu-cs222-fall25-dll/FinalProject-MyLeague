# MyLeague

**Authors:** Chris Burke, Evan Wilson, Jack Kerth, and Oladayo David Ayorinde

## Project Summary

MyLeague is a Fantasy Football Draft application that allows users to view the current NFL offensive players and their stats, create custom leagues and teams, and manage their fantasy rosters. The application features player search and filtering capabilities, team management, and real-time player data from the NFL.

### Key Features
- View current NFL offensive players with detailed statistics
- Create multiple leagues with customizable roster configurations
- Build and manage teams within leagues
- Search players by name and filter by position or NFL team
- Add players to team rosters with position-specific slots (QB, RB, WR, TE, K, FLEX)
- View team rosters and remove players
- View detailed player statistics (season and weekly stats)
- Compare player statistics side-by-side
- Calculate team scores based on fantasy scoring system
- Reload player data from API
- Player data fetched from Tank01 NFL Live Statistics API with local caching

## New Features (Iteration 2)
- **Player Statistics Modal:** View detailed season and weekly statistics for any player
- **Player Comparison:** Compare two players' statistics side-by-side
- **Team Score Calculation:** Automatically calculate team scores based on player performance
- **Reload Functionality:** Refresh player data from the API without restarting
- **Enhanced UI:** Tooltips on all interactive elements for better user experience
- **Error Handling:** Network error detection and user-friendly error messages
- **Data Caching:** Improved player data caching to reduce API calls


## Build Instructions
1. **Clone the repository:**
```bash
   git clone https://github.com/bsu-cs222-fall25-dll/FinalProject-MyLeague.git
   cd FinalProject-MyLeague
```

2. **Set up API credentials:**
    - Locate the `.env.example` file at the root of the project
    - Replace `YOUR_API_KEY` so the second line reads: `API_KEY=your_actual_api_key`
    - Get your API key from [RapidAPI - Tank01 NFL Statistics](https://rapidapi.com/tank01/api/tank01-nfl-live-in-game-real-time-statistics-nfl) by signing up for the basic subscription.
    - Rename `.env.example` to `.env`

3. **Build and run the program:**
```bash
   ./gradlew run
```
- JDK 22 preferred
- Requires JavaFX 17 or higher

## Program Instructions

### Players View
When you launch the program, you'll start at the **Players View** where you can browse current NFL offensive players.

**Navigation Controls:**
- **Search Bar** (top): Search players by name
- **Position Filter** (top right): Filter by position (QB, RB, WR, TE, K)
- **Team Filter** (top right): Filter by NFL team
- **League Selector** (left sidebar): Dropdown labeled "Default" - select or create leagues
- **Team Selector** (left sidebar): Dropdown labeled "None" - select or create teams
- **Reload Button** (left sidebar): Refresh player data from the API

**Creating Leagues:**
1. Select "Create" from the league selector dropdown
2. Enter your league name in the modal
3. Click "Add" to create the league

**Creating Teams:**
1. Select a league from the league selector
2. Select "Create" from the team selector dropdown
3. Enter your team name in the modal
4. Click "Add" to create the team

**Adding Players:**
1. Select a team from the team selector
2. Click the "Add" button on any player row
3. In the modal, click the button corresponding to the roster position (QB, RB, WR, TE, K, FLEX)
4. The player will be added to your team at that position

**Viewing Player Stats:**
1. Click on any player row to open the player statistics modal
2. Toggle between "Season" and "Weekly" stats
3. Click "Compare" to compare the selected player with another player
4. Search and filter by position or team to find players to compare

### Team View
Click the **"MyTeam"** button (left sidebar) to view your team's roster.

**Team View Features:**
- View all players on your selected team
- Search and filter your roster
- Each player displays their assigned roster position
- Click the "Calculate" button to calculate your team's total fantasy score
- Click "Remove" to remove a player from your team
- Click on any player to view their detailed statistics
- Use the "Players" button to return to the Players View

**Note:** You cannot create new leagues or teams from the Team View page.

## Fantasy Scoring System

The application uses the following scoring system:
- **Rushing/Receiving Yards:** 0.1 points per yard
- **Rushing/Receiving Touchdowns:** 7 points each
- **Passing Yards:** 0.04 points per yard
- **Passing Touchdowns:** 4 points each
- **Receptions:** 1 point each (PPR)
- **Interceptions:** -2 points
- **Fumbles:** -2 points
- **Field Goals Made:** 4 points
- **Extra Points Made:** 2 points
- **Field Goal/Extra Point Attempts:** -1 point per attempt

## Technology Stack
- **Language:** Java 21
- **UI Framework:** JavaFX 17
- **API:** Tank01 NFL Live Statistics API (RapidAPI)
- **Environment Management:** dotenv-java
- **Data Format:** JSON
- **Testing:** JUnit 5

---

## Version
Current release: **v0.2.1**