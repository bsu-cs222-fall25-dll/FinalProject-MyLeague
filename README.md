# MyLeague

**Authors:** Chris Burke, Evan Wilson, Jack Kerth, and Oladayo David Ayorinde

## Project Summary

MyLeague is a Fantasy Football Draft application that allows users to view the current NFL offensive players and their stats, create custom leagues and teams, and manage their fantasy rosters. The application features player search and filtering capabilities, team management, and real-time player data from the NFL.

### Key Features
- View current NFL offensive players with detailed statistics
- Create multiple leagues with customizable roster configurations and scoring systems
- Build and manage teams within leagues
- Search players by name and filter by position or NFL team
- Add players to team rosters with position-specific slots (QB, RB, WR, TE, K, FLEX)
- View team rosters with weekly and season scores
- View detailed player statistics (season and weekly stats)
- Compare player statistics side-by-side
- Calculate team scores based on customizable fantasy scoring system
- Save and load leagues with teams and rosters
- Edit league roster positions and scoring coefficients
- Delete leagues and teams
- Reload player data from API
- Player data fetched from Tank01 NFL Live Statistics API with local caching

## New Features (Iteration 3)
- **Persistent Storage:** Save and load leagues, teams, and rosters across sessions
- **League Management:** Edit league roster configurations and scoring coefficients after creation
- **Custom Scoring System:** Configure custom point values for all fantasy scoring categories
- **Delete Functionality:** Remove unwanted leagues and teams with confirmation dialogs
- **Weekly Scoring:** View individual player scores for the most recent week
- **Team View Enhancements:** Display last game matchup, weekly scores, and season totals for each player
- **Improved Data Architecture:** Refactored player data structure for better organization and maintainability
- **Enhanced UI:** Added league editor, confirmation modals, and improved visual feedback

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
- **League Selector** (left sidebar): Dropdown to select or create leagues
- **Team Selector** (left sidebar): Dropdown to select or create teams
- **Reload Button** (left sidebar): Refresh player data from the API
- **Edit Button** (left sidebar): Modify league roster positions and scoring
- **Save Button** (left sidebar): Save current league to disk
- **Delete League Button** (bottom left): Remove the current league
- **Delete Team Button** (bottom left): Remove the current team

**Creating Leagues:**
1. Select "Create" from the league selector dropdown
2. Enter your league name in the modal
3. Set the number of roster position for each position type (QB, RB, WR, TE, K, FLEX)
4. Configure scoring coefficients for each statistical category
5. Click "Create League" to finalize

**Editing Leagues:**
1. Select the league you want to edit
2. Click the "Edit" button (pencil icon) in the left sidebar
3. Modify roster positions and/or scoring coefficients
4. Click "Edit" to save changes
    - **Note:** Reducing roster positions may remove players from teams if they exceed new limits

**Saving Leagues:**
- Click the "Save" button (floppy disk icon) to persist your league to disk
- Saved leagues will automatically load when you restart the application
- Saving overwrites any previously saved league with the same name

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
    - **Note:** Only positions available in your league and matching the player's position will be enabled


**Viewing Player Stats:**
1. Click on any player row to open the player statistics modal
2. Toggle between "Season" and "Weekly" stats
3. Click "Compare" to compare the selected player with another player
4. Search and filter by position or team to find players to compare

**Deleting Leagues and Teams:**
- Click "Delete League" to remove the current league (requires at least 2 leagues)
- Click "Delete Team" to remove the current team
- Both actions require confirmation before proceeding

### Team View
Click the **"MyTeam"** button (left sidebar) to view your team's roster.

**Team View Features:**
- View all players on your selected team with their assigned positions
- See weekly and season fantasy point totals for each player
- View the last game matchup for each player
- Total team score displayed at the top (weekly points)
- Search and filter your roster by position
- Click "Remove" to remove a player from your team
- Click on any player to view their detailed statistics
- Use the "Players" button to return to the Players View

**Team Scoring Display:**
Each player card shows:
- **Last Match:** The most recent game matchup
- **Last Week:** Fantasy points scored in the most recent week
- **Season:** Total fantasy points for the season


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
- **Field Goal/Extra Point Attempts:** -1 point per attempt (default deducted, made points added separately)

All scoring coefficients can be customized when creating or editing a league.

## Data Persistence

### Saved Data Location
- **Leagues:** Stored in `SavedFiles/SavedLeagues/` directory
- **Player Data:** Cached in `SavedFiles/PlayerList.json`

### Saved League Format
Each league is saved as a JSON file containing:
- League name and roster configuration
- Custom scoring coefficients
- All teams within the league
- Complete player rosters with positions

### Loading Behavior
- Saved leagues automatically load on application startup
- If no saved leagues exist, a default league is created
- Player data is cached locally to reduce API calls

## Technology Stack
- **Language:** Java 21
- **UI Framework:** JavaFX 17
- **API:** Tank01 NFL Live Statistics API (RapidAPI)
- **Environment Management:** dotenv-java
- **Data Format:** JSON
- **Testing:** JUnit 5

---

## Version
Current release: **v0.3.0**